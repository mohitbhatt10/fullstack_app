package com.leetcode.practice.array.and.strings;

public class BestTimeToBuyAndSellStock {

	public static void main(String[] args) {

		BestTimeToBuyAndSellStock obj = new BestTimeToBuyAndSellStock();
		int[] prices = { 7, 1, 5, 3, 6, 4 };
		System.out.println(obj.maxProfit(prices));

	}
	
	/*
	 * Have 2 local variables(2 pointers), 1st is for checking the minimum buy-price 
	 * and 2nd is for maintaining the max-profit 
	 * only a single iteration would be enough to get the desired profit.
	 */

	public int maxProfit(int[] prices) {
		int buyingPrice = prices[0];
		int maxProfit = 0;
		for(int i=1 ; i<prices.length; i++) {
			if(buyingPrice>prices[i]) {
				buyingPrice = prices[i];
			}
			maxProfit = Math.max(maxProfit, prices[i]-buyingPrice);
		}
		return maxProfit;
	}

	public int maxProfitUsingBrootForce(int[] prices) {
		
		int maxProfit = 0;
		for(int i=0;i<prices.length-1;i++) {
			for(int j=i+1;j<prices.length;j++) {
				int profit = prices[j] - prices[i];
				if(profit > maxProfit) {
					maxProfit = profit;
				}
			}
		}
		return maxProfit;
	}

}
