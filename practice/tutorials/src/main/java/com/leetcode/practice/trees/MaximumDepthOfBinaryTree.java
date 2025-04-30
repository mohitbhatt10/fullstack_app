package com.leetcode.practice.trees;

public class MaximumDepthOfBinaryTree {

	public static void main(String[] args) {

		Integer[] bfsArray = { 3, 9, 20, null, null, 15, 7 };

		TreeNode tree = TreeNode.buildTreeFromBFS(bfsArray);
		
		MaximumDepthOfBinaryTree obj = new MaximumDepthOfBinaryTree();
		System.out.println(obj.maxDepth(tree));

	}

	public int maxDepth(TreeNode root) {
		
		if (root == null) {
			return 0;
		}

		int left = maxDepth(root.left);
		int right = maxDepth(root.right);

		return 1 + Math.max(left, right);

	}

}
