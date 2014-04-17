package http.html;
/**
 * @author Romain Mormont
 * A class for representing a html closing tag
 */
public class HTMLClosingTag implements HTMLElement {
	private String tagName;
	
	/**
	 * Constructs a HTMLClosingTag objects with the name of the tag
	 * @param tagName a String containing the name of the tag
	 */
	public HTMLClosingTag(String tagName)
	{
		this.tagName = tagName;
	}
	
	/**
	 * Returns the closing tag
	 * @return a String containing the closing tag
	 */
	public String toString()
	{
		return "</" + tagName + ">";
	}
}

