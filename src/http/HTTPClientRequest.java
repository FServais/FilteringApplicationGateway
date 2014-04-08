package http;

import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import http.html.HTMLPage;
import datastructures.Cache;
import displayer.Displayer;

/**
 * Class that handle a connection from a client that request a page.
 * @author Fabs
 *
 */
public class HTTPClientRequest extends Thread {
	
	private Socket socket;
	private Cache<String, HTMLPage> cache;
	//Displayer display = Displayer.getInstance();
	
	public HTTPClientRequest(Socket socket)
	{
		this.socket = socket;
		cache = new Cache<String, HTMLPage>();
	}
	
	public void run()
	{
		// Wait for the request
		InputStream is = null;
		String URL = new String();
		try 
		{
			is = socket.getInputStream();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
			// Read the request
			String line;
			while((line = br.readLine()) != null)
			{
				if(line.length() >= "GET /?s=".length())
				{
					if(line.substring(0, "GET /?s=".length()).equals("GET /?s="))
					{
						URL = decodeURL(line.substring("GET /?s=".length(), line.length()-(" HTTP/1.1".length())));
						break;
					}
				}
			}
		} 
		catch (IOException e) 
		{
			System.err.println("Error while getting request"); // Use Displayer instead...
		}
		
		// Analysis of "forceRefresh" flag
		boolean forceRefresh = false;
		
		// If already in cache and don't need to be refreshed and don't have "forceRefresh" flag
		if(cache.isContained(URL) && cache.getEntry(URL).isValid() && !forceRefresh)
		{
			/* Return the page */
		}
		else
		{
			/* Get the page and update cache */
		}

	}

	
	// QUELLE CLASSE ?
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
}
