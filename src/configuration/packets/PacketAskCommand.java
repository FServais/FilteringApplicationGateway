/**
 * 
 */
package configuration.packets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A class for constructing and sending a packet for asking client to enter a command
 * @author Fabrice Servais & Romain Mormont
 */
public class PacketAskCommand extends ConfigurationPacketSender {
	private String message = null;
	/**
	 * Construct a packet for asking client to enter a command
	 * @param os an output stream (from the Socket)
	 * @throws IOException if an I/O error occurs
	 */
	public PacketAskCommand(OutputStream os) throws IOException {
		super(os);
		buildPacket();
	}
	
	/**
	 * Construct a packet for printing a message and then asking the client to enter a command 
	 * @param os an output stream (from the Socket)
	 * @param message a String containing the message to display before requesting command
	 * @throws IOException if an I/O error occurs
	 */
	public PacketAskCommand(OutputStream os, String message) throws IOException {
		super(os);
		this.message = message;
		buildPacket();
	}

	/**
	 * Build packet for asking a command
	 * <br>
	 * Structure :
	 * <tt>
	 * [BOM message EOM]
	 * WAIT_CMD
	 * EOP
	 * </tt>
	 * @throws IOException if an error occurs while building the packet
	 */
	protected void buildPacket() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		// write packet
		if(message != null)
		{
			baos.write(SPInstr.BOM.getByte());
			baos.write(message.getBytes());
			baos.write(SPInstr.EOM.getByte());
		}
		
		baos.write(SPInstr.WAIT_CMD.getByte());
		baos.write(SPInstr.EOP.getByte());
		
		// save packet
		packet = baos.toByteArray();
	}

}
