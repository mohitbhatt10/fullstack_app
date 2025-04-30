package com.leetcode.practice.trees;

/*
Given the root of a binary tree and an integer targetSum, return true if the tree has a root-to-leaf path such that adding up all the values along the path equals targetSum.

A leaf is a node with no children.



Example 1:


Input: root = [5,4,8,11,null,13,4,7,2,null,null,null,1], targetSum = 22
Output: true
Explanation: The root-to-leaf path with the target sum is shown.
Example 2:


Input: root = [1,2,3], targetSum = 5
Output: false
Explanation: There are two root-to-leaf paths in the tree:
(1 --> 2): The sum is 3.
(1 --> 3): The sum is 4.
There is no root-to-leaf path with sum = 5.
Example 3:

Input: root = [], targetSum = 0
Output: false
Explanation: Since the tree is empty, there are no root-to-leaf paths.
 */
public class PathSum {

    public static void main(String[] args) {

        Integer[] root = {5,4,8,11,null,13,4,7,2,null,null,null,1};
        int target = 22;
        TreeNode bfsTree = TreeNode.buildTreeFromBFS(root);
        PathSum obj = new PathSum();

        System.out.println(obj.hasPathSum(bfsTree, target));


    }

    public boolean hasPathSum(TreeNode root, int targetSum) {
        return hasSum(root, targetSum, 0);
    }

    private boolean hasSum(TreeNode root, int targetSum, int currentSum){

        //Base case when root is null means no target sum
        if(root == null){
            return false;
        }
        //Recursively adding root value in current sum
        currentSum += root.val;

        // if the current node is a leaf node (means its left and right child is null)
        // then check the current sum should be equal to the target sum
        if(root.left == null && root.right == null){
            return currentSum == targetSum;
        }

        //recursively calling both left and right side of tree
        return hasSum(root.left, targetSum, currentSum) || hasSum(root.right, targetSum,currentSum);

    }

    public boolean hasPathSumBetter(TreeNode root, int targetSum) {
        if (root == null) {
            return false;
        }

        if (root.left == null && root.right == null) {
            return targetSum - root.val == 0;
        }

        targetSum -= root.val;

        return hasPathSum(root.left, targetSum) || hasPathSum(root.right, targetSum);
    }
}
