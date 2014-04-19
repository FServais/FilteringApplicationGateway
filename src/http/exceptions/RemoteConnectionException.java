package http.exceptions;

public class RemoteConnectionException extends Exception 
{
	private static final long serialVersionUID = 1L;
	
	public RemoteConnectionException()
	{
		super();
	}
	
	public RemoteConnectionException(String s)
	{
		super(s);
	}
}
