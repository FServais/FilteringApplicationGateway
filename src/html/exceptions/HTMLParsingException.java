package html.exceptions;

/**
 * A class for parsing error of html code
 * @author Romain
 *
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
