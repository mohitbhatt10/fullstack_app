package com.mb.tutorials.lru;

import java.util.*;

class LeastRecentlyUsedImpl {

	Set<Integer> cache;
	int capacity;

	public LeastRecentlyUsedImpl(int capacity) {
		this.cache = new LinkedHashSet<>(capacity);
		this.capacity = capacity;
	}

	public boolean get(int key) {
		if (!cache.contains(key))
			return false;
		cache.remove(key);
		cache.add(key);
		return true;
	}

	public void getValue(int key) {
		if (!get(key))
			put(key);
	}

	public void display() {
		LinkedList<Integer> list = new LinkedList<>(cache);
		Iterator<Integer> itr = list.descendingIterator();

		while (itr.hasNext())
			System.out.print(itr.next() + " ");
	}

	public void put(int key) {

		if (cache.size() == capacity) {
			int firstKey = cache.iterator().next();
			cache.remove(firstKey);
		}

		cache.add(key);
	}

	public static void main(String[] args) {
		LeastRecentlyUsedImpl obj = new LeastRecentlyUsedImpl(4);
		obj.getValue(4);
		obj.getValue(5);
		obj.getValue(6);
		obj.getValue(4);
		obj.getValue(7);
		obj.getValue(5);
		obj.display();
	}
}