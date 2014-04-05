/**
 * 
 */
package configuration.packets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A class for constructing and sending a packet containing a user command
 * @author Romain Mormont
 */
public class PacketSendCommand extends ConfigurationPacketSender {
	private String command = null;
	
	/**
	 * Construct a packet for sending a command to the server
	 * @param os an output stream (from the Socket)
	 * @param command the command to send to the server (must be different from null)
	 * @throws IOException if an I/O error occurs
	 */
	public PacketSendCommand(OutputStream os, String command) throws IOException {
		super(os);
		this.command = command;
		buildPacket();
	}

	/**
	 * Build packet for sending command (and its arguments to the server)
	 * <br>
	 * Structure :
	 * <tt>
	 * CMD "command and args" EOM
	 * EOP
	 * <tt>
	 * @throws IOException if an error occurs while building the packet
	 */
	protected void buildPacket() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		// write packet
		baos.write(SPInstr.CMD.getByte());
		baos.write(command.getBytes());
		baos.write(SPInstr.EOM.getByte());
		baos.write(SPInstr.EOP.getByte());
		
		// save packet
		packet = baos.toByteArray();
	}
}
