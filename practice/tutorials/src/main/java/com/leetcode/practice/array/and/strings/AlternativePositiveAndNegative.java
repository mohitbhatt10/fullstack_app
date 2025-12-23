package com.leetcode.practice.array.and.strings;

public class AlternativePositiveAndNegative {

    public static void main(String[] args) {

        int[] arr = { -1, 2, -3, 4, 5, -6, -7, 8, 9 };
        AlternativePositiveAndNegative obj = new AlternativePositiveAndNegative();
        obj.sortTheArrayAlternatively(arr);

        // Print the sorted array
        for (int num : arr) {
            System.out.print(num + " ");
        }
    }

    private void sortTheArrayAlternatively(int[] arr) {
        int n = arr.length;
        int[] result = new int[n];
        int posIndex = 0;
        int negIndex = 1;

        for (int num : arr) {
            if (num >= 0) {
                if (posIndex < n) {
                    result[posIndex] = num;
                    posIndex += 2;
                }
            } else {
                if (negIndex < n) {
                    result[negIndex] = num;
                    negIndex += 2;
                }
            }
        }

        // Copy the result back to the original array
        System.arraycopy(result, 0, arr, 0, n);
    }

}
