package com.leetcode.practice.heaps;

import java.util.Comparator;
import java.util.PriorityQueue;

public class KthLargestElementInAnArray {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] nums = {3,2,3,1,2,4,5,5,6};
		int k = 4;
		
		System.out.println(findKthLargest(nums, k));
	}
	
	public static  int findKthLargest(int[] nums, int k) {
        
		PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.reverseOrder());
		
		for(int i : nums) {
			pq.add(i);
		}
		
		int i=1;
		while(i<k) {
			pq.poll();
			i++;
		}
		
		return pq.peek();
    }
}
