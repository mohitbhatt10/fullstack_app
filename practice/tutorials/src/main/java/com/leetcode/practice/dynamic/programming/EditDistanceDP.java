package com.leetcode.practice.dynamic.programming;

public class EditDistanceDP {

	public static void main(String[] args) {
		
		String word1 = "horse";
        String word2 = "ros";
        
        System.out.println("Minimum operations to convert \"" + word1 + "\" to \"" + word2 + "\": " 
                            + minDistance(word1, word2));

	}
	
	public static int minDistance(String word1, String word2) {
		
		
		int m = word1.length();
		int n = word2.length();
		
		int[][] dp = new int[m+1][n+1];
		
		for(int i=0; i<=m ;i++) {
			dp[i][0] = i;
		}
		
		for(int j= 0; j<=n ; j++) {
			dp[0][j] = j;
		}
		
		for(int i = 1; i<= m ;i++) {
			for(int j = 1; j<= n; j++) {
				
				if(word1.charAt(i-1) == word2.charAt(j-1)) {
					dp[i][j] = dp[i-1][j-1];
				}
				else {
					int top = dp[i-1][j];
					int left = dp[i][j-1];
					int digonal = dp[i-1][j-1];
					
					dp[i][j] = Math.min(top, Math.min(left, digonal)) +1;
				}
			}
		}
			
		return dp[m][n];	
    }

}
