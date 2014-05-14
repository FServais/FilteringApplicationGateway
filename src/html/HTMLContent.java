package html;

/**
 * A class for representing html content (everything which is not a tag)
 * @author Romain Mormont
 */
public class HTMLContent extends HTMLElement {
	String data; // Content
	
	/**
	 * Constructs a HTMLContent object with its content
	 * @param data a String containing the content
	 */
	public HTMLContent(String data)
	{
		this.data = data;
	}
	
	/**
	 * Converts the HTMLContent object to a String
	 * @return a String containing the content
	 */
	public String toString()
	{
		return data;
	}
	
	/**
	 * Returns an array containing the words contained in the HTMLContent object
	 * In ordre to get the words, the content is split around any word boundaries (e.g. regex class '\b')
	 * @return a String array containing the words of the content 
	 */
	public String[] getWordsArray()
	{
		return data.split("\\b+");
	}
	
	/**
	 * Replaces every character of every occurrence of the given substring
	 * in the data of the HTMLContent object by the character rep_char
	 * @param substr a String containing the word to replace
	 * @param rep_char a char for replacing the substring
	 */
	public void replaceWord(String substr, char rep_char)
	{
		String rep = getUnicharString(rep_char, substr.length());
		
		data = data.replaceAll("(?i)" + substr, rep);
	}
	
	/**
	 * Returns a String composed of 'count' occurrences of c
	 * @param c the char to repeat
	 * @param count the number of occurrences of c
	 * @return a String containing 'count' occurences of c
	 */
	private String getUnicharString(char c, int count)
	{
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < count; i++)
			sb.append(c);
		
		return sb.toString();
	}
	
	/**
	 * Makes a deep copy of the HTMLContent object
	 * @return a copy of the HTMLContent object
	 */
	public Object clone() throws CloneNotSupportedException
	{
		return super.clone();
	}
}
