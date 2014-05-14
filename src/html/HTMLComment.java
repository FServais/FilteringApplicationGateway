package html;

/**
 * A class for representing an HTML comment
 * @author Romain Mormont
 */
public class HTMLComment extends HTMLElement implements  Cloneable {
	
	String comment; // comment content
	
	/**
	 * Constructs a HTMLElement based on the comment
	 * @param comment a String containing the comment content
	 */
	public HTMLComment(String comment)
	{
		this.comment = comment;
	}
	
	/**
	 * Converts the HTMLComment object to a String
	 * @return a String containing the comment tag with its content
	 */
	public String toString()
	{
		return "<!-- " + comment + "-->";
	}
	
	/**
	 * Makes a deep copy of the HTMLComment object
	 * @return a copy of the HTMLComment object
	 */
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
}
