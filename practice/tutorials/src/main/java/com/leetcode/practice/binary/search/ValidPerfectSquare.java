package com.leetcode.practice.binary.search;

public class ValidPerfectSquare {

	public static void main(String[] args) {
		int num = 2147483647;
		System.out.println(isPerfectSquare(num));

	}
	
	public static boolean isPerfectSquare(int num) {
        
		int l = 1;
		int h = num;
		
		while(l<=h) {
			int m = l+(h-l)/2;
			long tempSquare = (long)m*m;
			if(tempSquare == num) {
				return true;
			}
			else if(tempSquare < num ) {
				l = m+1;
			}
			else {
				h = m-1; 
			}
		}
		
		
		return false;
    }

}
