package com.mb.tutorials;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StreamsExamples {

	public static void main(String[] args) {
		
		List<Integer> list = new ArrayList<>();
		list.add(1);
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(2);
		
		System.out.println(removeDuplicates(list));
		
	}
	
	public static List<Integer> removeDuplicates(List<Integer> list) {
		
		return list.stream().distinct().collect(Collectors.toList());
	}

}
