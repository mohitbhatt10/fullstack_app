package com.mb.tutorials.ll;

class Node {
	int data;
	Node next;

	public Node(int data) {
		this.data = data;
		this.next = null;
	}
}

// Define a LinkedList class to manage the linked list
class LinkedList {
	Node head;

	// Constructor to initialize an empty linked list
	public LinkedList() {
		head = null;
	}

	// Method to add a new element to the end of the linked list
	public void add(int data) {
		Node newNode = new Node(data);
		if (head == null) {
			head = newNode;
		} else {
			Node current = head;
			while (current.next != null) {
				current = current.next;
			}
			current.next = newNode;
		}
	}

	// Method to display the elements of the linked list
	public void display() {
		Node current = head;
		System.out.print("Linked List: ");
		while (current != null) {
			System.out.print(current.data + " -> ");
			current = current.next;
		}
		System.out.println("null");
	}

	public int delete(int data) {
		int toBedeleted = -1;
		if (head == null) {
			return toBedeleted;
		} else {
			Node current = head;
			while (current != null) {
				if (current.data == data) {
					toBedeleted = current.data;
					current = null;
				}
				current = current.next;
			}
		}
		return toBedeleted;
	}
}
