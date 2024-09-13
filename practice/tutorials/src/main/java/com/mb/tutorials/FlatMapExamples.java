package com.mb.tutorials;

import java.util.List;
import java.util.stream.Collectors;

public class FlatMapExamples {
	
	
	public static void main(String[] args) {
		
		List<List<Integer>> lists = List.of(List.of(1,2,3),List.of(4,5,6));
		
		List<Integer> collect = lists.stream().flatMap(list -> list.stream()).collect(Collectors.toList());
		
		System.out.println(collect);
		
		
	}

}
