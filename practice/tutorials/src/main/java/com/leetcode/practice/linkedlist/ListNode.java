package com.leetcode.practice.linkedlist;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public class ListNode {
      public int val;
      public ListNode next;
      
      public ListNode() {}
      public ListNode(int val) { this.val = val; }
      public ListNode(int val, ListNode next) { this.val = val; this.next = next; }
      
      public void display(ListNode head) {
    	  ListNode current = head;
    	  while(current != null) {
    		  System.out.print(current.val+"-->");
    		  current = current.next;
    	  }
    	  System.out.println("NULL");
      }
      
      public ListNode deleteDuplicates(ListNode head) {
          ListNode current = head;
          while(current != null && current.next != null) {
          	if(current.val == current.next.val) {
          		current.next = current.next.next;
          	}else {
          		current = current.next;
          	}
          }
          return head;
      }
      
      public void deleteDuplicatesUsingSet(ListNode head) {

          Set<Integer> set = new HashSet<>();
          ListNode current = head;
          ListNode prev = null;

          while (current != null) {
              if (set.contains(current.val)) {
                  prev.next = current.next; // Remove the duplicate
              } else {
                  set.add(current.val);
                  prev = current;
              }
              current = current.next;
          }
      }
      
      public void addNode(int val, ListNode head) {   	  
    	  ListNode newNode = new ListNode(val);
    	  if(head == null) {
    		  head = newNode;
    	  }
    	  else {
    		  ListNode current = head;
    		  while(current.next != null) {
    			  current = current.next;
    		  }
    		  current.next = newNode;
    	  }
      }
      
      public ListNode reverseListWithStack(ListNode head) {
          
    	  if(head == null) {
    		  return null;
    	  }
    	  Stack<Integer> stack = new Stack<>();
    	  
    	  while(head != null) {
    		  stack.push(head.val);
    		  head = head.next;
    	  }
    	  
    	  ListNode newHead = new ListNode(stack.pop());
    	  ListNode newCurrent = newHead;
    	  while(!stack.isEmpty()) {
    		  newCurrent.next = new ListNode(stack.pop());
    		  newCurrent = newCurrent.next;
    	  }
    	  
    	  return newHead;
      }
      
      public ListNode reverseList(ListNode head) {
          
    	  if(head == null) {
    		  return null;
    	  }
    	  if(head.next == null) {
    		  return head;
    	  }
    	  
    	  ListNode current = head;
    	  ListNode previous = null;
    	  
    	  while(current != null) {
    		  ListNode forward = current.next;
    		  current.next = previous;
    		  previous = current;
    		  current = forward;
    	  }
    	  
    	  return previous;
      }
      
      // Method to create a cycle in the linked list at a given position for testing purposes
      public void createCycle(ListNode head, int position) {
          if (head == null) return;

          ListNode temp = head;
          ListNode cycleNode = null;
          int index = 0;

          while (temp.next != null) {
              if (index == position) {
                  cycleNode = temp;
              }
              temp = temp.next;
              index++;
          }

          // Creating the cycle by connecting the last node to the cycle node
          if (cycleNode != null) {
              temp.next = cycleNode;
          }
      }
}