package com.leetcode.practice.linkedlist;

public class RemoveDuplicatesFromSortedList {
	
	public static void main(String[] args) {
			
		ListNode head = new ListNode(1);
		head.addNode(1, head);
		head.addNode(2, head);
		head.addNode(3, head);
		head.addNode(3, head);
		head.addNode(5, head);
		head.addNode(5, head);
		head.display(head);
		head.deleteDuplicatesUsingSet(head);
		head.display(head);
		
	}
}
