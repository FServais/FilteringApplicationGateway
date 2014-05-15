package html;

/**
 * A class for representing an HTML attribute
 * @author Fabrice Servais & Romain Mormont
 */
public class HTMLAttribute implements Cloneable
{
	private String name; // attribute name
	private String value; // attribute value
	private boolean quoted; // true if the attribute values is quoted
	
	/**
	 * Constructs a HTMLAttribute with its name and its value (the value is considered 
	 * to be quoted)
	 * @param name a String containing the name of the attribute
	 * @param value a String containing the value of the attribute
	 */
	public HTMLAttribute(String name, String value)
	{
		this(name, value, true);
	}
	
	/**
	 * Constructs a HTMLAttribute with its name (and no attribute value)
	 * @param name a String containing the name of the attribute
	 */
	public HTMLAttribute(String name)
	{
		this(name, null, true);
	}
	
	/**
	 * Constructs a HTMLAttribute with its name, value
	 * @param name a String containing the name
	 * @param value a String containing the value
	 * @param quoted true if the value must be quoted
	 */
	public HTMLAttribute(String name, String value, boolean quoted)
	{
		this.name = name;
		this.value = value;
		this.quoted = quoted;
	}
	
	/**
	 * Returns the name of the attibute
	 * @return a String containing the name of the attribute
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Returns the value of the attribute
	 * @return a String containing the value of the attribute
	 */	
	public String getValue()
	{
		return value;
	}
	
	/**
	 * Sets a new value for the attribute
	 * @param new_value a String containing the new value
	 */
	public void setValue(String new_value)
	{
		value = new_value;
	}
	
	/**
	 * Sets a new name and new value for the attribute
	 * @param new_name a String containing the new name
	 * @param new_value a String containing the new value
	 */
	public void setName(String new_name, String new_value)
	{
		name = new_name;
		value = new_value;
	}
	
	/**
	 * Converts the HTML attribute to a string
	 * @return a String containing the html attribute
	 */
	public String toString()
	{
		if(value != null && !value.isEmpty())
		{
			if(quoted)
				return name + "=\"" + value + "\"";
			else
				return name + "=" + value;
		}
		else
			return name;
	}
	
	/**
	 * Makes a deep copy of the HTMLAttribute object
	 * @return a copy of the HTMLAttribute object
	 */
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
}
