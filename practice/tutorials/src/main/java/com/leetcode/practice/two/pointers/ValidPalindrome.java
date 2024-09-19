package com.leetcode.practice.two.pointers;

import java.util.stream.Collectors;

public class ValidPalindrome {

	public static void main(String[] args) {
		String s = "A man, a plan, a canal: Panama";
		ValidPalindrome obj = new ValidPalindrome();
		System.out.println(obj.isPalindrome(s));
	}
	
	public boolean isPalindromeOldWay(String s) {
		
		String str = s.chars().mapToObj(ch -> (char)ch)
		.filter(Character::isLetterOrDigit)
		.map(Character::toLowerCase).map(String::valueOf)
		.collect(Collectors.joining());
		
		int n = str.length();
		int l = 0;
		int r = n-1;
		
		while(l<r) {
			if(str.charAt(l) != str.charAt(r)) {
				return false;
			}
			l++;
			r--;
		}
		
		return true;
    }

	
	public boolean isPalindrome(String s) {
		
		
		int n = s.length();
		int l = 0;
		int r = n-1;
		
		while(l<r) {
			if(!Character.isLetterOrDigit(s.charAt(l))) {
				l++;
				continue;
			}
			if(!Character.isLetterOrDigit(s.charAt(r))) {
				r--;
				continue;
			}
			if(Character.toLowerCase(s.charAt(l)) != Character.toLowerCase(s.charAt(r))) {
				return false;
			}
			l++;
			r--;
		}	
		return true;
    }
}
