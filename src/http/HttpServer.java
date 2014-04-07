package http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import datastructures.WordList;

/**
 * Class that handle the connections of the Gateway.
 * @author Fabs
 *
 */
public class HTTPServer extends Thread 
{
	private static int PORT = 8000;
	private WordList wordlist;
	private LinkedBlockingQueue<String> msgQueue = null;// Queue for outputting threads messages in std ouput
	private ServerSocket ss = null;
	private ExecutorService threadPool = null;
	
	public HTTPServer(WordList wordlist, LinkedBlockingQueue<String> msgQueue, int maxThreads) 
			throws IOException 
	{
		// create thread pool
		this.threadPool = Executors.newFixedThreadPool(maxThreads);
		
		// create server socket
		this.ss = new ServerSocket(PORT);
		
		this.wordlist = wordlist;
		this.msgQueue = msgQueue;
	}
	
	public void run()
	{	

		while(true)
		{
			try
			{
				Socket client_gateway = ss.accept();
				threadPool.execute(new HTTPClientRequest(client_gateway));
			}
			catch(IOException e)
			{
				System.err.println("Creation of new Socket failed.");
			}
		}
	}
}


