package configuration;
/**
 * 
 */
import java.util.regex.PatternSyntaxException;
/**
 * A class for interpreting configuration commands
 * @author Fabrice Servais & Romain Mormont
 */
public class CommandInterpreter {
	private String command = null;
	private String arg = null;
	
	/**
	 * Construct a CommandInterpreter object for this given cmd
	 * @param cmd a String containing the command
	 * @throws IllegalArgumentException if the command don't follow the USAGE
	 */
	public CommandInterpreter(String cmd) throws IllegalArgumentException
	{
		try {
			// splits the command into array of strings
		    String[] cmdArgArray = cmd.split("\\s+");
		    
		    if(cmdArgArray.length == 0 || cmdArgArray.length > 2)
		    	throw new IllegalArgumentException("A command contains 1 or 2 words");
		    
		    command = cmdArgArray[0];
		    if(cmdIsAdd() || cmdIsDelete())
		    {
		   		if(cmdArgArray.length == 1)
		   			throw new IllegalArgumentException("ADD and DEL commands take 1 argument");
		    	arg = cmdArgArray[1];
		    }
	    
		}catch(PatternSyntaxException ex){

		}
	}
	
	/**
	 * Returns true if the command is ADD
	 * @return true if the command is ADD, false otherwise
	 */
	public boolean cmdIsAdd()
	{
		return command.equals("ADD");
	}
	
	/**
	 * Returns true if the command is QUIT
	 * @return true if the command is QUIT, false otherwise
	 */
	public boolean cmdIsQuit()
	{
		return command.equals("QUIT");
	}

	/**
	 * Returns true if the command is DEL
	 * @return true if the command is DEL, false otherwise
	 */
	public boolean cmdIsDelete()
	{
		return command.equals("DEL");
	}

	/**
	 * Returns true if the command is HELP
	 * @return true if the command is HELP, false otherwise
	 */
	public boolean cmdIsHelp()
	{
		return command.equals("HELP");
	}

	/**
	 * Returns true if the command is LIST
	 * @return true if the command is LIST, false otherwise
	 */
	public boolean cmdIsList()
	{
		return command.equals("LIST");
	}

	/**
	 * Returns the argument if there is one
	 * @return a String containing the argument if there is one, null otherwise
	 */
	public String getArg()
	{
		return arg;
	}
	
	/** 
	 * Returns the command sent by the user
	 * @return a String containing the command
	 */
	public String getCmd()
	{
		return command;
	}
}
