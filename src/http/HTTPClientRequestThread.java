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
import html.filter.PageGatewayStatus;
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
	
	private Socket socket;
	private Cache<String, HTMLPage> cache;
	private LinkedBlockingQueue<DisplayerMessage> msgQueue;
	private WordList wordlist;
	private int connection_number = 0;
	private String gateway_ip;
	
	public HTTPClientRequestThread(int connection_number, Socket socket, LinkedBlockingQueue<DisplayerMessage> msgQueue, 
									WordList wordlist, Cache<String, HTMLPage> cache, String gateway_ip)
	{
		this.socket = socket;
		this.cache = cache;
		this.msgQueue = msgQueue;
		this.wordlist = wordlist;
		this.connection_number = connection_number;
		this.gateway_ip = gateway_ip;
	}
	
	public void run()
	{
		try 
		{
			long begin = System.currentTimeMillis(), duration;
			
			message("New request");
	
			HTTPRequest request = new HTTPRequest(socket);
			
			duration = (System.currentTimeMillis() - begin);
			message("Request received (" + duration  + " ms)"); 
			
			// Decode the request : get URL
			GatewayRequestDecoder grd = new GatewayRequestDecoder(request);
			
			if(!grd.validRequest())
			{	
				// TODO implement response to this error
				message("Invalid request : path cannot be handled\n"); 
				return;
			}

			URL request_url = new URL(grd.getUrl());
			
			message("Requested page : \"" + request_url.toString() + "\"");
			
			// checks if "forceRefresh" flag is set
			boolean forceRefresh = grd.refreshIsForced();
			
			HTMLPage response_page;
			
			duration = System.currentTimeMillis() - begin;
			message("Starts getting the page (" + duration + " ms)");

			// If already in cache and don't need to be refreshed (timeout) and don't have "forceRefresh" flag
			if(cache.isContained(request_url.toString()) && cache.getEntry(request_url.toString()).isValid() && !forceRefresh)
			{
				response_page = cache.getEntry(request_url.toString()).getData();
				
				duration = System.currentTimeMillis() - begin;
				message("Page retrieved (from cache : " + duration + " ms)");
			}
			else // get page from remote server
			{
				response_page = getPageFromRemote(request_url);
				
				duration = System.currentTimeMillis() - begin;
				message("Page retrieved (from remote : " + duration + " ms)");
				
				// filters the link of the page
				HTMLPageFilter filter = new HTMLPageFilter(response_page, request_url, wordlist, gateway_ip);
				filter.filterLinks();
				
				duration = System.currentTimeMillis() - begin;
				message("Page's links fitlered (" + duration + " ms)");
				
				// add entry to the cache
				cache.addEntry(request_url.toString(), response_page);
				duration = System.currentTimeMillis() - begin;
				message("Page cached (" + duration + " ms)");
			}	
			
			duration = System.currentTimeMillis() - begin;
			message("Start cloning (" + duration + " ms)");
			HTMLPage cloned_page = (HTMLPage) response_page.clone();
			
			duration = System.currentTimeMillis() - begin;
			message("End cloning (" + duration + " ms)");
			// filters page 
			
			duration = System.currentTimeMillis() - begin;
			message("Start filtering keywords (" + duration + " ms)");
			HTMLPageFilter hpl = new HTMLPageFilter(cloned_page, request_url, wordlist, gateway_ip);
			String filtered_page = hpl.getFilteredPage();
			duration = System.currentTimeMillis() - begin;
			message("End filtering keywords (" + duration + " ms)");
			
			duration = System.currentTimeMillis() - begin;
			message("Start writing (" + duration + " ms)");
			
			HTTPResponse httpResponse = new HTTPResponse(filtered_page);		
			httpResponse.send(socket);
			
			duration = System.currentTimeMillis() - begin;
			message("End writing (" + duration + " ms)\n");
		}
		catch (RemoteConnectionException e)
		{
			message(e.getMessage(), true);
		}
		catch (MalformedURLException e)
		{
			message("Bad url : " + e.getMessage(), true);
		}
		catch (BadRequestException e)
		{
			e.printStackTrace();
			message("Error while parsing the http request", true);
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			message("Error while getting the response page", true);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			message("Exception : " + e.getMessage(), true);
		}
		finally
		{
			try {
				socket.close();
			} catch (IOException e) {
				message("Closing socket failed", true);
			}
		}
	}// End run
	
	
	/**
	 * This method connects to the url and returns an HTMLPage representing the content
	 * loaded from the remote server
	 * @param url an URL object from which the content must be gathered
	 * @return an HTMLPage object containing the page of the remote
	 * @throws RemoteConnectionException exception thrown on parsing error or on connection failure
	 */
	private HTMLPage getPageFromRemote(URL url) throws RemoteConnectionException
	{
		HttpURLConnection huc = null;
		try
		{			
			// connect to the remote
			huc = (HttpURLConnection) url.openConnection();
			
			huc.setRequestMethod("GET");
			
			// get the streams associated with the connection
			InputStreamReader isr = new InputStreamReader(huc.getInputStream());
			BufferedReader in = new BufferedReader(isr);
			
			String inputLine;
			StringBuilder response = new StringBuilder();
	 
			while ((inputLine = in.readLine()) != null) 
				response.append(inputLine + "\n");
			
			in.close();
			isr.close();
			
			// create page
			return new HTMLPage(response.toString());
		}
		catch(IOException | HTMLParsingException e)
		{
			try {
				new HTTPResponse(huc.getResponseCode()).send(socket);
				message("HTTP Error : " + huc.getResponseCode());
			} catch (IOException e1) {
				message("Getting response code error");
			}
			
			throw new RemoteConnectionException("Cannot get targeted page from remote website : " + e.getMessage());
		}
	}
	
	void message(String msg)
	{
		message(msg, false);
	}
	void message(String msg, boolean error)
	{
		msgQueue.add(new DisplayerMessage("[" + connection_number + "] " + msg, error));
	}
}
