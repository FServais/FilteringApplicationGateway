package http.html;

public class HTMLAttribute 
{
	private String name;
	private String value;
	
	/**
	 * Constructs a HTMLAttribute with its name and its value
	 * @param name a String containing the name of the attribute
	 * @param value a String containing the value of the attribute
	 */
	public HTMLAttribute(String name, String value)
	{
		this.name = name;
		this.value = value;
	}
	
	/**
	 * Returns the value of the attribute
	 * @return a String containing the value of the attribute
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Returns the name of the attribute
	 * @return a String containing the name of the attribute
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
	
	public String toString()
	{
		return name + "=\"" + value + "\"";
	}
}
