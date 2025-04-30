package com.leetcode.practice.trees;

import java.util.*;

/*
Given the root of a binary tree, return the level order traversal of its nodes' values. (i.e., from left to right, level by level).

Example 1:

Input: root = [3,9,20,null,null,15,7]
Output: [[3],[9,20],[15,7]]
Example 2:

Input: root = [1]
Output: [[1]]
Example 3:

Input: root = []
Output: []


Constraints:

The number of nodes in the tree is in the range [0, 2000].
-1000 <= Node.val <= 1000
 */
public class BinaryTreeLevelOrderTraversal {
    public static void main(String[] args) {

        TreeNode root = TreeNode.buildTreeFromBFS(new Integer[]{1,2,3,4,null,null,5});
        BinaryTreeLevelOrderTraversal obj = new BinaryTreeLevelOrderTraversal();
        System.out.println(obj.levelOrder(root));
    }

    public List<List<Integer>> levelOrder(TreeNode root) {

        if(root == null){
            return new ArrayList<>();
        }

        List<List<Integer>> result = new ArrayList<>();
        result.add(Collections.singletonList(root.val));
        Queue<TreeNode> queue = new LinkedList<>();
        List<Integer> level = new ArrayList<>();
        queue.add(root);

        int maxLevelElements = 2;
        int currentLevelElements = 0;
        while(!queue.isEmpty()){
            TreeNode current = queue.poll();
            if(current != null){
                TreeNode left = current.left;
                if(left != null){
                    level.add(left.val);
                    queue.add(left);
                }
                TreeNode right = current.right;
                if(right != null) {
                    level.add(right.val);
                    queue.add(right);
                }
            }
            currentLevelElements += 2;
            if(currentLevelElements == maxLevelElements){
                if(!level.isEmpty()){
                    result.add(new ArrayList<>(level));
                }
                maxLevelElements *= 2;
                level.clear();
                currentLevelElements = 0;
            }
        }
        return result;
    }

    public List<List<Integer>> levelOrderBetter(TreeNode root) {

        if (root == null) {
            return new ArrayList<>();
        }
        List<List<Integer>> ans = new ArrayList<>();
        Queue<TreeNode> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            List<Integer> level = new ArrayList<>();
            int n = queue.size();

            for (int i = 0; i < n; i++) {
                TreeNode node = queue.poll();
                if(node != null) {
                    level.add(node.val);
                    if (node.left != null) {
                        queue.add(node.left);
                    }
                    if (node.right != null){
                        queue.add(node.right);
                    }
                }
            }

            ans.add(level);
        }

        return ans;
    }
}
