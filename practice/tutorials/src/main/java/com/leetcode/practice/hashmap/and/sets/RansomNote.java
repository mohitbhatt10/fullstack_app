package com.leetcode.practice.hashmap.and.sets;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RansomNote {

	public static void main(String[] args) {

		String ransomNote = "baa";
		String magazine = "aa";

		RansomNote obj = new RansomNote();
		System.out.println(obj.canConstruct(ransomNote, magazine));

	}

	public boolean canConstructOldWay(String ransomNote, String magazine) {

		Map<Character, Long> ransomCharCount = ransomNote.chars().mapToObj(letter -> (char) letter)
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		Map<Character, Long> magazineCharCount = magazine.chars().mapToObj(letter -> (char) letter)
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		for (Map.Entry<Character, Long> entry : ransomCharCount.entrySet()) {
			if (magazineCharCount.containsKey(entry.getKey())
					&& magazineCharCount.get(entry.getKey()) >= entry.getValue()) {
				continue;
			}
			return false;
		}
		return true;
	}
	
	public boolean canConstruct(String ransomNote, String magazine) {

		Map<Character, Long> magazineCharCount = magazine.chars().mapToObj(letter -> (char) letter)
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		for(Character letter : ransomNote.toCharArray()) {
			if(!magazineCharCount.containsKey(letter)) {
				return false;
			}
			else if(magazineCharCount.get(letter) ==  1) {
				magazineCharCount.remove(letter);
			}
			else {
				magazineCharCount.put(letter, magazineCharCount.get(letter) - 1);
			}
		}
		return true;
	}
}
