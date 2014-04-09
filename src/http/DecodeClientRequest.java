package http;

import java.net.URI;
import java.net.URISyntaxException;

public class DecodeClientRequest {
	
	private String request;
	private String GETLine;
	
	public DecodeClientRequest(String req)
	{
		this.request = req;
		this.GETLine = GETLineFromRequest(req);
	}
	
	/**
	 * Get the address requested by a GET request message.
	 * @return
	 */
	public String getPath()
	{		
		int indexOfPath = max(GETLine.indexOf("?s="), GETLine.indexOf("&s=")) + "?s=".length(); // Maybe 's' is not the first argument
		
		return decodeURL(GETLine.substring(indexOfPath, GETLine.length()-(" HTTP/1.1".length())));
	}
	
	
	/**
	 * Get the boolean 'forceRefresh' in the URL.
	 * @return Value of 'forceRefresh'. False by default.
	 */
	public boolean forceRefresh()
	{
		int indexOfBool = max(GETLine.indexOf("?forceRefresh="), GETLine.indexOf("&forceRefresh=")) + "&forceRefresh=".length();
		
		if(GETLine.substring(indexOfBool, GETLine.length()).startsWith("true"))
			return true;
		
		return false;
	}
	
	/**
	 * Take the line of the request concerning the "GET request", e.g : "GET \<site\> HTTP/1.1"
	 * @param request
	 * @return Line of "GET request"
	 */
	private String GETLineFromRequest(String request)
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
	private String decodeURL(String URL)
	{ 
		try 
		{
			return new URI(URL).getPath();
		} 
		catch (URISyntaxException e) 
		{
			return null;
		}
	}
	
	/**
	 * Choose the maximum value bewteen i and j;
	 * @param i Integer
	 * @param j Integer
	 * @return Maximum of i and j.
	 */
	private int max(int i, int j)
	{
		if(i >= j)
			return i;
		return j;
	}
}
