package http;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class HTTPResponse {
	// Header
	private static final String LINEBREAK, PROTOCOL;
	private static final String CONTENT_LENGTH, CONTENT_TYPE, CHARSET;
	
	
	// Response codes
	public static final String OK_200, 
								MOVED_PERMANENTLY_301, 
								MOVED_TEMPORARILY_302, 
								BAD_REQUEST_400,
								FORBIDDEN_403, 
								NOT_FOUND_404, 
								INTERNAL_SERVER_ERROR_500, 
								NOT_IMPLEMENTED_501, 
								BAD_GATEWAY_502,
								GATEWAY_TIMEOUT_504;
	
	private String content;
	private int content_length;
	private String content_type, charset, response_code;
	
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
		BAD_REQUEST_400 = "400 Bad Request";
		FORBIDDEN_403 = "403 Forbidden";
		NOT_FOUND_404 = "404 Not Found"; 
		INTERNAL_SERVER_ERROR_500 = "500 Internal Server Error";
		NOT_IMPLEMENTED_501 = "501 Not Implemented";
		BAD_GATEWAY_502 = "502 Bad Gateway";
		GATEWAY_TIMEOUT_504 = "504 Gateway Timeout";
	}
	
	/**
	 * Construct an HTTP response from a page to return (can be null).
	 * @param content Page to return.
	 */
	public HTTPResponse(String content)
	{
		this(content, OK_200);
	}
	
	/**
	 * Construct an HTTP response from the response code. 
	 * @param response_code HTTP response code
	 */
	public HTTPResponse(int code)
	{
		this(null, codeToString(code));
	}
	
	/**
	 * Construct an HTTP response from a page to return and the response code (String).
	 * @param content Page to return, null if none.
	 * @param response_code String corresponding to the response code
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
	
	/*
	 * ----- Parts of the error page -----
	 */
	private String getErrorPageHeader()
	{
		return "<!DOCTYPE html><html><head><style type=\"text/css\">" +
				"body{ background-color: #F7F7F7; font-family:\"Trebuchet MS\", Arial, Verdana, sans-serif; }"
					+ "#error_head{color:rgba(214,60,54,1); text-align: center; margin-left:auto; margin-right:auto;"
						+ "border: 1px solid rgba(214,60,54,0.6); font-size: 14px; margin-top: 20%; width:35%;}"
					+ "p{margin-top: 20px;}"
					+ "</style><meta charset=\"UTF-8\"/><title>";
	} 
	
	private String getErrorPageEndHeader()
	{
		return  "</title></head><body><div id=\"error_head\"><h3>Gateway : </h3><h1>Error ";
	}
	
	private String getErrorPageEndTitle()
	{
		return " </h1></div><p>";
	}
	
	private String getErrorPageEndContent()
	{
		return "</p></body></html>";
	}
	/*
	 * ----------------------------------
	 */
	
	/**
	 * Get an error page depending of the response code.
	 * @return Error page
	 */
	private String getErrorPage() 
	{
		return getErrorPageHeader() + response_code + getErrorPageEndHeader() + response_code.toUpperCase() + getErrorPageEndTitle()
				+ getErrorPageEndContent();
	}
	
	/**
	 * Convert a integer representing the code of the response to the corresponding String code.
	 * @param code Code of the response
	 * @return Corresponding String
	 */
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
			case 400:
				code_message = HTTPResponse.BAD_REQUEST_400;
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
			case 502:
				code_message = HTTPResponse.BAD_GATEWAY_502;
				break;
			case 504:
				code_message = HTTPResponse.GATEWAY_TIMEOUT_504;
				break;
			default:
				code_message = code + " (Not implemented error)";
		}
		
		return code_message;
	}
	
	/**
	 * Set the response code (String) of the response.
	 * @param code (String) New code.
	 */
	public void setResponseCode(String code)
	{
		this.response_code = code;
	}
	
	/**
	 * Set the content_length field (practical for HEAD response)
	 * @param new_length New length
	 */
	public void setContentLength(int new_length)
	{
		this.content_length = new_length;
	}
	
	/**
	 * Send an HTTP response through the socket, depending of the code of the response and the method of the HTTP request.
	 * @param socket Socket through which the message has to be sent.
	 * @param content Boolean that determine if the content as to be part of response or not
	 * @throws IOException
	 */
	public void send(Socket socket, boolean with_content) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		PrintWriter out = new PrintWriter(socket.getOutputStream()); 
		
		sb.append(PROTOCOL);
		sb.append(" ");
		sb.append(response_code);
		sb.append(LINEBREAK);
		
		if(response_code != HTTPResponse.OK_200)
		{
			this.content = getErrorPage();
			this.content_length = content.length();
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
		
		if(with_content)
		{
			sb.append(content);
			sb.append(LINEBREAK);
		}
		
		out.print(sb.toString());		
		
		out.flush();
		out.close();
	}
}
