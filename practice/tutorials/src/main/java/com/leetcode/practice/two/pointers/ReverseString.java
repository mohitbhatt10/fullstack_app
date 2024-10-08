package com.leetcode.practice.two.pointers;

import java.util.Arrays;

public class ReverseString {

	public static void main(String[] args) {
		
		char[] s = {'h','e','l','l','o','m'};
		ReverseString obj = new ReverseString();
		obj.reverseString(s);

	}

	public void reverseString(char[] s) {
		
		int left = 0;
		int right = s.length-1;
		
		while(left<=right) {
			char temp = s[left];
			s[left] = s[right];
			s[right] = temp;
			left++;
			right--;
			
		}
		System.out.println(Arrays.toString(s));
	}

}
