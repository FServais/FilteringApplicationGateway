package http;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import html.HTMLPage;
import html.exceptions.HTMLParsingException;
import http.exceptions.RemoteConnectionException;
import http.htmlFilter.HTMLPageFilter;
import datastructures.Cache;
import datastructures.WordList;
import displayer.DisplayerMessage;

/**
 * Class that handle a connection from a client that request a page.
 * @author Fabs & Romain Mormont
 */
public class HTTPClientRequestThread extends Thread {
	
	private Socket socket = null;
	private Cache<String, HTMLPage> cache = null;
	private LinkedBlockingQueue<DisplayerMessage> msgQueue = null;
	private WordList wordlist = null;
	
	public HTTPClientRequestThread(Socket socket, LinkedBlockingQueue<DisplayerMessage> msgQueue, WordList wordlist, Cache<String, HTMLPage> cache)
	{
		this.socket = socket;
		this.cache = cache;
		this.msgQueue = msgQueue;
		this.wordlist = wordlist;
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
			
			if(request == null)
			{
				msgQueue.add(new DisplayerMessage("Bad request (null pointer)", true));
				return;
			}
			
			msgQueue.add(new DisplayerMessage("Request received")); 

			
			// Decode the request : get URL

			GatewayRequestDecoder grd = new GatewayRequestDecoder(request);
			
			if(!grd.validRequest())
			{	
				msgQueue.add(new DisplayerMessage("Invalid request")); 
				return;
			}

			URL urlRequested = new URL(grd.getUrl());

			if(urlRequested == null)
				msgQueue.add(new DisplayerMessage("# DEBUG # Null Object URL : " + request, true));
			else
			{
				msgQueue.add(new DisplayerMessage("Requested page : \"" + urlRequested.toString() + "\""));
				
				// Analysis of "forceRefresh" flag
				boolean forceRefresh = grd.refreshIsForced();
				
				HTMLPage response_page;

				String url_string = grd.getUrl();
	
				// If already in cache and don't need to be refreshed (timeout) and don't have "forceRefresh" flag
				if(cache.isContained(url_string) && cache.getEntry(url_string).isValid() && !forceRefresh)
				{
					response_page = cache.getEntry(url_string).getData();
				}
				else // get page from remote server
				{
					response_page = getPageFromRemote(urlRequested);
					// add entry to the cache
					cache.addEntry(url_string, response_page);
				}	
				
				HTMLPage cloned_page = (HTMLPage) response_page.clone();
				
				// filters page 

				HTMLPageFilter hpl = new HTMLPageFilter(cloned_page, urlRequested, wordlist);

				writeResponse(HTTP.OK_HEADERS, hpl.getFilteredPage());
			}
		}
		catch (RemoteConnectionException e)
		{
			msgQueue.add(new DisplayerMessage(e.getMessage(), true));
		}
		catch (MalformedURLException e)
		{
			msgQueue.add(new DisplayerMessage("Bad url : " + e.getMessage(), true));
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			msgQueue.add(new DisplayerMessage("Error while getting the response page", true));
		}
		catch (Exception e)
		{
			msgQueue.add(new DisplayerMessage("Exception : " + e.getMessage(), true));
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
		
		if(headers.equals(HTTP.OK_HEADERS))
			out.print(headers + content.length());
		else
			out.print(headers);
		
		out.print(HTTP.END_OF_HEADERS);
		out.print(content);
		out.print(HTTP.LINEBREAK);
		
		out.flush();
		out.close();
	}
	
	/**
	 * Don't take into account bad response from remote server
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
		catch(IOException | HTMLParsingException e)
		{
			throw new RemoteConnectionException("Cannot get targeted page from remote website : " + e.getMessage());
		}
	}
}
