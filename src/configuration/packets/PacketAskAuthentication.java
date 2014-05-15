package configuration.packets;
/**
 * 
 */
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
/**
 * A class for constructing and sending a packet for asking client for authentication
 * @author Fabrice Servais & Romain Mormont
 */
public class PacketAskAuthentication extends ConfigurationPacketSender {
	private String message;
	
	/**
	 * Construct a packet for asking client for authentication. 
	 * If {@code message} is an empty string or null, no message is attached to the packet
	 * @param os an output stream (from the Socket)
	 * @param message the message to display on the client before asking for authentication
	 * @throws IOException if an I/O error occurs
	 */
	public PacketAskAuthentication(OutputStream os, String message) throws IOException {
		super(os);
		this.message = message;
		buildPacket();
	}

	/**
	 * Build packet for asking authentication
	 * <br>
	 * Structure :
	 * <tt>
	 * [BOM message EOM]
	 * WAIT_AUTH
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
		
		baos.write(SPInstr.WAIT_AUTH.getByte()); // action
		baos.write(SPInstr.EOP.getByte());

		// save packet
		packet = baos.toByteArray();
	}

}
