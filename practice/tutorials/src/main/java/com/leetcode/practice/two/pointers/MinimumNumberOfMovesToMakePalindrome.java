package com.leetcode.practice.two.pointers;

public class MinimumNumberOfMovesToMakePalindrome {

    public static void main(String[] args) {
        String s = "aletelt";
        System.out.println(minMovesToMakePalindromeLC(s));
    }

    public static int minMovesToMakePalindrome(String s) {
        char[] arr = s.toCharArray();
        int left = 0;
        int right = arr.length - 1;
        int moves = 0;

        while (left < right) {
            int l = left;
            int r = right;

            while (l < r && arr[l] != arr[r]) {
                r--;
            }

            if (l == r) {
                swap(arr, l, l + 1);
                moves++;
            } else {
                for (int i = r; i < right; i++) {
                    swap(arr, i, i + 1);
                    moves++;
                }
                left++;
                right--;
            }

        }

        return moves;
    }

    private static void swap(char[] arr, int i, int j) {
        char temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static int minMovesToMakePalindromeLC(String s) {

        int l = 0;
        int r = s.length() -1;
        int steps = 0;
        char[] arr = s.toCharArray();

        while(l<r){

            if(arr[l] == arr[r]){
                l++;
                r--;
            }
            else{
                int k = r;
                while(k > l && arr[k] != arr[l] ){
                    k--;
                }

                if(k == l){
                    swap(arr, l);
                    steps++;
                }
                else{
                    while(k<r){
                        swap(arr,k);
                        steps++;
                        k++;
                    }
                    l++;
                    r--;
                }
            }
            printArray(arr);
        }
        return steps;
    }

    private static void  swap(char[] arr, int l){
        char temp = arr[l];
        arr[l] = arr[l+1];
        arr[l+1] = temp;
    }

    private static void printArray(char[] arr){
        for(char c: arr){
            System.out.print(c);
        }
        System.out.println();
    }
}
