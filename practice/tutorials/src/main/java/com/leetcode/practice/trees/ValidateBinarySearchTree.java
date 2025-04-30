package com.leetcode.practice.trees;

/*
Given the root of a binary tree, determine if it is a valid binary search tree (BST).

A valid BST is defined as follows:

The left subtree of a node contains only nodes with keys less than the node's key.
The right subtree of a node contains only nodes with keys greater than the node's key.
Both the left and right subtrees must also be binary search trees.


Example 1:


Input: root = [2,1,3]
Output: true
Example 2:


Input: root = [5,1,4,null,null,3,6]
Output: false
Explanation: The root node's value is 5 but its right child's value is 4.


Constraints:

The number of nodes in the tree is in the range [1, 104].
-231 <= Node.val <= 231 - 1
 */
public class ValidateBinarySearchTree {

    TreeNode previous = null;
    boolean isValid = true;
    public static void main(String[] args) {

        TreeNode root = TreeNode.buildTreeFromBFS(new Integer[]{2,1,3});
        ValidateBinarySearchTree obj = new ValidateBinarySearchTree();
        System.out.println(obj.isValidBST(root));
    }

    public boolean isValidBST(TreeNode root) {
        dfsInOrder(root);
        return isValid;
    }

    //Performing the inorder DFS to check the previous visited node should always
    // be less than the current visiting node for a valid BST
    public void dfsInOrder(TreeNode root){
        if(root == null) {
            return;
        }

        //Left inOrder DFS
        dfsInOrder(root.left);

        //Actual logic to compare the previous vs current node values
        if(previous != null && previous.val >= root.val){
                isValid = false;
                return;
        }
        // assigning previous as current for next check
        previous = root;

        //Inorder right DFS
        dfsInOrder(root.right);
    }
}
