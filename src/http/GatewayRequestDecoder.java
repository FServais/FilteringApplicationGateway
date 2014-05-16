package http;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class for decoding a request from the client.
 * This class provides methods for accessing arguments of the request ("s", "forceRefresh").
 * @author Fabrice Servais & Romain Mormont
 */
public class GatewayRequestDecoder 
{
	private String remote_address;
	private HTTPRequest http_req;
	private HashMap<String, String> params;
	private boolean is_acceptable_file = true;
	
	// TODO : remove this
	private void debug()
	{
		System.out.println("\n# DEBUG # GatewayRequestDecoder");
		System.out.println("# remote : " + remote_address);
		System.out.println("# force  : " + params.containsKey("forceRefresh") + " (val: " + params.get("forceRefresh") + ")\n");
	}
	
	/**
	 * Constructs a GatewayRequestDecoder from the received http request
	 * @param request the request received by the gateway
	 */
	public GatewayRequestDecoder(HTTPRequest request)
	{
		http_req = request;
		params = new HashMap<String, String>();

		parsePath();
		parseRemoteAddress();
		//debug();		

		checkPath();
	}
	
	/**
	 * Parses the remote address in order to make it readable.
	 * If the 's' argument was not contained in the request, the 
	 * remote_address attribute isn't modified
	 */
	private void parseRemoteAddress() 
	{
		if(!params.containsKey("s"))
			return;
		
		remote_address = decodeURL(params.get("s"));
	}

	/**
	 * Parses the path contained in the http request : extracts the argument
	 * of the client request (s, forceRefresh,...) and put them in the map
	 */
	private void parsePath()
	{
		// regex for extracting the request parameters
		String regex = "^(?:/?\\?)?((?:&?[\\w%~\\.=\\-]*)*)(#[\\w%~\\.=]*)?$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(http_req.getPath());
		
		if(!m.find()) // no param
			return;
		
		String[] args_array = m.group(1).split("&");
		
		for(String arg : args_array) // run through arguments of the path
		{
			if(!arg.isEmpty())
			{
				String[] arg_exploded = arg.split("=", 2); // split arg name and value
				
				if(arg_exploded.length == 1)
					params.put(arg_exploded[0], "");
				else
					params.put(arg_exploded[0], arg_exploded[1]);
			}
		}
	}
	
	/**
	 * Checks if the path is a path to a unsupported file type 
	 * unsupported => pdf, gzip, gz, tgz, tar.gz, zip, rar,...
	 */
	private void checkPath()
	{
		if(remote_address == null)
			return;
		
		// regex for finding a forbidden file extension in the url
		String regex = "\\.(pdf|(tar\\.|t)?gz(ip)?|zip|rar)(\\?.+)?$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(remote_address);
		
		is_acceptable_file = !m.find();
	}
	
	/**
	 * Decode an url from a percent encoded one to a readable one
	 * @param url URL to decode.
	 * @return a String containing the decoded url or null if the url is not acceptable
	 */
	private String decodeURL(String url)
	{
		try 
		{	
			if(!url.startsWith("http"))
				url = "http%3A%2F%2F" + url;
			
			return new URI(url).getPath();
		} 
		catch (URISyntaxException e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Returns true if the user requested to refresh the cache
	 * @return true if the user requested to refresh the cache, false otherwise
	 */
	public boolean refreshIsForced()
	{
		return (params.containsKey("forceRefresh") && params.get("forceRefresh").equals("true"));
	}
	
	/**
	 * Returns the url of the targeted website
	 * @return a String containing the url of the targeted website, 
	 * 			null if it was not specified in the request r malformed
	 */
	public String getUrl()
	{
		return remote_address;
	}
	
	/**
	 * Returns true if the client request is valid (if the url is valid)
	 * @return true if the client request is valid, false otherwise
	 */
	public boolean validRequest()
	{
		return remote_address != null && !remote_address.equals("http://");
	}
	
	/**
	 * Returns true if the file to which the path points to can be managed by the gateway
	 * @return true if the file to which the path points to can be managed by the gateway, false otherwise
	 */
	public boolean fileTypeIsOk()
	{
		return is_acceptable_file;
	}
}
