package html.filter;

/**
 * Enumeration containing the possible status of an html page. 
 *   - PAGE_OK : page can be sent to the user
 *	 - PAGE_REFUSED : page cannot be sent to the user
 *	 - PAGE_NEED_ALTERATION : page can be sent to the user but 
 *					with some words replacement
 * @author Fabrice Servais & Romain Mormont
 */
public enum PageGatewayStatus 
{
	PAGE_OK,
	PAGE_REFUSED,
	PAGE_NEED_ALTERATION;
}
