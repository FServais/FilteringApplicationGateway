package html;

public class HTMLContent extends HTMLElement {
	String data; // Content
	
	public HTMLContent(String _data)
	{
		System.out.println("New Content");
		data = _data;
	}
	
	/**
	 * toString
	 */
	public String toString()
	{
		return data;
	}
}
