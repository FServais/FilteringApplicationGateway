package html;

import java.util.LinkedList;
import java.util.Vector;

import html.exceptions.HTMLParsingException;
import html.parser.HTMLParser;

/**
 * A class for representing an HTML page 
 * @author Fabrice Servais & Romain Mormont
 */
public class HTMLPage implements Cloneable
{
	private boolean links_filtered = false;
	private LinkedList<HTMLElement> list;
	
	/**
	 * Constructs an HTMLPage object 
	 * @param html the html code of the page
	 * @throws HTMLParsingException if an error occured while parsing the code
	 */
	public HTMLPage(String html) throws HTMLParsingException
	{
		HTMLParser parser = new HTMLParser(html); // parse the page
		this.list = parser.getList();
	}
	
	/**
	 * Returns all the content elements of the page (with or without the javascript code)
	 * @param add_script if true then the content in script tags are added to the vector, it's not otherwise
	 * @return a Vector of HTMLContent objects containing all the content elements of the page
	 */
	public synchronized Vector<HTMLContent> getContentElements(boolean add_script)
	{
		Vector<HTMLContent> vec = new Vector<HTMLContent>();
		boolean in_javascript = false;
		
		// run through elements of the pages
		for(HTMLElement htmlElement : list)
		{
			if(htmlElement instanceof HTMLOpeningTag // starts script
					&& ((HTMLOpeningTag) htmlElement).nameEquals("script"))
				in_javascript = true;
			else if(htmlElement instanceof HTMLClosingTag // ends script
					&& ((HTMLClosingTag) htmlElement).nameEquals("script"))
				in_javascript = false;
			
			if(htmlElement instanceof HTMLContent // add content
					&& (!in_javascript || add_script))
				vec.add((HTMLContent) htmlElement);
		}
		
		return vec;
	}	
	
	/**
	 * Converts an HTMLPage object to a String
	 * @return a String containing the html code of the page
	 */
	public synchronized String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		for(HTMLElement htmlElement : list)
			sb.append(((HTMLElement) htmlElement).toString());
		
		return sb.toString();
	}
	
	/**
	 * Returns all the content elements of the page without the javascript code
	 * @return a Vector of HTMLContent objects containing all the content elements of the page (except javascript)
	 */
	public synchronized Vector<HTMLContent> getContentElements()
	{
		return getContentElements(false);
	}
	
	/**
	 * Returns a vector containing the tags of the given name.
	 * If name == "*", then all the opening tags are returned in the vector
	 * @param name a String containing the name of the desired tag or "*"
	 * @return a Vector of HTMLOpeningTag containing the desired tags
	 */
	public synchronized Vector<HTMLOpeningTag> getOpeningTagElements(String name)
	{
		Vector<HTMLOpeningTag> vec = new Vector<HTMLOpeningTag>();
		
		for(HTMLElement element : list)
			if(element instanceof HTMLOpeningTag 
					&& (name.equals("*") || ((HTMLOpeningTag) element).nameEquals(name)))
				vec.add((HTMLOpeningTag) element);
		
		return vec;
	}
	
	/**
	 * Makes a deep copy of the HTMLPage object
	 * @return a copy of the HTMLPage object
	 */
	public synchronized Object clone() throws CloneNotSupportedException
	{
		HTMLPage page = (HTMLPage) super.clone();
		
		page.list = new LinkedList<HTMLElement>();
		
		for(HTMLElement elem : list)
			page.list.addLast((HTMLElement) elem.clone());
		return page;
	}
	
	/**
	 * Returns true if the links were filtered
	 * @return true if the links were filtered, false otherwise
	 */
	public synchronized boolean linksFiltered()
	{
		return links_filtered;
	}
	
	/**
	 * Sets te links_filtered flag which indicates that the links have been filtered
	 * @param links_filtered the new value of the flag (true, filtered ; false, not filtered)
	 */
	public synchronized void setLinkFiltered(boolean links_filtered)
	{
		if(this.links_filtered && !links_filtered)
			System.err.println("Try to unset the flag of link filtering while links were already filtered");
		
		this.links_filtered = links_filtered;
	}
	
	/** TODO : remove this method */
	public void print()
	{
		for(HTMLElement elem : list)
		{
			if(elem instanceof HTMLOpeningTag)
			{
				HTMLOpeningTag h = (HTMLOpeningTag) elem;
				
				if(h.getName().equalsIgnoreCase("a"))
					System.out.println();
				System.out.print(h.getName());
				if(h.getName().equalsIgnoreCase("a"))
					System.out.print(" : " + h.getAttributeValue("href"));
				System.out.println();
			}	
		}
	}
 }
