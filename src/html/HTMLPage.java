package html;
/*
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;*/
import java.util.LinkedList;
import java.util.Vector;

import html.exceptions.HTMLParsingException;
import html.parser.HTMLParser;;

/**
 * A class for representing a HTML page 
 * @author Romain Mormont
 */
public class HTMLPage implements Cloneable
{
	private LinkedList<HTMLElement> list;
	
	/**
	 * Constructs a HTMLPage object 
	 * @param html the html code of the page
	 * @throws HTMLParsingException if an error occured while parsing the code
	 */
	public HTMLPage(String html) throws HTMLParsingException
	{
		HTMLParser parser = new HTMLParser(html); // parse the page
		this.list = parser.getList();
	}
	
/*	public static void main(String[] args) throws IOException, HTMLParsingException 
	{
		URL u = new URL("http://www.montefiore.ulg.ac.be/~pw/");
		HttpURLConnection huc = (HttpURLConnection) u.openConnection();
		
		huc.setRequestMethod("GET");
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(huc.getInputStream()));
		
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) 
		{
			response.append(inputLine);
		}
		in.close();
 
		//print result
		System.out.println(response.toString());
		
		HTMLPage hp = new HTMLPage(response.toString());
		System.out.println(hp.toString());
	}*/
	
	
	
	/**
	 * Returns all the content elements of the page (with or without the javascript code)
	 * @param the content in script tags are added to the vector, they're not otherwise
	 * @return a Vector of HTMLContent objects containing all the content elements of the page
	 */
	public Vector<HTMLContent> getContentElements(boolean add_script)
	{
		Vector<HTMLContent> vec = new Vector<HTMLContent>();
		boolean in_javascript = false;
		
		// run through elements of the pages
		for(HTMLElement htmlElement : list)
		{
			if(htmlElement instanceof HTMLOpeningTag // starts script
					&& ((HTMLOpeningTag) htmlElement).getName().equals("script"))
				in_javascript = true;
			else if(htmlElement instanceof HTMLClosingTag // ends script
					&& ((HTMLClosingTag) htmlElement).getName().equals("script"))
				in_javascript = false;
			
			if(htmlElement instanceof HTMLContent // add content
					&& (!in_javascript || add_script))
				vec.add((HTMLContent) htmlElement);
		}
		
		return vec;
	}	
	
	/**
	 * Returns the html code of the page
	 * @return a String containing the html code of the page
	 */
	public String toString()
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
	public Vector<HTMLContent> getContentElements()
	{
		return getContentElements(false);
	}
	
	/**
	 * Returns a vector containing the tags of the given name.
	 * If name == "*", then all the opening tags are returned in the vector
	 * @param name a String containing the name of the desired tag or "*"
	 * @return a Vector of HTMLOpeningTag containing the desired tags
	 */
	public Vector<HTMLOpeningTag> getOpeningTagElements(String name)
	{
		Vector<HTMLOpeningTag> vec = new Vector<HTMLOpeningTag>();
		
		for(HTMLElement element : list)
			if(element instanceof HTMLOpeningTag 
					&& (name.equals("*") || ((HTMLOpeningTag) element).getName().equalsIgnoreCase(name)))
				vec.add((HTMLOpeningTag) element);
		
		return vec;
	}
	
	/**
	 * Makes a deep copy of the HTMLPage object
	 */
	public Object clone() throws CloneNotSupportedException
	{
		HTMLPage page = (HTMLPage) super.clone();
		
		page.list = new LinkedList<HTMLElement>();
		
		for(HTMLElement elem : list)
			page.list.addLast((HTMLElement) elem.clone());
		return page;
	}
 }
