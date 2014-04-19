package http.html;

/**
 * @author Romain Mormont
 * A class for representing html content (everything which is not a tag)
 */
public class HTMLContent implements HTMLElement {
	String data; // Content
	
	/**
	 * Constructs a HTMLContent object with the
	 * @param data a String containing the content
	 */
	public HTMLContent(String data)
	{
		this.data = data;
	}
	
	/**
	 * Returns the data as a string
	 * @return a String containing the content
	 */
	public String toString()
	{
		return data;
	}
	
	/**
	 * Returns an array containing the words 
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
		
		//System.out.println("Substr = " + substr + " (" + rep + ")");
		//System.out.println(data);
		data = data.replace(substr, rep);
		//System.out.println(data);
	}
	
	/**
	 * Returns a String composed of count occurrences of c
	 * @param c the char to repeat
	 * @param count the number of occurrences of c
	 * @return the restulting String
	 */
	private String getUnicharString(char c, int count)
	{
		StringBuilder sb = new StringBuilder();
		
		for(int i = 0; i < count; i++)
			sb.append(c);
		
		return sb.toString();
	}
}
