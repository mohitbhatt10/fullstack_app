package com.mb.tutorials;

/*
 * Given two strings word1 and word2, return the minimum number of operations required to convert word1 to word2.

You have the following three operations permitted on a word:

Insert a character
Delete a character
Replace a character
 

Example 1:

Input: word1 = "horse", word2 = "ros"
Output: 3
Explanation: 
horse -> rorse (replace 'h' with 'r')
rorse -> rose (remove 'r')
rose -> ros (remove 'e')
Example 2:

Input: word1 = "intention", word2 = "execution"
Output: 5
Explanation: 
intention -> inention (remove 't')
inention -> enention (replace 'i' with 'e')
enention -> exention (replace 'n' with 'x')
exention -> exection (replace 'n' with 'c')
exection -> execution (insert 'u')
 
       ""   e   x   e   c   u   t   i   o   n
    -------------------------------------------
"" |    0   1   2   3   4   5   6   7   8   9
i  |    1   1   2   3   4   5   6   6   7   8
n  |    2   2   2   3   4   5   6   7   7   8
t  |    3   3   3   3   4   5   6   7   8   8
e  |    4   3   4   4   3   4   5   6   7   8
n  |    5   4   4   5   4   4   5   6   7   7
t  |    6   5   5   5   5   5   5   6   7   8
i  |    7   6   6   6   6   6   6   5   6   7
o  |    8   7   7   7   7   7   7   6   5   6
n  |    9   8   8   8   8   8   8   7   6   5

 
 
 */


public class EditDistance {
    public static int minDistance(String word1, String word2) {
        int m = word1.length();
        int n = word2.length();
        
        // Create a DP table
        int[][] dp = new int[m + 1][n + 1];
        
        // Initialize base cases
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i; // All deletes
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j; // All inserts
        }
        
        // Fill the DP table
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (word1.charAt(i - 1) == word2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1]; // Characters match
                } else {
                    dp[i][j] = Math.min(
                        dp[i - 1][j] + 1,              // Delete
                        Math.min(
                            dp[i][j - 1] + 1,          // Insert
                            dp[i - 1][j - 1] + 1       // Replace
                        )
                    );
                }
            }
        }
        
        // Result is in the bottom-right cell
        return dp[m][n];
    }

    public static void main(String[] args) {
        String word1 = "horse";
        String word2 = "ros";
        
        System.out.println("Minimum operations to convert \"" + word1 + "\" to \"" + word2 + "\": " 
                            + minDistance(word1, word2));
    }
}