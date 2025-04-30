package com.mb.tutorials;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class FlatMapExamples {
	
	
	public static void main(String[] args) {
		
		List<List<Integer>> lists = Arrays.asList(Arrays.asList(1, 2, 3), Arrays.asList(4, 5, 6));
		
		List<Integer> collect = lists.stream().flatMap(Collection<Integer>::stream).collect(Collectors.toList());
		
		System.out.println(collect);
		
		
	}

}
