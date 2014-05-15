package http;

import html.HTMLPage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import datastructures.Cache;
import datastructures.WordList;
import displayer.DisplayerMessage;

/**
 * Class that handle the connections of the Gateway.
 * @author Fabs & Romain Mormont
 *
 */
public class HTTPServer extends Thread 
{
	private static int PORT = 8005;
	private WordList wordlist;
	private LinkedBlockingQueue<DisplayerMessage> msgQueue = null;// Queue for outputting threads messages in std ouput
	private ServerSocket ss = null;
	private ExecutorService threadPool = null;
	private Cache<String, HTMLPage> cache = null;
	
	public HTTPServer(WordList wordlist, LinkedBlockingQueue<DisplayerMessage> msgQueue, int maxThreads) 
			throws IOException 
	{
		// create thread pool
		this.threadPool = Executors.newFixedThreadPool(maxThreads);
		
		// create server socket
		this.ss = new ServerSocket(PORT);
		
		this.wordlist = wordlist;
		this.msgQueue = msgQueue;
		
		this.cache = new Cache<String, HTMLPage>();
	}
	
	public void run()
	{	
		int i = 0;
		while(true)
		{
			try
			{
				Socket client_gateway = ss.accept();
				Runnable r = new HTTPClientRequestThread(++i, client_gateway, msgQueue, wordlist, cache);
				threadPool.execute(r);
			}
			catch(IOException e)
			{
				msgQueue.add(new DisplayerMessage("Creation of new Socket failed.", true));
			}
		}
	}
}


