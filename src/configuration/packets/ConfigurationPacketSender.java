package configuration.packets;
/**
 * 
 */
import java.io.IOException;
import java.io.OutputStream;
/**
 * A abstract class for constructing and sending a configuration packet to server or client
 * @author Romain Mormont
 */
public abstract class ConfigurationPacketSender {
	protected OutputStream os;
	protected byte[] packet = null;
	
	/**
	 * Construct a configuration packet 
	 * @param os an Output Stream (from the Socket)
	 * @throws IOException if an I/O error occurs
	 */
	public ConfigurationPacketSender(OutputStream os) throws IOException
	{
		this.os = os;
	}
	
	/**
	 * Sends the packet on the OuputStream 
	 * @throws IOException if an I/O error occurs while writing on the stream
	 */
	public void sendPacket() throws IOException
	{
		os.write(packet);
	}
	
	/**
	 * Builds a packet (builds the associated byte array)
	 * @throws IOException if an I/O error occurs while creating the packet
	 */
	abstract protected void buildPacket() throws IOException;
}
