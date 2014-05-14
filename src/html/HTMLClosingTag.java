package html;
/**
 * A class for representing an html closing tag
 * @author Romain Mormont
 */
public class HTMLClosingTag extends HTMLElement implements Cloneable {
	private String tag_name;
	
	/**
	 * Constructs a HTMLClosingTag objects with the name of the tag
	 * @param tag_name a String containing the name of the tag
	 */
	public HTMLClosingTag(String tag_name)
	{
		this.tag_name = tag_name;
	}
	
	/**
	 * Returns the closing tag
	 * @return a String containing the closing tag
	 */
	public String toString()
	{
		return "</" + tag_name + ">";
	}
	
	/**
	 * Returns the name of the tag
	 * @return a String containing the name of the tag
	 */
	public String getName()
	{
		return tag_name;
	}
	
	/**
	 * Makes a deep copy of the HTMLClosingTag object
	 * @return a copy the HTMLClosingTag object
	 * @throws CloneNotSupportedException if clonage is not supported
	 */
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
	
	/**
	 * Returns true if the name of the tag equals s (case insensitive)
	 * @param s a String containing the name to compare
	 * @return true if the names are the same, false otherwise
	 */
	public boolean nameEquals(String s)
	{
		return tag_name.equalsIgnoreCase(s);
	}
}

