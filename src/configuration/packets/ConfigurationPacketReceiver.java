package configuration.packets;
/**
 * 
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;

import configuration.exceptions.ConnectionLostException;

/**
 * A class for deconstructing a packet (having the configuration described in the 
 * enum SPInstr) received from the client or the server.
 * @author Fabrice Servais & Romain Mormont
 * @see SPInstr, ConfigurationPacketSender
 */
public class ConfigurationPacketReceiver {
	private String message = null; // null if no message
	private String user = null; // null if no username
	private String password = null; // null if no password
	private String command = null; // null if no command
	private byte action = 0x0; // contains the byte code corresponding to the message
	private static final String CHAR_ENCODING = "ISO-8859-1";
	
	/**
	 * Deconstruct a packet received in the given input stream (reads it till an EOP byte is reached) 
	 * @param is an InputStream 
	 * @throws IOException if an I/O error occurs 
	 */
	public ConfigurationPacketReceiver(InputStream is) throws IOException, ConnectionLostException
	{
		readPacket(is);
	}
	
	/**
	 * Read a packet from the input stream till an EOP byte is reached 
	 * @param is an input stream
	 * @throws IOException if an I/O error occurs 
	 */
	private void readPacket(InputStream is) throws IOException, ConnectionLostException
	{
		byte byteFromIS = 0; 
		boolean endOfPacket = false;
		
		do{
			byteFromIS = (byte) is.read();
			
			if(byteFromIS == SPInstr.BOM.getByte())
			{
				// read messages
				message = readTillEOM(is);
			}
			else if(byteFromIS == SPInstr.USER.getByte())
			{
				// read username 
				user = readTillEOM(is);
			}
			else if(byteFromIS == SPInstr.PASS.getByte())
			{
				// read password
				password = readTillEOM(is);
			}
			else if(byteFromIS == SPInstr.CMD.getByte())
			{
				// read command
				command = readTillEOM(is);
			}
			else if(byteFromIS == SPInstr.WAIT_CMD.getByte() 
					|| byteFromIS == SPInstr.WAIT_AUTH.getByte()
					|| byteFromIS == SPInstr.CLOSE.getByte()) // server waits for a command
			{
				// store the action
				action = byteFromIS;
			}
			else if(byteFromIS == SPInstr.EOP.getByte()) // end of packet is reached
				endOfPacket = true;
			else if(byteFromIS == -1)
				throw new ConnectionLostException();
			else
				throw new IOException("Bad byte '" + Byte.toString(byteFromIS) + "' read in input stream");

		}while(!endOfPacket);
	}

	/**
	 * Reads bytes from an input stream until a EOM byte and converts the byte sequence
	 * into a string (using "ISO-8859-1" encoding) 
	 * @param is an input stream
	 * @return a string
	 * @throws IOException if an I/O error occurs 
	 */
	private String readTillEOM(InputStream is) throws IOException, ConnectionLostException
	{
		ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
		byte byteFromIS;
		
		
		// reads messages till EOM byte
		do 
		{
			byteFromIS = (byte) is.read();
			
			if(byteFromIS == -1) // connection is lost
				throw new ConnectionLostException();
			
			if(byteFromIS != SPInstr.EOM.getByte()) // is end of message reached?
				byteArrayOS.write(byteFromIS);
			
		}while(byteFromIS != SPInstr.EOM.getByte());
		
		// converts the message
		return byteArrayOS.toString(CHAR_ENCODING);
	}
	
	/**
	 * Returns true whether the packet contains a message
	 * @return true if the packet contains a message, false otherwise
	 */
	public boolean hasMessage()
	{
		return (message != null);
	}
	
	/**
	 * Returns the message contained in the packet
	 * @return the message, null if there wasn't any
	 */
	public String getMessage()
	{
		return message;
	}
	
	/**
	 * Returns true whether the packet contains authentication data (username AND password).
	 * @return true if the packet contains authentication data, false otherwise
	 */
	public boolean hasAuthentication()
	{
		return (user != null && password != null);
	}
	
	/**
	 * Returns the username sent in the packet if all authentication data have been received. 
	 * Otherwise, returns null if the packet didn't contain any authentication data or if one of them was missing 
	 * @return the username or a null pointer
	 */
	public String getUsername()
	{
		return hasAuthentication() ? user : null;
	}
	
	/**
	 * Returns the password sent in the packet if all authentication data have been received.
	 * Otherwise, returns null if the packet didn't contain any authentication data or if one of them was missing 
	 * @return the password or a null pointer
	 */
	public String getPassword()
	{
		return hasAuthentication() ? password : null;
	}

	/**
	 * Returns true whether the packet contains a command
	 * @return true if the packet contains a command, false otherwise
	 */
	public boolean hasCommand()
	{
		return (command != null);
	}

	/**
	 * Returns the command contained in the packet and its arguments
	 * @return the command and its arguments, null if the packet didn't contain any command
	 */
	public String getCommand()
	{
		return command;
	}

	/**
	 * Returns true if the action requested was WAIT_CMD
	 * @return true if the action is WAIT_CMD, false otherwise
	 */
	public boolean actionIsWaitCmd()
	{
		return (action == SPInstr.WAIT_CMD.getByte());
	}

	/**
	 * Returns true if the action requested was WAIT_AUTH
	 * @return true if the action is WAIT_AUTH, false otherwise
	 */
	public boolean actionIsWaitAuthentication()
	{
		return (action == SPInstr.WAIT_AUTH.getByte());
	}

	/**
	 * Returns true if the action requested was CLOSE
	 * @return true if the action is CLOSE, false otherwise
	 */
	public boolean actionIsClose()
	{
		return (action == SPInstr.CLOSE.getByte());
	}
}


