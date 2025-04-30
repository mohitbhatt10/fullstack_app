package com.leetcode.practice.trees;

/*
Given the roots of two binary trees root and subRoot, return true if there is a subtree of root with the same structure and node values of subRoot and false otherwise.

A subtree of a binary tree tree is a tree that consists of a node in tree and all of this node's descendants. The tree tree could also be considered as a subtree of itself.



Example 1:


Input: root = [3,4,5,1,2], subRoot = [4,1,2]
Output: true
Example 2:


Input: root = [3,4,5,1,2,null,null,null,null,0], subRoot = [4,1,2]
Output: false


Constraints:

The number of nodes in the root tree is in the range [1, 2000].
The number of nodes in the subRoot tree is in the range [1, 1000].
-104 <= root.val <= 104
-104 <= subRoot.val <= 104
 */
public class SubtreeOfAnotherTree {

    public static void main(String[] args) {
        Integer[] root = {3,4,5,1,2};
        Integer[] subRoot = {4,1,2};

        TreeNode r = TreeNode.buildTreeFromBFS(root);
        TreeNode s = TreeNode.buildTreeFromBFS(subRoot);

        SubtreeOfAnotherTree obj = new SubtreeOfAnotherTree();
        System.out.println(obj.isSubtree(r,s));
    }

    public boolean isSubtree(TreeNode root, TreeNode subRoot) {
        //Base case. If root itself is null then there is no need to check further
        if(root == null) {
            return false;
        }

        // if both trees are same means other tree automatically becomes the subtree of first
        if(isSameTree(root,subRoot)){
             return true;
        }

        // if either left or right side of the first tree is equal to the second tree
        // means second tree is the subtree of the first one
        return isSubtree(root.left, subRoot) || isSubtree(root.right, subRoot);
    }

    /*
    This method is same as the logic we applied for checking if both trees are same
     */
    public boolean isSameTree(TreeNode root1, TreeNode root2){

        if(root1 == null && root2 == null){
            return true;
        }

        if(root1 != null && root2 != null && root1.val == root2.val){
            return isSameTree(root1.left, root2.left) && isSameTree(root1.right, root2.right);
        }

        return false;
    }
}
