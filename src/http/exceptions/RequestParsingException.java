package http.exceptions;

/**
 * Exception for dealing with bad HTTP request (bad request structure or content)
 * @author Fabrice Servais & Romain Mormont
 */
public class RequestParsingException extends Exception 
{
	private static final long serialVersionUID = 1L;
	
	public RequestParsingException()
	{
		super();
	}
	
	public RequestParsingException(String s)
	{
		super(s);
	}
}
