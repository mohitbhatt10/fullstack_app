package com.leetcode.practice.dynamic.programming;

public class FibonacciSeriesUsingDP {

	public static void main(String[] args) {
		
		System.out.println(getNthFibonacciUsingDP(8));

	}
	
	public static long getNthFibbonacciNumberUsingReccursion(int n) {
		
		if(n == 1 || n == 2) {
			return 1;
		}
		else {
			return getNthFibbonacciNumberUsingReccursion(n-1) + getNthFibbonacciNumberUsingReccursion(n-2);
		}
    }
	
	public static int getNthFibonacciUsingDP(int n) {
        // Handle edge cases for the first two Fibonacci numbers
        if (n <= 1) {
            return n;
        }

        // Create an array to store Fibonacci numbers up to n
        int[] dp = new int[n + 1];

        // Base cases
        dp[0] = 0; // Fibonacci(0) = 0
        dp[1] = 1; // Fibonacci(1) = 1

        // Fill the array with Fibonacci numbers up to n
        for (int i = 2; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }

        // Return the nth Fibonacci number
        return dp[n];
    }

}
