
/**
 *
 * AVLTree
 *
 * An implementation of a AVL Tree with distinct integer keys and info
 *
 */

public class AVLTree {

	private IAVLNode root;
	private final NodeFactory factory = new NodeFactory();

	/**
	 * public boolean empty()
	 *
	 * returns true if and only if the tree is empty complexity: O(1)
	 *
	 */
	public boolean empty() {
		return (this.root == null || this.root.getKey() == -1);
	}

	/**
	 * public String search(int k)
	 *
	 * returns the info of an item with key k if it exists in the tree otherwise,
	 * returns null complexity: O(log n)
	 * 
	 */
	public String search(int k) {
		return nodeSearch(k).getValue();
	}

	/**
	 * public IAVLNode nodeSearch(int k)
	 *
	 * returns the node with key k if it exists in the tree otherwise, returns
	 * virtual node complexity: O(log n)
	 * 
	 */
	public IAVLNode nodeSearch(int k) {
		if (this.empty()) {
			return this.factory.createNode();
		}
		return nodeSearchRec(getRoot(), k);
	}

	/**
	 * public IAVLNode nodeSearchRec(IAVLNode node, int k)
	 *
	 * recursive method returns the node with key k if it equals node's key.
	 * otherwise, returns virtual node
	 * 
	 * complexity: O(log n)
	 * 
	 */
	private IAVLNode nodeSearchRec(IAVLNode node, int k) {
		if (node.getKey() == -1) {
			return this.factory.createNode();
		}
		if (node.getKey() == k) {
			return node;
		}
		if (k < node.getKey()) {
			return nodeSearchRec(node.getLeft(), k);
		} else {
			return nodeSearchRec(node.getRight(), k);
		}
	}

	/**
	 * public int insert(int k, String i)
	 *
	 * inserts an item with key k and info i to the AVL tree. the tree must remain
	 * valid (keep its invariants). returns the number of rebalancing operations, or
	 * 0 if no rebalancing operations were necessary. returns -1 if an item with key
	 * k already exists in the tree.
	 * 
	 * complexity: O(log n)
	 * 
	 */
	public int insert(int k, String i) {
		// 1st base case: tree is empty. Easy insert.
		IAVLNode newNode = this.factory.createNode(k, i);
		if (this.empty()) {
			this.root = newNode;
			return 0;
		}
		// 2nd base case: item with key k already exists. No insert
		if (this.search(k) != null) {
			return -1;
		}
		// otherwise
		IAVLNode father = findPotentialFather(this.root, k);
		if (k < father.getKey()) {
			father.setLeft(newNode);
		} else {
			father.setRight(newNode);
		}
		newNode.setParent(father);
		IAVLNode pointer = newNode.getParent();
		while (pointer != null) {
			pointer.setSize(pointer.getSize() + 1);
			pointer = pointer.getParent();
		}
		// finished insertion
		/*
		 * System.out.println("before rebalancing"); printTree(root, 0);
		 */
		return rebalanceInsert(newNode); // rebalance & update heights
	}

	/**
	 * private static IAVLNode findPotentialFather(IAVLNode node, int k)
	 *
	 * preconditions: the key doesn't already exist in tree, node is not null or
	 * virtual postconditions: finds the correct father for new node insert
	 * complexity: O(log n)
	 * 
	 */
	private static IAVLNode findPotentialFather(IAVLNode node, int k) {
		// Base case. If reached leaf - return it.
		if (node.getHeight() == 0) {
			return node;
		}
		// otherwise continue travel to leaf
		if (k < node.getKey()) {
			if (node.getLeft().getKey() == -1) {
				return node;
			}
			return findPotentialFather(node.getLeft(), k);
		} else {
			if (node.getRight().getKey() == -1) {
				return node;
			}
			return findPotentialFather(node.getRight(), k);
		}
	}

	/**
	 * private int rebalanceInsert(IAVLNode node)
	 *
	 *
	 * Returns the number of rotations made to balance tree after a node insertion
	 * precondition: node is a real node postcondition: AVLTree is balanced, returns
	 * the number of rebalances done
	 * 
	 */
	public int rebalanceInsert(IAVLNode node) {
		return rebalanceInsertRec(node, 0);
	}

	private int rebalanceInsertRec(IAVLNode node, int rebalances) {
		// base case 1 - node is root
		if (node.getParent() == null) {
			return rebalances;
		}
		// base case 2 - tree is balanced
		if (internalRank(node) == 1) {
			return rebalances;
		}
		// find brother
		IAVLNode brother = node.getParent().getLeft();
		boolean iAmLeft = false;
		if (node.getKey() < node.getParent().getKey()) {
			brother = node.getParent().getRight();
			iAmLeft = true;
		}
		// case A - The parent was a leaf or parent is a 0-1 node
		if (node.getParent().getHeight() == 0 || (internalRank(node) == 0 && internalRank(brother) == 1)) {
			node.getParent().setHeight(node.getParent().getHeight() + 1); // promote father
			return rebalanceInsertRec(node.getParent(), rebalances + 1); // recursive call father to check if problem
																			// was
																			// moved up
		}
		// case B - The parent had 1 child. 0-1 already checked.
		// 0-2
		else {
			if (iAmLeft) {
				// 1st case - son with internalRank 0 is a 1-2 node
				if (internalRank(node.getLeft()) == 1 && internalRank(node.getRight()) == 2) {
					rebalances += rotateRight(node.getParent());
					return rebalances;
				} else {// 2nd case - son with internalRank 0 is a 2-1 node
					rebalances += rotateLeft(node);
					rebalances += rotateRight(node.getParent().getParent());
					return rebalances;
				}
			} else {
				// 1st case - son with internalRank 0 is a 2-1 node
				if (internalRank(node.getLeft()) == 2 && internalRank(node.getRight()) == 1) {
					rebalances += rotateLeft(node.getParent());
					return rebalances;
				} else {// 2nd case - son with internalRank 0 is a 1-2 node
					rebalances += rotateRight(node);
					rebalances += rotateLeft(node.getParent().getParent());
					return rebalances;
				}
			}
		}
	}

	/**
	 * public int delete(int k)
	 *
	 * deletes an item with key k from the binary tree, if it is there; the tree
	 * must remain valid (keep its invariants). returns the number of rebalancing
	 * operations, or 0 if no rebalancing operations were needed. returns -1 if an
	 * item with key k was not found in the tree.
	 */
	public int delete(int k) {
		IAVLNode node = nodeSearch(k);
		if (!node.isRealNode()) {
			return -1;
		}
		int rebalances = 0;
		if (this.getRoot().getKey() != k) {
			IAVLNode parent = node.getParent();
			boolean right = (parent.getRight().getKey() == node.getKey());
			// Leaf cases
			if (isLeaf(node)) {
				IAVLNode virtual = this.factory.createNode();
				if (right) {
					parent.setRight(virtual);
					parent.getRight().setParent(parent);
				} else {
					parent.setLeft(virtual);
					parent.getLeft().setParent(parent);
				}
				
				if (!isBalanced(parent)) {
					// if rebalance is needed, send to rebalance, else-send to treeDemotion
					rebalances = rebalanceDelete(parent);
				} else {
					treeResize(parent);
				}
			} else {
				// non-leaf cases
				if (!node.getRight().isRealNode() || !node.getLeft().isRealNode()) {
					// one of node sons is a virtual node
					if (!node.getRight().isRealNode()) {
						node.getLeft().setParent(parent);
						if (right) {
							parent.setRight(node.getLeft());
							parent.getRight().setParent(parent);
						} else {
							parent.setLeft(node.getLeft());
							parent.getLeft().setParent(parent);
						}
					} else {
						node.getRight().setParent(parent);
						if (right) {
							parent.setRight(node.getRight());
							parent.getRight().setParent(parent);
						} else {
							parent.setLeft(node.getRight());
							parent.getLeft().setParent(parent);
						}
					}
					if (!isBalanced(parent)) {
						// if rebalance is needed, send to rebalance, else-send to treeDemotion
						rebalances = rebalanceDelete(parent);
					} else {
						treeResize(parent);
					}

				} else {
					// using successor to find the replacement of node, then rabalancing the AVL
					// Tree
					rebalances = successorBalance(node, right);
				}
			}
		} else {
			// root cases
			if (isLeaf(this.getRoot())) {// tree deletion
				this.setRoot(null);
			} else {
				if (!node.getRight().isRealNode() || !node.getLeft().isRealNode()) {
					// unary tree
					if (node.getRight().isRealNode()) {
						this.setRoot(node.getRight());
						node.getRight().setParent(null);
					} else {
						this.setRoot(node.getLeft());
						node.getLeft().setParent(null);
					}
					this.getRoot().setSize(1);
				} else {// successor replacement is needed
					rebalances = successorBalance(node, true);
				}
			}

		}
		return rebalances;
	}

	/**
	 * public int successorBalance(IAVLNode node, boolean right)
	 *
	 * deletes node and replaces it with it's successor,then rebalances tree returns
	 * the number of rotations in rebalance. precondition: node has two sons.
	 * postcondition:tree is balanced
	 * 
	 */
	private int successorBalance(IAVLNode node, boolean right) {
		int rebalances = 0;
		IAVLNode successor = successor(node);
		IAVLNode balancer;
		if (successor.getParent().getKey() != node.getKey()) {// if successor is not right son of node,
			// successor original parent bypasses successor, so balance check is needed for
			// it
			balancer = successor.getParent();
			balancer.setLeft(successor.getRight());// successor has no left son
			successor.getRight().setParent(balancer);
			successor.setRight(node.getRight());
			successor.getRight().setParent(successor);
		}
		else {
			if(successor.getParent().getKey()!=getRoot().getKey()) {
				balancer = successor.getParent().getParent();
			}else {
				balancer = successor;
			}
		}
		successor.setLeft(node.getLeft());
		successor.setParent(node.getParent());
		successor.getLeft().setParent(successor);
		successor.setHeight(1 + Math.max(successor.getLeft().getHeight(), successor.getRight().getHeight()));
		successor.setSize(1+ successor.getLeft().getSize() + successor.getRight().getSize());// size is changed in treeDemotion
		if (node.getKey()!=this.getRoot().getKey()) {// checking if node has parent or root change needed
			if (right) {
				node.getParent().setRight(successor);
			} else {
				node.getParent().setLeft(successor);
			}
		} else {
			this.setRoot(successor);
		}
		// if balance of the original parent of the successor is changed,
		// rebalanceDelete the AVLTree starting from the original parent
		// else, treeDemote from original parent and return 0
		if(isBalanced(successor)){
			if (isBalanced(balancer)) {
				balancer.setHeight(1 + Math.max(balancer.getLeft().getHeight(), balancer.getRight().getHeight()));
				treeResize(balancer);
				return rebalances;
			} else {
				return rebalances + rebalanceDelete(balancer);
			}
		}else {
			return rebalances + rebalanceDelete(successor);
		}
	}

	/**
	 * public private void treeResize(IAVLNode node)
	 *
	 * Updates sizes of tree nodes after deletion
	 * 
	 */
	private void treeResize(IAVLNode node) {
		while (node.getKey() != this.getRoot().getKey()) {
			node.setSize(node.getSize() - 1);
			node = node.getParent();
		}
		node.setSize(node.getSize() - 1);
	}

	/**
	 * private int rebalanceDelete(IAVLNode node)
	 *
	 *
	 * Returns the number of rotations made to balance tree after a node deletion
	 * precondition: AVLTree beneath node is not balanced, node is a real node
	 * postcondition: AVLTree is balanced
	 * 
	 */
	private int rebalanceDelete(IAVLNode node) {
		int rebalances = 0;
		boolean isRoot = node.getKey() == this.getRoot().getKey();
		if (internalRank(node.getRight()) == 2) {
			// demoting node, 2-2 case
			node.setHeight(node.getHeight() - 1);
			node.setSize(node.getSize() - 1);
			// A balanced parent or root with 1-1
			if (isRoot || (isBalanced(node.getParent()))) { // if root or father is balanced
				if (!isRoot) {
					treeResize(node.getParent());
				}
				return 1;
				// An unbalanced parent case - rebalance
			} else {
				rebalances += 1 + rebalanceDelete(node.getParent());
			}
		} else {
			// A 3-1 case - rebalance using rotations
			if (internalRank(node.getRight()) == 3) {
				// balance problem is between node and node's right son
				if (node.getLeft().getLeft().getHeight() == node.getLeft().getRight().getHeight()) {
					// 2nd option - check if brother is 1-1 node
					rebalances += rotateRight(node);
				} else {
					// 3rd option - check if son's left son has internalRank 1
					if (internalRank(node.getLeft().getLeft()) == 1) {
						rebalances += rotateRight(node);
					} else {
						// 4th option - if son's left son has internalRank 2, double rotation
						rebalances += rotateLeft(node.getLeft());
						rebalances += rotateRight(node);
					}
				}
			} else {
				
				// SAME CHECK FOR LEFT SON - balance problem is between node and node's left son
				// 2nd option - check if brother is 1-1 node
				
				if (node.getRight().getLeft().getHeight() == node.getRight().getRight().getHeight()) {
					rebalances += rotateLeft(node);

				} else {
					if (internalRank(node.getRight().getRight()) == 1) {
						// 3rd option - check if son's left son has internalRank 1
						rebalances += rotateLeft(node);
					} else {
						// 4th option - if son's left son has internalRank 2, double rotation
						rebalances += rotateRight(node.getRight());
						rebalances += rotateLeft(node);
					}
				
				}
			}
			{// if node's original parent subtree is balanced- all tree is balanced
				// use treeResize to update nodes height and size,
				// else - rebalance the original parent subtree
			}
			if (isRoot || isBalanced(node.getParent().getParent())) {
				if (!isRoot) {// else,size changed in rotation up to root
					treeResize(node.getParent().getParent());
				}
			} else {
				rebalances += rebalanceDelete(node.getParent().getParent());
			}
		}
		return rebalances;
	}

	/**
	 * private IAVLNode minNode()
	 *
	 * Returns the item with the smallest key in the tree, or null if the tree is
	 * empty
	 */
	private IAVLNode minNode(IAVLNode node) {
		if (!node.isRealNode()) {
			return null;
		}
		IAVLNode minNode = node;
		while (minNode.getLeft().getKey() != -1) {
			minNode = minNode.getLeft();
		}
		return minNode;
	}

	/**
	 * public String min()
	 *
	 * Returns the info of the item with the smallest key in the tree, or null if
	 * the tree is empty
	 */
	public String min() {
		if (this.empty()) {
			return null;
		}
		IAVLNode thisNode = getRoot();
		return minNode(thisNode).getValue();
	}

	/**
	 * private IAVLNode maxNode()
	 *
	 * Returns the item with the largest key in the tree, or null if the tree is
	 * empty
	 */
	public IAVLNode maxNode(IAVLNode node) {
		if (!node.isRealNode()) {
			return null;
		}
		IAVLNode maxNode = node;
		while (maxNode.getRight().getKey() != -1) {
			maxNode = maxNode.getRight();
		}
		return maxNode;
	}

	/**
	 * public String max()
	 *
	 * Returns the info of the item with the largest key in the tree, or null if the
	 * tree is empty
	 */
	public String max() {
		if (this.empty()) {
			return null;
		}
		IAVLNode thisNode = getRoot();
		return maxNode(thisNode).getValue();
	}

	/**
	 * public IAVLNode successor(IAVLNode node)
	 *
	 * Returns the item with the next key in the tree, or null if this node is the
	 * maximum node
	 */
	private IAVLNode successor(IAVLNode node) {
		if (node.getRight().isRealNode()) {
			return minNode(node.getRight());
		} else {
			IAVLNode thisNode = node;
			while (thisNode.getKey() != getRoot().getKey()) {
				if (thisNode.getParent().getLeft().getKey() == thisNode.getKey()) {
					return thisNode.getParent();
				}
				thisNode = thisNode.getParent();
			}
			return null;
		}
	}

	/**
	 * public private void rotateRight (IAVLNode node)
	 *
	 * Rotates the tree right, like the algorithm learned in class
	 */
	private int rotateRight(IAVLNode node) {
		int rebalances = 1;
		IAVLNode axis = node.getLeft();
		node.setLeft(axis.getRight());
		axis.setParent(node.getParent());
		axis.setRight(node);
		if (node.getKey() == this.getRoot().getKey()) {// root rotation
			this.setRoot(axis);
		} else {

			if (node.getParent().getLeft().getKey() == node.getKey()) {// non root rotation
				node.getParent().setLeft(axis);
			} else {
				node.getParent().setRight(axis);
			}
		}
		node.setParent(axis);
		node.getLeft().setParent(node);
		int nodeHightBefore = node.getHeight();
		node.setHeight(Math.max(node.getLeft().getHeight(), node.getRight().getHeight()) + 1);
		if (nodeHightBefore != node.getHeight()) { // if node was demoted/promoted
			rebalances += Math.abs(nodeHightBefore - node.getHeight());
		}
		node.setSize(1 + node.getLeft().getSize() + node.getRight().getSize());
		int axisHightBefore = axis.getHeight();
		axis.setHeight(Math.max(axis.getLeft().getHeight(), axis.getRight().getHeight()) + 1);
		if (axisHightBefore != axis.getHeight()) { // if axis was demoted/promoted
			rebalances += Math.abs(axisHightBefore - axis.getHeight());
		}
		axis.setSize(1 + axis.getLeft().getSize() + axis.getRight().getSize());
		return rebalances;
	}

	/**
	 * public private void rotateLeft (IAVLNode node)
	 *
	 * Rotates the tree left, like the algorithm learned in class
	 */
	private int rotateLeft(IAVLNode node) {
		int rebalances = 1;
		IAVLNode axis = node.getRight();
		node.setRight(axis.getLeft());
		axis.setParent(node.getParent());
		axis.setLeft(node);
		if (node.getKey() == this.getRoot().getKey()) {// root rotation
			this.setRoot(axis);
		} else {

			if (node.getParent().getLeft().getKey() == node.getKey()) {// non root rotation
				node.getParent().setLeft(axis);
			} else {
				node.getParent().setRight(axis);
			}
		}
		node.setParent(axis);
		node.getRight().setParent(node);
		int nodeHightBefore = node.getHeight();
		node.setHeight(Math.max(node.getLeft().getHeight(), node.getRight().getHeight()) + 1);
		if (nodeHightBefore != node.getHeight()) { // if node was demoted/promoted
			rebalances += Math.abs(nodeHightBefore - node.getHeight());
		}
		node.setSize(1 + node.getLeft().getSize() + node.getRight().getSize());
		int axisHightBefore = node.getHeight();
		axis.setHeight(Math.max(axis.getLeft().getHeight(), axis.getRight().getHeight()) + 1);
		if (axisHightBefore != axis.getHeight()) { // if axis was demoted/promoted
			rebalances += Math.abs(axisHightBefore - axis.getHeight());
		}
		axis.setSize(1 + axis.getLeft().getSize() + axis.getRight().getSize());
		return rebalances;
	}

	/**
	 * public int[] keysToArray()
	 *
	 * Returns a sorted array which contains all keys in the tree, or an empty array
	 * if the tree is empty.
	 */
	public int[] keysToArray() {
		if (this.root == null) {
			int[] emptyArr = {};
			return emptyArr;
		}
		int[] arr = new int[this.root.getSize()];
		IAVLNode thisNode = minNode(getRoot());
		for (int i = 0; i < arr.length; i++) {
			arr[i] = thisNode.getKey();
			thisNode = successor(thisNode);
		}
		return arr;
	}

	/**
	 * public String[] infoToArray()
	 *
	 * Returns an array which contains all info in the tree, sorted by their
	 * respective keys, or an empty array if the tree is empty.
	 */
	public String[] infoToArray() {
		if (this.root == null) {
			String[] emptyArr = {};
			return emptyArr;
		}
		String[] arr = new String[this.getRoot().getSize()];
		IAVLNode thisNode = minNode(getRoot());
		for (int i = 0; i < arr.length; i++) {
			arr[i] = thisNode.getValue();
			thisNode = successor(thisNode);
		}
		return arr;
	}

	/**
	 * public int size()
	 *
	 * Returns the number of nodes in the tree.
	 *
	 * precondition: none postcondition: none
	 */
	public int size() {
		if (this.root == null) {
			return 0;
		}
		return this.root.getSize();
	}

	/**
	 * public IAVLNode getRoot()
	 *
	 * Returns the root AVL node, or null if the tree is empty
	 *
	 * precondition: none postcondition: none
	 */
	public IAVLNode getRoot() {
		return this.root;
	}

	/**
	 * public void setRoot(IAVLNode)
	 *
	 * sets the root of the tree to be node.
	 *
	 * precondition: none postcondition: none
	 */
	private void setRoot(IAVLNode node) {
		this.root = node;
	}

	/**
	 * private boolean isLeaf(IAVLNode node)
	 *
	 * Returns True if the IAVLNode is a leaf
	 *
	 * precondition: none postcondition: none
	 */

	private boolean isLeaf(IAVLNode node) {
		if (node.getLeft().isRealNode() || node.getRight().isRealNode()) {
			return false;
		}
		return true;

	}

	/**
	 * private int internalRank(IAVLNode node)
	 *
	 * Returns the difference in height between node and it's parent If node has no
	 * parent (is root) returns 0
	 *
	 * precondition: none postcondition: none
	 */

	private int internalRank(IAVLNode node) {
		if (getRoot().getKey() != node.getKey()) {
			return node.getParent().getHeight() - node.getHeight();
		}
		return 0;
	}

	/**
	 * private boolean isBalanced(IAVLNode node)
	 *
	 * Returns true if the IAVLNode node is balanced, false else
	 *
	 * precondition: none postcondition: none
	 */

	private boolean isBalanced(IAVLNode node) {
		if (!node.isRealNode()) {
			return true;
		}
		if (internalRank(node.getLeft()) == 1 && internalRank(node.getRight()) == 1) {
			return true;
		}
		if (internalRank(node.getLeft()) == 2 && internalRank(node.getRight()) == 1) {
			return true;
		}
		if (internalRank(node.getLeft()) == 1 && internalRank(node.getRight()) == 2) {
			return true;
		}
		return false;
	}

	/**
	 * public string split(int x)
	 *
	 * splits the tree into 2 trees according to the key x. Returns an array [t1,
	 * t2] with two AVL trees. keys(t1) < x < keys(t2). precondition: search(x) !=
	 * null postcondition: none
	 */
	public AVLTree[] split(int x) {
		AVLTree[] splitTrees = new AVLTree[2];
		IAVLNode splitNode = nodeSearch(x); // precondition is that x exists
		AVLTree lessTree = new AVLTree();
		lessTree.root = splitNode.getLeft();
		lessTree.root.setParent(null);
		AVLTree moreTree = new AVLTree();
		moreTree.root = splitNode.getRight();
		moreTree.root.setParent(null);
		IAVLNode thisNode = splitNode.getParent();
		int cameFrom = splitNode.getKey();
		while (thisNode != null) {
			AVLTree addTree = new AVLTree();
			if (thisNode.getLeft().getKey() == cameFrom) { // if went up right
				addTree.root = thisNode.getRight();
				addTree.root.setParent(null);
				moreTree.join(this.factory.createNode(thisNode.getKey(), thisNode.getValue()), addTree);
			}
			if (thisNode.getRight().getKey() == cameFrom) { // if went up left
				addTree.root = thisNode.getLeft();
				addTree.root.setParent(null);
				lessTree.join(this.factory.createNode(thisNode.getKey(), thisNode.getValue()), addTree);
			}
			cameFrom = thisNode.getKey();
			thisNode = thisNode.getParent();

		}
		splitTrees[0] = lessTree;
		splitTrees[1] = moreTree;
		return splitTrees;
	}

	// -----------------------------------------------------------------------------------------------

	/**
	 * public string exSplit(int x)
	 *
	 * splits the tree into 2 trees according to the key x. Returns an array [t1,
	 * t2] with two AVL trees. keys(t1) < x < keys(t2). precondition: search(x) !=
	 * null postcondition: none
	 */
	public int[] exSplit(int x) {
		int complexityJoins = 0;
		int moneJoins = 0;
		int maxComplexityJoin = 0;
		IAVLNode splitNode = nodeSearch(x); // precondition is that x exists
		AVLTree lessTree = new AVLTree();
		lessTree.root = splitNode.getLeft();
		lessTree.root.setParent(null);
		AVLTree moreTree = new AVLTree();
		moreTree.root = splitNode.getRight();
		moreTree.root.setParent(null);
		IAVLNode thisNode = splitNode.getParent();
		int cameFrom = splitNode.getKey();
		while (thisNode != null) {
			AVLTree addTree = new AVLTree();
			if (thisNode.getLeft().getKey() == cameFrom) { // if went up right
				addTree.root = thisNode.getRight();
				addTree.root.setParent(null);
				int thisJoin;
				thisJoin = moreTree.join(this.factory.createNode(thisNode.getKey(), thisNode.getValue()), addTree);
				complexityJoins += thisJoin;
				moneJoins++;
				if (maxComplexityJoin < thisJoin) {
					maxComplexityJoin = thisJoin;
				}
			}
			if (thisNode.getRight().getKey() == cameFrom) { // if went up left
				addTree.root = thisNode.getLeft();
				addTree.root.setParent(null);
				int thisJoin = lessTree.join(this.factory.createNode(thisNode.getKey(), thisNode.getValue()), addTree);
				complexityJoins += thisJoin;
				moneJoins++;
				if (maxComplexityJoin < thisJoin) {
					maxComplexityJoin = thisJoin;
				}
			}
			cameFrom = thisNode.getKey();
			thisNode = thisNode.getParent();

		}
		int[] results = { complexityJoins, moneJoins, maxComplexityJoin };
		return results;
	}

	// -----------------------------------------------------------------------------------------------

	/**
	 * public join(IAVLNode x, AVLTree t)
	 *
	 * joins t and x with the tree. Returns the complexity of the operation (rank
	 * difference + 1 between the tree and t) precondition: keys(x,t) < keys() or
	 * keys(x,t) > keys() postcondition: none
	 */
	public int join(IAVLNode x, AVLTree t) {
		if (t.empty()) {
			if (!this.empty()) {
				// if received empty tree and this is not empty - recursively call join
				return join(x, this, t);
			} else {
				// if both empty - return tree with root
				this.setRoot(x);
				return 1;
			}
		}
		if (this.empty()) {
			// if // if received tree empty and received is not empty - recursively call
			// join
			return join(x, t, this);
		}
		int operations = 0;
		AVLTree big;
		AVLTree small;
		// if heights of this and t are equal - make x root of joined tree
		if (this.getRoot().getHeight() == t.getRoot().getHeight()) {
			if (this.getRoot().getKey() > t.getRoot().getKey()) {
				x.setLeft(t.getRoot());
				x.setRight(this.getRoot());
			} else {
				x.setLeft(this.getRoot());
				x.setRight(t.getRoot());
			}
			x.getLeft().setParent(x);
			x.getRight().setParent(x);
			x.setHeight(this.getRoot().getHeight() + 1);
			x.setSize(x.getLeft().getSize() + x.getRight().getSize() + 1);
			this.setRoot(x);
			return 1;
		}
		// check which tree has bigger height and recursive call join when bigger tree
		// is big
		if (this.getRoot().getHeight() > t.getRoot().getHeight()) {
			big = this;
			small = t;
		} else {
			big = t;
			small = this;
		}
		operations = join(x, big, small);
		return operations;
	}

	private int join(IAVLNode x, AVLTree big, AVLTree small) {

		int operations = 0;
		IAVLNode pointer = big.getRoot();
		if (big.getRoot().getKey() > x.getKey()) {// bigger tree has bigger keys
			// insert x as root of small tree with small keys and calculate operations
			if (small.empty()) {
				operations = big.getRoot().getHeight();
			} else {
				operations = big.getRoot().getHeight() - small.getRoot().getHeight();
				small.getRoot().setParent(x);
				x.setLeft(small.getRoot());
				x.setHeight(small.getRoot().getHeight() + 1);
			}
			small.setRoot(x);
			// if big tree is a leaf, make x root
			if (big.getRoot().getHeight() == 0) {
				big.setRoot(x);
			} else {
				while (pointer.getHeight() >= small.getRoot().getHeight()) {
					pointer = pointer.getLeft();
				}
			}

			// joining x with big
			x.setRight(pointer);
			x.setParent(pointer.getParent());
			x.setHeight(Math.max(x.getLeft().getHeight(), x.getRight().getHeight()) + 1);
			pointer.setParent(x);
			if (x.getParent() != null) {
				x.getParent().setLeft(x);
				// A 0-2 case, rotate to rebalance
				if (big.internalRank(x) == 0 && big.internalRank(x.getParent().getRight()) == 2) {
					big.rotateRight(x.getParent());
				}
			}

		} else { // bigger tree has smaller keys
			// insert x as root of small tree with small keys and calculate operations
			if (small.empty()) {
				operations = big.getRoot().getHeight();
			} else {
				operations = big.getRoot().getHeight() - small.getRoot().getHeight();
				small.getRoot().setParent(x);
				x.setRight(small.getRoot());
				x.setHeight(small.getRoot().getHeight() + 1);
			}
			small.setRoot(x);
			// if big tree is a leaf, make x root
			if (big.getRoot().getHeight() == 0) {
				big.setRoot(x);
			} else {
				while (pointer.getHeight() >= small.getRoot().getHeight()) {
					pointer = pointer.getRight();
				}
			}
			// joining x with big
			x.setLeft(pointer);
			x.setParent(pointer.getParent());
			x.setHeight(Math.max(x.getLeft().getHeight(), x.getRight().getHeight()) + 1);
			pointer.setParent(x);
			if (x.getParent() != null) {
				x.getParent().setRight(x);
				// A 0-2 case, rotate to rebalance
				if (big.internalRank(x) == 0 && big.internalRank(x.getParent().getLeft()) == 2) {
					big.rotateLeft(x.getParent());
				}
			}
		}

		x.setSize(x.getLeft().getSize() + x.getRight().getSize() + 1);
		big.rebalanceInsert(x);// insert rebalance
		pointer = x.getParent();
		while (pointer != null) {
			// size update
			pointer.setSize(pointer.getLeft().getSize() + pointer.getRight().getSize() + 1);
			pointer = pointer.getParent();
		}
		// updating this tree as the joined tree
		this.setRoot(big.getRoot());
		return operations + 1;
	}

	// --------------------------------end new join test--------------------------

	public static void printTree(IAVLNode root, int space) {
		// Base case
		if (root == null || !root.isRealNode()) {
			System.out.println("the tree contains no elements");
		} else {
			printTreeRec(root, space);
			System.out.println("----------------------------------------------------------");
		}
	}

	private static void printTreeRec(IAVLNode root, int space) {
		if (root != null && root.getKey() != -1) {
			// Increase distance between levels
			space += 10;

			// Process right child first
			printTreeRec(root.getRight(), space);

			// Print current node after space
			// count
			System.out.print(System.lineSeparator());
			for (int i = 10; i < space; i++)
				System.out.print(" ");
			System.out.print(root.getKey() + System.lineSeparator());

			// Process left child
			printTreeRec(root.getLeft(), space);
		}
	}

	public static void printThisNode(IAVLNode node) {
		if (node == null) {
			System.out.println("The node is null");
		} else {
			System.out.println("node key: " + node.getKey() + System.lineSeparator() + "node info: " + node.getValue()
					+ System.lineSeparator() + "node hight: " + node.getHeight());
		}
	}

	/**
	 * public interface IAVLNode ! Do not delete or modify this - otherwise all
	 * tests will fail !
	 */
	public interface IAVLNode {
		public int getKey(); // returns node's key (for virtuval node return -1)

		public String getValue(); // returns node's value [info] (for virtuval node return null)

		public void setLeft(IAVLNode node); // sets left child

		public IAVLNode getLeft(); // returns left child (if there is no left child return null)

		public void setRight(IAVLNode node); // sets right child

		public IAVLNode getRight(); // returns right child (if there is no right child return null)

		public void setParent(IAVLNode node); // sets parent

		public IAVLNode getParent(); // returns the parent (if there is no parent return null)

		public boolean isRealNode(); // Returns True if this is a non-virtual AVL node

		public int getHeight(); // Returns the height of the node (-1 for virtual nodes)

		public void setHeight(int height); // sets the height of the node

		public int getSize(); // Returns the amount of sons the node has + 1 (for itself)

		public void setSize(int s); // Sets the size of the node to s
	}

	/**
	 * public class AVLNode
	 *
	 * If you wish to implement classes other than AVLTree (for example AVLNode), do
	 * it in this file, not in another file. This class can and must be modified.
	 * (It must implement IAVLNode)
	 */
	public class AVLNode implements IAVLNode {

		private static final int DEFAULT_KEY = -1;
		private String info;
		private int key;
		private IAVLNode left;
		private IAVLNode right;
		private IAVLNode parent;
		private int height;
		private int size;

		/*
		 * public AVLNode() { this(DEFAULT_KEY, null, -1); }
		 */

		public AVLNode() {
			this(DEFAULT_KEY, null);
			this.height = -1;
			this.size = 0;
		}

		public AVLNode(int key, String info) {
			this.info = info;
			this.key = key;
			if (key != -1) {
				this.left = new AVLNode();
				this.left.setParent(this);
				this.right = new AVLNode();
				this.right.setParent(this);
			} else {
				this.left = null;
				this.right = null;
			}
			this.parent = null;
			this.height = 0;
			this.size = 1;

		}

		/*
		 * public AVLNode(int key, String info, int height) { this.info = info; this.key
		 * = key; this.left = new AVLNode(); this.right = new AVLNode(); this.parent =
		 * null; this.height = height;
		 * 
		 * }
		 */

		public int getKey() {
			return this.key;
		}

		public String getValue() {
			return this.info;
		}

		public void setLeft(IAVLNode node) {
			this.left = node;
		}

		public IAVLNode getLeft() {
			return this.left;
		}

		public void setRight(IAVLNode node) {
			this.right = node;
		}

		public IAVLNode getRight() {
			return this.right;
		}

		public void setParent(IAVLNode node) {
			this.parent = node;
		}

		public IAVLNode getParent() {
			return this.parent;
		}

		// Returns True if this is a non-virtual AVL node
		public boolean isRealNode() {
			if (key == -1) {
				return false;
			}
			return true;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		public int getHeight() {
			return this.height;
		}

		public int getSize() {
			return this.size;
		}

		public void setSize(int s) {
			this.size = s;

		}

	}

	public class NodeFactory {

		public IAVLNode createNode(int k, String v) {
			return new AVLNode(k, v);
		}

		public IAVLNode createNode() {
			return new AVLNode();
		}
	}

}