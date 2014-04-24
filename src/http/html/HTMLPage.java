package http.html;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.Vector;

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
	 */
	public HTMLPage(String html)
	{
		list = new LinkedList<HTMLElement>(); 
		parse(html); // parse the page
	}
	
	/*
	public static void main(String[] args) throws IOException 
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
		System.out.println();
		
		HTMLPage hp = new HTMLPage(response.toString());
		hp.displayList();
	}
	
	*/
	
	/**
	 * Convert a String containing the code of a HTML page to a Tree that code.
	 * @param s String containing the HTML code.
	 * @return The tree.
	 */
	private void parse(String s)
	{
		for(int i = 0; i < s.length(); i++) // run through the string
		{
			char c = s.charAt(i);
			
			if(c == '<') // a tag is reached
				i = parseTag(s, i);
			else
				i = parseContent(s, i);
		}
	}
	
	/**
	 * Parses a html tag and adds it to the list
	 * @param html the string containing the html code
	 * @param currentCharIndex the index of the '<' char opening the tag
	 * @return the index of the closing char of the tag '>'
	 */
	private int parseTag(String html, int currentCharIndex)
	{
		int i = currentCharIndex; 
		
		// skip "space" and "less than" chars
		while(html.charAt(i) == ' ' || html.charAt(i) == '<')
			i++;
			
		if(html.charAt(i) == '/')
			return parseClosingTag(html, i);
		else if(html.charAt(i) == '!' 
					&& (i + 2 < html.length()) // checks that there is two more chars in the string
					&& html.charAt(i + 1) == '-'
					&& html.charAt(i + 2) == '-')
			return parseCommentTag(html, i + 3);
				
		return parseOpeningTag(html, currentCharIndex);
	}
	
	/**
	 * Parses a html comment and adds it to the list
	 * @param html the String containing the html code
	 * @param currentCharIndex 
	 * @return the index of the closing char of the tag '>'
	 */
	private int parseCommentTag(String html, int currentCharIndex) 
	{
		StringBuilder sb = new StringBuilder();
		boolean endOfComment1 = false, // first '-' read
				endOfComment2 = false; // second '-' read

		int i = currentCharIndex;
		for(; i < html.length(); i++)
		{
			char c = html.charAt(i);
			
			if(c == '>') // end of tag
				break;
			
			// update the state
			boolean prevEOC1 = endOfComment1,
					prevEOC2 = endOfComment2;
			
			endOfComment1 = (!prevEOC2 | prevEOC1) & c == '-';
			endOfComment2 = prevEOC1 & c == 'c';
			
			// append char if necessary
			if(c != '-')
			{
				if(endOfComment2) // two '-' read but other char was following
					sb.append("-" + c);
				else if(endOfComment1) // one '-' read but other char was following
					sb.append("--" + c);
				else
					sb.append(c);
			}
		}
		
		HTMLElement he = new HTMLComment(sb.toString());
		list.add(he);
		
		return i;
	}

	/**
	 * TODO some attribute values don't have " "
	 * Parses an opening html tag 
	 * @param html the String containing the html code
	 * @param currentCharIndex the index of the first char of the tag '<'
	 * @return the index of the closing char of the tag '>'
	 */
	private int parseOpeningTag(String html, int currentCharIndex)
	{
		Vector<String> attr = new Vector<String>();
		StringBuilder sb = new StringBuilder();
		String tagName = "";
		boolean tagNameOK = false,  // true if tag name was read
				inTagName = false,
				inAttr = false,		// true if is in an attribute
				inAttrVal = false;  // true if is in the value of the attribute
		int i;
		
		if(html.charAt(currentCharIndex) == '<')
			currentCharIndex++;
		
		// while tag not read and string end not reached
		for(i = currentCharIndex; i < html.length(); i++)
		{
			char c = html.charAt(i);
			
			if(!tagNameOK) // must get tag name
			{
				if(!inTagName && c != ' ') // begin of tag name
				{
					inTagName = true;
					sb.append(c);
				}
				else if(inTagName && (c == ' ' || c == '>' || c == '/')) // reach end of tag name (' ', '/' , '>')
				{
					tagName = sb.toString();
					sb.setLength(0);
					inTagName = false; 
					tagNameOK = true;
				}
				else if(inTagName) // in tag name
					sb.append(c);
				
			}
			else // tag name already read
			{
				if(!inAttr && !(c == ' ' || c == '>' || c == '/'))  // begin of attribute
				{
					inAttr = true;
					sb.append(c);
				}
				else if(inAttr && !inAttrVal && (c == ' ' || c == '>' || c == '/')) // end of "no-value" attribute
				{
					attr.add(sb.toString());
					sb.setLength(0);
					inAttr = false;
				}
				else if(inAttrVal && c == '"') // end of attributes with value
				{
					sb.append(c);
					attr.add(sb.toString());
					sb.setLength(0);
					inAttr = false;
					inAttrVal = false;
				}
				else if(!inAttrVal && c == '"') // begin of attribute value
				{
					inAttrVal = true;
					sb.append(c);
				}
				else if(inAttr) // in attribute
				{
					sb.append(c);
				}
			}
			
			if(c == '>') // end of tag reached
				break;
		}
		
		HTMLElement he = new HTMLOpeningTag(tagName, attr);
		list.addLast(he);
		
		// return the index of the first char to read after '>'
		return i;
	}
	
	/**
	 * Parses a html closing tag
	 * @param html the string containing the html code
	 * @param currentCharIndex the index of the "slash" (or the following) char in the html string
	 * @return the index of the closing char of the tag '>'
	 */
	private int parseClosingTag(String html, int currentCharIndex)
	{
		StringBuilder sb = new StringBuilder();
		String tagName = "";
		boolean inTagName = false;
		int i = currentCharIndex;
		
		// skip "slash" char
		if(html.charAt(i) == '/')
			i++;
		
		for(; i < html.length(); i++)
		{
			char c = html.charAt(i);
			if((!inTagName) && c != ' ') // begin of tag name
			{
				inTagName = true;
				sb.append(c);
			}
			else if(inTagName && (c == '>' || c == ' ')) // end of tag name
			{
				inTagName = false;
				tagName = sb.toString();
			}
			else // in tag name
				sb.append(c);
			
			if(c == '>')
				break;
		}
		
		HTMLElement he = new HTMLClosingTag(tagName);
		list.add(he);
		
		return i;
	}
	
	/**
	 * Parses a content of a html page
	 * @param html the string containing the html code
	 * @param currentCharIndex the index of the first char (of the html string) to add to the content
	 * @return the index of the first "non-content" char in the html string ('<')
	 */
	private int parseContent(String html, int currentCharIndex)
	{
		StringBuilder sb = new StringBuilder();
		int i = currentCharIndex;
		
		for(; i < html.length(); i++)
		{
			char c = html.charAt(i);
			if(c == '<') // has reached a tag
				break;
			sb.append(c);
		}
		
		String content = sb.toString();
		
		if(content.length() != 0) // add content to the list if some content was parsed
			list.add(new HTMLContent(content));
		
		return i - 1;
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
	@SuppressWarnings("unchecked")
	public Object clone() throws CloneNotSupportedException
	{
		HTMLPage page = (HTMLPage) super.clone();
		page.list = (LinkedList<HTMLElement>) list.clone();
		return page;
	}
 }
