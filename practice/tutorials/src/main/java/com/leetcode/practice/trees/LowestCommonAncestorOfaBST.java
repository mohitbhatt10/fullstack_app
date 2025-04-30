package com.leetcode.practice.trees;

/*
Given a binary search tree (BST), find the lowest common ancestor (LCA) node of two given nodes in the BST.

According to the definition of LCA on Wikipedia: “The lowest common ancestor is defined between two nodes p and q as the lowest node in T that has both p and q as descendants (where we allow a node to be a descendant of itself).”

Example 1:
Input: root = [6,2,8,0,4,7,9,null,null,3,5], p = 2, q = 8
Output: 6
Explanation: The LCA of nodes 2 and 8 is 6.

Example 2:
Input: root = [6,2,8,0,4,7,9,null,null,3,5], p = 2, q = 4
Output: 2
Explanation: The LCA of nodes 2 and 4 is 2, since a node can be a descendant of itself according to the LCA definition.

Example 3:
Input: root = [2,1], p = 2, q = 1
Output: 2


Constraints:

The number of nodes in the tree is in the range [2, 105].
-109 <= Node.val <= 109
All Node.val are unique.
p != q
p and q will exist in the BST.
 */
public class LowestCommonAncestorOfaBST {

    //A global variable to store the Least common ancestor
    TreeNode commonAncestor = null;
    public static void main(String[] args) {
        Integer[] bst = {6,2,8,0,4,7,9,null,null,3,5};
        TreeNode root = TreeNode.buildTreeFromBFS(bst);
        TreeNode p = new TreeNode(2);
        TreeNode q = new TreeNode(4);

        LowestCommonAncestorOfaBST obj = new LowestCommonAncestorOfaBST();
        System.out.println(obj.lowestCommonAncestor(root,p,q).val);
    }

    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        commonAncestor = root;
        helper(root,p,q);
        return commonAncestor;
    }

    public void helper(TreeNode root, TreeNode p, TreeNode q){
        //Base case
        if(root == null){
            return;
        }

        //assigning lca to root as the current root always be lca
        commonAncestor = root;

        //if either p or q's value is equal to the root the return
        if(root.val == p.val || root.val == q.val){
            return;
        }
        //if root value is less than both p & q then go right
        else if(root.val < p.val && root.val < q.val){
            helper(root.right,p,q);
        }
        //if root value is greater that both p & q then go left
        else if(root.val > p.val && root.val > q.val){
            helper(root.left,p,q);
        }
    }

    /*
    if p and q < root → go left
    if p and q > root → go right
    if p < root < q or p > root > q → root is lowest common ancestor
     */
    public TreeNode lowestCommonAncestorBetter(TreeNode root, TreeNode p, TreeNode q) {
        TreeNode lca = root;

        while (root != null) {
            if (root.val < p.val && root.val < q.val) {
                root = root.right;
            } else if (root.val > p.val && root.val > q.val) {
                root = root.left;
            } else {
                lca = root;
                break;
            }
        }

        return lca;
    }
}
