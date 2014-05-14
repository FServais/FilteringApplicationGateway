package html.exceptions;

/**
 * An exception class for dealing with the parsing error
 * @author Romain
 */
public class HTMLParsingException extends Exception 
{
	private static final long serialVersionUID = 1L;
	
	public HTMLParsingException()
	{
		super();
	}
	
	public HTMLParsingException(String s)
	{
		super(s);
	}
}
