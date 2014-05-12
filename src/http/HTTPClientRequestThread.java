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
import html.filter.HTMLPageFilter;
import http.exceptions.BadRequestException;
import http.exceptions.RemoteConnectionException;
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
			long begin = System.currentTimeMillis(), duration;
			
			msgQueue.add(new DisplayerMessage("New request"));
	
			HTTPRequest request = readRequest();
			
			duration = (System.currentTimeMillis() - begin);
			msgQueue.add(new DisplayerMessage("Request received (" + duration  + " ms)")); 
			
			// Decode the request : get URL
			GatewayRequestDecoder grd = new GatewayRequestDecoder(request);
			
			if(!grd.validRequest())
			{	
				msgQueue.add(new DisplayerMessage("Invalid request : request : \n")); 
				return;
			}

			URL urlRequested = new URL(grd.getUrl());

			
			msgQueue.add(new DisplayerMessage("Requested page : \"" + urlRequested.toString() + "\""));
			
			// Analysis of "forceRefresh" flag
			boolean forceRefresh = grd.refreshIsForced();
			
			HTMLPage response_page;

			String url_string = grd.getUrl();
			
			duration = System.currentTimeMillis() - begin;
			msgQueue.add(new DisplayerMessage("Starts getting the page (" + duration + " ms)"));

			// If already in cache and don't need to be refreshed (timeout) and don't have "forceRefresh" flag
			if(cache.isContained(url_string) && cache.getEntry(url_string).isValid() && !forceRefresh)
			{
				System.out.println("Cache");
				response_page = cache.getEntry(url_string).getData();
				
				duration = System.currentTimeMillis() - begin;
				msgQueue.add(new DisplayerMessage("Page retrieved (from cache : " + duration + " ms)"));
			}
			else // get page from remote server
			{
				System.out.println("NoCache");
				response_page = getPageFromRemote(urlRequested);
				duration = System.currentTimeMillis() - begin;
				msgQueue.add(new DisplayerMessage("Page retrieved (from remote : " + duration + " ms)"));
				// add entry to the cache
				cache.addEntry(url_string, response_page);
				duration = System.currentTimeMillis() - begin;
				msgQueue.add(new DisplayerMessage("Page cached (" + duration + " ms)"));
			}	
			
			duration = System.currentTimeMillis() - begin;
			msgQueue.add(new DisplayerMessage("Start cloning (" + duration + " ms)"));
			HTMLPage cloned_page = (HTMLPage) response_page.clone();
			
			duration = System.currentTimeMillis() - begin;
			msgQueue.add(new DisplayerMessage("End cloning (" + duration + " ms)"));
			// filters page 
			
			duration = System.currentTimeMillis() - begin;
			msgQueue.add(new DisplayerMessage("Start filtering (" + duration + " ms)"));
			HTMLPageFilter hpl = new HTMLPageFilter(cloned_page, urlRequested, wordlist);
			String filtered_page = hpl.getFilteredPage();
			duration = System.currentTimeMillis() - begin;
			msgQueue.add(new DisplayerMessage("End filtering (" + duration + " ms)"));
			
			duration = System.currentTimeMillis() - begin;
			msgQueue.add(new DisplayerMessage("Start writing (" + duration + " ms)"));
			writeResponse(HTTP.OK_HEADERS, filtered_page);
			duration = System.currentTimeMillis() - begin;
			msgQueue.add(new DisplayerMessage("End writing (" + duration + " ms)\n"));
		}
		catch (RemoteConnectionException e)
		{
			msgQueue.add(new DisplayerMessage(e.getMessage(), true));
		}
		catch (MalformedURLException e)
		{
			msgQueue.add(new DisplayerMessage("Bad url : " + e.getMessage(), true));
		}
		catch (BadRequestException e)
		{
			e.printStackTrace();
			msgQueue.add(new DisplayerMessage("Error while parsing the http request", true));
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			msgQueue.add(new DisplayerMessage("Error while getting the response page", true));
		}
		catch (Exception e)
		{
			e.printStackTrace();
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
	 * Reads the http request received 
	 * @return an HTTPRequest object representing the request
	 * @throws IOException if the reading of the socket input stream fails
	 * @throws BadRequestException if an error occurs while creating the http request
	 */
	private HTTPRequest readRequest() throws BadRequestException, IOException
	{
		StringBuilder sb = new StringBuilder();
		String request = null;

		// reads the HTTP request

		InputStreamReader isr = new InputStreamReader(socket.getInputStream());
		BufferedReader br = new BufferedReader(isr);
		
		// Read the request
		String line;
		while((line = br.readLine()) != null && !line.equals(""))
			sb.append(line + "\n");

		return new HTTPRequest(sb.toString());
	}
	/**
	 * This method connects to the url and returns an HTMLPage representing the content
	 * loaded from the remote server
	 * @param url an URL object from which the content must be gathered
	 * @return an HTMLPage object containing the page of the remote
	 * @throws RemoteConnectionException exception thrown on parsing error or on connection failure
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
			
			// create page
			return new HTMLPage(response.toString());
		}
		catch(IOException | HTMLParsingException e)
		{
			throw new RemoteConnectionException("Cannot get targeted page from remote website : " + e.getMessage());
		}
	}
}
