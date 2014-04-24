package html.parser;

import html.HTMLAttribute;
import html.HTMLClosingTag;
import html.HTMLComment;
import html.HTMLContent;
import html.HTMLElement;
import html.HTMLOpeningTag;
import html.exceptions.HTMLParsingException;

import java.util.LinkedList;
import java.util.Vector;

public class HTMLParser
{
	private LinkedList<HTMLElement> list = null;
	
	// states for attributes parsing
	StateAttr current_state = StateAttr.WAIT_ATTR_NAME;
	StateAttr prev_state = StateAttr.WAIT_ATTR_NAME;
	
	public HTMLParser(String html) throws HTMLParsingException
	{
		list = new LinkedList<HTMLElement>();
		parse(html);
	}
	
	/**
	 * Converts a String containing the code of a HTML page to a list of objects
	 * @param s String containing the HTML code.
	 * @throws HTMLParsingException if an error occured while parsing the code
	 */
	private void parse(String s) throws HTMLParsingException
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
	 * @throws HTMLParsingException if an error occured while parsing the code
	 */
	private int parseTag(String html, int currentCharIndex) 
			throws HTMLParsingException
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
		boolean end_of_comment1 = false, // first '-' read
				end_of_comment2 = false; // second '-' read

		int i = currentCharIndex;
		for(; i < html.length(); i++)
		{
			char c = html.charAt(i);
			
			if(c == '>' && end_of_comment1 && end_of_comment2) // end of tag
				break;
			
			// update the state
			boolean prevEOC1 = end_of_comment1,
					prevEOC2 = end_of_comment2;
			
			end_of_comment1 = (c == '-');
			end_of_comment2 = (prevEOC1 & c == '-');
			
			// append char if necessary
			if(c == '-' && prevEOC1 && prevEOC2)
				sb.append("-");
			else if(c != '-')
			{
				if(prevEOC2) // one '-' read but other char was following
					sb.append("--" + c); 
				else if(prevEOC1) // two '-' read but other char was following
					sb.append("-" + c);
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
	 * @throws HTMLParsingException if an error occured while parsing the code
	 */
	private int parseOpeningTag(String html, int currentCharIndex) 
			throws HTMLParsingException
	{
		Vector<HTMLAttribute> attr = new Vector<HTMLAttribute>();
		StringBuilder sb = new StringBuilder();
		String tag_name = "";
		StateOpeningTag current_state = StateOpeningTag.WAIT_TAG_NAME;
		int i;
		
		if(html.charAt(currentCharIndex) == '<')
			currentCharIndex++;
		
		// while tag not read and string end not reached
		for(i = currentCharIndex; i < html.length(); i++)
		{
			char c = html.charAt(i);
			
			if(current_state.equals(StateOpeningTag.READ_TAG_NAME))
			{
				if(c == '/' || c == '>' || c == ' ') // end of tag name reached
					tag_name = sb.toString();
				
				if(c == '/' || c == '>') // end of tag
					break;
				else if(c == ' ') // end of tag or attributes after some spaces
				{
					i = parseAttribute(html, i, attr);
					break;
				}
				else
					sb.append(c);
			}
			else if(c != ' ')
			{
				current_state = StateOpeningTag.READ_TAG_NAME;
				sb.append(c);
			}
			else
				throw new HTMLParsingException("Parsing error : illegal state reached while parsing an opening tag (index : " + i + ")");
		}
		
		HTMLElement he = new HTMLOpeningTag(tag_name, attr);
		list.addLast(he);
		
		// return the index of the first char to read after '>'
		return i;
	}
	
	/**
	 * Parses the attibutes of a html opening tag
	 * @param html the string containing the html code
	 * @param currentCharIndex the index of the "slash" (or the following) char in the html string
	 * @param a Vector of String in which the parsed attributes must be placed
	 * @return the index of the closing char of the tag '>'
	 * @throws HTMLParsingException if an error occured while parsing the code
	 */
	private int parseAttribute(String html, int currentCharIndex, Vector<HTMLAttribute> attr) 
			throws HTMLParsingException
	{
		// states of the attribute parser
		String attr_name = "";
		
		// mainly for !DOCTYPE tag which sometimes contains a standalone quoted entity
		boolean quoted_attr_name = false; 
		
		StringBuilder sb = new StringBuilder();
		
		current_state = prev_state = StateAttr.WAIT_ATTR_NAME;
		
		for(int i = currentCharIndex; i < html.length(); i++)
		{
			char c = html.charAt(i);
			
			// end of the tag reached
			if(!current_state.equals(StateAttr.READ_ATTR_VAL_QUOTED) 
					&& !current_state.equals(StateAttr.WAIT_ATTR_VAL)
					&& !quoted_attr_name
					&&  (c == '>' || c == '/'))
			{
				// save attribute that remains unsaved
				String built_string = sb.toString();
				if(prev_state == StateAttr.READ_ATTR_NAME 
						|| prev_state == StateAttr.WAIT_ATTR_EQUAL)
					attr.add(new HTMLAttribute(built_string));
				else if(current_state == StateAttr.READ_ATTR_VAL_UNQUOTED )
					attr.add(new HTMLAttribute(attr_name, built_string, false));
				
				// go to the end of the tag chat
				while((c = html.charAt(i)) != '>')
					i++;
				
				return i;
			}
			
			// evaluates what action should be taken for current char 'c'
			// and possibly updates the state
			switch(current_state)
			{
				case WAIT_ATTR_NAME :
				{
					if(c != ' ') // first char of the attribute name found
					{
						quoted_attr_name = (c == '"');
						sb.append(c);
						switchStateAttr(StateAttr.READ_ATTR_NAME);
					}
					
					break;
				}
				
				case READ_ATTR_NAME : 
				{
					if(!quoted_attr_name && (c == ' ' || c == '=')) // end of attr name
					{
						attr_name = sb.toString();
						sb.setLength(0);
						
						if(c == ' ') 
							switchStateAttr(StateAttr.WAIT_ATTR_EQUAL);
						else if(c == '=')
							switchStateAttr(StateAttr.WAIT_ATTR_VAL);
					}
					else if(c == '"')
					{
						quoted_attr_name = false;
						sb.append(c);
					}
					else // still in attribute name
						sb.append(c);
					
					break;
				}
				
				case WAIT_ATTR_EQUAL :
				{
					if(c == '=') // equal found after few spaces after an attribute name
						switchStateAttr(StateAttr.WAIT_ATTR_VAL);
					else if(c != ' ') // found the beginning of a new attribute name
					{
						quoted_attr_name = (c == '"');
						attr.add(new HTMLAttribute(attr_name));
						sb.append(c);
						switchStateAttr(StateAttr.READ_ATTR_NAME);
					}

					break;
				}
				
				case WAIT_ATTR_VAL :
				{
					if(c == '"') // start of a quoted attribute value
						switchStateAttr(StateAttr.READ_ATTR_VAL_QUOTED);
					else if(c != ' ')  // start of an unquoted attribute value
					{
						sb.append(c);
						switchStateAttr(StateAttr.READ_ATTR_VAL_UNQUOTED);
					}

					break;
				}
				
				case READ_ATTR_VAL_QUOTED :
				{
					if(c != '"') // still reading the value
						sb.append(c);
					else  // end of a quoted attribute value
					{
						attr.add(new HTMLAttribute(attr_name, sb.toString()));
						sb.setLength(0);
						switchStateAttr(StateAttr.WAIT_ATTR_NAME);
					}
					
					break;
				}
				
				case READ_ATTR_VAL_UNQUOTED :
				{
					if(c != ' ') // still reading the value
						sb.append(c);
					else // end of an unquoted attribute value
					{
						attr.add(new HTMLAttribute(attr_name, sb.toString(), false));
						sb.setLength(0);
						switchStateAttr(StateAttr.WAIT_ATTR_NAME);
					}
					
					break;
				}
			}
			
		}
		
		throw new HTMLParsingException("Parsing error : illegal state reached while parsing attributes of a tag");
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
		String tag_name = "";
		boolean intag_name = false;
		int i = currentCharIndex;
		
		// skip "slash" char
		if(html.charAt(i) == '/')
			i++;
		
		for(; i < html.length(); i++)
		{
			char c = html.charAt(i);
			if((!intag_name) && c != ' ') // begin of tag name
			{
				intag_name = true;
				sb.append(c);
			}
			else if(intag_name && (c == '>' || c == ' ')) // end of tag name
			{
				intag_name = false;
				tag_name = sb.toString();
			}
			else // in tag name
				sb.append(c);
			
			if(c == '>')
				break;
		}
		
		HTMLElement he = new HTMLClosingTag(tag_name);
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
	 * Switches from current_state to a new state and stores
	 * te previous state in the prev_state variable
	 * @param new_state the new state
	 */
	private void switchStateAttr(StateAttr new_state)
	{
		prev_state = current_state;
		current_state = new_state;
	}
	
	/**
	 * Returns the list representing the parsed html page
	 * @return a LinkedList of HTMLElement representing the html page
	 */
	public LinkedList<HTMLElement> getList()
	{
		return list;
	}

}