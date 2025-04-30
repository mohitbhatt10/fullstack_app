package com.leetcode.practice.trees;


/*
 * Given the root of a binary tree, invert the tree, and return its root.

 

Example 1:


Input: root = [4,2,7,1,3,6,9]
Output: [4,7,2,9,6,3,1]
Example 2:


Input: root = [2,1,3]
Output: [2,3,1]
Example 3:

Input: root = []
Output: []
 

Constraints:

The number of nodes in the tree is in the range [0, 100].
-100 <= Node.val <= 100
 */
public class InvertBinaryTree {
	
	public static void main(String[] args) {
	
		TreeNode root = new TreeNode(4);
		root.left = new TreeNode(2);
		root.right = new TreeNode(7);
		root.left.left = new TreeNode(1);
		root.left.right = new TreeNode(3);
		root.right.left = new TreeNode(6);
		root.right.right = new TreeNode(9);
		
		System.out.print("Before Inversion(BFS): ");
		TreeNode.bfs(root);
		
		System.out.print("\nBefore Inversion(DFS PreOrder):");
		TreeNode.dfsPreorder(root);
		
		System.out.print("\nBefore Inversion(DFS InOrder):");
		TreeNode.dfsInorder(root);
		
		System.out.print("\nBefore Inversion(DFS PostOrder):");
		TreeNode.dfsPostorder(root);
		
		InvertBinaryTree obj = new InvertBinaryTree();
		
		TreeNode invertedTree = obj.invertTree(root);
		
		System.out.print("\nAfter Inversion(BFS): ");
		
		TreeNode.bfs(invertedTree);
		
	}
	
	public TreeNode invertTree(TreeNode root) {
        
		//Base/Exit case
		if(root == null) {
			return null;
		}
		
		//Swapping between left and right nodes
		TreeNode temp = root.left;
		root.left = root.right;
		root.right = temp;

		//Recursively calling with left and right side
		invertTree(root.left);
		invertTree(root.right);
			
		return root;
    }

}
