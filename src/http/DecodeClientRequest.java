package http;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class DecodeClientRequest 
{
	private String request;
	private String GETLine;
	
	public DecodeClientRequest(String req)
	{
		if(req == null)
			System.err.println("What???");
		this.request = req;
		this.GETLine = GETLineFromRequest();
	}
	
	/**
	 * Get the address requested by a GET request message.
	 * @return
	 */
	public URL getURL()
	{	
		int sFirstArgument = GETLine.indexOf("?s="),
			sOtherArgument = GETLine.indexOf("&s="); // Maybe 's' is not the first argument
		
		if(sFirstArgument == -1 && sOtherArgument == -1) // Argument 's' not there
			return null;
			
		int indexOfPath = Math.max(sFirstArgument, sOtherArgument) + "?s=".length(); 
		
		return decodeURL(GETLine.substring(indexOfPath, GETLine.length()-(" HTTP/1.1".length())));
	}
	
	
	/**
	 * Get the boolean 'forceRefresh' in the URL.
	 * @return Value of 'forceRefresh'. False by default.
	 */
	public boolean forceRefresh()
	{
		int indexOfBool = Math.max(GETLine.indexOf("?forceRefresh="), GETLine.indexOf("&forceRefresh=")) + "&forceRefresh=".length();
		
		if(GETLine.substring(indexOfBool, GETLine.length()).startsWith("true"))
			return true;
		
		return false;
	}
	
	/**
	 * Take the line of the request concerning the "GET request", e.g : "GET \<site\> HTTP/1.1"
	 * @param request
	 * @return Line of "GET request"
	 */
	private String GETLineFromRequest()
	{
		String[] lines = request.split("\n");
		for(String line : lines)
		{
			if(line.length() >= "GET".length() && line.substring(0, "GET".length()).equals("GET"))
				return line;
		}

		return null;
	}
	
	/**
	 * Percent decoding for an URL.
	 * @param URL URL to decode.
	 * @return URL decoded.
	 */
	private URL decodeURL(String URL)
	{ 
		try 
		{
			return new URL(new URI(URL).getPath());
		} 
		catch (URISyntaxException e) 
		{
			return null;
		}
		catch (MalformedURLException e) 
		{
			System.out.println("LOL");
			return null;
		}
	}
}
