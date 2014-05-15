package http;

import http.exceptions.RequestParsingException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class for representing an HTTP request
 * @author Fabrice Servais & Romain Mormont
 */
public class HTTPRequest
{
	private String path = null; 
	private String method = null;
	private String version = null;
	private HashMap<String, String> headers = null;
	
	/**
	 * Initializes the http request object from the socket input stream
	 * @param s an initialized socket 
	 * @throws RequestParsingException if an error occurs while parsing the request
	 * @throws IOException if an error occurs while reading from the socket
	 */
	public HTTPRequest(Socket s) throws IOException, RequestParsingException
	{
		init();
		getRequest(s);
		//debug();
	}

	/**
	 * Get the request from the socket input stream
	 * @param socket an initialized socket 
	 * @throws RequestParsingException if an error occurs while parsing the request
	 * @throws IOException if an error occurs while reading from the socket
	 */
	private void getRequest(Socket socket) throws IOException, RequestParsingException 
	{
		// reads the HTTP request
		InputStreamReader isr = new InputStreamReader(socket.getInputStream());
		BufferedReader br = new BufferedReader(isr);
		
		// Read the request
		String current = ""; // contains the current char
		boolean request_line_read = false;
		
		// reads the headers
		while((current = br.readLine()) != null && !current.equals(""))
		{ 
			if(!request_line_read)
			{
				parseRequestLine(current);
				request_line_read = true;
			}
			else
				parseHeader(current);
		}
	}

	/**
	 * Initializes the data members of the class
	 */
	private void init()
	{
		path = new String();
		method = new String();
		version = new String();
		this.headers = new HashMap<String, String>();
	}
	
	// TODO : remove this
	private void debug()
	{
		System.out.println("\n# DEBUG # HTTPRequest");
		System.out.println("# method : " + method);
		System.out.println("# path : " + path);
		System.out.println("# version : " + version);
	}
	
	/**
	 * Parses a header field and value and adds it to the headers map
	 * @param header a String containing a header line
	 */
	private void parseHeader(String header) 
	{
		// regex that splits a http header into two groups
		String regex = "^([\\w-]+)\\s*:\\s*(.*)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(header);
		
		if(!m.find())
			return; 
	
		headers.put(m.group(1), m.group(2));
	}

	/**
	 * Parses the request line of the http request (1st line)
	 * @param line a String containing the request line
	 * @throws RequestParsingException if an error happens during parsing
	 */
	private void parseRequestLine(String line) throws RequestParsingException
	{
		// regex that splits the request line into three groups
		String regex = "^([A-Z]{3,7})\\s+(.+)\\s+(.+)$";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(line);
		
		if(!m.matches())
			throw new RequestParsingException("Failure while parsing request line");
		
		method = m.group(1);
		path = m.group(2);
		version = m.group(3);
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
	 * Returns the version of the http request
	 * @return a String containing the version
	 */
	public String getVersion()
	{
		return version;
	}
	
	/**
	 * Returns the value a header of given name
	 * @return a String containing the value, null if the request didn't contain this header field
	 */
	public String getHeaderValue(String header_name)
	{
		return headers.containsKey(header_name) ? headers.get(header_name) : null;
	}
}
