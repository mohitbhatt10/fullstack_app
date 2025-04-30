package com.mb.tutorials;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ListStreamsExamples {

    public static void main(String[] args) {
        List<Integer> nums = Arrays.asList(4, 5, 6, 5, 4, 3, 5, 4, 2, 2, 2, 1);

        List<Integer> sortedKeys = getSortedKeysByFrequency(nums);
        System.out.println(sortedKeys);
    }

    public static List<Integer> getSortedKeysByFrequency(List<Integer> nums){

        // Step 1: Count frequency
        Map<Integer, Integer> freqMap = nums.stream()
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.summingInt(count->1)
                ));

        // Step 2: Sort keys based on frequency desc, then integer value asc
        return freqMap.entrySet().stream()
                .sorted(Map.Entry.<Integer, Integer>comparingByValue()
                        .reversed()
                        .thenComparing(Map.Entry.<Integer, Integer>comparingByKey()))
                .map(Map.Entry::getKey).collect(Collectors.toList());

    }
}
