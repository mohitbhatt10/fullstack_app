package com.mb.tutorials;

import java.util.*;
public class FrequentAccessTracker {
    private Map<String, Integer> freqMap; // key -> frequency
    private TreeMap<Integer, TreeSet<String>> countMap; // frequency -> keys

    public FrequentAccessTracker() {
        freqMap = new HashMap<>();
        countMap = new TreeMap<>();
    }

    public void add(String key) {
        if (freqMap.containsKey(key)) return;

        freqMap.put(key, 1);
        countMap.computeIfAbsent(1, k -> new TreeSet<>()).add(key);
    }

    public void access(String key) {
        int oldFreq = freqMap.getOrDefault(key, 0);
        int newFreq = oldFreq + 1;
        freqMap.put(key, newFreq);

        // Remove from old frequency set
        if (oldFreq > 0) {
            TreeSet<String> oldSet = countMap.get(oldFreq);
            oldSet.remove(key);
            if (oldSet.isEmpty()) countMap.remove(oldFreq);
        }

        // Add to new frequency set
        countMap.computeIfAbsent(newFreq, k -> new TreeSet<>()).add(key);
    }

    public List<String> get_most_frequent(int n) {
        List<String> result = new ArrayList<>();
        
        // Traverse from highest frequency
        for (int freq : countMap.descendingKeySet()) {
            TreeSet<String> keys = countMap.get(freq);
            for (String key : keys) {
                if (result.size() == n) return result;
                result.add(key);
            }
        }
        
        return result;
    }

    public static void main(String[] args) {
        FrequentAccessTracker tracker = new FrequentAccessTracker();

        tracker.add("apple");
        tracker.add("banana");
        tracker.access("apple");
        tracker.access("banana");
        tracker.access("banana");
        tracker.access("cherry"); // Not added, should count as 1

        List<String> top2 = tracker.get_most_frequent(2);
        System.out.println(top2); // Expected: [banana, apple]
    }
}