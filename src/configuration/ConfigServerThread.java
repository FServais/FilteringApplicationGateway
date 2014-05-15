/**
 * 
 */
package configuration;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import configuration.exceptions.*;
import configuration.packets.*;
import datastructures.*;
import displayer.DisplayerMessage;
import displayer.DisplayerMessageSender;

/**
 * A class for executing a client session on its own thread (for configuring the gateway)
 * @author Fabrice Servais & Romain Mormont
 */
public class ConfigServerThread implements Runnable {
	private WordList wordlist = null; // rejected words list
	private UserMap usermap = null; // list of the users
	private String username = null; // store the username
	private boolean isConnected = false, // true if authentication has succeeded
			hasToBeClosed = false; // true if the connection has to be closed
	private Socket socket = null; // the socket object
	private InputStream is = null; // the input stream object associated with the socket
	private OutputStream os = null; // the output stream object associated with the socket
	private DisplayerMessageSender disp_sender = null; // object for sending message to 
															// output on the server terminal
	private final static String NEWLINE = System.lineSeparator();

	private static final String WELCOME, BAD_AUTH, BAD_COMMAND, LOG_PREF;

	static 
	{
		WELCOME = "\tGateway configuration platform";
		BAD_AUTH = "Bad password or username : ";
		BAD_COMMAND = "Bad command : ";
		LOG_PREF = "[LOG] ";
	}

	/**
	 * Initialize a thread for dealing with a client connection (especially for
	 * configuring the server)
	 * 
	 * @param wordlist a WordList which contains the list of the words forbidden by
	 *            the server
	 * @param usermap a UserMap that contains all data about users
	 * @param socket a initialized Socket for communicating with the Client
	 * @param msgQueue  a Queue for outputting messages on server terminal
	 * @throws NullPointerException if either wordlist or usermap are null
	 * @throws IOException if an I/O error relative to the Socket occurs
	 */
	public ConfigServerThread(WordList wordlist, UserMap usermap, Socket socket,
						LinkedBlockingQueue<DisplayerMessage> disp_sender) 
			throws IOException 
	{
		// store data about restricted words and users
		this.wordlist = wordlist;
		this.usermap = usermap;

		// store streams associated with the socket
		this.socket = socket;
		this.is = socket.getInputStream();
		this.os = socket.getOutputStream();

		// store the queue object for outputting messages
		this.disp_sender = new DisplayerMessageSender(disp_sender);
	}

	/**
	 * Method from Runnable interface
	 */
	public void run() {
		try 
		{
			login();
			while (!hasToBeClosed)
				executeClientRequest();

		} catch (LoginFailureException e) { // authentication failure
			try 
			{
				// signals client that connection will be closed
				(new PacketSendClose(os, "Login has failed - close connection"))
						.sendPacket();
				// print a message on the server terminal
				disp_sender.sendMessage(LOG_PREF + "Login has failed from address '"
											+ socket.getRemoteSocketAddress() + "'");
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} catch (ConnectionLostException e) { // connection lost with the client
			// print message on the server terminal
			String message;
			if (username == null)
				message = "Connection lost before login from address '"
						+ socket.getRemoteSocketAddress() + "'";
			else
				message = "Connection lost with client '" + username + "'";

			disp_sender.sendMessage(LOG_PREF + message);
		} catch (IOException e) {
			//e.printStackTrace();
		} finally {
			
			try 
			{
				if(username != null) 
					usermap.disconnect(username);
				socket.close();
			} catch (IOException | UserNotFoundException e) {
				//e.printStackTrace();
			}
		}
	}

	/**
	 * Manages the client authentication. The client has 3 attempts for giving a
	 * correct authentication otherwise the method throws an exception.
	 * 
	 * @throws LoginFailure if the authentication has failed
	 */
	private void login() throws LoginFailureException, ConnectionLostException
	{
		try {
			ConfigurationPacketSender cps;
			ConfigurationPacketReceiver cpr;
			int nb_attempt = 0;
			String msg = "";

			do
			{
				if(nb_attempt == 0)
					msg = WELCOME;
					
				// send auth. request
				cps = new PacketAskAuthentication(os, msg);
				cps.sendPacket();

				// waits for client answer
				cpr = new ConfigurationPacketReceiver(is);

				// check authentication data
				try
				{
					if(cpr.hasAuthentication())
					{
						synchronized(usermap) // to prevent many user from connecting
											  // under the same login
						{
							if(!usermap.hasUser(cpr.getUsername())) 
								// checks that user is in the list
								msg = BAD_AUTH;
							else if(usermap.isUserConnected(cpr.getUsername())) 
								// checks if this user is connected
								msg = "User '" + cpr.getUsername() + "' already connected";
							else if(!usermap.checkPassword(cpr.getUsername(), cpr.getPassword())) 
								// checks the password
								msg = BAD_AUTH;
							else 
							{
								isConnected = true;
								usermap.connect(cpr.getUsername());
							}
						}
					}			
				}catch(UserNotFoundException e) { }
				
				++nb_attempt;
			} while (!isConnected && nb_attempt < 3);

			if (!isConnected) // connection has failed
				throw new LoginFailureException();

			// authentication has worked
			username = cpr.getUsername();

			try
			{
				cps = new PacketAskCommand(os, "Last connection : "
						+ usermap.getLastConnection(username));
				cps.sendPacket();

				usermap.updateLastConnection(username); // update last connection date					
			}catch(UserNotFoundException e){
				throw new LoginFailureException("User '" + username + "' not found");
			}

			// write message on server terminal
			disp_sender.sendMessage(LOG_PREF + "User '" + username + "' is connected");
		} catch (IOException e) { }
	}

	/**
	 * Receives, interprets and executes a client request
	 * 
	 * @throws IOException if an I/O error occurs (while reading the packet or answering
	 *             the client)
	 */
	private void executeClientRequest() throws IOException,
			ConnectionLostException {
		ConfigurationPacketReceiver cpr = new ConfigurationPacketReceiver(is);
		ConfigurationPacketSender cps;
		try {
			if (cpr.hasCommand()) // checks if client packet contains a command
			{
				CommandInterpreter cmdInt = new CommandInterpreter(
						cpr.getCommand());
				String answerMsg; // answer message to client

				if (cmdInt.cmdIsAdd()) // ADD
				{
					if (wordlist.contains(cmdInt.getArg())) // checks if word is
														// already in the list
						answerMsg = "Word '" + cmdInt.getArg() + "' already in the list";
					else {
						wordlist.insert(cmdInt.getArg());
						answerMsg = "Word '" + cmdInt.getArg() + "' added to the list";
						// writes message to server terminal 
						disp_sender.sendMessage(LOG_PREF + "Word added. List now counts " + wordlist.size() + " elements (user : " + username + ").");
					}
				} else if (cmdInt.cmdIsDelete()) // DEL
				{
					if (!wordlist.contains(cmdInt.getArg())) // checks if word is
														// already in the list
						answerMsg = "Word '" + cmdInt.getArg() + "' not in the list";
					else {
						answerMsg = "Word '" + cmdInt.getArg() + "' deleted from the list";
						wordlist.remove(cmdInt.getArg());
						disp_sender.sendMessage(LOG_PREF + "Word deleted. List now counts " + wordlist.size() + " elements (user : " + username + ").");
					}
				} else if (cmdInt.cmdIsList()) // LIST
				{
					answerMsg = wordlist.toString(NEWLINE);
				} else if (cmdInt.cmdIsHelp()) // HELP
				{
					answerMsg = "USAGE : CMD <ARG>" + NEWLINE
								+ "CMD can be : " + NEWLINE
								+ "\t - ADD 'word' : for adding a word to the list (1 arg)" + NEWLINE
								+ "\t - DEL 'word' : for deleting a word from the list (1 arg)" + NEWLINE
								+ "\t - LIST : for displaying the list of illegal words" + NEWLINE 
								+ "\t - HELP : for help" + NEWLINE
								+ "\t - QUIT : for closing the connection";
				} else if (cmdInt.cmdIsQuit()) // CLOSE
				{
					hasToBeClosed = true;
					answerMsg = "Client request for closing the connection";
					
					// write message on server terminal
					disp_sender.sendMessage(LOG_PREF + "User '" + username + "' has quit.");
					
				} else {
					
					String message = BAD_COMMAND + "'" + cmdInt.getCmd() + "' is unknown";
					// write message on server terminal
					disp_sender.sendMessage(LOG_PREF + message + " (user : " + username + ").");
					answerMsg = message;
				}

				// create anwser packet
				if (hasToBeClosed)
					cps = new PacketSendClose(os, answerMsg);
				else
					cps = new PacketAskCommand(os, answerMsg);

				cps.sendPacket();
			}
		} catch (IllegalArgumentException e) {
			cps = new PacketAskCommand(os, BAD_COMMAND + e.getMessage());
			cps.sendPacket();
		}
	}
}
