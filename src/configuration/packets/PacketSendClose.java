/**
 * 
 */
package configuration.packets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A class for constructing and sending a packet for asking end of connection
 * @author Fabrice Servais & Romain Mormont
 */
public class PacketSendClose extends ConfigurationPacketSender {
	private String message = null;
	/**
	 * Construct a packet for closing the connection
	 * @param os an output stream (from the Socket)
	 * @throws IOException if an I/O error occurs
	 */
	public PacketSendClose(OutputStream os) throws IOException {
		super(os);
		buildPacket();
	}
	
	/**
	 * Construct a packet for printing a message (client side) and then closing the connection
	 * @param os an output stream (from the Socket)
	 * @param message a String containing the message to display
	 * @throws IOException if an I/O error occurs
	 */
	public PacketSendClose(OutputStream os, String message) throws IOException {
		super(os);
		this.message = message;
		buildPacket();
	}

	/**
	 * Build packet for closing the connection
	 * <br>
	 * Structure :
	 * <tt>
	 * [BOM message EOM]
	 * CLOSE
	 * EOP
	 * </tt>
	 * @throws IOException if an error occurs while building the packet
	 */
	protected void buildPacket() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		// write packet
		if(message != null && !message.isEmpty())
		{
			baos.write(SPInstr.BOM.getByte()); // message
			baos.write(message.getBytes()); 
			baos.write(SPInstr.EOM.getByte());
		}
		
		baos.write(SPInstr.CLOSE.getByte()); // action
		baos.write(SPInstr.EOP.getByte());

		// save packet
		packet = baos.toByteArray();
	}

}
