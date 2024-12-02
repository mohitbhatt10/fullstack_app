package com.leetcode.practice.binary.search;

import java.util.Arrays;

/*
 * Koko(an ape) loves to eat bananas. There are n piles of bananas, the ith pile has piles[i] bananas. The guards have gone and will come back in h hours.

Koko can decide her bananas-per-hour eating speed of k. Each hour, she chooses some pile of bananas and eats k bananas from that pile. If the pile has less than k bananas, she eats all of them instead and will not eat any more bananas during this hour.

Koko likes to eat slowly but still wants to finish eating all the bananas before the guards return.

Return the minimum integer k such that she can eat all the bananas within h hours.

 

Example 1:

Input: piles = [3,6,7,11], h = 8
Output: 4
Example 2:

Input: piles = [30,11,23,4,20], h = 5
Output: 30
Example 3:

Input: piles = [30,11,23,4,20], h = 6
Output: 23
 * 
 */

public class KokoEatingBananas {

	public static void main(String[] args) {
		
		int[] piles = {30,11,23,4,20};
		int h = 6;
		
		System.out.println(minEatingSpeed(piles, h));
		

	}
	
	public static int minEatingSpeed(int[] piles, int h) {
		
		// Minimum value of hours that can be considered
		int minBanana = 1;
		// Maximum value of hours which is equals to the max value in the piles array
		int maxBanana = Arrays.stream(piles).max().getAsInt();
		
		
		// answer lies between 1 to max(piles) so applying Binary search 
		while(minBanana < maxBanana) {
			int mid = (minBanana + maxBanana)/2;
			
			// if its edible with mid hours then ignore the right sub-array and just search in left one 
			if(isEdibleInGivenHours(piles, h, mid)) {
				maxBanana = mid;
			}
			// if not edible under mid hours then ignore the left sub-array and just search in right one.
			else {
				minBanana = mid+1;
			}
		}
		
		return minBanana; // can be returned as maxBanana hours as both are same after above loop;
    }
	
	// this function calculates whether the piles of banana can be eaten in given hours (m) when max hours are (h)
	private static boolean isEdibleInGivenHours(int [] piles, int h, int m) {
		
		int totalHours = 0;
		for(int i =0;i<piles.length; i++) {
			totalHours += Math.ceil((double)piles[i]/m); 
		}
		
		return totalHours <= h;
	}
}
