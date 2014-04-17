package http.exceptions;

public class RequestDecodingException extends Exception 
{
	private static final long serialVersionUID = 1L;
	
	public RequestDecodingException()
	{
		super();
	}
	
	public RequestDecodingException(String s)
	{
		super(s);
	}
}
