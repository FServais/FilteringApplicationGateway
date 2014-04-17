package http.html;

/**
 * @author Romain Mormont
 * A class for representing html content (everything which is not a tag)
 */
public class HTMLContent implements HTMLElement {
	String data; // Content
	
	/**
	 * Constructs a HTMLContent object with the
	 * @param data a String containing the content
	 */
	public HTMLContent(String data)
	{
		this.data = data;
	}
	
	/**
	 * Returns the data as a string
	 * @return a String containing the content
	 */
	public String toString()
	{
		return data;
	}
}
