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
import displayer.DisplayerMessageSender;

/**
 * Class that handle a connection from a client that request a page.
 * @author Fabs & Romain Mormont
 */
public class HTTPClientRequestThread extends Thread {
	
	private Socket socket;
	private Cache<String, HTMLPage> cache;
	private DisplayerMessageSender disp_sender;
	private WordList wordlist;
	private int connection_number = 0;
	
	public HTTPClientRequestThread(int connection_number, Socket socket, LinkedBlockingQueue<DisplayerMessage> msgQueue, 
									WordList wordlist, Cache<String, HTMLPage> cache)
	{
		this.socket = socket;
		this.cache = cache;
		this.disp_sender = new DisplayerMessageSender(msgQueue);
		this.wordlist = wordlist;
		this.connection_number = connection_number;
	}
	
	public void run()
	{
		try 
		{
			long begin = System.currentTimeMillis(), duration;
			
			msg("New request (0 ms)");
	
			HTTPRequest request = new HTTPRequest(socket);
			String gateway_ip = request.getHeaderValue("Host");
					
			duration = (System.currentTimeMillis() - begin);
			msg("Request received (" + duration  + " ms)"); 
			
			// Decode the request : get URL
			GatewayRequestDecoder grd = new GatewayRequestDecoder(request);
			
			if(!grd.validRequest())
			{	
				// TODO implement response to this error
				error_msg("Invalid request : path cannot be handled " + request.getPath() + "\n"); 
				return;
			}

			URL request_url = new URL(grd.getUrl());
			
			msg("Requested page : \"" + request_url.toString() + "\"");
			
			// checks if "forceRefresh" flag is set
			boolean forceRefresh = grd.refreshIsForced();
			
			HTMLPage response_page;
			
			duration = System.currentTimeMillis() - begin;
			msg("Starts getting the page (" + duration + " ms)");

			// If already in cache and don't need to be refreshed (timeout) and don't have "forceRefresh" flag
			if(cache.isContained(request_url.toString()) && cache.getEntry(request_url.toString()).isValid() && !forceRefresh)
			{
				response_page = cache.getEntry(request_url.toString()).getData();
				
				duration = System.currentTimeMillis() - begin;
				msg("Page retrieved (from cache : " + duration + " ms)");
			}
			else // get page from remote server
			{
				response_page = getPageFromRemote(request_url);
				
				duration = System.currentTimeMillis() - begin;
				msg("Page retrieved (from remote : " + duration + " ms)");
				
				// filters the link of the page
				HTMLPageFilter filter = new HTMLPageFilter(response_page, request_url, wordlist, gateway_ip);
				filter.filterLinks();
				
				duration = System.currentTimeMillis() - begin;
				msg("Page's links fitlered (" + duration + " ms)");
				
				// add entry to the cache
				cache.addEntry(request_url.toString(), response_page);
				duration = System.currentTimeMillis() - begin;
				msg("Page cached (" + duration + " ms)");
			}	
			
			duration = System.currentTimeMillis() - begin;
			msg("Start cloning (" + duration + " ms)");
			HTMLPage cloned_page = (HTMLPage) response_page.clone();
			
			duration = System.currentTimeMillis() - begin;
			msg("End cloning (" + duration + " ms)");
			// filters page 
			
			duration = System.currentTimeMillis() - begin;
			msg("Start filtering keywords (" + duration + " ms)");
			HTMLPageFilter hpl = new HTMLPageFilter(cloned_page, request_url, wordlist, gateway_ip);
			String filtered_page = hpl.getFilteredPage();
			duration = System.currentTimeMillis() - begin;
			msg("End filtering keywords (" + duration + " ms)");
			
			duration = System.currentTimeMillis() - begin;

			msg("Start writing (" + duration + " ms)");
			
			HTTPResponse httpResponse = new HTTPResponse(filtered_page);		
			httpResponse.send(socket);
			Z
			duration = System.currentTimeMillis() - begin;
			msg("End writing (" + duration + " ms)\n");
		}
		catch (RemoteConnectionException e)
		{
			error_msg(e.getMessage());
		}
		catch (MalformedURLException e)
		{
			error_msg("Bad url : " + e.getMessage());
		}
		catch (BadRequestException e)
		{
			e.printStackTrace();
			error_msg("Error while parsing the http request");
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			error_msg("Error while getting the response page");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			error_msg("Exception : " + e.getMessage());
		}
		finally
		{
			try {
				socket.close();
			} catch (IOException e) {
				error_msg("Closing socket failed");
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
	
	/**
	 * 
	 * @param msg
	 */
	void msg(String msg)
	{
		disp_sender.sendMessage("[" + connection_number + "] " + msg);
	}
	
	/**
	 * 
	 * @param msg
	 */
	void error_msg(String msg)
	{
		disp_sender.sendErrorMessage("[" + connection_number + "] " + msg);
	}
}
