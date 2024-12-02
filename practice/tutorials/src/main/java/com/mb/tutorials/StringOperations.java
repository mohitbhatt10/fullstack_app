package com.mb.tutorials;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StringOperations {

	public static void main(String[] args) {
		
		System.out.println(stringOperations("intention", "execution"));
		
	}
	
	public static long stringOperations(String str1, String str2) {
		
		long count = 0;
		
		Map<Character, Long> map1 = str1.chars().mapToObj(ch -> (char) ch)
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		
		Map<Character, Long> map2 = str2.chars().mapToObj(ch -> (char) ch)
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		
		for(Map.Entry<Character, Long> entry : map2.entrySet()) {
			
			if(map1.containsKey(entry.getKey())){
				count += map1.get(entry.getKey()) - entry.getValue();
			}
			else {
				count += entry.getValue();
			}
		}
		
		return count;
	}

}
