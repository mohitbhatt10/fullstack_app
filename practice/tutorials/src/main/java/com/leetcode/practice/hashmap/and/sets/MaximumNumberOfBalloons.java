package com.leetcode.practice.hashmap.and.sets;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MaximumNumberOfBalloons {
	
	public static void main(String[] args) {
		String text = "loonbalxballpoon";
		MaximumNumberOfBalloons obj = new MaximumNumberOfBalloons();
		System.out.println(obj.maxNumberOfBalloons(text));
	}

	public int maxNumberOfBalloons(String text) {
        
		Map<Character, Integer> charCounts = new HashMap<>();
		
		charCounts.put('b', 0);
		charCounts.put('a', 0);
		charCounts.put('l', 0);
		charCounts.put('o', 0);
		charCounts.put('n', 0);
		
		for(String letter: text.split("")) {
			if(charCounts.containsKey(letter.charAt(0))) {
				charCounts.put(letter.charAt(0), charCounts.get(letter.charAt(0))+1);
			}
		}
		
		if(charCounts.containsKey('l')) {
			charCounts.put('l', charCounts.get('l')/2);
		}
		if(charCounts.containsKey('o')) {
			charCounts.put('o', charCounts.get('o')/2);
		}
		
		return Collections.min(charCounts.values());
    }
	
}
