package html;

/**
 * An abstract class for representing an html element. 
 * @author Fabrice Servais & Romain Mormont
 * An html element is either a tag (closing or opening), some html comment or some content (text, javascript,...)
 */
public abstract class HTMLElement implements Cloneable 
{ 
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
}