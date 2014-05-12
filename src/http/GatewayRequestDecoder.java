package http;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class for decoding a request from the client to the gateway
 * @author Romain Mormont
 */
public class GatewayRequestDecoder 
{
	private String remote_address = null;
	private HTTPRequest http_req = null;
	private HashMap<String, String> path_params = null;
	
	private void disp()
	{
		System.out.println("\n# DEBUG # GatewayRequestDecoder");
		System.out.println("# remote : " + remote_address + "\n");
	}
	
	public GatewayRequestDecoder(HTTPRequest request)
	{
		http_req = request;
		path_params = new HashMap<String, String>();

		parsePath();
		parseRemoteAddress();
		//disp();
	}
	
	/**
	 * Parses the remote address in order to make it readable.
	 * If the 's' argument was not contained in the request, the 
	 * remote_address attribute isn't modified
	 */
	private void parseRemoteAddress() 
	{
		if(!path_params.containsKey("s"))
			return;
		
		remote_address = decodeURL(path_params.get("s"));
	}

	/**
	 * Parses the path contained in the http request : extracts the argument
	 * of the client request (s, forceRefresh,...) and put them in the map
	 */
	private void parsePath()
	{
		String regex = "^(?:/?\\?)?((?:&?[\\w%~\\.=\\-]*)*)(#[\\w%~\\.=]*)?$";
		Pattern p = Pattern.compile(regex);

		Matcher m = p.matcher(http_req.getPath());
		
		if(!m.find())
			return;
		
		String[] args_array = m.group(1).split("&");
		
		for(String arg : args_array) // run through arguments of the path
		{
			if(!arg.isEmpty())
			{
				String[] arg_exploded = arg.split("=", 2); // split arg name and value
				
				if(arg_exploded.length == 1)
					path_params.put(arg_exploded[0], "");
				else
					path_params.put(arg_exploded[0], arg_exploded[1]);
			}
		}
	}
	
	/**
	 * Decode an url from a percent encoded one to a readable one
	 * @param url URL to decode.
	 * @return a String containing the decoded url or null if the url is not acceptable
	 */
	private String decodeURL(String url)
	{
		try 
		{	if(!url.startsWith("http"))
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
		return (path_params.containsKey("forceRefresh") && path_params.get("forceRefresh") == "true");
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
		return remote_address != null;
	}
}
