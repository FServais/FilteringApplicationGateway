package html;

import java.util.Vector;

/**
 * A class for representing an HTML opening tag.
 * @author Romain Mormont
 */
public class HTMLOpeningTag extends HTMLElement implements Cloneable
{
	protected String tagName;
	protected Vector<HTMLAttribute> attributes;
	
	/**
	 * Constructs an opening html tag 
	 * @param tagName a String containing the name of the tag
	 * @param attributes a Vector of HTMLAttribute containing every attributes and their values
	 */
	public HTMLOpeningTag(String tagName,  Vector<HTMLAttribute> attributes)
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
		this(tagName, new Vector<HTMLAttribute>());
	}
	
	/**
	 * Converts the opening tag to a String
	 * @return a String containing the opening tag
	 */
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		// append name
		sb.append("<" + tagName);
		
		// append attributes
		for(HTMLAttribute attribute : attributes)
			sb.append(" " + attribute.toString());
		
		sb.append(">");
		
		return sb.toString();
	}
	
	/**
	 * Modifies the value of the attribute of given name. Does nothing if no 
	 * attribute was found.
	 * @param name a String containing the name of the attribute to modify
	 * @param new_value a String containing the new value of attribute to modify
	 */
	public void setAttributeValue(String name, String new_value)
	{
		HTMLAttribute attribute = findAttribute(name);
		
		if(attribute == null)
			return;
		
		attribute.setValue(new_value);
	}
	
	/**
	 * Returns the value of an attribute of the given name
	 * @param name a String containing the value of the attribute
	 * @return the attribute value if it is found, null otherwise
	 */
	public String getAttributeValue(String name)
	{
		HTMLAttribute attribute = findAttribute(name);
		return attribute == null ? null : attribute.getValue();
	}
	
	/**
	 * Returns the HTMLAttribute of the tag having the given name
	 * @param name a String containing the name of the searched attribute
	 * @return the HTMLAttribute object, null if there was no matching
	 */
	private HTMLAttribute findAttribute(String name)
	{
		for(HTMLAttribute attribute : attributes)
			if(attribute.getName().equalsIgnoreCase(name))
				return attribute;
		
		return null;
	}
	
	/**
	 * Returns the name of the tag
	 * @return a String containing the name of the tag
	 */
	public String getName()
	{
		return tagName;
	}
	
	
	/**
	 * Makes a deep copy of the HTMLOpeningTag object
	 * @return a copy of the HTMLOpening tag object
	 */
	public Object clone() throws CloneNotSupportedException
	{
		HTMLOpeningTag tag = (HTMLOpeningTag) super.clone();
		tag.attributes = new Vector<HTMLAttribute>();
		for(HTMLAttribute attr : attributes)
			tag.attributes.add((HTMLAttribute) attr.clone());
		
		return tag;
	}
	
	/**
	 * Returns true if the name of the tag equals s (case insensitive)
	 * @param s a String containing the name to compare
	 * @return true if the names are the same, false otherwise
	 */
	public boolean nameEquals(String s)
	{
		return tagName.equalsIgnoreCase(s);
	}
}
