package displayer;

/**
 * A class for representing a message to be displayed by the displayer thread
 * @author Fabrice Servais & Romain Mormont
 */
public class DisplayerMessage {
	private String msg;
	private boolean isError;
	
	/**
	 * Constructs a message for the Displayer 
	 * @param msg a String containing the message
	 * @param isError true if the message is an error message, false otherwise
	 */
	public DisplayerMessage(String msg, boolean error)
	{
		this.msg = msg;
		this.isError = error;
	}
	
	/**
	 * Constructs a normal message 
	 * @param msg a String containing the message
	 */
	public DisplayerMessage(String msg)
	{
		this(msg, false);
	}
	
	/**
	 * Returns true if the message is an error message, false otherwise
	 * @return true if the message is an error message, false otherwise
	 */
	public boolean isError()
	{
		return isError;
	}
	
	/**
	 * Returns the message
	 * @return a String containing the message
	 */
	public String getMessage()
	{
		return msg;
	}
}
