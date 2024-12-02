package com.mb.tutorials;
import java.util.ArrayList;
import java.util.List;

public class NumberTransformer {
	
	public static void main(String[] args) {
		
		List<Integer> list = List.of(1,2,3,4,5,6,7,8);
		
		System.out.println(filterAndTransform2(list));
		
	}
	
	public static List<Integer> filterAndTransform(List<Integer> numbers) {
    	List<Integer> result = new ArrayList<>();

    	// Process each number in one loop
    	for (int num : numbers) {
        	if (num % 2 != 0) {  // Check if the number is odd
            	result.add(num * 2);  // Double odd numbers
        	}
    	}

    	return result;
	}
	
	public static List<Integer> filterAndTransform2(List<Integer> numbers) {
    	List<Integer> oddNumbers = new ArrayList<>();

    	// First, filter out the even numbers
    	for (int num : numbers) {
        	if (num % 2 != 0) {
            	oddNumbers.add(num);
        	}
    	}

    	// Now, double each of the remaining odd numbers
    	List<Integer> result = new ArrayList<>();
    	for (int odd : oddNumbers) {
        	result.add(odd * 2);
    	}

    	return result;
	}

}
