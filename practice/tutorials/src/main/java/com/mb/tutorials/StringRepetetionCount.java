package com.mb.tutorials;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StringRepetetionCount {

	public static void main(String[] args) {
		
		
		String s = "there is a tree tree has leaves leaves are green";
		
		Map<String, Long> collect = Arrays.stream(s.split(" ")).collect(Collectors.groupingBy(word-> word, Collectors.counting()));
		
		collect.forEach((word, count) -> {
			System.out.println("Word: "+word+", count: "+count);
		});
		
	}
}

