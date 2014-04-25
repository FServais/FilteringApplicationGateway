package http;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class for decoding an HTTP request from the gateway client
 * @author Romain Mormont
 *
 */
public class HTTPRequest
{
	private String path; 
	private String method;
	private String content;
	private HashMap<String, String> headers;
	
	public HTTPRequest(String http_request)
	{
		this.headers = new HashMap<String, String>();

		parseRequest(http_request);
		
		System.out.println("\n# DEBUG # HTTPRequest");
		System.out.println("# method : " + method);
		System.out.println("# path : " + path);
		System.out.println("# content : " + content + "\n");
	}
	
	/**
	 * Parses the request and sets the attributes of the class with the correct values
	 * @param request a String containing the whole request
	 */
	private void parseRequest(String request)
	{
		// split http request around the newline character
		String[] splitted_request = request.split("\\r?\\n|\\r");
		
		parseRequestLine(splitted_request[0]);
		
		for(int i = 1; i < splitted_request.length; i++)
		{
			if(splitted_request[i].isEmpty()) // content reached
			{
				parseContent(splitted_request, i + 1);
				break;
			}
			
			parseHeader(splitted_request[i]);
		}
	}
	
	/**
	 * Parses a header field and value and adds it to the headers map
	 * @param header a String containing the header
	 */
	private void parseHeader(String header) 
	{
		// regex that splits a http header into two groups
		String regex = "^([\\w\\-]+)\\s*:\\s*(.*)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(header);
		
		if(!m.find())
			return; 
	
		headers.put(m.group(1), m.group(2));
	}

	/**
	 * Parses the content part of the request
	 * @param splitted_request the array containing every line of the request 
	 * @param contentFirstIndex the index (in the splitted_request array) of the content first line
	 */
	private void parseContent(String[] splitted_request, int contentFirstIndex)
	{
		StringBuilder sb = new StringBuilder();
		
		for(int i = contentFirstIndex; i < splitted_request.length; i++)
			sb.append(splitted_request[i]);
		
		content = sb.toString();
	}

	/**
	 * Parses the request line of the http request (1st line)
	 * @param line a String containing the request line
	 */
	private void parseRequestLine(String line)
	{
		// regex that splits the request line into three groups
		String regex = "^([A-Z]{3,7})\\s+([\\w\\.\\\\/\\+\\-\\?&=#%~]+)\\s+([\\w/\\.\\s]+)$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(line);
		
		if(!m.find())
			return; 
		
		method = m.group(1);
		path = m.group(2);
	}
	
	/**
	 * Returns the path of the request
	 * @return a String containing the path
	 */
	public String getPath()
	{	
		return path;
	}
	
	/**
	 * Returns the method of the http request
	 * @return a String containing the method
	 */
	public String getMethod()
	{
		return method;
	}
	
	/**
	 * Returns the content of the http request
	 * @return a String containing the content
	 */
	public String getContent()
	{
		return content;
	}
	
	/**
	 * Returns the map containing the headers
	 * @return a structure mapping header fields and values
	 */
	public HashMap<String, String> getHeaders()
	{
		return headers;
	}
}
