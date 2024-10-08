package com.mb.tutorials.ll;

public class LinkedListImpl {
	
	
	public static void main(String[] args) {
		
		LinkedList l = new LinkedList();
		l.add(1);
		l.add(2);
		l.add(3);
		l.display();
		l.deleteOtherApproach(4);	
		l.display();
	}

}
