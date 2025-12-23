package com.leetcode.practice.array.and.strings;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FindNumberWithMaxOccurrence {

    public static void main(String[] args) {
        int[] numbs = {1,3,2,3,4,1,3,2,3,5,3,5,5,5,5};
        FindNumberWithMaxOccurrence obj = new FindNumberWithMaxOccurrence();
        System.out.println(obj.findMaxOccurrenceUsingMap(numbs));
    }

    public int findMaxOccurrence(int[] numbs) {

        int maxCount = 0;
        int result = -1;

        for(int i=0;i<numbs.length;i++) {
        	int count = 0;
        	for(int j=0;j<numbs.length;j++) {
        		if(numbs[i] == numbs[j]) {
        			count++;
        		}
        	}
        	if(count > maxCount) {
        		maxCount = count;
        		result = numbs[i];
        	}
        }
        return result;
    }

    public int findMaxOccurrenceUsingMap(int[] numbs) {

        Map<Integer, Integer> countMap = new HashMap<>();
        int maxCount = 0;
        int result = -1;

        for(int num : numbs) {
        	countMap.put(num, countMap.getOrDefault(num, 0) + 1);
        	if(countMap.get(num) > maxCount) {
        		maxCount = countMap.get(num);
        		result = num;
        	}
        }

        Map<Integer,Integer> unsortedMap = Arrays.stream(numbs).
                mapToObj(i -> (Integer) i).
                collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(c->1)));

        Map<Integer, Integer> sortedMap = unsortedMap.entrySet()
                .stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey(Comparator.reverseOrder())))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, // Merge function for duplicate keys (not relevant for sorting)
                        LinkedHashMap::new // Ensure insertion order is preserved
                ));

        int max = unsortedMap.entrySet()
                .stream()
                .max(Map.Entry.<Integer, Integer>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey(Comparator.reverseOrder()))).get().getKey();

        System.out.println(sortedMap);
        return max;


    }

}
