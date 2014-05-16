package http;

import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import html.HTMLPage;
import html.exceptions.HTMLParsingException;
import html.filter.HTMLPageFilter;
import http.exceptions.BadFileRequestException;
import http.exceptions.HTTPMethodNotSupportedException;
import http.exceptions.InvalidRequestException;
import http.exceptions.RequestParsingException;
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
	
	/**
	 * Constructs the object for dealing with a client http request
	 * @param connection_number the number of the thread
	 * @param socket the socket connected to the client
	 * @param msgQueue a msgQueue for sending message to the displayer thread
	 * @param wordlist the list of the restricted keywords
	 * @param cache the cache of html pages
	 */
	public HTTPClientRequestThread(int connection_number, Socket socket, LinkedBlockingQueue<DisplayerMessage> msgQueue, 
									WordList wordlist, Cache<String, HTMLPage> cache)
	{
		this.socket = socket;
		this.cache = cache;
		this.disp_sender = new DisplayerMessageSender(msgQueue);
		this.wordlist = wordlist;
		this.connection_number = connection_number;
	}
	
	/**
	 * Main method of the thread
	 */
	public void run()
	{
		try 
		{
			long begin = System.currentTimeMillis(), duration;
			
			msg("New request (0 ms)");
	
			// get client rquest
			HTTPRequest request = new HTTPRequest(socket);
			String gateway_ip = request.getHeaderValue("Host");
					
			duration = (System.currentTimeMillis() - begin);
			msg("Request received (" + duration  + " ms)"); 
			
			if(!request.getMethod().equals("HEAD") && !request.getMethod().equals("GET"))
				throw new HTTPMethodNotSupportedException("Bad http method : " + request.getMethod());
			
			// Decode the request : get URL and args
			GatewayRequestDecoder grd = new GatewayRequestDecoder(request);
			
			if(!grd.validRequest()) // if path is erroneous
				throw new InvalidRequestException("bad http path : " + request.getPath());
			
			if(!grd.fileTypeIsOk()) // checks if the file type can be managed by the gateway
				throw new BadFileRequestException("file format can't be managed : " + grd.getUrl());

			URL request_url = new URL(grd.getUrl());
			
			msg("Requested page : \"" + request_url.toString() + "\"");
			
			// checks if "forceRefresh" flag is set
			boolean forceRefresh = grd.refreshIsForced();
			
			HTMLPage response_page;
			
			duration = System.currentTimeMillis() - begin;
			msg("Starts getting the page (" + duration + " ms)");

			// If already in cache and don't need to be refreshed (timeout) and don't have "forceRefresh" flag, then go to the cache
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
			
			// clone des pages
			duration = System.currentTimeMillis() - begin;
			msg("Start cloning (" + duration + " ms)");
			HTMLPage cloned_page = (HTMLPage) response_page.clone();
			duration = System.currentTimeMillis() - begin;
			msg("End cloning (" + duration + " ms)");
			
			// filters keywords in the page
			duration = System.currentTimeMillis() - begin;
			msg("Start filtering keywords (" + duration + " ms)");
			
			HTMLPageFilter hpl = new HTMLPageFilter(cloned_page, request_url, wordlist, gateway_ip);

			String filtered_page = hpl.getFilteredPage(socket.getRemoteSocketAddress());

			duration = System.currentTimeMillis() - begin;
			msg("End filtering keywords (" + duration + " ms)");
			
			// sends response page to the client
			duration = System.currentTimeMillis() - begin;
			msg("Start writing (" + duration + " ms)");
	
			new HTTPResponse(filtered_page).send(socket, "GET");
			
			duration = System.currentTimeMillis() - begin;
			msg("End writing (" + duration + " ms)");
		}
		catch (RequestParsingException e) // cannot parse client request
		{
			try { // bad request
				new HTTPResponse(400).send(socket, "GET");
			} catch (IOException e1) { }
			
			error_msg("Error while parsing the http request\n");
		}
		catch (HTTPMethodNotSupportedException e)
		{
			try { // not implemented
				new HTTPResponse(501).send(socket, "GET");
			} catch (IOException e1) { }
			
			error_msg(e.getMessage());
		}
		catch (InvalidRequestException e) // invalid request from client
		{
			try { // bad request
				new HTTPResponse(400).send(socket, "GET");
			} catch (IOException e1) { }
			
			error_msg("Invalid request : " + e.getMessage()); 
		}
		catch (BadFileRequestException e) // file format requested cannot be handled
		{
			try { // not implemented
				new HTTPResponse(501).send(socket, "GET");
			} catch (IOException e1) { }
			
			error_msg(e.getMessage() + "\n");
		}
		catch (RemoteConnectionException e) // cannot get page from remote
		{
			// HTTP response already handled in the method getPageFromRemote
			error_msg("Remote connection error : " + e.getMessage());
		}
		catch (HTMLParsingException e) // error while parsing the html code
		{
			try { // internal error
				new HTTPResponse(500).send(socket, "GET");
			} catch (IOException e1) { }
			
			error_msg("Error while parsing the html page " + e.getMessage());
		}
		catch (Exception e)
		{
			try { // internal error
				new HTTPResponse(500).send(socket, "GET");
			} catch (IOException e1) { }
			
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
	 * @throws RemoteConnectionException if a connection failure happens
	 * @throws HTMLParsingException if an error occurs while parsing
	 */
	private HTMLPage getPageFromRemote(URL url) throws RemoteConnectionException, HTMLParsingException
	{
		HttpURLConnection huc = null;
		try
		{			
			// connect to the remote
			huc = (HttpURLConnection) url.openConnection();
			huc.setConnectTimeout(2500);
			huc.setReadTimeout(15000);
			huc.setRequestMethod("GET");
			huc.connect();
			
			// get the streams associated with the connection
			InputStreamReader isr = new InputStreamReader(huc.getInputStream());
			BufferedReader in = new BufferedReader(isr);
			
			String inputLine;
			StringBuilder response = new StringBuilder();
	 
			while ((inputLine = in.readLine()) != null) 
				response.append(inputLine + "\r\n");
			
			in.close();
			isr.close();
			
			// create page
			return new HTMLPage(response.toString());
		}
		catch(SocketTimeoutException e)
		{
			try { // Gateway Timeout
				new HTTPResponse(504).send(socket, "GET");
				error_msg("Timeout from remote");
			} catch (IOException e1) { }
			
			throw new RemoteConnectionException("Cannot get page from remote server : " + e.getMessage());
		}
		catch(IOException e)
		{
			try {
				new HTTPResponse(502/*huc.getResponseCode()*/).send(socket, "GET");
				error_msg("HTTP Error from remote : " + huc.getResponseCode());
			} catch (IOException e1) { }	
			
			throw new RemoteConnectionException("Cannot get page from remote server : " + e.getMessage());
		}
		
	}
	
	/**
	 * Sends a normal message to the displayer
	 * @param msg
	 */
	void msg(String msg)
	{
		disp_sender.sendMessage("[GATEW][" + connection_number + "] " + msg);
	}
	
	/**
	 * 
	 * @param msg
	 */
	void error_msg(String msg)
	{
		disp_sender.sendErrorMessage("[GATEW][" + connection_number + "] " + msg);
	}
}
