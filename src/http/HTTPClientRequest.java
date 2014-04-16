package http;

import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

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
	
	private static final String OUTPUT = "<html><head><title>Example</title></head><body><p>Worked!!!</p></body></html>\n";
	private static final String OUTPUT_HEADERS = "HTTP/1.1 200 OK\r\n" +
	    "Content-Type: text/html\r\n" + 
	    "Content-Length: ";
	private static final String OUTPUT_END_OF_HEADERS = "\r\n\r\n";
	
	public HTTPClientRequest(Socket socket)
	{
		this.socket = socket;
		cache = new Cache<String, HTMLPage>();
	}
	
	public void run()
	{
		System.out.println("New request");
		// Wait for the request
		String URL = new String(), request = new String();
		try 
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			// Read the request
			String line;
			while((line = br.readLine()) != null && !line.equals(""))
			{
				System.out.println(line);
				request += (line + "\n");
			}
			
			br.close();
		} 
		catch (IOException e) 
		{
			System.err.println("Error while getting request"); // Use Displayer instead...
		}
		
		DecodeClientRequest dcr = new DecodeClientRequest(request);
		URL = dcr.getPath();
		System.out.println("URL = " + URL);
		
		// Analysis of "forceRefresh" flag
		boolean forceRefresh = dcr.forceRefresh();
		
		if(URL == null)
			System.out.println("Null URL.");
		
		else{
			// If already in cache and don't need to be refreshed (timeout) and don't have "forceRefresh" flag
			if(cache.isContained(URL) && cache.getEntry(URL).isValid() && !forceRefresh)
			{
				/* Return the page */
			}
			else
			{
				/* Get the page and update cache */
				try 
				{
					System.out.println("Try to write");
					BufferedWriter out = new BufferedWriter(
							new OutputStreamWriter(
									new BufferedOutputStream(socket.getOutputStream())));
					out.write(OUTPUT_HEADERS + OUTPUT.length() + OUTPUT_END_OF_HEADERS + OUTPUT);
					out.flush();
					out.close();
					socket.close();
				} 
				catch (IOException e) 
				{

					System.err.println("Error while writing response");
				}
			}
		}
		
	}
}
