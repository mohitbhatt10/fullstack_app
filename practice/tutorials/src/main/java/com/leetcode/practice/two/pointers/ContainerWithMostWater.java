package com.leetcode.practice.two.pointers;

public class ContainerWithMostWater {

	public static void main(String[] args) {
		
		int[] height = {1,8,6,2,5,4,8,3,7};
		ContainerWithMostWater obj = new ContainerWithMostWater();
		System.out.println(obj.maxArea(height));
	}
	
	public int maxArea(int[] height) {
        
		int n = height.length;
		int l = 0;
		int r = n-1;
		int maxArea = Integer.MIN_VALUE;
		
		while(l<r) {
			
			int currentArea = Math.min(height[l], height[r])* (r-l);
			maxArea = Math.max(maxArea, currentArea);
			if(height[l]<height[r])
				l++;
			else
				r--;
		}
		
		return maxArea;
    }

}
