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
		Node<HTMLElement> currentNode = new Node<HTMLElement>();
		Tree<HTMLElement> tree = new Tree<HTMLElement>(currentNode);
		char[] code = s.toCharArray();
		int begin = 0, end = begin;
		System.out.println("length : " + s.length());
		
		while(begin < s.length()-1){
			// Reach tag
			while(code[begin] != '<' && begin < s.length()-1){++begin;}

			// Take name
			if(begin >= s.length()-1)
				break;
			else
				end = begin+1;
			while(code[end] != ' ' && code[end] != '>' && end < s.length()-1){++end;}
			
			// Begin = '<'
			// End = either '>' or ' '
			/*
			if(end+1 >= code.length)
				System.out.println(s.substring(begin+1));
			else
				System.out.println(s.substring(begin+1, end));
			*/
			// If new tag -> new Node
			// If closing tag -> goes up in the tree
			if(code[begin+1] == '/')
				currentNode = currentNode.getParent();
			else if(code[end-1] == '\\'){
				Node<HTMLElement> newNode = new Node<HTMLElement>(new HTMLTag(s.substring(begin+1, end), false));
				newNode.setParent(currentNode);
				
				currentNode.addChild(newNode);
			}	
			else{
				Node<HTMLElement> newNode = new Node<HTMLElement>(new HTMLTag(s.substring(begin+1, end), true));
				newNode.setParent(currentNode);
				
				currentNode.addChild(newNode);
				currentNode = newNode;
			}
			
			
			begin = end;
		}
		
		return tree;
	}
	
	/**
	 * Display the tree that represent the page.
	 */
	public void displayTree(){
		tree.display();
	}
}