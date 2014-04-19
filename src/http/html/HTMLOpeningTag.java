package http.html;

import java.util.Vector;

/**
 * @author Romain Mormont
 * Class that represents a HTML opening tag.
 */
public class HTMLOpeningTag implements HTMLElement 
{
	protected String tagName;
	protected Vector<HTMLAttribute> attributes;
	
	/**
	 * Constructs an opening html tag 
	 * @param tagName a String containing the name of the tag
	 * @param attributes a Vector of String containing every attributes and their values
	 */
	public HTMLOpeningTag(String tagName,  Vector<String> attributes_raw)
	{
		this.tagName = tagName;
		this.attributes = new Vector<HTMLAttribute>();
		parseAttributes(attributes_raw);
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
		for(HTMLAttribute attribute : attributes)
			sb.append(" " + attribute.toString());
		
		sb.append(">");
		
		return sb.toString();
	}
	
	/**
	 * Parses all the attributes of a html tag. The attributes must have the 
	 * following structure :
	 * 		'name="values"'
	 * @param attributes_raw a Vector of String containing the attributes 
	 */
	private void parseAttributes(Vector<String> attributes_raw)
	{
		for(String attribute : attributes_raw)
			attributes.add(parseAttribute(attribute));
	}
	
	/**
	 * Parses a raw attribute of an html tag. The attribute must have the 
	 * following structure :
	 * 		'name="values"'
	 * @param attribute_raw a String containing the attribute
	 * @return the corresponding HTMLAttribute object
	 */
	private HTMLAttribute parseAttribute(String attribute_raw)
	{
		int name_last_index = attribute_raw.indexOf('='),
			value_1st_index = attribute_raw.indexOf('"') + 1,
			value_last_index = attribute_raw.lastIndexOf('"');
		
		String name = name_last_index > 0 ? attribute_raw.substring(0, name_last_index) : attribute_raw,
			   value = "";
		
		if(value_1st_index >= 0 && value_1st_index <= value_last_index)
			value = attribute_raw.substring(value_1st_index, value_last_index);
		
		//System.out.println("raw = " + attribute_raw + " | name : " + name + " | value : " + value);	
		return new HTMLAttribute(name, value);
	}
	
	/**
	 * Modifies the value of the attribute of given name. Does nothing if no 
	 * attribute was found.
	 * @param name a String containing the name of the attribute to modify
	 * @param newValue a String containing the new value of attribute to modify
	 */
	public void setAttributeValue(String name, String newValue)
	{
		HTMLAttribute attribute = findAttribute(name);
		
		if(attribute == null)
			return;
		
		attribute.setValue(newValue);
	}
	
	/**
	 * Returns the value of an attribute of the given name
	 * @param name a String containing the value of the attribute
	 * @return the attribute value if it is found, false otherwise
	 */
	public String getAttributeValue(String name)
	{
		HTMLAttribute attribute = findAttribute(name);
		return attribute == null ? null : attribute.getValue();
	}
	
	/**
	 * Returns the HTMLAttribute of the tag matching the given name
	 * @param name a String containing the name of the searched attribute
	 * @return the HTMLAttribute object, null if there was no matching
	 */
	private HTMLAttribute findAttribute(String name)
	{
		for(HTMLAttribute attribute : attributes)
			if(attribute.getName().equals(name))
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
}
