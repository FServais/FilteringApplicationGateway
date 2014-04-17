package http.htmlFilter;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import datastructures.WordList;
import http.html.HTMLContent;
import http.html.HTMLPage;

public class HTMLPageFilter 
{
	private FilterStatus status;
	private WordList wordlist;
	private HTMLPage page;
	private String url;
	
	public HTMLPageFilter(HTMLPage page, URL url, WordList wordlist)
	{
		this.page = page;
		this.wordlist = wordlist;
		this.url = url.toString();
		
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
		
		// counts occurences of each word in each content elements
		HashMap<String, Integer> counting_map = new HashMap<String, Integer>();
		int max_count;
		
		// run through html content elements
		for(HTMLContent content : contents)
		{
			// TODO Splits word around other types of char than space
			String[] splitted_content = content.getWordsArray();
			
			// run through words of the current content element
			for(String s : splitted_content)
				if(wordlist.contains(s))
				{
					if(!counting_map.containsKey(s)) // first occurence of a word
						counting_map.put(s, 1);
					else
					{
						int curr_count = counting_map.get(s) + 1;
						if(curr_count >= 4)
						{
							status = FilterStatus.PAGE_REFUSED;
							return;
						}
						
						counting_map.put(s, curr_count);
					}
				}
		}
		
		// checks status criteria
		if(counting_map.size() >= 3)
			status = FilterStatus.PAGE_REFUSED;
		else if(counting_map.size() == 0)
			status = FilterStatus.PAGE_OK;
		else 
			status = FilterStatus.PAGE_NEED_ALTERATION;
	}
	
	/**
	 * Sets the status variable that specifies if the page is ok, refused or 
	 * if it must be alterated by analyzing the url of the page
	 */
	private void determineStatusFromURL()
	{
		Vector<String> restricted_keywords = wordlist.getVector();
		
		for(String keyword : restricted_keywords)
			if(url.contains(keyword))
			{	
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
	
	public void filterPage()
	{
		
	}
}
