package http;

import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import http.html.HTMLPage;
import datastructures.Cache;
import displayer.DisplayerMessage;

/**
 * Class that handle a connection from a client that request a page.
 * @author Fabs
 *
 */
public class HTTPClientRequest extends Thread {
	
	private Socket socket;
	private Cache<String, HTMLPage> cache;
	private LinkedBlockingQueue<DisplayerMessage> msgQueue = null;
	
	private static final String OUTPUT = "<html><head><title>Introduction to computer networking</title></head><body><p>Worked!!!</p></body></html>";
	private static final String OUTPUT_HEADERS = "HTTP/1.1 200 OK\r\n" +
	    "Content-Type: text/html\r\n" + 
	    "Content-Length: ";
	private static final String OUTPUT_END_OF_HEADERS = "\r\n\r\n";
	
	public HTTPClientRequest(Socket socket, LinkedBlockingQueue<DisplayerMessage> msgQueue)
	{
		this.socket = socket;
		cache = new Cache<String, HTMLPage>();
		this.msgQueue = msgQueue;
	}
	
	public void run()
	{
		msgQueue.add(new DisplayerMessage("New request"));
		// Wait for the request
		String URL = new String(), request = new String();
		try 
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			// Read the request
			String line;
			while((line = br.readLine()) != null && !line.equals(""))
			{
				request += (line + "\n");
			}
			
			//br.close();
		} 
		catch (IOException e) 
		{
			msgQueue.add(new DisplayerMessage("Error while getting request", true)); // Use Displayer instead...
		}
		
		if(request == null)
			msgQueue.add(new DisplayerMessage("Null URL."));
		
		else
		{
			DecodeClientRequest dcr = new DecodeClientRequest(request);
			URL = dcr.getPath();
			msgQueue.add(new DisplayerMessage("URL = " + URL));
			
			// Analysis of "forceRefresh" flag
			boolean forceRefresh = dcr.forceRefresh();

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
					PrintWriter out = new PrintWriter(socket.getOutputStream()); 
					
					out.println(OUTPUT_HEADERS + OUTPUT.length());
					out.println();
					out.println(OUTPUT);
					out.flush();
					out.close();
				} 
				catch (IOException e) 
				{
					msgQueue.add(new DisplayerMessage("Error while writing response", true));
				}
				finally
				{
					try {
						socket.close();
					} catch (IOException e) {
						msgQueue.add(new DisplayerMessage("Closing socket failed", true));
					}
				}
			}
		}
		
	}
}
