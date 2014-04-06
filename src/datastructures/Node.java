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
	private Node<K> parent;
	
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
		this.parent = null;
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
	 * Set the parent of the node.
	 * @param _parent Parent node.
	 */
	public void setParent(Node<K> _parent)
	{
		this.parent = _parent;
	}
	
	/**
	 * Get the parent of the node.
	 * @return The parent of the node.
	 */
	public Node<K> getParent()
	{
		return parent;
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
	 * @param n the number of the node (in [0, # child])
	 * @return the nth child if n was ok, null otherwise
	 */
	public Node<K> getNthChild(int n)
	{
		if(n < 0 || n > children.size())
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
	
	/**
	 * Adds a child node containing the data to the current node 
	 * @param data the data to store in the new node
	 */
	public void addChild(K data)
	{
		Node<K> new_child = new Node<K>(data);
		
		children.add(new_child);
	}
	
	/**
	 * Adds a child node to the current node
	 * @param new_child the node to be added
	 */
	public void addChild(Node<K> new_child)
	{
		children.add(new_child);
	}
	
	/**
	 * Display the data.
	 */
	public void display()
	{
		if(data != null)
			System.out.print(data.toString());
	}
	
	/**
	 * Display the subtree starting from this node.
	 */
	public void displaySubtree()
	{
		System.out.print("-> ");
		this.display();
		System.out.println("");
		for(int i = 0 ; i < this.nbChildren()  ; ++i){
			System.out.print("-> ");
			this.getNthChild(i).displaySubtree();
			System.out.println("");
		}
	}
}
