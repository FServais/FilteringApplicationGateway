package configuration.exceptions;

/**
 * Exception for connection lost with server or client
 * @author Fabrice Servais & Romain Mormont
 */
public class ConnectionLostException extends Exception 
{
	private static final long serialVersionUID = 1L;
	
	public ConnectionLostException()
	{
		super();
	}
	
	public ConnectionLostException(String s)
	{
		super(s);
	}
}
