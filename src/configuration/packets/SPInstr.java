package configuration.packets;

/**
 * <b> SPInstr for Server Packet Instructions </b>
 * <br>
 * Defines the byte codes structuring a packet from client to server or server to client
 * <br>
 * Packet configuration : ([KEY BYTE] CONTENT [EOM]){0,2} [ACTION BYTE] [EOP]
 * <br>
 * Key bytes are :
 * <ul>
 * 	<li>BOM</li>
 *  <li>USER</li>
 *  <li>PASS</li>
 *	<li>CMD</li>
 * </ul>
 * <br>
 * Action bytes :
 * <ul>
 * 	<li>WAIT_CMD</li>
 *  <li>WAIT_AUTH</li>
 *  <li>CLOSE</li>
 * </ul>
 * @author Romain Mormont
 */
public enum SPInstr {
	/**
	 * Wait for Command
	 */
	WAIT_CMD(0x81),
	/**
	 * Wait for Authentication
	 */
	WAIT_AUTH(0x82), 
	/**
	 * Beginning of Message
	 */
	BOM(0x83),
	/**
	 *  End of Message (or end of a sequence of char)
	 */
	EOM(0x84), 
	/**
	 * Close Connection
	 */
	CLOSE(0x85),
	/**
	 * Beginning of a command
	 */
	CMD(0x86),
	/**
	 * Beginning of user name
	 */
	USER(0x87), 
	/**
	 * Begging of password
	 */
	PASS(0x88),
	/**
	 * End of Packet
	 */
	EOP(0x89); // 
	
	private final int value;
	
	SPInstr(int value)
	{
		this.value = value;
	}
	
	/**
	 * Returns the value of the enum variable as a byte
	 * @return the 'byte' value of the enum variable
	 */
	public byte getByte()
	{
		return (byte) value;
	}
}
