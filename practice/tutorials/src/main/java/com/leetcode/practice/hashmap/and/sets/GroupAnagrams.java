package com.leetcode.practice.hashmap.and.sets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupAnagrams {

	public static void main(String[] args) {

		
		String[] strs = {"eat","tea","tan","ate","nat","bat"};//{"a"};//{"eat","tea","tan","ate","nat","bat"};
		GroupAnagrams obj = new GroupAnagrams();
		System.out.println(obj.groupAnagrams(strs));
		

	}
	
	public List<List<String>> groupAnagrams(String[] strs) {
        
		if(strs.length == 1) {
			List<String> innerList = new ArrayList<>();
			innerList.add(strs[0]);
			List<List<String>> wholeString = new ArrayList<>();
			wholeString.add(innerList);
			return wholeString;
		}
		
		Map<String,List<String>> map = new HashMap<>();
		
		for(int i=0; i<strs.length; i++) {
			String frequencyString = getFrequencyString(strs[i]);
			
			if(map.containsKey(frequencyString)) {
				List<String> existingList = map.get(frequencyString);
				existingList.add(strs[i]);
			}
			else {
				List<String> newList = new ArrayList<>();
				newList.add(strs[i]);
				map.put(frequencyString, newList);
			}
		}
		
		return new ArrayList<>(map.values());
    }

	private String getFrequencyString(String str) {
		
		int[] arr = new int[26];
		
		for(int i =0; i<str.length();i++) {
			int charIndex = str.charAt(i)-'a';
			arr[charIndex]++;
		}
		StringBuilder sb = new StringBuilder("");
		
		for(int i =0; i< arr.length;i++) {
			if(arr[i] != 0) {
				sb.append((char)(i+'a'));
				sb.append(arr[i]);
			}
		}
		return sb.toString();
	}
}
