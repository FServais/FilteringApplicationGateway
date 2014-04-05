package datastructures;

import java.util.Hashtable;

import configuration.exceptions.UserNotFoundException;

/**
 * A class for dealing with a list of users. This class is synchronized.
 * @author Romain Mormont
 */
public class UserMap {
	private Hashtable<String, User> map;
	
	/**
	 * Construct an empty list
	 */
	public UserMap()
	{
		map = new Hashtable<String, User>();
	}
	
	/**
	 * Returns true whether the user (of given username) is in the list
	 * @param username a String containing the user name
	 * @return true if the user is in the list, false otherwise
	 */
	public synchronized boolean hasUser(String username)
	{
		return map.containsKey(username);
	}
	
	/**
	 * Returns true if the given password matches the user's one. False is also returned if the user is not in the list.
	 * @param username a String containing the username
	 * @param password a String containing the password
	 * @return true whether the password matches, false otherwise
	 * @throws UserNotFoundException 
	 */
	public synchronized boolean checkPassword(String username, String password) 
			throws UserNotFoundException
	{		
		return getUser(username).getPassword().equals(password);
	}
	
	/**
	 * Updates the last connection date of the given user. Does nothing if the user is not in the list.
	 * @param username a String containing the username
	 * @throws UserNotFoundException 
	 */
	public synchronized void updateLastConnection(String username) 
			throws UserNotFoundException
	{
		getUser(username).updateConnectionDate();
	}
	
	/**
	 * Returns the formatted date of the user's last connection
	 * @param username a String containing the username
	 * @return a String containing the formatted data (null if the user is not in the list)
	 * @throws UserNotFoundException 
	 */
	public synchronized String getLastConnection(String username) 
			throws UserNotFoundException
	{		
		return getUser(username).getFormattedLastConnection();
	}
	
	/**
	 * Adds a user in the list
	 * @param username a String containing the username
	 * @param password a String containing the password
	 */
	public synchronized void addUser(String username, String password)
	{
		User u = new User(username, password);
		map.put(username, u);
	}
	
	/**
	 * Returns the user mapped by the given name
	 * @param username a String containing the username
	 * @return an User object 
	 * @throws UserNotFoundException 
	 */
	private User getUser(String username) 
			throws UserNotFoundException
	{
		User u = map.get(username);
		if(u == null)
			throw new UserNotFoundException();
		return map.get(username);
	}
	
	/**
	 * Returns true if the user is connecting
	 * @param username a String containing the username
	 * @return true if the user is connected false otherwise
	 * @throws UserNotFoundException
	 */
	public boolean isUserConnected(String username) 
			throws UserNotFoundException
	{
		return getUser(username).isConnected();
	}
	
	/**
	 * Sets the given user connected
	 * @param username a String containing the username
	 * @throws UserNotFoundException
	 */
	public void connect(String username) 
			throws UserNotFoundException
	{
		getUser(username).connect();
	}
	
	/**
	 * Sets the given user disconnected
	 * @param username a String containing the username
	 * @throws UserNotFoundException
	 */
	public void disconnect(String username) 
			throws UserNotFoundException
	{
		getUser(username).disconnect();
	}
}
