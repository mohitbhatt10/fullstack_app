package com.leetcode.practice.hashmap.and.sets;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ValidAnagram {

	public static void main(String[] args) {
		
		String s = "anagram";
		String t = "nagaram";
		ValidAnagram obj = new ValidAnagram();
		System.out.println(obj.isAnagram(s, t));

	}
	
	public boolean isAnagram(String s, String t) {

		Map<Character, Long> sCountingMap = s.chars().mapToObj(letter -> (char) letter)
				.collect(Collectors.groupingBy(Function.identity(), TreeMap<Character, Long> :: new , Collectors.counting()));
		
		Map<Character, Long> tCountingMap = t.chars().mapToObj(letter -> (char) letter)
				.collect(Collectors.groupingBy(Function.identity(), TreeMap<Character, Long> :: new , Collectors.counting()));
		
		return sCountingMap.equals(tCountingMap);
	}

}