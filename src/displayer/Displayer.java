package displayer;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * A class for displaying message from server threads arriving in a queue.
 * This class follows the singleton pattern.
 * @author Romain
 */
public class Displayer extends Thread {
	private LinkedBlockingQueue<DisplayerMessage> lbq = null;
	private boolean close = false;
	
	/** holds the single instance of the Displayer class */
	private static Displayer singleton = null;
	
	/**
	 * Constructs a Displayer
	 */
	private Displayer()
	{
		
	}
	
	/**
	 * Returns the unique instance of Displayer (singleton pattern). 
	 * Before using the Displayer, the method setQueue() must be called to initialize the 
	 * blocking queue.
	 * @return the unique instance of the Displayer class
	 */
	public static synchronized Displayer getInstance()
	{ 
		if(singleton == null)
			singleton = new Displayer();
		
		return singleton;
	}
	
	/**
	 * Initialize the queue of Displayer
	 * @param lbq is the blocking queue in which the messages will come
	 */
	public synchronized void setQueue(LinkedBlockingQueue<DisplayerMessage> lbq)
	{
		this.lbq = lbq;
	}
	
	public void run()
	{
		if(lbq == null) // displayer queue hasn't been initialized
			return;

		while(!close || !lbq.isEmpty()) // output the messages as long as the thread has to run
		{
			try 
			{
				DisplayerMessage dm = lbq.take();
				if(dm.isError())
					System.err.println(dm.getMessage());
				else
					System.out.println(dm.getMessage());
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Marks the thread as it has to be closed
	 */
	public void close()
	{
		close = true;
	}
}
