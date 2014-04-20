package datastructures;

import java.util.Calendar;
import java.util.Date;

/**
 * Class that represent an entry in the cache.
 * @author Fabs
 *
 */
public class CacheEntry<K> 
{
	private K data;
	private Date timeout;
	
	public CacheEntry(K data)
	{
		this.data = data;
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, 1); // 24h before need refeshing
		this.timeout = cal.getTime();
	}
	
	public CacheEntry(K data, Date timeout)
	{
		this.data = data;
		this.timeout = timeout;
	}
	
	
	/**
	 * Get the timeout.
	 * @return The timeout.
	 */
	public Date getTimeout()
	{
		return timeout;
	}
	
	/**
	 * Get the data.
	 * @return The data contained in the entry.
	 */
	public K getData()
	{
		return data;
	}
	
	/**
	 * Method that check if the entry need to be refreshed
	 * @return True if still valid.
	 */
	public boolean isValid()
	{
		if(Calendar.getInstance().getTime().compareTo(timeout) <= 0)
			return true;
		else
			return false;
	}
	
	/**
	 * Return a representation of the entry.
	 * @return Representation of the entry.
	 */
	public String toString()
	{
		return data.toString();
	}
	
	
}
