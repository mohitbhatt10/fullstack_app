package com.mb.tutorials;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

public class CheckDuplicatesInAList {

	public static void main(String[] args) {
		
		
		List<Integer> list = List.of(1,2,3,4,5,1,2,3);
		
		 Set<Integer> keySet = list.stream()
		.collect(Collectors.groupingBy(num -> num, Collectors.counting()))
		.entrySet().stream().filter(entry -> entry.getValue()> 1 )
		.collect(Collectors.toMap(Entry::getKey, Entry::getValue)).keySet();
		
		
		System.out.println(keySet);
		
	}
}
