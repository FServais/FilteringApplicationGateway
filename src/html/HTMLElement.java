package html;

/**
 * @author Romain Mormont
 * Interface for representing an html element. 
 * An html element is either a tag (closing or opening) or some content (text, javascript,...)
 */
public abstract class HTMLElement implements Cloneable 
{ 
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
}