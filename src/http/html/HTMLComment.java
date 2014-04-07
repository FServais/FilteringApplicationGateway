package http.html;

public class HTMLComment extends HTMLElement {
	String comment; // Content
	
	public HTMLComment(String comment)
	{
		this.comment = comment;
	}
	
	/**
	 * toString
	 */
	public String toString()
	{
		return "<!-- " + comment + "-->";
	}
}
