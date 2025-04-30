package com.leetcode.practice.trees;

import java.util.ArrayList;
import java.util.List;

/*
Given the root of a binary search tree, and an integer k, return the kth smallest value (1-indexed) of all the values of the nodes in the tree.
Example 1:
Input: root = [3,1,4,null,2], k = 1
Output: 1

Example 2:
Input: root = [5,3,6,2,4,null,null,1], k = 3
Output: 3

Constraints:

The number of nodes in the tree is n.
1 <= k <= n <= 104
0 <= Node.val <= 104
 */
public class KthSmallestElementInaBST {
    //This global list would contain the sorted order because we are using BST
    List<Integer> list = new ArrayList<>();
    public static void main(String[] args) {

        KthSmallestElementInaBST obj = new KthSmallestElementInaBST();
        TreeNode root = TreeNode.buildTreeFromBFS(new Integer[]{5,3,6,2,4,null,null,1});
        int k = 3;
        System.out.println(obj.kthSmallest(root,k));
    }
    public int kthSmallest(TreeNode root, int k) {
        dfsInOrder(root);
        //It would return the k-1th indexed element from the list
        return list.get(k-1);
    }

    // InOrder traversal would make sure to add the element in the list as it comes in order.
    public void dfsInOrder(TreeNode root){
        if(root == null) {
            return;
        }
        dfsInOrder(root.left);
        list.add(root.val);
        dfsInOrder(root.right);
    }
}
