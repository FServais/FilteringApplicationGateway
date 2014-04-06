package configuration.exceptions;

public class HttpServerException extends Exception 
{
	private static final long serialVersionUID = 1L;

	public HttpServerException()
	{
		super();
	}
	
	public HttpServerException(String s)
	{
		super(s);
	}
}
