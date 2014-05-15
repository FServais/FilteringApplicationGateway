package configuration.packets;
/**
 * 
 */
import java.io.IOException;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

/**
 * A class for constructing and sending a packet containing authentication data for the server
 * @author Fabrice Servais & Romain Mormont
 */
public class PacketSendAuthentication extends ConfigurationPacketSender {
	String password = null, username = null;
	
	/**
	 * Construct a packet containing authentication data
	 * @param os an output stream (from the Socket)
	 * @param username a String containing the username (must be different from null)
	 * @param password a String containing the password (must be different from null)
	 * @throws IOException if an I/O error occurs
	 */
	public PacketSendAuthentication(OutputStream os, String username, String password) throws IOException {
		super(os);
	    this.password = password;
	    this.username = username;
	    buildPacket();
	}
	
	/**
	 * Build packet for sending authentication. 
	 * <br>
	 * Structure :
	 * <tt>
	 * USER "username" EOM 
	 * PASS "password" EOM
	 * EOP
	 * </tt>
	 * @throws IOException if an error occurs while building the packet
	 */
	protected void buildPacket() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		// write packet
		baos.write(SPInstr.USER.getByte()); // username
		baos.write(username.getBytes());
		baos.write(SPInstr.EOM.getByte());
		baos.write(SPInstr.PASS.getByte()); // password
		baos.write(password.getBytes());
		baos.write(SPInstr.EOM.getByte());
		baos.write(SPInstr.EOP.getByte()); // no action required since it is sent from the client
		
		// save packet
		packet = baos.toByteArray();
	}
}
