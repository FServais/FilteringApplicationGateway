package http.html;

public class HTMLContent extends HTMLElement {
	String data; // Content
	
	public HTMLContent(String data)
	{
		this.data = data;
	}
	
	/**
	 * toString
	 */
	public String toString()
	{
		return data;
	}
}
