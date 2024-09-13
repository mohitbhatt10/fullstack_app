package com.leetcode.practice.hashmap.and.sets;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JewelsAndStones {

	public static void main(String[] args) {
		
		String jewels = "aA";
		String stones = "aAAbbbb";
		
		JewelsAndStones obj = new JewelsAndStones();
		System.out.println(obj.numJewelsInStones(jewels, stones));
		
	}

	public int numJewelsInStones(String jewels, String stones) {

		int jewelCount = 0;
		Map<Character, Long> charCountingMap = stones.chars().mapToObj(letter -> (char) letter)
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		
		for(Character jewel : jewels.toCharArray()) {
			
			if(charCountingMap.containsKey(jewel)) {
				jewelCount += charCountingMap.get(jewel); 
			}
		}
		
		return jewelCount;
	}

}
