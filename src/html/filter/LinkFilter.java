package html.filter;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A class for filtering links
 * @author Romain Mormont
 */
public class LinkFilter 
{
	private URL url;
	private String link;
	private boolean need_encode = false;
	private String correct_link;
	
	/**
	 * Create a link filter object
	 * @param url the absolute URL of the current page
	 * @param link the raw link
	 * @param need_encode true if the url must be encoded
	 */
	public LinkFilter(URL url, String link, boolean need_encode)
	{
		this.url = url;
		this.link = link;
		this.need_encode = need_encode;
		this.correct_link = buildCorrectLink();
	}
	
	/**
	 * Create a link filter object. The resulting url being encoded.
	 * @param url the absolute URL of the current page
	 * @param link the raw link
	 */
	public LinkFilter(URL url, String link)
	{
		this(url, link, true);
	}
	
	/**
	 * Returns the filtered link
	 * @return a String containing the filtered link
	 */
	public String getFilteredLink()
	{
		return correct_link;
	}
	
	/**
	 * Returns the absolute link
	 * @return the absolute link
	 */
	private String buildCorrectLink()
	{
		/**
		 * Optimization : some links are straightforward so don't need the possible
		 * 	overhead of a regex pattern matching. These are the following :
		 * 	- starts with http (http or https)
		 *  - starts with "/" or "." (./ ../)
		 *  - starts with "?"
		 *  - starts with "#"
		 *  - starts with "javascript:" or "mailto:"
		 *  - starts with "//"
		 *  - empty string
		 * The cases that must be checked are all the ones starting with another char sequence
		 */
		String new_url = null;
		
		//System.out.println("Link : " + link);
		//System.out.print("[CORR] ");
		if(link.isEmpty()) // use the current url
		{
			//System.out.print("empty : ");
			new_url = url.toString();
		}
		else if(link.startsWith("mailto:") 
				|| link.startsWith("javascript:")) // mailto: or javascript: no treatment needed
		{
			//System.out.print("mailto|javascript : " + link);
			return link;
		}
		else if(link.startsWith("#")) // link to itself
		{
			//System.out.print("# : ");
			new_url = url.toString();
		}
		else if(link.startsWith("http")) // no specific treatment needed
		{
			//System.out.print("http : ");
			new_url = link;
		}
		else if(link.startsWith("//"))
		{
			new_url = link.replaceFirst("//", url.getProtocol() + "://");
		}
		else if(link.startsWith("/")) // absolute path (need protocol + host)
		{
			new_url = url.getProtocol() + "://" + url.getHost() + link;
			//System.out.print("absolute[b] : ");
		}
		else if(link.startsWith(".")) // relative path (need prev url)
		{
			System.out.print("relative[a] : ");
			new_url = getCompleteURL();
		}
		else
		{
			//System.out.print("other");
			
			// regex matching relative path link
			String regex = "^[\\w-]+(?:(?:/.*|\\.(?:[jx]?html?|ph(?:p[345]?|tml)|j(?:s(f|p[xa]?)?)" 
						   + "|do|action|a(?:xd|s[pmh]?x?)|cgi|css|png|jpe?g|gif|pdf))(?:[\\?#].*)?)?$";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(link);
			
			if(m.matches())
			{
				//System.out.print("-match : ");
				new_url = getCompleteURL();
			}
			else
			{
				System.err.print("-nomatch : ");
				System.err.flush();
				new_url = link;
			}
		}
		
		//System.out.println(new_url);

		if(!need_encode) // no need for encoding
			return new_url;
		
		// replace &amp; in the url
		new_url = new_url.replaceAll("&amp;", "&");
		
		String final_url = null;
		
		// percent encoding of the url
		try 
		{
			final_url = URLEncoder.encode(new_url, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		
		return final_url;
	}
	
	/**
	 * Returns the complete URL from for a relative URL link
	 * @return the complete link
	 */
	private String getCompleteURL()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(url.getProtocol() + "://" + url.getHost());
		
		//System.out.println("real path : " + url.getPath());
		//System.out.println("real link : " + link);
		
		int last_slash = url.getPath().lastIndexOf("/");
		
		if(last_slash != -1) // contains at least a slash, then check if we have to remove the file 
		{
			String file_from_path = url.getPath().substring(last_slash + 1),
				   path = url.getPath().substring(0, last_slash);
			
			//System.out.println("split path : " + path);
			//System.out.println("split file : " + file_from_path);
			
			if(file_from_path.length() == 0 || 
					(file_from_path.equals("index") || file_from_path.contains("."))) 
			{
				sb.append(path + "/" + link);
				//System.out.println("Final : " + sb.toString());
				return sb.toString();
			}	
		}
		
		sb.append(url.getPath());
		
		if(!link.startsWith("/") && !url.getPath().endsWith("/"))
			sb.append("/");
		
		sb.append(link);
		//System.out.println("Final : " + sb.toString());
		return sb.toString();
	}
	
}
