package com.leetcode.practice.linkedlist;

import java.util.HashSet;
import java.util.Set;

public class LinkedListCycle {

	public static void main(String[] args) {
		ListNode head = new ListNode(1);
		head.addNode(2, head);
		head.addNode(3, head);
		head.addNode(4, head);
		head.addNode(5, head);
		head.createCycle(head, 2);

		
		System.out.println(hasCycle(head));
		System.out.println(hasCycleHashSetApproach(head));
	}
	
	/*
	 *  The problem of detecting a cycle in a linked list is commonly solved using
	 *  Floyd's Cycle Detection Algorithm,
	 *  also known as the Tortoise and Hare Algorithm. 
	 */
	public static boolean hasCycle(ListNode head) {
		
		if(head == null) {
			return false;
		}
		
		ListNode slow = head;
		ListNode fast = head;

		while(fast != null && fast.next != null) {
			
			slow = slow.next;
			fast = fast.next.next;
			
			if(slow == fast) {
				return true;
			}
		}
		return false;
    }
	
	/*
	 * This is the basic Set approach which would keep the track of every node present in the list
	 * and would check the given node already visited or not 
	 * based on that we can say there exists a loop.
	 */
	public static boolean hasCycleHashSetApproach(ListNode head) {
		
		if(head == null) {
			return false;
		}
		
		Set<ListNode> set = new HashSet<>();
		ListNode current = head;

		while(current != null) {		
			if(set.add(current)) {
				current = current.next;
			}
			else {
				return true;
			}
		}
		return false;
    }
}
