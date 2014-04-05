package configuration;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

import datastructures.*;
/**
 * Server program
 * @author Romain Mormont
 */
public class ConfigurationServer extends Thread
{
	private static final int PORT = 9040;
	private datastructures.WordList wordlist = null; // list of forbidden words
	private UserMap usermap = null; // user map
	private ExecutorService execServ = null; // thread pool for dealing with clients connection
	private ServerSocket ss = null;
	private LinkedBlockingQueue<String> msgQueue = null;// Queue for outputting threads messages in std ouput
	
	/**
	 * Initializes the Server data
	 * @throws IOException if ServerSocket connection initialization fails
	 */
	public ConfigurationServer(LinkedBlockingQueue<String> msgQueue, WordList wordlist, int maxThreads) 
			throws IOException
	{
		this.wordlist = wordlist;
		this.msgQueue = msgQueue;
		this.usermap = new UserMap();
		
		// add a bunch of users
		this.usermap.addUser("root", "root");
		this.usermap.addUser("admin", "admin");
		this.usermap.addUser("user", "pass");
		this.usermap.addUser("user2", "user");
		this.usermap.addUser("user3", "azerty123");
		
		this.execServ = Executors.newFixedThreadPool(maxThreads);
		this.ss = new ServerSocket(PORT);
	}
	
	/**
	 * Launches the server
	 */
	public void run()
	{
		try
		{
			while(true)
			{
				Socket connectionSocket = ss.accept();
				Runnable r = new ConfigServerThread(wordlist, usermap, connectionSocket, msgQueue);
				execServ.execute(r);
			}
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				ss.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
