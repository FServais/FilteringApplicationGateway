


import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import datastructures.WordList;
import displayer.Displayer;
import displayer.DisplayerMessage;import configuration.ConfigurationServer;

import http.HTTPServer;

/**
 * Class representing the server of the Gateway
 * @author Romain Mormont
 *
 */
public class Server
{
	private WordList wordlist;
	private LinkedBlockingQueue<DisplayerMessage> msgQueue = null;// Queue for outputting threads messages in std ouput
	private Displayer displayerThread = null;
	//private ConfigurationServer configThread = null;
	private HTTPServer httpServer = null;

	/**
	 * Constructs a server object
	 * @param maxThreadsHttp max number of threads for http connections
	 * @param maxThreadConfig max number of threads for configuration connections
	 * @throws IOException 
	 */
	public Server(int maxThreadsHttp, int maxThreadConfig) 
			throws IOException
	{
		wordlist = new WordList();
		wordlist.insert("concurrent");

		// initialize displayer thread
		msgQueue = new LinkedBlockingQueue<DisplayerMessage>(); 
		displayerThread = Displayer.getInstance();
		displayerThread.setQueue(msgQueue);
		//displayerThread.setDaemon(true);
		displayerThread.start();

		// initalize thread for dealing with http connection
		httpServer = new HTTPServer(wordlist, msgQueue, maxThreadsHttp);
		//httpServer.setDaemon(true);
		httpServer.start();			

		// initalize thread for dealing with connection to the configuration platform
		//configThread = new ConfigurationServer(msgQueue, wordlist, maxThreadConfig);
		//configThread.setDaemon(true);
		//configThread.start();	
	}

	public static void main(String args[])
	{
		if(args.length != 1)
		{
			System.err.println("USAGE : java Server <maxThread>");
			return;
		}
		
		int maxThreadsConfig = 5;
		
		try
		{
			new Server(Integer.parseInt(args[0]), maxThreadsConfig);
		}
		catch(Exception e)
		{
			
		}
	}
}