package displayer;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * A class for sending message to the displayer thread
 * @author Fabrice Servais & Romain Mormont
 */
public class DisplayerMessageSender {
	private LinkedBlockingQueue<DisplayerMessage> msgQueue;
	
	/**
	 * Constructs a message for the Displayer 
	 * @param msg a String containing the message
	 * @param isError true if the message is an error message, false otherwise
	 */
	public DisplayerMessageSender(LinkedBlockingQueue<DisplayerMessage> msgQueue)
	{
		this.msgQueue = msgQueue;
	}
	
	/**
	 * Sends a message to the displayer thread
	 * @param msg a String containing the message
	 */
	public void sendMessage(String msg)
	{
		sendMessage(msg, false);
	}
	
	/**
	 * Sends an error message to the displayer thread
	 * @param msg a String containing the error message
	 */
	public void sendErrorMessage(String msg)
	{
		sendMessage(msg, true);
	}
	
	/**
	 * Sends a message to the displayer thread
	 * @param msg a Strign containing the message
	 * @param error true if the message is an error message, false otherwise
	 */
	private void sendMessage(String msg, boolean error)
	{
		try {
			msgQueue.put(new DisplayerMessage(msg, error));
		} catch (InterruptedException e) { }
	}
}
