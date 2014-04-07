package http;

import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


import displayer.Displayer;

/**
 * Class that handle a connection from a client that request a page.
 * @author Fabs
 *
 */
public class HTTPClientRequest extends Thread {
	
	Socket socket;
	//Displayer display = Displayer.getInstance();
	
	public HTTPClientRequest(Socket socket)
	{
		this.socket = socket;
	}
	
	public void run()
	{
		// Wait for the request
		InputStream is = null;
		try 
		{
			is = socket.getInputStream();
			
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			
			// Read the request
			String line, URL = new String();
			while((line = br.readLine()) != null)
			{
				if(line.length() >= "GET /?s=".length())
				{
					if(line.substring(0, "GET /?s=".length()).equals("GET /?s="))
					{
						URL = line.substring("GET /?s=".length(), line.length()-(" HTTP/1.1".length()));
						break;
					}
				}
			}
			
			
			
			System.out.println("URL : ");
			System.out.println(decodeURL(URL));
			
		} 
		catch (IOException e) 
		{
			System.err.println("Error while getting request"); // Use Displayer instead...
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
