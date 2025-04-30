package com.leetcode.practice.trees;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/*
Given the root of a binary tree, return the length of the diameter of the tree.

The diameter of a binary tree is the length of the longest path between any two nodes in a tree. This path may or may not pass through the root.

The length of a path between two nodes is represented by the number of edges between them.



Example 1:


Input: root = [1,2,3,4,5]
Output: 3
Explanation: 3 is the length of the path [4,2,1,3] or [5,2,1,3].
Example 2:

Input: root = [1,2]
Output: 1


Constraints:

The number of nodes in the tree is in the range [1, 104].
-100 <= Node.val <= 100
 */
public class DiameterOfBinaryTree {

    int d =0;
    public static void main(String[] args) {

        Integer[] bfs = {1,2,3,4,5};
        TreeNode root = TreeNode.buildTreeFromBFS(bfs);

        DiameterOfBinaryTree obj = new DiameterOfBinaryTree();
        System.out.println(obj.diameterOfBinaryTree(root)); // Iterative approach

        System.out.println(obj.diameterOfBinaryTreeRecursion(root)); // Recursive approach

    }

    public int diameterOfBinaryTree(TreeNode root) {

        Map<TreeNode, Integer> map = new HashMap<>(); // To memoize and track which node has how much depth
        Stack<TreeNode> stack = new Stack<>(); // To store each node in BFS manner
        int diameter = 0; // to track the max diameter of entire tree

        // if root is not null then simply push it into the stack
        if(root != null){
            stack.push(root);
        }

        //Loop it till its empty, will fill this within loop itself
        while(!stack.isEmpty()){
            //To get top element of stack without poping it.
            TreeNode node = stack.peek();

            // if left of the node is not null, and it's not already traversed then push it into the stack for later operation
            if(node.left != null && !map.containsKey(node.left)){
                stack.push(node.left);
            }
            //same applies for right as mentioned above.
            else if(node.right != null && !map.containsKey(node.right)){
                stack.push(node.right);
            }
            // if both above conditions are false
            // means either left of right is null or it's already contained in map
            // pop out that top of stack
            // calculate left & right depths
            // put that node in map with max of right/left depths +1
            // diameter would be the max of left+right or its previous value
            else{
                stack.pop();
                int leftDepth = map.getOrDefault(node.left,0);
                int rightDepth = map.getOrDefault(node.right,0);

                map.put(node, Math.max(leftDepth,rightDepth) +1);
                diameter = Math.max(diameter, leftDepth +rightDepth);
            }
        }
        return diameter;
    }

    public int diameterOfBinaryTreeRecursion(TreeNode root) {
        if(root == null) return 0;
        heightRecursion(root);
        return d;
    }
    private int heightRecursion (TreeNode root){
        if(root == null) return 0;
        int l = heightRecursion(root.left);
        int r = heightRecursion(root.right);
        d= Math.max(d,l+r); // setting diameter which is a global variable.
        return Math.max(l,r)+1; // it will return the height of the entire tree but not needed as such in final result.
    }


}
