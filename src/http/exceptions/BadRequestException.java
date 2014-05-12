package http.exceptions;

public class BadRequestException extends Exception 
{
	private static final long serialVersionUID = 1L;
	
	public BadRequestException()
	{
		super();
	}
	
	public BadRequestException(String s)
	{
		super(s);
	}
}
