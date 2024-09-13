package com.mb.tutorials;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class RemoveDuplicateFromList {
	
	public static void main(String[] args) {
		
		
		List<Integer> list = List.of(1,2,1,2,3,4,5,6);
		
		List<Integer> distinctElements = list.stream()
		.collect(Collectors.groupingBy(num-> num, LinkedHashMap :: new, Collectors.counting()))
		.entrySet().stream().filter(entry -> entry.getValue() == 1)
		.map(entry -> entry.getKey())
		.collect(Collectors.toList());
		
		System.out.println(distinctElements);
		
		
	}

}
