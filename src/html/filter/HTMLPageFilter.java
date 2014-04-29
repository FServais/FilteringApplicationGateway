package html.filter;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import datastructures.WordList;
import html.HTMLContent;
import html.HTMLOpeningTag;
import html.HTMLPage;

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
	 * Returns the html code of the filtrated page (or not according to the status)
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
			System.out.println("Filter images...");
			filterImg();
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
	 * Replace all the 'href' attributes of 'a' tags that are relatives to absolutes
	 */
	private void filterLinks()
	{
		int i = 0;
		//page.print();
		
		Vector<HTMLOpeningTag> tags = new Vector<HTMLOpeningTag>();
		
		tags.addAll(page.getOpeningTagElements("a"));
		tags.addAll(page.getOpeningTagElements("link"));
		
		for(HTMLOpeningTag tag : tags)
		{
			i++;
			System.out.print(tag.getName() + " " + i + "\n");
			String href_value = tag.getAttributeValue("href");
			if(href_value == null)
			{
				System.err.println("<a|link> with no href");
				continue;
			}
			String ur = getCorrectLink(href_value);
			//System.out.println("From : " + href_value);
			//href_value.System.out.println("To   : " + ur);
			tag.setAttributeValue("href", ur);

		}
	}
	
	private String getCorrectLink(String link)
	{
		String regex_rel = "^\\s*(\\.{0,2}/)?((?:[\\w~-]*/?)*" + 
				   "(?:[\\w]+\\.(?:[jx]?html?|ph(?:p[345]?|tml)" + 
				   "|j(?:s(f|p[xa]?)?|son)|do|action|a(?:xd|s[pmh]?x?)|py|xml|css))?" +
				   "(?:\\?(?:\\w+=[-\\w%\\.]+)(?:&(?:\\w+=[-\\w%\\.]+))*)?)?$";

		Pattern p = Pattern.compile(regex_rel);
		Matcher m = p.matcher(link);
		
		String new_url;
		
		if(m.matches())
		{
			//System.out.print("match ");
			String link_path = m.group(2),
				   pref_path = m.group(1);
			
			int port = url.getPort();
			String pref = url.getProtocol() + "://" + url.getHost() + (port == -1 ? "" : ":" + port);
			
			if(pref_path == null)
			{
				// System.out.println("1");
				new_url = pref + "/" + link_path;
			} 
			else if(!pref_path.equals("/"))
			{
				// System.out.println("2");
				new_url = pref + url.getPath() + link_path;
			}
			else
			{
				// System.out.println("3");
				new_url = pref + link;
			}
			
			// System.out.println("From : " + link);
			// System.out.println("To   : " + "http://localhost:8005/?s=" + pref + m.group(2) + "\n");
		}
		else
		{
			//System.out.println("no match");
			new_url = link;
			// System.out.println("From : " + link);
			// System.out.println("To   : " + link + "\n");
		}
		
		String final_url = "";
		
		try 
		{
			final_url = URLEncoder.encode(new_url, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
	
		return "http://localhost:8005/?s=" + final_url;
	}
	
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
