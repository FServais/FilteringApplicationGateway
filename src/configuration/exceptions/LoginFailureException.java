/**
 * 
 */
package configuration.exceptions;

/**
 * Exception for login failure at server initialization
 * @author Fabrice Servais & Romain Mormont
 */
public class LoginFailureException extends Exception 
{
	private static final long serialVersionUID = 1L;

	public LoginFailureException()
	{
		super();
	}
	
	public LoginFailureException(String s)
	{
		super(s);
	}
}
