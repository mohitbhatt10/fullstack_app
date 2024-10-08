package com.leetcode.practice.linkedlist;

public class MiddleOfTheLinkedList {

	public static void main(String[] args) {
		
		ListNode head = new ListNode(1);
		head.addNode(2, head);
		head.addNode(3, head);
		head.addNode(4, head);
		head.addNode(5, head);
		head.addNode(6, head);
		
		System.out.println(middleNode(head).val);

	}
	
	public static ListNode middleNode(ListNode head) {
		
		if(head == null) {
			return head;
		}
		
		ListNode slow = head;
		ListNode fast = head;
		
		while(fast != null && fast.next != null) {
			slow = slow.next;
			fast = fast.next.next;
		}
		
		return slow;
		
    }

}
