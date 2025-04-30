package com.leetcode.practice.dynamic.programming;

public class JumpGame {

	public static void main(String[] args) {
		
		JumpGame game = new JumpGame();

        int[] nums1 = {2, 3, 1, 1, 4};
        int[] nums2 = {3, 2, 1, 0, 4};

        System.out.println("Can jump from nums1? " + game.canJump(nums1)); // true
        System.out.println("Can jump from nums2? " + game.canJump(nums2)); // false

	}
	
	public boolean canJump(int[] nums) {
        int maxReach = 0; // maximum index we can reach

        for (int i = 0; i < nums.length; i++) {
            if (i > maxReach) {
                // if current index is beyond the furthest we can reach, return false
                return false;
            }
            maxReach = Math.max(maxReach, i + nums[i]);
        }

        return true;
    }
}
