package http;

import java.net.Socket;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import http.html.HTMLPage;
import datastructures.Cache;

/**
 * Class that handle a connection from a client that request a page.
 * @author Fabs
 *
 */
public class HTTPClientRequest extends Thread {
	
	private Socket socket;
	private Cache<URL, HTMLPage> cache;
	//Displayer display = Displayer.getInstance();
	
	private static final String OUTPUT_HEADERS = "HTTP/1.1 200 OK\r\n" +
	    "Content-Type: text/html\r\n" + 
	    "Content-Length: ";
	private static final String OUTPUT_END_OF_HEADERS = "\r\n\r\n";
	
	public HTTPClientRequest(Socket socket)
	{
		this.socket = socket;
		cache = new Cache<URL, HTMLPage>();
	}
	
	public void run()
	{
		System.out.println("New request");
		URL urlRequested = null;
		// Wait for the request

		StringBuilder sb = new StringBuilder();
		String URL = null, request = null;

		try 
		{
			InputStreamReader isr = new InputStreamReader(socket.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			
			// Read the request
			String line;
			while((line = br.readLine()) != null && !line.equals(""))
			{
				sb.append(line + "\n");
			}

			request = sb.toString();
		} 
		catch (IOException e) 
		{
			System.err.println("Error while getting request"); // Use Displayer instead...
			return;
		}
		
		// Decode the request : get URL
		DecodeClientRequest dcr = null;
		if(request == null)
			System.out.println("Null request.");
		else
		{
			dcr = new DecodeClientRequest(request);
			urlRequested = dcr.getURL();
			System.out.println("URL = " + urlRequested.toString());
		}
		
		if(urlRequested == null)
			System.out.println("Null URL");
		else
		{
			// Analysis of "forceRefresh" flag
			boolean forceRefresh = dcr.forceRefresh();

			// If already in cache and don't need to be refreshed (timeout) and don't have "forceRefresh" flag
			if(cache.isContained(urlRequested) && cache.getEntry(urlRequested).isValid() && !forceRefresh)
			{
				/* Return the page */
			}
			else
			{
				/* Get the page and update cache */
				try 
				{
					String OUTPUT = "<html><head><title>Introduction to computer networking</title></head><body><p>Works !!!</p></body></html>";
					writeResponse(OUTPUT);
				} 
				catch (IOException e) 
				{
					System.err.println("Error while writing response");
				}
				finally
				{
					try {
						socket.close();
					} catch (IOException e) {
						System.err.println("Closing socket failed");
					}
				}
			}
		}	
	}// End run
	
	/**
	 * 
	 * @param outputMessage
	 * @throws IOException
	 */
	private void writeResponse(String outputMessage) throws IOException
	{
		PrintWriter out = new PrintWriter(socket.getOutputStream()); 
		
		out.println(OUTPUT_HEADERS + outputMessage.length());
		out.println();
		out.println(outputMessage);
		
		out.flush();
		out.close();
	}
	
}
