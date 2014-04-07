package datastructures;

import java.util.Hashtable;
import datastructures.CacheEntry;

/**
 * Class that will keep a copy of the visited pages.
 * @author Fabrice Servais
 *
 */
public class Cache<K,V> 
{
	private Hashtable<K,CacheEntry<V>> cache;
	
	
	public Cache()
	{
		cache = new Hashtable<K,CacheEntry<V>>();
	}
	
	
	/**
	 * Add an entry in the hashtable.
	 * @param key Key.
	 * @param value Value.
	 */
	public synchronized void addEntry(K key, V value)
	{
		cache.put(key, new CacheEntry<V>(value));
	}
	
	/**
	 * Remove an entry from the hashtable.
	 * @param key
	 */
	public synchronized void delEntry(K key)
	{
		cache.remove(key);
	}
	
	/**
	 * Return a representation of the cache.
	 * @return Representation of the cache.
	 */
	public String toString()
	{
		return cache.toString();
	}
	
}
