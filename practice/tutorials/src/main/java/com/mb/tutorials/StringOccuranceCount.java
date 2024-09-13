package com.mb.tutorials;

import java.util.Arrays;

public class StringOccuranceCount {

	public static void main(String[] args) {
		String[] strArr = {"java Ruby struds spring","spring java","python spring"};
		System.out.println(getStringCount(strArr, "spring"));
	}
	
	private static int getStringCount(String[] strArr, String str) {
		
		return (int) Arrays.stream(strArr)
		.flatMap(element -> Arrays.stream(element.split(" ")))
		.filter(element -> element.equals(str))
		.count();
		
	} 
	
	
}
