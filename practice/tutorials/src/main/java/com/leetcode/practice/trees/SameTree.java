package com.leetcode.practice.trees;

import java.util.LinkedList;
import java.util.Queue;

/*
Given the roots of two binary trees p and q, write a function to check if they are the same or not.

Two binary trees are considered the same if they are structurally identical, and the nodes have the same value.



Example 1:


Input: p = [1,2,3], q = [1,2,3]
Output: true
Example 2:


Input: p = [1,2], q = [1,null,2]
Output: false
Example 3:


Input: p = [1,2,1], q = [1,1,2]
Output: false

 */
public class SameTree {

    public static void main(String[] args) {
        TreeNode p = TreeNode.buildTreeFromBFS(new Integer[]{0,null});
        TreeNode q = TreeNode.buildTreeFromBFS(new Integer[]{0});

        System.out.println(new SameTree().isSameTree(p,q));
    }

    public boolean isSameTree(TreeNode p, TreeNode q) {

        Queue<TreeNode> t1 = new LinkedList<>();
        Queue<TreeNode> t2 = new LinkedList<>();

        t1.add(p);
        t2.add(q);

        while(!t1 .isEmpty() && !t2.isEmpty()){

            TreeNode firstNode = t1.poll();
            TreeNode secondNode = t2.poll();

            if(firstNode == null && secondNode == null){
                continue;
            }
            else if(firstNode == null || secondNode == null || firstNode.val != secondNode.val ){
                return false;
            }

            t1.add(firstNode.left);
            t1.add(firstNode.right);

            t2.add(secondNode.left);
            t2.add(secondNode.right);
        }

        return true;
    }

    public boolean isSameTreeBetter(TreeNode p, TreeNode q) {
        if (p == null && q == null) {
            return true;
        }

        if (p != null && q != null && p.val == q.val) {
            return isSameTree(p.left, q.left) && isSameTree(p.right, q.right);
        }

        return false;
    }


}
