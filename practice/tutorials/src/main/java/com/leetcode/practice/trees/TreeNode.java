package com.leetcode.practice.trees;

import java.util.LinkedList;
import java.util.Queue;

public class TreeNode {

	int val;
	TreeNode left;
	TreeNode right;

	TreeNode() {
	}

	TreeNode(int val) {
		this.val = val;
	}

	TreeNode(int val, TreeNode left, TreeNode right) {
		this.val = val;
		this.left = left;
		this.right = right;
	}
	
	//BFS
	// Root -> Left -> Right -> Left.Left -> Left.Right -> Right.Left -> Right.Right and so on
	public static void bfs(TreeNode root) {
		if (root == null)
			return;
		Queue<TreeNode> queue = new LinkedList<>();
		queue.add(root);

		while (!queue.isEmpty()) {
			TreeNode current = queue.poll();
			System.out.print(current.val + " ");

			if (current.left != null)
				queue.add(current.left);
			if (current.right != null)
				queue.add(current.right);
		}
	}

	// Depth-First Search (Preorder)
	// Traverse as reaches
	public static void dfsPreorder(TreeNode root) {
		if (root == null)
			return;
		System.out.print(root.val + " ");
		dfsPreorder(root.left);
		dfsPreorder(root.right);
	}

	// Depth-First Search (Inorder)
	// 
	public static void dfsInorder(TreeNode root) {
		if (root == null)
			return;
		dfsInorder(root.left);
		System.out.print(root.val + " ");
		dfsInorder(root.right);
	}

	// Depth-First Search (Postorder)
	public static void dfsPostorder(TreeNode root) {
		if (root == null)
			return;
		dfsPostorder(root.left);
		dfsPostorder(root.right);
		System.out.print(root.val + " ");
	}
	
	public static TreeNode buildTreeFromBFS(Integer[] arr) {
		if (arr == null || arr.length == 0 || arr[0] == null) return null;

		TreeNode root = new TreeNode(arr[0]);
		Queue<TreeNode> queue = new LinkedList<>();
		queue.add(root);

		int i = 1;

		while (i < arr.length) {
			TreeNode current = queue.poll();

			// Left child
			if ( arr[i] != null && current != null) {
				current.left = new TreeNode(arr[i]);
				queue.add(current.left);
			}
			i++;

			// Right child
			if (i < arr.length && arr[i] != null && current != null) {
				current.right = new TreeNode(arr[i]);
				queue.add(current.right);
			}
			i++;
		}

		return root;
	}
}