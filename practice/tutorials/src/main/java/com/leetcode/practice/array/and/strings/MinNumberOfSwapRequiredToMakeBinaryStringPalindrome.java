package com.leetcode.practice.array.and.strings;

public class MinNumberOfSwapRequiredToMakeBinaryStringPalindrome {

    public static void main(String[] args) {
        String s = "1010";
        MinNumberOfSwapRequiredToMakeBinaryStringPalindrome obj = new MinNumberOfSwapRequiredToMakeBinaryStringPalindrome();
        System.out.println(obj.minSwapBinPalindrome(s));
    }

    public int minSwapBinPalindrome (String s){
        int n = s.length();
        int swaps = 0;
        for (int i = 0; i < Math.abs(n / 2); i++) {
            int j = n - i - 1;

            if (s.charAt(i) != s.charAt(j)){
                swaps++;
            }
        }

        if((swaps % 2) == 0) {
            return swaps / 2;
        } else if((n % 2) == 1) {
            return 2;
        }

        return -1;
    }

}

