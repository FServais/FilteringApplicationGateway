package http;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class HTTPResponse {
	// Header
	private static final String LINEBREAK, PROTOCOL;
	private static final String CONTENT_LENGTH, CONTENT_TYPE, CHARSET;
	
	
	// Error codes
	public static final String OK_200, MOVED_PERMANENTLY_301, MOVED_TEMPORARILY_302, FORBIDDEN_403, 
								NOT_FOUND_404, INTERNAL_SERVER_ERROR_500, NOT_IMPLEMENTED_501;
	
	private String content;
	private int content_length;
	private String content_type, charset, response_code;
	
	// Error page
	private String page_header;
	private String page_content;
	
	static
	{
		LINEBREAK = "\r\n";
		PROTOCOL = "HTTP/1.1";
		
		CONTENT_LENGTH = "Content-Length:";
		CONTENT_TYPE = "Content-Type:";
		CHARSET = "charset=";
		
		OK_200 = "200 OK";
		MOVED_PERMANENTLY_301 = "301 Moved Permanently";
		MOVED_TEMPORARILY_302 = "302 Moved Temporarily"; 
		FORBIDDEN_403 = "403 Forbidden";
		NOT_FOUND_404 = "404 Not Found"; 
		INTERNAL_SERVER_ERROR_500 = "500 Internal Server Error";
		NOT_IMPLEMENTED_501 = "501 Not Implemented";
	}
	
	/**
	 * Construct an HTTP response from a page to return (can be null).
	 * @param content Page to return.
	 */
	public HTTPResponse(String content)
	{
		this(content, OK_200);
	}
	
	public HTTPResponse(int code)
	{
		this(null, codeToString(code));
	}
	
	/**
	 * Construct an HTTP response from a page to return and .
	 * @param content
	 */
	public HTTPResponse(String content, String response_code)
	{
		this.content = content;
		
		setResponseCode(response_code);
		
		if(content != null)
			content_length = content.length();
		
		content_type = "text/html";
		charset = "UTF-8";
	}
	
	/**
	 * 
	 * @param code
	 */
	public void setResponseCode(String code)
	{
		response_code = code;
	}
	
	public void send(Socket socket) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		PrintWriter out = new PrintWriter(socket.getOutputStream()); 
		
		sb.append(PROTOCOL);
		sb.append(" ");
		sb.append(response_code);
		sb.append(LINEBREAK);
		
		if(response_code != HTTPResponse.OK_200)
		{
			content = getErrorPage(response_code);
			content_length = content.length();
		}
		
		sb.append(CONTENT_LENGTH);
		sb.append(" ");
		sb.append(content_length);
		sb.append(LINEBREAK);
		
		sb.append(CONTENT_TYPE);
		sb.append(" ");
		sb.append(content_type);
		sb.append("; ");
		sb.append(CHARSET);
		sb.append(charset);
		sb.append(LINEBREAK);
		
		sb.append(LINEBREAK);
		
		sb.append(content);
		sb.append(LINEBREAK);
		
		out.print(sb.toString());		
		
		out.flush();
		out.close();
	}
	
	public static String codeToString(int code)
	{
		String code_message;
		switch(code)
		{
			case 200:
				code_message = HTTPResponse.OK_200;
				break;
			case 301:
				code_message = HTTPResponse.MOVED_PERMANENTLY_301;
				break;
			case 302:
				code_message = HTTPResponse.MOVED_TEMPORARILY_302;
				break;
			case 403:
				code_message = HTTPResponse.FORBIDDEN_403;
				break;
			case 404:
				code_message = HTTPResponse.NOT_FOUND_404;
				break;
			case 500:
				code_message = HTTPResponse.INTERNAL_SERVER_ERROR_500;
				break;
			case 501:
				code_message = HTTPResponse.NOT_IMPLEMENTED_501;
				break;
			default:
				code_message = HTTPResponse.NOT_FOUND_404;
		}
		
		return code_message;
	}
	
	private String getErrorPageHeader()
	{
		return "<!DOCTYPE html>"
				+ "<html>"
				+ "<head>"
					+ "<style type=\"text/css\">"
					+ "body{ background-color: #F7F7F7; font-family:\"Trebuchet MS\", Arial, Verdana, sans-serif; }"
					+ "#error_head"
					+ "{"
						+ "color:rgba(214,60,54,1);"
						+ "text-align: center;"
						+ "border-top: 1px solid rgba(214,60,54,0.6);"
						+ "border-bottom: 1px solid rgba(214,60,54,0.6);"
						+ "font-size: 14px;" 
						+ "margin-top: 25%;"
					+ "}"
					+ "p{margin-top: 20px;}"
					+ "</style>"
					+ "<meta charset=\"UTF-8\"/> "
					+ "<title>";
	} 
	
	private String getErrorPageEndHeader()
	{
		return  "</title></head><body><div id=\"error_head\"><h1>Error ";
	}
	
	private String getErrorPageEndTitle()
	{
		return " </h1></div><p>";
	}
	
	private String getErrorPageEndContent()
	{
		return "</p></body></html>";
	}
	
	private String getErrorPage(String response_code) 
	{
		return getErrorPageHeader() + response_code + getErrorPageEndHeader() + response_code.toUpperCase() + getErrorPageEndTitle()
				+ getErrorPageEndContent();
	}
}
