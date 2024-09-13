package com.mb.tutorials;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class GiftPerchase {

	
	/*
	 * What is the maximum number of gifts the uncle can buy for his nephew, given a
	 * list of gifts available on specific days with their corresponding prices? The
	 * uncle receives 1 credit every day, and the list consists of tuples in the
	 * format [day, giftPrice]. Determine the maximum number of gifts the uncle can
	 * purchase with the available amount of credits in the bank account.
	 * 
	 * NOTE: Only one gift can be purchased in a day
	 * 
	 * Example 1 - Sample Input: [ [1,3], [2,3], [3,2], [4,1], [5,3] ]
	 * 
	 * Output: 2 Example 2 - Sample Input: [ [100,100], [102,3], [103,4], [104,5],
	 * [105,6], [107,8], [109,10] ]
	 * 
	 * Output: 6
	 * 
	 * Example 3 - Sample Input: [ [1,2], [2,3], [3,2], [4,1], [5,3] ]
	 * 
	 * Output: 2
	 * 
	 * Example 4 - Sample Input: [ [1,1], [2,2], [3,3], [4,4] ]
	 * 
	 * Output: 1
	 * 
	 * Example 5 - [[1, 5], [2, 4], [3, 3], [4, 2], [5, 1]]
	 * 
	 * Output: 2
	 */
	
	public static void main(String[] args) {
		
		int credit = 0;
		int giftPurchase = 0;
		boolean flag = false; 
		Map<Integer,Integer> map = new LinkedHashMap<>();
		Scanner sc = new Scanner(System.in);
		String input = sc.nextLine();
		String[] inputArr = input.replaceAll("\\[|\\]", "").split(", ");
		
		for(int i=0;i<inputArr.length;i=i+2) {
            int key = Integer.parseInt(inputArr[i]);
            int value = Integer.parseInt(inputArr[i+1]);
            map.put(key, value);
		}
		
		for(Map.Entry<Integer, Integer> entry : map.entrySet()) {
			
			if(!flag) {
				credit = entry.getKey();
				flag = true;
			}
			if(entry.getValue()<= credit) {
				giftPurchase++;
				credit = credit - entry.getValue();
			}
			credit++;
		}
		
		System.out.println(giftPurchase);
		sc.close();
	}

}
