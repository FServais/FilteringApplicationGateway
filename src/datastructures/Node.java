package datastructures;

import java.util.Vector;

/**
 * Class for representing nodes of a tree
 * @author Romain Mormont
 *
 * @param <K> the type of the data stored in the node
 */
public class Node<K> {
	private K data = null;
	private Vector<Node<K>> children;
	
	/**
	 * Constructs a node containing the given data
	 * @param data the data to store in the node
	 */
	public Node(K data)
	{
		this();
		this.data = data;
	}
	
	/**
	 * Constructs a node with no data in it
	 */
	public Node()
	{
		children = new Vector<Node<K>>();
	}
	
	/**
	 * Returns the data stored in the node
	 * @return the data stored in the node, null if the node is empty
	 */
	public K getData()
	{
		return data;
	}
	
	/**
	 * Returns true if the node does not contain any data
	 * @return false if the node stores some data, false otherwise
	 */
	public boolean isEmpty()
	{
		return (data == null);
	}
	
	/**
	 * Returns true if the node has children nodes
	 * @return true if the node has children nodes, false otherwise
	 */
	public boolean hasChildren()
	{
		return (children.size() > 0);
	}
	
	/**
	 * Returns true if the node is a leaf (it has no child)
	 * @return true if the node is a leaf, false otherwise
	 */
	public boolean isLeaf()
	{
		return !hasChildren();
	}
	
	/**
	 * Returns the number of children of the node
	 * @return the number of children of the node
	 */
	public int nbChildren()
	{
		return children.size();
	}
	
	/**
	 * Returns the nth child of the node
	 * @param n the number of the node (in [1, # child])
	 * @return the nth child if n was ok, null otherwise
	 */
	public Node<K> getNthChild(int n)
	{
		if(n <= 0 || n > children.size())
			return null;
		
		return children.elementAt(n);
	}
	
	/**
	 * Returns the number of descendant of the node
	 * @return the number of descendant of the node
	 */
	public int countDescendant()
	{
		int desc = 0;
		for(int i = 0; i < nbChildren(); i++)
			desc += getNthChild(i).countDescendant();
		
		return desc + nbChildren();
	}
}
