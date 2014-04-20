package datastructures;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import datastructures.CacheEntry;

/**
 * Class that will keep a copy of the visited pages.
 * @author Fabrice Servais
 *
 */
public class Cache<K,V> 
{
	private ConcurrentHashMap<K,CacheEntry<V>> cache;
	
	
	public Cache()
	{
		cache = new ConcurrentHashMap<K,CacheEntry<V>>();
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
	 * Check if a key is contained in the cache.
	 * @param key Key of the element.
	 * @return True if key is contained.
	 */
	public synchronized boolean isContained(K key)
	{
		return cache.containsKey(key);
	}
	
	/**
	 * Get the data of an entry (given by its key).
	 * @param key Key of the entry.
	 * @return Data associated to the key.
	 */
	public synchronized V getData(K key)
	{
		return cache.get(key).getData();
	}
	
	/**
	 * Get the entry of a key.
	 * @param key Key of the entry.
	 * @return Entry associated to the key.
	 */
	public synchronized CacheEntry<V> getEntry(K key)
	{
		return cache.get(key);
	}
	
	/**
	 * Return a representation of the cache.
	 * @return Representation of the cache.
	 */
	public String toString()
	{
		String toReturn = new String();
		for(Entry<K,CacheEntry<V>> e : cache.entrySet())
		{
			String line = "---> KEY = " + e.getKey() + "\n" + "---> VALUE = " + e.getValue() + "\n"; 
			toReturn += line;
		}
		return toReturn;
		//return cache.toString();
	}
	
}
