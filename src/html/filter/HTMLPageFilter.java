package html.filter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;
import datastructures.WordList;
import html.HTMLContent;
import html.HTMLOpeningTag;
import html.HTMLPage;

/**
 * A class for filtering an html page
 * @author Fabrice Servais & Romain Mormont
 */
public class HTMLPageFilter 
{
	private FilterStatus status;
	private HTMLPage page;
	private URL url;
	private Vector<String> restricted_keywords;
	
	public HTMLPageFilter(HTMLPage page, URL url, WordList wordlist)
	{
		this.page = page;
		this.url = url;
		this.restricted_keywords = wordlist.getVector();
		
		determineStatusFromURL(); // checks url
		if(status != FilterStatus.PAGE_REFUSED)
			determineStatusFromPage(); // checks html content
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
		{
			sb.append(content.toString());
		}
		
		String pageContent = sb.toString();
		int keyword_in_page = 0;
		
		// count occurrences of each restricted keyword in the page
		for(int i = 0; i < restricted_keywords.size(); i++)
		{
			String keyword = restricted_keywords.get(i);
			
			int count = countOccurrence(pageContent, keyword);
			
			if(count > 0)
				keyword_in_page++;
			
			if(count >= 4 || keyword_in_page >= 3) // checks "refused" critera
			{	
				if(count >= 4)
					System.out.println("Keyword in page : '" + keyword + "' (4 occurences)");
				if(keyword_in_page >= 3)
					System.out.println("Third keyword in the page");
				
				status = FilterStatus.PAGE_REFUSED;
				return;
			}
		}
		
		// checks status criteria
		if(keyword_in_page == 0)
			status = FilterStatus.PAGE_OK;
		else 
			status = FilterStatus.PAGE_NEED_ALTERATION;
		
		// DEBUG
		String s;
		if(status == FilterStatus.PAGE_NEED_ALTERATION)
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
				status = FilterStatus.PAGE_REFUSED;
				return;
			}
		
		status = FilterStatus.PAGE_OK;
	}
	
	/**
	 * Returns the status of the page :
	 *   - PAGE_OK
	 *   - PAGE_NEED_ALTERATION
	 *   - PAGE_REFUSED
	 * @return the status of the page
	 */
	public FilterStatus getStatus()
	{
		return status;
	}
	
	/**
	 * Returns the html code of the filtered page (or not according to the status)
	 * @return a String containing the html code of the page
	 */
	public String getFilteredPage()
	{
		if(status == FilterStatus.PAGE_REFUSED){
			return getRefuseAccessPage();
		}
		else
		{
			System.out.println("Filter links...");
			filterLinks();
			
			if(status == FilterStatus.PAGE_OK)
				return page.toString();
			else 
			{
				System.out.println("Filter keywords...");
				filterRestrictedWords();
				return page.toString();
			}
		}
	}
	
	/**
	 * Filters the "href" or "src" attribute of "a", "frame" and "link" tags of a page
	 */
	private void filterLinks()
	{
		//System.out.println(page.toString());
		//page.print();
		
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
		
		//System.out.println("Base : " + base_url.toString());
		
		// filters tags
		filterAttribute("a", "href", base_url, true);
		filterAttribute("link", "href", base_url, false);
		filterAttribute("frame", "src", base_url, false);
		filterAttribute("img", "src", base_url, false);
		filterAttribute("script","src", base_url, false);
		
		//System.out.println("out");
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
		
		int i = 0;
		// run through the "tag_name" tags of the page
		for(HTMLOpeningTag tag : tags)
		{
			String attribute_value = tag.getAttributeValue(attribute_name);
			
			if(attribute_value != null) // checks if attribute is defined
			{
				LinkFilter lf = null;
				
				if(encode)
				{	
					lf = new LinkFilter(base_url, attribute_value);
					tag.setAttributeValue(attribute_name, "http://localhost:8005/?s=" 
																+ lf.getFilteredLink());
				}
				else
				{
					lf = new LinkFilter(base_url, attribute_value, false);
					tag.setAttributeValue(attribute_name, lf.getFilteredLink());
				}
				
				/*System.out.println("\n[" + tag_name + "] n°" + (++i));
				System.out.println("  URL  :  " + url.toString());
				System.out.println("  Link : " + attribute_value);
				System.out.println("  new  :  " + lf.getFilteredLink());
				*/
				
				 
				//System.out.println("set ok!!\n");
			}
			else
				System.err.println("\"" + tag_name + "\"" + " with no \"" + attribute_name + "\"");
		}	
	}

	/*
	private void filterImg()
	{
		for(HTMLOpeningTag img_tag : page.getOpeningTagElements("img"))
		{
			String srcValue = img_tag.getAttributeValue("src");
			if(srcValue == null)
				continue;
			
			try
			{
				new URL(srcValue);
				// If MalformedURLException not caught -> Absolute link -> OK
			}
			catch(MalformedURLException e)
			{
				// srcValue is a relative link OR begin with "www"
				if(!srcValue.startsWith("www"))
				{
					//System.out.println("IMAGE : getProtocol : " + url.getProtocol() + " --- getHost : " + url.getHost() + " --- getPath : " + url.getPath() + " --- cond : " + ((url.getPath().endsWith("/") == srcValue.startsWith("/")) && url.getPath().endsWith("/") ? "/" : "") + " --- srcValue : " + srcValue);
					img_tag.setAttributeValue("src", url.getProtocol() + "://" + url.getHost() + url.getPath() + (url.getPath().endsWith("/") || srcValue.startsWith("/") ? "" : "/") + srcValue);
				}
			}
		}
	}
	*/

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
	 * Filters the restricted keywords on the html page
	 */
	public void filterRestrictedWords()
	{
		Vector<HTMLContent> vec = page.getContentElements(false);
		
		for(HTMLContent text : vec) // replace keywords
			for(String word : restricted_keywords)
				text.replaceWord(word, '*');
	}
	
	
	private String getRefuseAccessPage() 
	{
		return "pas bon";
	}

}
