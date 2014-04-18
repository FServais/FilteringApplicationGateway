package http.htmlFilter;

import java.net.URL;
import java.util.Vector;

import datastructures.WordList;
import http.html.HTMLContent;
import http.html.HTMLPage;

public class HTMLPageFilter 
{
	private FilterStatus status;
	private HTMLPage page;
	private String url;
	private Vector<String> restricted_keywords;
	
	public HTMLPageFilter(HTMLPage page, URL url, WordList wordlist)
	{
		this.page = page;
		this.url = url.toString();
		this.restricted_keywords = wordlist.getVector();
		
		determineStatusFromURL(); // checks url
		if(status != FilterStatus.PAGE_REFUSED)
			determineStatusFromPage(); // checks html content
	}
	
	/**
	 * Sets the status variable that specifies if the page is ok, refused or 
	 * if it must be alterated by analyzing the html content of the page
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
		
		for(String keyword : restricted_keywords)
			if(url.contains(keyword))
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
	 * Returns the html code of the filtrated page (or not according to the status)
	 * @return a String containing the html code of the page
	 */
	public String getFilteredPage()
	{
		if(status == FilterStatus.PAGE_REFUSED)
			return getRefuseAccessPage();
		else
		{
			filterLinks();
			if(status == FilterStatus.PAGE_OK)
				return page.toString();
			else 
			{
				filterRestrictedWords();
				return page.toString();
			}
		}
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
	
	private void filterLinks()
	{
		/*Vector<HTMLOpeningTag> vec = page.getOpeningTagElements("a");
		
		for(HTMLOpeningTag tag : vec)
		{
			//String attr_val = tag.getAttributeValue("href");
			// TODO : create the new redirection link
			//tag.setAttributeValue("href", );
		}*/
	}
	
	private void filterRestrictedWords()
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
