package com.mb.tutorials;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

/*
 * https://www.hackerrank.com/challenges/minimum-loss/problem
 * Minimum Loss problem on HackerRank.
 * 
 * Lauren has a chart of distinct projected prices for a house over the next several years. She must buy the house in one year and sell it in another, and she must do so at a loss. She wants to minimize her financial loss.
	Example
	price : [20, 15, 8, 2, 12]

	Her minimum loss is incurred by purchasing in year 2 at price[1]
    and reselling in year price[4]  at 15-12 Return 3.
 * 
 * 
 * 
 */


public class MinimumLossWithTreeSet {

    public static void main(String[] args) {
        List<Long> list = Arrays.asList(20l, 15l, 8l, 2l, 12l);
        System.out.println(minimumLoss(list)); // Output should be 3
    }

    public static long minimumLoss(List<Long> price) {

        TreeSet<Long> priceSet = new TreeSet<>();
        long min = Long.MAX_VALUE;
        for (int i = 0; i < price.size(); i++) {
            Long higher = priceSet.higher(price.get(i));
            if (higher != null) {
                min = Math.min(higher - price.get(i), min);     
            }
            priceSet.add(price.get(i));
        }
        
        return min;
    }
}