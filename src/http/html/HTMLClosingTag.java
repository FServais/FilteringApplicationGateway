package http.html;
public class HTMLClosingTag extends HTMLElement {
	private String tagName;
	
	public HTMLClosingTag(String tagName)
	{
		this.tagName = tagName;
	}
	
	public String toString()
	{
		return "</" + tagName + ">";
	}
}
