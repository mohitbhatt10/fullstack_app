package com.leetcode.practice.trees;

/*
Given the root of a binary tree, check whether it is a mirror of itself (i.e., symmetric around its center).



Example 1:


Input: root = [1,2,2,3,4,4,3]
Output: true
Example 2:


Input: root = [1,2,2,null,3,null,3]
Output: false


Constraints:

The number of nodes in the tree is in the range [1, 1000].
-100 <= Node.val <= 100
 */
public class SymmetricTree {

    public static void main(String[] args) {

        Integer[] bfs  = {1,2,2,3,4,4,3};
        TreeNode root = TreeNode.buildTreeFromBFS(bfs);
        SymmetricTree obj = new SymmetricTree();
        System.out.println(obj.isSymmetric(root));
    }

    public boolean isSymmetric(TreeNode root) {
        return same(root, root);
    }

    private boolean same(TreeNode root1, TreeNode root2){

        //If both symmetric sides are null means that's a true case
        if(root1 == null && root2 == null){
            return true;
        }

        //if either of symmetric side is null and the other is not then it's a false case
        if(root1 == null || root2 == null){
            return false;
        }

        //if the values of symmetric side is not equal then it's a false case
        if(root1.val != root2.val){
            return false;
        }

        //recursively call this method for both left and right side
        return same(root1.left, root2.right) && same(root1.right, root2.left);
    }
}
