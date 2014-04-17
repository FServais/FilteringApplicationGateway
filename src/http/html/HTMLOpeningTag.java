package http.html;

import java.util.Vector;

/**
 * @author Romain Mormont
 * Class that represents a HTML opening tag.
 */
public class HTMLOpeningTag implements HTMLElement 
{
	protected String tagName;
	protected Vector<String> attributes;
	
	/**
	 * Constructs an opening html tag 
	 * @param tagName a String containing the name of the tag
	 * @param attributes a Vector of String containing every attributes and their values
	 */
	public HTMLOpeningTag(String tagName,  Vector<String> attributes)
	{
		this.tagName = tagName;
		this.attributes = attributes;
	}
	
	/**
	 * Constructs an opening hmtl tag with no attribute
	 * @param tagName a String containing the name of the tag
	 */
	public HTMLOpeningTag(String tagName)
	{
		this(tagName, new Vector<String>());
	}
	
	/**
	 * Returns the opening tag as a String 
	 * @return a String containing the opening tag
	 */
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		// append name
		sb.append("<" + tagName);
		
		// append attributes
		for(String attribute : attributes)
			sb.append(" " + attribute);
		
		sb.append(">");
		
		return sb.toString();
	}
}
