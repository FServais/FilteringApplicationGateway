package http.html;

/**
 * @author Romain Mormont
 * A class for representing an HTML comment
 */
public class HTMLComment implements HTMLElement, Cloneable {
	String comment; // Content
	
	/**
	 * Constructs a HTMLElement based on the comment
	 * @param comment a String containing the comment
	 */
	public HTMLComment(String comment)
	{
		this.comment = comment;
	}
	
	/**
	 * Returns the comment as a String
	 * @return a String containing the comment tag with its content
	 */
	public String toString()
	{
		return "<!-- " + comment + "-->";
	}
	
	/**
	 * Makes a deep copy of the HTMLPage object
	 */
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
}
