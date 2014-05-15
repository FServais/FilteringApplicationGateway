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
	private String gateway_ip;
	
	/**
	 * Create a link filter object
	 * @param url the absolute URL of the current page
	 * @param link the raw link
	 * @param gateway_ip the ip address/hostname of the gateway server
	 * @param need_encode true if the url must be encoded
	 */
	public LinkFilter(URL url, String link, String gateway_ip, boolean need_encode)
	{
		this.url = url;
		this.link = link;
		this.need_encode = need_encode;
		this.gateway_ip = gateway_ip;
		this.correct_link = buildCorrectLink();
	}
	
	/**
	 * Create a link filter object. The resulting url being encoded.
	 * @param url the absolute URL of the current page
	 * @param link the raw link
	 */
	public LinkFilter(URL url, String link, String gateway_ip)
	{
		this(url, link, gateway_ip, true);
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
	 * Returns an absolute link of a ressource for a given link and its absolute url.
	 * If the flag 'need_encode' is set, then the returned link is encoded.
	 * @return an absolute link, null on error
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
	

		if(link.isEmpty()) // use the current url
			new_url = url.toString();
		else if(link.startsWith("mailto:") || link.startsWith("javascript:")) // mailto: or javascript: no treatment needed
			return link;
		else if(link.startsWith("#")) // link to itself
			new_url = url.toString();
		else if(link.startsWith("http")) // no specific treatment needed
			new_url = link;
		else if(link.startsWith("//")) // must be replaced by the current protocol
			new_url = link.replaceFirst("//", url.getProtocol() + "://");
		else if(link.startsWith("/")) // absolute path (need protocol + host)
			new_url = url.getProtocol() + "://" + url.getHost() + link;
		else if(link.startsWith(".")) // relative path (need prev url)
			new_url = getCompleteURL();
		else
		{
			// regex matching relative path link
			String regex = "^[\\w-]+(?:(?:/.*|\\.(?:[jx]?html?|ph(?:p[345]?|tml)|j(?:s(f|p[xa]?)?)" 
						   + "|do|action|a(?:xd|s[pmh]?x?)|cgi|css|png|jpe?g|gif|pdf))(?:[\\?#].*)?)?$";
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(link);
			
			if(m.matches())
				new_url = getCompleteURL();
			else
			{
				//System.err.println("nomatch : " + link);
				new_url = link;
			}
		}

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
		
		return "http://" + gateway_ip + "/?s=" + final_url;
	}
	
	/**
	 * Builds an complete URL based on the absolute url and the link
	 * The link must be a relative path to a ressource.
	 * @return a complete link
	 */
	private String getCompleteURL()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append(url.getProtocol() + "://" + url.getHost());
		
		int last_slash = url.getPath().lastIndexOf("/");
		
		if(last_slash != -1) // contains at least a slash, then check if we have to remove the file 
		{
			String file_from_path = url.getPath().substring(last_slash + 1),
				   path = url.getPath().substring(0, last_slash);
			
			/**
			 * Cases when the last part of the path (of the absolute url) is not needed
			 * 	 - this last part is empty
			 *   - or it corresponds to a file 
			 */
			if(file_from_path.length() == 0 || 
					(file_from_path.equals("index") || file_from_path.contains("."))) 
			{
				sb.append(path + "/" + link);
				return sb.toString();
			}	
		}
		
		sb.append(url.getPath());
		
		if(!link.startsWith("/") && !url.getPath().endsWith("/"))
			sb.append("/");
		
		sb.append(link);

		return sb.toString();
	}
	
}
