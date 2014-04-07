package http.html;

import java.net.URL;
import java.util.LinkedList;
import java.util.Vector;

/**
 * Class that represent a HTML page, stored in a Tree.
 * 
 * @author Fabrice Servais
 *
 */
public class HTMLPage {
	private LinkedList<HTMLElement> list;
	
	public HTMLPage(String html)
	{
		list = new LinkedList<HTMLElement>(); 
		parse(html);
	}
	
	public HTMLPage(URL u)
	{
		
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
	private LinkedList<HTMLElement> parse(String s)
	{
		LinkedList<HTMLElement> list = new LinkedList<HTMLElement>();

		for(int i = 0; i < s.length(); i++)
		{
			char c = s.charAt(i);
			
			if(c == '<')
				i = parseTag(s, i);
			else
				i = parseContent(s, i);
		}

		return list;
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
	 */
	public String toString()
	{
		Object[] tagArray = list.toArray();
		StringBuilder sb = new StringBuilder();
		
		for(Object htmlElement : tagArray)
			sb.append(((HTMLElement) htmlElement).toString());
		
		return sb.toString();
	}
}
