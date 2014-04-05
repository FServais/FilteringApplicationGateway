package datastructures;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
/**
 * A class for representing users
 * @author Romain Mormont
 */
public class User
{
	private String name = null, password = null;
	private Date lastConnection = null;
	private boolean isConnected = false;
	
	/**
	 * Constructs a User based on its name and its password (the user is disconnected)
	 * @param name a String containing the name of the user
	 * @param password a String containing the password of the user
	 */
	public User(String name, String password)
	{
		if(name == null || password == null)
			throw new NullPointerException("Name and password must be different from null");
		
		this.name = name;
		this.password = password;
	}
	
	/**
	 * Returns the name of the user
	 * @return a String containing the name of the user
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Returns the password of the user
	 * @return a String containing the name of the user
	 */
	public String getPassword()
	{
		return password;
	}
	
	/**
	 * Updates the last connection date of the user with the current date and time
	 */
	public void updateConnectionDate()
	{
		lastConnection = new Date();
	}
	
	/**
	 * Returns the last connection date formatted
	 * @return a String containing the formatted last connection date
	 */
	public String getFormattedLastConnection()
	{
		if(lastConnection == null)
			return "First connection - Welcome!!!";
		
		return DateFormat.getDateTimeInstance(DateFormat.FULL, 
											  DateFormat.FULL, 
											  new Locale("EN","en")).format(lastConnection);
	}
	
	/**
	 * Sets the user as connected
	 */
	public void connect()
	{
		isConnected = true;
	}
	
	/**
	 * Sets the user as disconnected
	 */
	public void disconnect()
	{
		isConnected = false;
	}
	
	/**
	 * Returns true if the user is connected
	 * @return true if the user is connected, false otherwise
	 */
	public boolean isConnected()
	{
		return isConnected;
	}
}
