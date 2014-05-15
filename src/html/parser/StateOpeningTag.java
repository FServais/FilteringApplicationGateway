package html.parser;

/** 
 * Enumeration containing the states of the html opening tag parser
 * @author Fabrice Servais & Romain Mormont
 */
public enum StateOpeningTag 
{
	WAIT_TAG_NAME,
	READ_TAG_NAME;
}
