package configuration.client;

import java.net.*;
import java.io.*;

import configuration.exceptions.ConnectionLostException;
import configuration.packets.*;

/**
 * Client program
 * @author Fabrice Servais & Romain Mormont
 */
public class Client 
{
	private static final int PORT = 9005;
	private String server_address; 
	private InputStream socketIS;
	private OutputStream socketOS;
	private Socket s;
	private Console csl;
	private boolean closeConnection = false;
	
	/**
	 * Initialize the client for interacting with the Server
	 * @param server_address
	 * @throws IOException
	 */
	public Client(String server_address) throws IOException
	{
		this.server_address = server_address;

		// acquiring connection
		s = new Socket(this.server_address, PORT);
		socketIS = s.getInputStream();
		socketOS = s.getOutputStream();
		
		// get the system console
		csl = System.console();
	}

	/**
	 * Launch the client
	 * @param args contain the server address at index 0 (ip or name)
	 */
	public static void main(String[] args) 
	{
		if(args.length != 1)
		{
			System.err.println("USAGE : client <IP_ADDR|SERVER_NAME>");
			return;
		}
		
		try
		{
			Client client = new Client(args[0]);
			client.run();
		}catch(ConnectException e){
			System.err.println("Connection timeout");
		}catch(IOException e){ // cannot create or close the socket
			e.printStackTrace();
		}
	}
	
	/**
	 * Start interacting with the server
	 * @throws IOException
	 */
	public void run()
	{
		try
		{
			while(!closeConnection) 
				executeServerRequest();

		}catch(ConnectionLostException e){
			System.err.println("Connection lost with server");
		}catch(Exception e){
			e.printStackTrace();
		}
		finally
		{
			try
			{
				s.close();
			}catch(IOException e){ 
				e.printStackTrace();
			}
		}
	}

	/**
	 * Decodes the packet received from the server and takes appropriate action according to the
	 * server request. 
	 * This methods is <b> blocking </b> if the InputStream is empty (waiting for server response).
	 * @throws IOException if an I/O error occurs (while reading or sending a packet)
	 */
	private void executeServerRequest() throws IOException, ConnectionLostException
	{
		ConfigurationPacketReceiver cpr = new ConfigurationPacketReceiver(socketIS);
		
		if(cpr.hasMessage()) // print any message
			System.out.println(cpr.getMessage());
		
		if(cpr.actionIsWaitCmd())  // server waits for a command
		{
			String cmd;
			
			// get the command from user
			do{
				System.out.print("> ");
				cmd = csl.readLine();
			}while(cmd.isEmpty());
			
			// send the command to the server
			(new PacketSendCommand(socketOS, cmd)).sendPacket();
		}
		else if(cpr.actionIsWaitAuthentication()) // server waits for authentication
		{
			String username, password;
		
			System.out.println("Please provide your username and password.");
			// get username an password
			System.out.print("User : ");
			username = csl.readLine();
		
			System.out.print("Password : ");
			password = new String(csl.readPassword());
			
			// send auth. to the server
			(new PacketSendAuthentication(socketOS, username, password)).sendPacket();
		}
		else if(cpr.actionIsClose()) // server requested to close the connection
		{
			closeConnection = true;
		}
	}
}
