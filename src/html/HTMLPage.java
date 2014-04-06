package html;

import datastructures.*;
import html.*;

/**
 * Class that represent a HTML page, stored in a Tree.
 * 
 * @author Fabrice Servais
 *
 */
public class HTMLPage {
	
	private Tree<HTMLElement> tree;
	
	
	public HTMLPage(String s){
		tree = parse(s);
	}
	
	/**
	 * Convert a String containing the code of a HTML page to a Tree that code.
	 * @param s String containing the HTML code.
	 * @return The tree.
	 */
	private Tree<HTMLElement> parse(String s){
		Tree<HTMLElement> tree = new Tree<HTMLElement>(new Node<HTMLElement>());
		char[] code = s.toCharArray();
		int begin = 0, end = code.length-1;
		
		while(begin <= code.length-1){
			// Reach tag
			while(code[begin] != '<'){++begin;}
			
			// Take name
			end = begin+1;
			while(code[end] != ' ' || code[end] != '>'){++end;}
			if(end+1 > code.length)
				System.out.println(s.substring(begin));
			else
				System.out.println(s.substring(begin, end+1));
			
			begin = end;
		}
		
		return tree;
	}
}
