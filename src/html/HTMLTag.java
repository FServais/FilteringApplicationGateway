package html;

import java.util.Vector;

/**
 * Class that represent a HTML tag.
 * @author Fabrice Servais
 *
 */
public class HTMLTag extends HTMLElement {
	protected String tagName;
	protected boolean pair; // If pair tag (e.g <p>...</p>)
	protected Vector<String> attributes;
	
	/**
	 * Constructors
	 * @param _tagName
	 * @param _pair
	 * @param _attributes
	 */
	public HTMLTag(String _tagName, boolean _pair, Vector<String> _attributes){
		tagName = _tagName;
		pair = _pair;
		attributes = _attributes;
	}
	
	public HTMLTag(String _tagName, boolean _pair){
		this(_tagName, _pair, new Vector<String>());
	}
	
	/**
	 * toString
	 */
	public String toString(){
		return tagName;
	}
}
