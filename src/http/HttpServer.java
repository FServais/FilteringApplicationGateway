package http;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import datastructures.WordList;

public class HttpServer extends Thread 
{
	private static int PORT = 8000;
	private WordList wordlist;
	private LinkedBlockingQueue<String> msgQueue = null;// Queue for outputting threads messages in std ouput
	private ServerSocket ss = null;
	private ExecutorService threadPool = null;
	
	public HttpServer(WordList wordlist, LinkedBlockingQueue<String> msgQueue, int maxThreads) 
			throws IOException 
	{
		// create thread pool
		this.threadPool = Executors.newFixedThreadPool(maxThreads);
		
		// create server socket
		this.ss = new ServerSocket(PORT);
	}
	
	public void run()
	{
		
	}
}


