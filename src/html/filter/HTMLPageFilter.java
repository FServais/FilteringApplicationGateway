package html.filter;

import java.net.MalformedURLException;
import java.net.SocketAddress;
import java.net.URL;
import java.util.Vector;
import datastructures.WordList;
import html.HTMLContent;
import html.HTMLOpeningTag;
import html.HTMLPage;

/**
 * A class for filtering links and keywords of an html page 
 * @author Fabrice Servais & Romain Mormont
 */
public class HTMLPageFilter 
{
	private PageGatewayStatus status; // status of the page
	private boolean status_determined; // true if the status was already determined, false otherwise
	private HTMLPage page;
	private URL url; // url of the page
	private String gateway_ip; // ip/hostname of the gateway server
	private Vector<String> restricted_keywords; // contains the restricted keywords

	
	/**
	 * Constructs an HTMLPageFilter object.
	 * @param page an HTMLPage object representing the page that must be filtered
	 * @param url the url of the page
	 * @param wordlist a Wordlist object containing the restricted keywords
	 */
	public HTMLPageFilter(HTMLPage page, URL url, WordList wordlist, String gateway_ip)
	{
		this.page = page;
		this.url = url;
		this.restricted_keywords = wordlist.getVector();
		this.status_determined = false;
		this.gateway_ip = gateway_ip;
	}
	
	/**
	 * Determine the status of the page 
	 */
	private void determineStatus()
	{
		determineStatusFromURL(); // checks url
		if(status != PageGatewayStatus.PAGE_REFUSED)
			determineStatusFromPage(); // checks html content
		status_determined = true;
	}
	
	/**
	 * Sets the status variable that specifies if the page is ok, refused or 
	 * if it must be altered by analyzing the html content of the page
	 */
	private void determineStatusFromPage()
	{
		// get all content elements of the page
		Vector<HTMLContent> contents = page.getContentElements();
		StringBuilder sb = new StringBuilder();
		
		// build a big string containing only the contents of the page
		for(HTMLContent content : contents)
			sb.append(content.toString());
		
		String pageContent = sb.toString();
		int keyword_in_page = 0;
		
		// count occurrences of each restricted keyword in the page
		for(int i = 0; i < restricted_keywords.size(); i++)
		{
			String keyword = restricted_keywords.get(i);
			
			int count = countOccurrence(pageContent, keyword);
			System.out.println("//===================");
			System.out.println("|| Words : " + keyword);
			System.out.println("|| Count : " + count);
			if(count > 0)
				keyword_in_page++;
			
			if(count >= 4 || keyword_in_page >= 3) // checks "refused" critera
			{	
				if(count >= 4)
					System.out.println("Keyword in page : '" + keyword + "' (4 occurences)");
				if(keyword_in_page >= 3)
					System.out.println("Third keyword in the page");
				
				status = PageGatewayStatus.PAGE_REFUSED;
				return;
			}
		}
		
		// checks status criteria
		if(keyword_in_page == 0)
			status = PageGatewayStatus.PAGE_OK;
		else 
			status = PageGatewayStatus.PAGE_NEED_ALTERATION;
		
		// TODO : remove DEBUG
		String s;
		if(status == PageGatewayStatus.PAGE_NEED_ALTERATION)
			s = "alter";
		else
			s = "ok";
		
		System.out.println(keyword_in_page + " keyword(s) in the page (" + s + ")");
		// DEBUG
	}
	
	/**
	 * Sets the status variable that specifies if the page is ok, refused or 
	 * if it must be alterated by analyzing the url of the page
	 */
	private void determineStatusFromURL()
	{
		String url_string = url.toString();
		for(String keyword : restricted_keywords)
			if(url_string.contains(keyword))
			{	
				System.out.println("Keyword in url : '" + keyword + "'");
				status = PageGatewayStatus.PAGE_REFUSED;
				return;
			}
		
		status = PageGatewayStatus.PAGE_OK;
	}
	
	/**
	 * Returns the number of occurences of a substring in a string
	 * @param str a String in which the occurences will be searched
	 * @param substr a String containing the substring
	 * @return the number of occurrence of the substring in the string
	 */
	private int countOccurrence(String str, String substr)
	{
		int strlen = str.length(),
			substrlen = substr.length();
		return (strlen - str.replace(substr, "").length()) / substrlen;
	}
	
	/**
	 * Returns the status of the page :
	 *   - PAGE_OK
	 *   - PAGE_NEED_ALTERATION
	 *   - PAGE_REFUSED
	 * @return the status of the page
	 */
	public PageGatewayStatus getStatus()
	{
		if(!status_determined)
			determineStatus();
		return status;
	}
	
	/**
	 * Returns the html code of the filtered page (or not according to the status)
	 * @return a String containing the html code of the page
	 */
	public String getFilteredPage(SocketAddress ip)
	{
		if(!status_determined)
			determineStatus();
		
		if(status == PageGatewayStatus.PAGE_REFUSED)
			return getRefuseAccessPage(ip);
		else
		{			
			if(status == PageGatewayStatus.PAGE_OK)
				return page.toString();
			else 
			{
				filterRestrictedWords();
				return page.toString();
			}
		}
	}
	
	/**
	 * Filters the restricted keywords on the html page
	 */
	public void filterRestrictedWords()
	{
		Vector<HTMLContent> vec = page.getContentElements(false);
		
		for(HTMLContent text : vec) // replace keywords
			for(String word : restricted_keywords)
				text.replaceWord(word, '*');
	}
	
	/**
	 * Filters the "href" or "src" attribute of "a", "frame", "scripts" and "link" tags of a page
	 */
	public void filterLinks()
	{
		// checks if the links must be filtered
		if(page.linksFiltered())
		{
			System.err.println("This page was already filtered");
			return;
		}
		
		// find the absolute path of the page
		URL base_url = null;
		
		Vector<HTMLOpeningTag> base = new Vector<HTMLOpeningTag>();
		base.addAll(page.getOpeningTagElements("base"));
		
		try
		{
			if(!base.isEmpty()) // base tag on the page
				base_url = new URL(base.firstElement().getAttributeValue("href"));
			else // no base tag on the page
				base_url = new URL(url.getProtocol() + "://" + url.getHost() + url.getPath());
		}
		catch(MalformedURLException e)
		{
			System.err.println(e.getMessage());
		}
		
		// filters tags
		filterAttribute("a", "href", base_url, true);
		filterAttribute("link", "href", base_url, false);
		filterAttribute("frame", "src", base_url, false);
		filterAttribute("img", "src", base_url, false);
		filterAttribute("script","src", base_url, false);
		
		page.setLinkFiltered(true);
	}
	
	
	/**
	 * Filters the attribute value of every tag having the given name in a html page.
	 * These values are expected to be links. If the tag_name is "a" or "frame", then the 
	 * links are encoded and are set as the "s" argument of an URL to the gateway platform
	 * @param tag_name a String containing the name of the tag ("a", "link", "img",...)
	 * @param attribute_name a String containing the name of the tag ("src", "href",...)
	 * @param base_url the absolute path to which must be appended any relative link
	 * @param encode true if the links must be encoded (and put in the string
	 * 				 "http://gateway_host:8005/?s=..."),	false otherwise
	 */
	private void filterAttribute(String tag_name, String attribute_name, URL base_url, boolean encode)
	{
		Vector<HTMLOpeningTag> tags = new Vector<HTMLOpeningTag>();
		
		tags.addAll(page.getOpeningTagElements(tag_name));
		
		//int i = 0;
		// run through the "tag_name" tags of the page
		for(HTMLOpeningTag tag : tags)
		{
			String attribute_value = tag.getAttributeValue(attribute_name);
			
			if(attribute_value != null) // checks if attribute is defined
			{
				LinkFilter lf = null;
				
				if(encode)
				{	
					lf = new LinkFilter(base_url, attribute_value, gateway_ip);
					tag.setAttributeValue(attribute_name, lf.getFilteredLink());
				}
				else
				{
					lf = new LinkFilter(base_url, attribute_value, gateway_ip, false);
					tag.setAttributeValue(attribute_name, lf.getFilteredLink());
				}

				/** TODO : remove this */
				
				/*System.out.println("\n[" + tag_name + "] n�" + (++i));
				System.out.println("  URL  :  " + url.toString());
				System.out.println("  Link : " + attribute_value);
				System.out.println("  new  :  " + lf.getFilteredLink());
				*/
				
				//System.out.println("set ok!!\n");
			}
		}	
	}
	
	private String getRefuseAccessPage(SocketAddress ip) 
	{
		return "<!DOCTYPE html><html><head><style type=\"text/css\">" +
				"body{ background-color: #F7F7F7; font-family:\"Trebuchet MS\", Arial, Verdana, sans-serif; }"
					+ "#error_head{color:rgba(214,60,54,1); text-align: center; margin-left:auto; margin-right:auto;"
						+ "border: 1px solid rgba(214,60,54,0.6); font-size: 14px; margin-top: 15%; width:35%;}"
					+ "p{margin-top: 15px;}"
					+ "#error_message{margin-top:2%; margin-left:auto; margin-right:auto; width:40%; border-bottom: 1px solid black;}"

					+ "</style><meta charset=\"UTF-8\"/><title>GATEWAY | Access denied</title></head>"
						+ "<body>"
							+ "<div id=\"error_head\"><h3>Gateway : </h3>"
								+ "<h1>ACCESS DENIED</h1>"
							+ "</div>"
							+ "<div id=\"error_message\">"
							+ ipMessage(ip)
							+ errorMessage()
							+ "</div>"
							+ "<p style=\"font-size:small;text-align:center;\">Please contact the network administrator if you think that this webpage shouldn't be blocked.</p>"
						+ "</body>"
						+ "</html>";
	}
	
	private String ipMessage(SocketAddress ip)
	{
		if(ip == null)
			return "";
		
		return "<p style=\"font-weight:bold;\">Your IP adress is : " + ip.toString() + "</p>";
	}
	
	private String errorMessage()
	{
		return "<p>The access to this resource has been <em>blocked</em> by the network gateway because either : </p>"
				+ "<ul><li>The address contains a restricted keyword</li><li>The webpage contains at least 3 distinct " +
				"restricted keywords</li><li>The webpage contains at least 4 instances of the same restricted keyword</li></ul>";
	}
}
