package http.exceptions;

/**
 * A class of exception for dealing with http request for files that cannot be
 * handled by the gateway
 * @author Fabrice Servais & Romain Mormont
 */
public class BadFileRequestException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public BadFileRequestException()
	{
		super();
	}
	
	public BadFileRequestException(String s)
	{
		super(s);
	}
}
