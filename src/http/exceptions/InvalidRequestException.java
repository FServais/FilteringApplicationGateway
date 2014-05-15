package http.exceptions;

/**
 * Exception for dealing with bad HTTP request (the request cannot be handled)
 * @author Fabrice Servais & Romain Mormont
 */
public class InvalidRequestException extends Exception 
{
	private static final long serialVersionUID = 1L;
	
	public InvalidRequestException()
	{
		super();
	}
	
	public InvalidRequestException(String s)
	{
		super(s);
	}
}
