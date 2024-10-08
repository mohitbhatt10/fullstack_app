package com.leetcode.practice.linkedlist;

public class RemoveNthNodeFromEndOfList {

	public static void main(String[] args) {
		
		ListNode head = new ListNode(1);
		head.addNode(2, head);
		/*head.addNode(3, head);
		head.addNode(4, head);
		head.addNode(5, head);
		head.addNode(6, head);
		head.addNode(7, head);
		*/
		ListNode newHead = removeNthFromEndOtherApproach(head, 2);
		newHead.display(newHead);

	}
	
	public static ListNode removeNthFromEnd(ListNode head, int n) {
     
		ListNode dummy = new ListNode(Integer.MIN_VALUE);
		dummy.next = head; 
		
		ListNode firstPointer = dummy;
		ListNode secondPointer = dummy;
		
		for(int i=0;i<n;i++) {
			secondPointer = secondPointer.next;
		}
		
		while(secondPointer.next != null) {
			firstPointer = firstPointer.next;
			secondPointer = secondPointer.next;
		}
		
		firstPointer.next = firstPointer.next.next;
		
		return dummy.next;
    }
	
	public static ListNode removeNthFromEndOtherApproach(ListNode head, int n) {
		
		if(head == null || head.next == null) {
			return null;
		}
	    
		int totalLength = 0;
		
		ListNode current = head;
		while(current != null) {
			current = current.next;
			totalLength++;
		}
		
		int positionFromStart = totalLength - n;
		current = head;
		ListNode prev = null;
		for(int i =0;i <positionFromStart ;i++) {
			prev= current;
			current = current.next;
		}
		if(prev != null) {
			prev.next = current.next;
			return head;
		}
		else {
			return head.next;
		}
    }

}
