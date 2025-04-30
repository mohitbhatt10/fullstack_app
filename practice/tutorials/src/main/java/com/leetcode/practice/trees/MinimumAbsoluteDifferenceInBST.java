package com.leetcode.practice.trees;

/*
Given the root of a Binary Search Tree (BST), return the minimum absolute difference between the values of any two different nodes in the tree.



Example 1:


Input: root = [4,2,6,1,3]
Output: 1
Example 2:


Input: root = [1,0,48,null,null,12,49]
Output: 1


Constraints:

The number of nodes in the tree is in the range [2, 104].
0 <= Node.val <= 105
 */
public class MinimumAbsoluteDifferenceInBST {

    int minDifference = Integer.MAX_VALUE;
    TreeNode previous = null;
    public static void main(String[] args) {
        Integer[] bfsOrder = {1,0,48,null,null,12,49};
        TreeNode root = TreeNode.buildTreeFromBFS(bfsOrder);
        MinimumAbsoluteDifferenceInBST obj = new MinimumAbsoluteDifferenceInBST();
        System.out.println(obj.getMinimumDifference(root));
    }
    public int getMinimumDifference(TreeNode root) {
        dfsInorder(root);
        return minDifference;

    }

    public void dfsInorder(TreeNode root){

        //Base case
        if(root == null){
            return;
        }

        //Inorder left traversal
        dfsInorder(root.left);

        //Actual compare logic when previous is not null then calculate the min difference
        if(previous != null) {
            minDifference = Math.min(minDifference, root.val - previous.val);
        }
        //assign previous as current and move to right
        previous = root;

        //Inorder right traversal
        dfsInorder(root.right);
    }
}
