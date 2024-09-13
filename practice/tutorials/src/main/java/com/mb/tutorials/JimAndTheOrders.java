package com.mb.tutorials;

import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class Result1 {

    /*
     * Complete the 'jimOrders' function below.
     *
     * The function is expected to return an INTEGER_ARRAY.
     * The function accepts 2D_INTEGER_ARRAY orders as parameter.
     */

    public static List<Integer> jimOrders(List<List<Integer>> orders) {
        
        Map<Integer,Integer> orderNumberVsServeTimes = new LinkedHashMap<>();
        
        for(int i=0;i<orders.size() ;i++){
            orderNumberVsServeTimes.put(i+1, orders.get(i).get(0) +orders.get(i).get(1));
        }
        
        return new ArrayList<>(sortMapByValuesAndKeys(orderNumberVsServeTimes).keySet());
    }
    
    private static  Map<Integer, Integer> sortMapByValuesAndKeys(Map<Integer, Integer> unsortedMap) {
        // Create a list of entries from the unsorted map
        List<Map.Entry<Integer, Integer>> entryList = new ArrayList<>(unsortedMap.entrySet());

        // Using a custom comparator to sort by values and then by keys if values are the same
        Comparator<Map.Entry<Integer, Integer>> valueComparator = (entry1, entry2) -> {
            int valueComparison = entry1.getValue().compareTo(entry2.getValue());
            return valueComparison == 0 ? entry1.getKey().compareTo(entry2.getKey()) : valueComparison;
        };

        // Sorting the list of entries using the custom comparator
        entryList.sort(valueComparator);

        // Creating a LinkedHashMap to store the sorted entries
        Map<Integer, Integer> sortedMap = new LinkedHashMap<>();

        // Adding sorted entries to the LinkedHashMap
        for (Map.Entry<Integer, Integer> entry : entryList) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

}

public class JimAndTheOrders {
    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        int n = Integer.parseInt(bufferedReader.readLine().trim());

        List<List<Integer>> orders = new ArrayList<>();

        IntStream.range(0, n).forEach(i -> {
            try {
                orders.add(
                    Stream.of(bufferedReader.readLine().replaceAll("\\s+$", "").split(" "))
                        .map(Integer::parseInt)
                        .collect(toList())
                );
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        List<Integer> result = Result1.jimOrders(orders);

        System.out.println(result);

        bufferedReader.close();
    }
}