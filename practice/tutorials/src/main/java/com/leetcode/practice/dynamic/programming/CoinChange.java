package com.leetcode.practice.dynamic.programming;

/*
 * You are given an integer array coins representing coins of different denominations and an integer amount representing a total amount of money.

Return the fewest number of coins that you need to make up that amount. If that amount of money cannot be made up by any combination of the coins, return -1.

You may assume that you have an infinite number of each kind of coin.

 

Example 1:

Input: coins = [1,2,5], amount = 11
Output: 3
Explanation: 11 = 5 + 5 + 1
Example 2:

Input: coins = [2], amount = 3
Output: -1
Example 3:

Input: coins = [1], amount = 0
Output: 0
 

Constraints:

1 <= coins.length <= 12
1 <= coins[i] <= 231 - 1
0 <= amount <= 104
 * 
 * 
 */

public class CoinChange {

	public static void main(String[] args) {
		
		int[] coins = {1,2,5};
		int amount = 11;
		
		CoinChange obj = new CoinChange();
		System.out.println(obj.coinChange(coins, amount));

	}
	
	public int coinChange(int[] coins, int amount) {
		
		//Edge case
		if(amount <=0) {
			return 0;
		}
		
		//Create an array minCoinsDp where minCoinsDp[i] will store the minimum coins needed for amount i.
		//Size is amount + 1 to include 0 to amount. 
		// Initialize minCoinsDp[0] = 0 as base case. till amount+1 (ammount =11 in example then length will be 12).
		int[] minCoinsDp = new int[amount+1]; 
		
		
		for(int i=1; i<=amount; i++) {
			
			//Initialization of each array element to Max integer
			minCoinsDp[i] = Integer.MAX_VALUE;
			
			//loop over each coin combination
			for(int j=0; j< coins.length; j++) {
				
				// it to calculate min only when the coin value is less or equal to the Ith amount 
				// and the in between combination is not Integer.Max  
				if(coins[j]<= i && minCoinsDp[i-coins[j]] != Integer.MAX_VALUE) {
					minCoinsDp[i] = Math.min(minCoinsDp[i], minCoinsDp[i-coins[j]]+1 );
				}
			}
		}
		
		//Special case when coin required reaches to max value 
		//means there is no coin combination to make that amount then return -1 
		if(minCoinsDp[amount] == Integer.MAX_VALUE) {
			return -1;
		}
		
		// return the final position value as the answer
		return minCoinsDp[amount];
    }

}

/*
 * Step-by-Step Iterations
Below, each row shows the state of minCoinsDp after calculating for amount i:

i	minCoinsDp[0..i] after iteration
0	[0]
1	[0, 1]
2	[0, 1, 1]
3	[0, 1, 1, 2]
4	[0, 1, 1, 2, 2]
5	[0, 1, 1, 2, 2, 1]
6	[0, 1, 1, 2, 2, 1, 2]
7	[0, 1, 1, 2, 2, 1, 2, 2]
8	[0, 1, 1, 2, 2, 1, 2, 2, 3]
9	[0, 1, 1, 2, 2, 1, 2, 2, 3, 3]
10	[0, 1, 1, 2, 2, 1, 2, 2, 3, 3, 2]
11	[0, 1, 1, 2, 2, 1, 2, 2, 3, 3, 2, 3]

Explanation for Each Amount
For each i, you check all coins:
If coins[j] <= i and [minCoinsDp[i - coins[j]]] is not MAX_VALUE, update minCoinsDp[i].
The value at each index is the minimum coins needed for that amount.
Final Answer:
minCoinsDp[11] = 3 (using coins 5, 5, 1).
 * 
 */
