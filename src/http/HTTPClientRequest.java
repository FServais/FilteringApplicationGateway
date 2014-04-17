package http;

import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.swing.text.html.HTML;

import http.exceptions.RemoteConnectionException;
import http.html.HTMLPage;
import datastructures.Cache;
import displayer.DisplayerMessage;

/**
 * Class that handle a connection from a client that request a page.
 * @author Fabs
 */
public class HTTPClientRequest extends Thread {
	
	private Socket socket;
	private Cache<URL, HTMLPage> cache;
	private LinkedBlockingQueue<DisplayerMessage> msgQueue = null;
	
	
	public HTTPClientRequest(Socket socket, LinkedBlockingQueue<DisplayerMessage> msgQueue)
	{
		this.socket = socket;
		cache = new Cache<URL, HTMLPage>();
		this.msgQueue = msgQueue;
	}
	
	public void run()
	{
		try 
		{
			msgQueue.add(new DisplayerMessage("New request"));
	
			StringBuilder sb = new StringBuilder();
			String request = null;
	
			// reads the HTTP request

			InputStreamReader isr = new InputStreamReader(socket.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			
			// Read the request
			String line;
			while((line = br.readLine()) != null && !line.equals(""))
			{
				sb.append(line + "\n");
			}

			request = sb.toString();
			
			msgQueue.add(new DisplayerMessage("Request received")); 

			
			// Decode the request : get URL
			DecodeClientRequest dcr = null;
			URL urlRequested = null;
			
			if(request == null)
				msgQueue.add(new DisplayerMessage("Bad request (null pointer)", true));
			else
			{
				dcr = new DecodeClientRequest(request);
				urlRequested = dcr.getURL();
			}


			if(urlRequested == null)
				msgQueue.add(new DisplayerMessage("Null URL", true));
			else
			{
				msgQueue.add(new DisplayerMessage("URL = " + urlRequested.toString()));
				
				// Analysis of "forceRefresh" flag
				boolean forceRefresh = dcr.forceRefresh();
				
				HTMLPage response_page;
	
				// If already in cache and don't need to be refreshed (timeout) and don't have "forceRefresh" flag
				if(cache.isContained(urlRequested) && cache.getEntry(urlRequested).isValid() && !forceRefresh)
				{
					response_page = cache.getEntry(urlRequested).getData();
				}
				else // get page frome remote server
				{
					response_page = getPageFromRemote(urlRequested);
					// add entry to the cache
					cache.addEntry(urlRequested, response_page);
					
				}	
			}
		}
		catch (IOException e) 
		{
			msgQueue.add(new DisplayerMessage("Error while getting the response page", true));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try {
				socket.close();
			} catch (IOException e) {
				msgQueue.add(new DisplayerMessage("Closing socket failed", true));
			}
		}
	}// End run
	
	/**
	 * 
	 * @param outputMessage
	 * @throws IOException
	 */
	private void writeResponse(String headers, String content) throws IOException
	{
		PrintWriter out = new PrintWriter(socket.getOutputStream()); 
		
		out.print(headers + content.length());
		out.print(HTTP.END_OF_HEADERS);
		out.print(content);
		
		out.flush();
		out.close();
	}
	
	/**
	 * Dont take into account bad response from remote server
	 * @param url
	 * @return
	 * @throws RemoteConnectionException
	 */
	private HTMLPage getPageFromRemote(URL url) throws RemoteConnectionException
	{
		try
		{
			// connect to the remote
			HttpURLConnection huc = (HttpURLConnection) url.openConnection();
			
			huc.setRequestMethod("GET");
			
			// get the streams associated with the connection
			InputStreamReader isr = new InputStreamReader(huc.getInputStream());
			BufferedReader in = new BufferedReader(isr);
			
			String inputLine;
			StringBuilder response = new StringBuilder();
	 
			while ((inputLine = in.readLine()) != null) 
			{
				response.append(inputLine);
			}
			
			in.close();
			isr.close();
			
			return new HTMLPage(response.toString());
		}
		catch(IOException e)
		{
			throw new RemoteConnectionException("Cannot get targeted page from remote website");
		}
	}
	
	private void filterPage(HTMLPage htmlpage)
	{
		
	}
}
