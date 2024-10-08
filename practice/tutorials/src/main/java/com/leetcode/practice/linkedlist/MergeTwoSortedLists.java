package com.leetcode.practice.linkedlist;

public class MergeTwoSortedLists {

	public static void main(String[] args) {
		
		ListNode list1 = new ListNode(1);
		list1.addNode(2, list1);
		list1.addNode(4, list1);
		
		ListNode list2 = new ListNode(1);
		list2.addNode(3, list2);
		list2.addNode(4, list2);
		//list2.addNode(2, list2);
		
		System.out.print("List1: ");list1.display(list1);
		System.out.print("List2: ");list2.display(list2);
		
		ListNode mergerListNode = mergeTwoListsMainAppraouch(list1, list2);

		mergerListNode.display(mergerListNode);

	}
	
	public static ListNode mergeTwoLists(ListNode list1, ListNode list2) {
  	  
  	  if(list1 == null && list2 != null) {
  		  return list2;
  	  }
  	  
  	  if(list1 != null && list2 == null) {
  		  return list1;
  	  }
  	  
  	  if(list1 == null && list2 == null) {
  		  return null;
  	  }
  	  
  	  ListNode current1 = list1;
  	  ListNode current2 = list2;
  	  ListNode mergedListHead = new ListNode();
  	  ListNode mergerCurrent = mergedListHead;
  	  
  	  while(current1 != null || current2 != null) {
  		  if((current1 != null && current2 != null && current1.val<= current2.val) || (current2 == null)) {
  			  mergerCurrent.val = current1.val;
  			  current1 = current1.next;
  		  }
  		  else if ((current1 != null && current2 != null && current1.val> current2.val) || (current1 == null)) {
  			  mergerCurrent.val = current2.val;
  			  current2 = current2.next;
  		  }
  		  if((current1 != null) || (current2 != null)) {
  			  mergerCurrent.next = new ListNode();
  			  mergerCurrent = mergerCurrent.next; 
  		  }
  	  }
  	  
  	  return mergedListHead;
    }

	public static ListNode mergeTwoListsMainAppraouch(ListNode list1, ListNode list2) {
		
		if(list1 == null && list2 != null) {
	  		  return list2;
	  	}
		
		ListNode current1 = list1;
	  	ListNode current2 = list2;
		ListNode mergedListHead = new ListNode();
	  	ListNode mergedCurrent = mergedListHead;
	  	
	  	while(current1 != null && current2 != null) {
	  		if(current1.val<current2.val) {
	  			mergedCurrent.next = current1;
	  			current1 = current1.next;
	  		}
	  		else {
	  			mergedCurrent.next = current2;
	  			current2 = current2.next;
	  		}
	  		mergedCurrent = mergedCurrent.next;
	  	}
	  	
	  	mergedCurrent.next = current1 != null ? current1 : current2;
	  	
	  	return mergedListHead.next;
	}
}
