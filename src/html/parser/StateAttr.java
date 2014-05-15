package html.parser;

/**
 * Enumeration containing the states of the html tag attributes parser
 * @author Fabrice Servais & Romain Mormont
 */
public enum StateAttr 
{
	WAIT_ATTR_NAME, /** Wait for attribute name */
	READ_ATTR_NAME, /** Read attribute name */
	WAIT_ATTR_EQUAL, /** Wait for the equal sign following the attribute */
	WAIT_ATTR_VAL, /** Wait for the attribute values */
	READ_ATTR_VAL_QUOTED, /** Read a quoted attribute value */
	READ_ATTR_VAL_UNQUOTED; /** Read an unquoted attribute value */
}
