package com.mb.tutorials;

import java.util.Scanner;

public class MinSwapsForNonDecreasing {

	public static int minSwaps(String S) {
        int swaps = 0;
        int countA = 0;
        int countB = 0;
        char prev = 'a'; // Initialize with 'a' to handle the first character

        for (char c : S.toCharArray()) {
            if (c == 'a') {
                countA++;
            } else {
                countB++;
            }
            // Check for imbalance based on current character and previous counts
            if ((c == 'a' && countB > countA) || (c == 'b' && countA > countB)) {
                swaps += Math.min(countA, countB); // Minimum swaps needed to make counts equal
            }
            prev = c;
        }

        return swaps;
    }

    public static void main(String[] args) {
    	Scanner scanner = new Scanner(System.in);
        String s = scanner.nextLine();
        scanner.close();
        int result = minSwaps(s);
        System.out.println(result);
    }
}
