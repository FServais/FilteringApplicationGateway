/**
 * 
 */
package datastructures;

/**
 * Class for representing a tree
 * @author Romain Mormont
 *
 * @param <K> the type of the data stored in the tree
 */
public class Tree<K> {
	private Node<K> root = null;
	
	/**
	 * Constructs an empty tree
	 */
	public Tree()
	{
		
	}
	
	/**
	 * Constructs a tree of which the root is root
	 * @param root
	 */
	public Tree(Node<K> root)
	{
		this.root = root;
	}
	
	/**
	 * Returns the root node of the tree
	 * @return the root node of the tree, null if the tree is empty
	 */
	public Node<K> getRoot()
	{
		return root;
	}
	
	/**
	 * Set a new root for the tree (if a root already exists, it is replaced by the new one)
	 * @param new_root the new root for the tree
	 */
	public void setRoot(Node<K> new_root)
	{
		this.root = new_root;
	}
	
	/**
	 * Returns true if the tree is empty
	 * @return true if the tree is empty, false otherwise
	 */
	public boolean isEmpty()
	{
		return (root == null);
	}
	
	/**
	 * Returns the number of nodes in the tree
	 * @return the number of nodes in the tree
	 */
	public int size()
	{
		if(isEmpty())
			return 0;
		
		return 1 + root.countDescendant();
	}
	
	/**
	 * Display the tree.
	 */
	public void display()
	{
		root.display();
	}
}
