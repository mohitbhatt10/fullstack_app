package com.leetcode.practice.array.and.strings;

public class FindClosestNumberToZero {
	
	public static void main(String[] args) {
		
		int[] arr =  {-4,-2,4,8,-1,1};
		FindClosestNumberToZero obj = new FindClosestNumberToZero();
		System.out.println(obj.findClosestNumber(arr));
		
	}
	
	public int findClosestNumber(int[] nums) {
        
        int closestDistance = Integer.MAX_VALUE;
        int closestToZero = nums[0];

        for(int i=0;i<nums.length;i++){
            int distance = Math.abs(nums[i] - 0);
            if(distance < closestDistance || (distance == closestDistance && nums[i]>closestToZero)) {
            	closestToZero = nums[i];
            	closestDistance = distance;
            }
        }
        return closestToZero;

    }
}
