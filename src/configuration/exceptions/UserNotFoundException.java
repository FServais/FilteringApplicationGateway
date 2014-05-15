package configuration.exceptions;

/**
 * Exception for a not found user
 * @author Fabrice Servais & Romain Mormont
 */
public class UserNotFoundException extends Exception 
{
	private static final long serialVersionUID = 1L;
	
	public UserNotFoundException()
	{
		super();
	}
	
	public UserNotFoundException(String s)
	{
		super(s);
	}
}
