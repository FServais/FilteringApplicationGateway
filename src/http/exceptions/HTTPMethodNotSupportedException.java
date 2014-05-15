package http.exceptions;

/**
 * Exception for dealing with not implemented http request method
 * @author Fabrice Servais & Romain Mormont
 */
public class HTTPMethodNotSupportedException extends Exception 
{
	private static final long serialVersionUID = 1L;
	
	public HTTPMethodNotSupportedException()
	{
		super();
	}
	
	public HTTPMethodNotSupportedException(String s)
	{
		super(s);
	}
}
