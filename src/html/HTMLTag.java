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
	
	
}
