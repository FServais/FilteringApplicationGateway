package http;

public class HTTP {
	public static final String LINEBREAK, PROTOCOL, END_OF_HEADERS;
	
	/* Response status line */
	public static final String OK_RESPONSE_STATUS_LINE,
						 	   BAD_GATEWAY_STATUS_LINE,
						 	   GATEWAY_TIMEOUT_STATUS_LINE;
	
	/* Headers */
	public static final String NEXT_HEADERS, // headers following the status line
							   OK_HEADERS,
							   BAD_GATEWAY_HEADERS;
	
	
	static
	{
		LINEBREAK = "\r\n";
		PROTOCOL = "HTTP/1.1 ";
		END_OF_HEADERS = LINEBREAK + LINEBREAK;
		
		OK_RESPONSE_STATUS_LINE = PROTOCOL + "200 OK" + LINEBREAK;
		
		BAD_GATEWAY_STATUS_LINE = PROTOCOL + "502 Bad gateway" + LINEBREAK;
	 	GATEWAY_TIMEOUT_STATUS_LINE = PROTOCOL + "504 Gateway timeout" + LINEBREAK;
		
	 	NEXT_HEADERS = "Content-Type: text/html" + LINEBREAK +
	 				   "Content-Length: ";
	 	
		OK_HEADERS = OK_RESPONSE_STATUS_LINE + NEXT_HEADERS;
		BAD_GATEWAY_HEADERS = BAD_GATEWAY_STATUS_LINE + LINEBREAK;
	}
}
