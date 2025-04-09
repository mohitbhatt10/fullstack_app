package com.leetcode.practice.heaps;

import java.util.PriorityQueue;

import com.leetcode.practice.linkedlist.ListNode;

public class MergeKSortedLists {

	public static void main(String[] args) {
		
		ListNode l1 = new ListNode();
		l1.addNode(1, l1);
		l1.addNode(4, l1);
		l1.addNode(5, l1);
		
		ListNode l2 = new ListNode();
		l2.addNode(1, l2);
		l2.addNode(3, l2);
		l2.addNode(4, l2);
		
		ListNode l3 = new ListNode();
		l3.addNode(2, l3);
		l3.addNode(6, l3);
		
		ListNode[] lists = new ListNode[3];
		lists[0] = l1;
		lists[1] = l2;
		lists[2] = l3;
		
		ListNode result = mergeKLists(lists);
		
		result.display(result);

	}
	
	public static ListNode mergeKLists(ListNode[] lists) {
		
		PriorityQueue<ListNode> minHeap = new PriorityQueue<>((a, b) -> Integer.compare(a.val, b.val));
		
		for(ListNode list: lists) {
			if(list != null)
				minHeap.offer(list);
		}
		
		ListNode dummy = new ListNode(0);
		ListNode cur = dummy;
		
		while(!minHeap.isEmpty()) {
			ListNode node = minHeap.poll();		
            cur.next = node;
            cur = node;
            if (node.next != null) {
                minHeap.offer(node.next);
            }
		}
        
		return dummy.next;
    }
}
