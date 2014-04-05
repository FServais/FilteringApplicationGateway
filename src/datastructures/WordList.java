package datastructures;
/**
 * 
 */
import java.util.Hashtable;
import java.util.Arrays;
/**
 * A class for storing words guaranteeing an almost constant access for :
 * <ul>
 * 	<li>contains()</li>
 * 	<li>insert()</li>
 * 	<li>remove()</li>
 * </ul>
 *
 * This class is synchronized.
 * 
 * @author Romain Mormont
 */
public class WordList 
{
	private Hashtable<String, String> stringTable;
	
	/**
	 * Construct an empty word list
	 */
	public WordList()
	{
		stringTable = new Hashtable<String, String>();
	}
	
	/**
	 * Inserts a new word in a WordList object.
	 * Does nothing if the word in already in the list.
	 * @param word the word to insert
	 */
	public synchronized void insert(String word)
	{
		stringTable.put(word, word);
	}
	
	/**
	 * Tests whether a word is in the list or not
	 * @param word the word searched in the list
	 * @return true if the list contains the word, false otherwise
	 */
	public synchronized boolean contains(String word)
	{
		return stringTable.containsKey(word);
	}
	
	/**
	 * Removes a word from the list.
	 * Does nothing if the word is not in the list.
	 * @param word the word to remove
	 */
	public synchronized void remove(String word)
	{
		stringTable.remove(word);
	}

	/**
	 * Returns a string containing each word of the list separated by a whitespaces
	 * @return a String containing the list
	 */
	public synchronized String toString()
	{
		return this.toString(" ");
	}

	/**
	 * Returns a string containing each word of the list separted by a character
	 * @param sep a String for separating each word
	 * @return a String containing the list
	 */
	public synchronized String toString(String sep)
	{
		return this.toString(sep, true);
	}

	/**
	 * Returns a string containing each word of the list separted by a character
	 * @param sep a String for separating each word
	 * @param sort true if the list must be sorted, false otherwise
	 * @return a String containing the list
	 */
	public synchronized String toString(String sep, boolean sort)
	{
		Object[] wordArray = stringTable.values().toArray();

		if(sort)
			Arrays.sort(wordArray);
		
		if(wordArray.length == 0)
			return "";

		StringBuilder sb = new StringBuilder((String) wordArray[0]);
		
		for(int i = 1; i < wordArray.length; i++)
			sb.append(sep + (String) wordArray[i]);

		return sb.toString();
	}
	
	/**
	 * Returns the number of words in the list
	 * @return the number of words in the list
	 */
	public synchronized int size()
	{
		return stringTable.size();
	}
}



