package displayer;

public class DisplayerMessage {
	private String msg = null;
	private boolean isError = false;
	
	/**
	 * Constructs a message for the Displayer 
	 * @param msg a String containing the message
	 * @param isError true if the message is an error message, false otherwise
	 */
	public DisplayerMessage(String msg, boolean isError)
	{
		this.msg = msg;
		this.isError = isError;
	}
	
	/**
	 * Constructs a normal message 
	 * @param msg a String containing the message
	 */
	public DisplayerMessage(String msg)
	{
		this.msg = msg;
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
