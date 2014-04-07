package http.html;

import java.util.Vector;

/**
 * Class that represent a HTML tag.
 * @author Fabrice Servais
 *
 */
public class HTMLOpeningTag extends HTMLElement 
{
	protected String tagName;
	protected Vector<String> attributes;
	
	/**
	 * Constructors
	 * @param tagName
	 * @param attributes
	 */
	public HTMLOpeningTag(String tagName,  Vector<String> attributes)
	{
		this.tagName = tagName;
		this.attributes = attributes;
	}
	
	public HTMLOpeningTag(String tagName)
	{
		this(tagName, new Vector<String>());
	}
	
	/**
	 * toString
	 */
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append("<" + tagName);
		
		for(String attribute : attributes)
			sb.append(" " + attribute);
		
		sb.append(">");
		
		return sb.toString();
	}
}
