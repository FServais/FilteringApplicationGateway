package http.html;

import java.util.LinkedList;
import datastructures.*;

public class HTMLPage 
{
	private LinkedList<HTMLElement> page;
	private String URL;
	
	public HTMLPage()
	{
		URL = new String();
		page = new LinkedList<HTMLElement>();
	}
	
	/**
	 * Return a representation of the page.
	 * @return Representation of the page.
	 */
	public String toString()
	{
		return page.toString();
	}
}
