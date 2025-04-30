package com.leetcode.practice.trees;

public class BalancedBinaryTree {

    public static void main(String[] args) {

        BalancedBinaryTree obj = new BalancedBinaryTree();

        TreeNode root = TreeNode.buildTreeFromBFS(new Integer[]{3,9,20,null,null,15,7});
        System.out.println(obj.isBalanced(root));
    }

    public boolean isBalanced(TreeNode root) {
        //return height(root) != -1;
        int[] result = dfs(root);
        return result[0] == 1;
    }


    //If at any point of recursion, the height of left subtree or right subtree is -1 (what we are setting)
    // Then it becomes an imbalanced one.
    private int height(TreeNode root){

        if(root == null){
            return 0;
        }

        int leftHeight = height(root.left);
        if (leftHeight == -1) {
            return -1;
        }

        int rightHeight = height(root.right);
        if (rightHeight == -1) {
            return -1;
        }

        if(Math.abs(leftHeight - rightHeight) >1){
            return -1;
        }
        return 1 + Math.max(leftHeight, rightHeight);
    }

    private int[] dfs(TreeNode node) {
        if (node == null) return new int[]{1, 0}; // {isBalanced (1 for true, 0 for false), height}

        int[] left = dfs(node.left);
        int[] right = dfs(node.right);

        boolean isBalanced = left[0] == 1 && right[0] == 1 && Math.abs(left[1] - right[1]) <= 1;

        return new int[]{isBalanced ? 1 : 0, 1 + Math.max(left[1], right[1])};
    }
}
