package com.leetcode.practice.array.and.strings;

import java.util.HashMap;
import java.util.Map;

public class MaxFrequentElementFinder {

    public static int findMaxFrequentElement(int[] arr) {
        if (arr == null || arr.length == 0) {
            // Handle empty or null array case
            // Depending on requirements, could throw exception or return a default value
            return Integer.MIN_VALUE; 
        }

        // Step 1: Store element frequencies in a HashMap
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        for (int element : arr) {
            frequencyMap.put(element, frequencyMap.getOrDefault(element, 0) + 1);
        }

        // Step 2: Iterate through the map to find the element with the max frequency
        int maxCount = 0;
        int resultElement = Integer.MIN_VALUE;

        for (Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
            int currentElement = entry.getKey();
            int currentCount = entry.getValue();

            // Check if current count is greater than maxCount
            // OR if counts are equal, check if current element is larger (tiebreaker)
            if (currentCount > maxCount) {
                maxCount = currentCount;
                resultElement = currentElement;
            } else if (currentCount == maxCount) {
                if (currentElement > resultElement) {
                    resultElement = currentElement;
                }
            }
        }

        return resultElement;
    }

    public static void main(String[] args) {
        int[] arr1 = {40, 50, 30, 40, 50, 30, 30}; 
        // Frequency: 40: 2, 50: 2, 30: 3. Result should be 30.
        System.out.println("Array 1: " + findMaxFrequentElement(arr1)); 

        int[] arr2 = {1, 2, 2, 3, 3, 3, 4, 4};
        // Frequency: 1: 1, 2: 2, 3: 3, 4: 2. Result should be 3.
        System.out.println("Array 2: " + findMaxFrequentElement(arr2));

        int[] arr3 = {10, 20, 20, 10, 30};
        // Frequency: 10: 2, 20: 2, 30: 1. Tie between 10 and 20. Result should be 20.
        System.out.println("Array 3: " + findMaxFrequentElement(arr3));
    }
}