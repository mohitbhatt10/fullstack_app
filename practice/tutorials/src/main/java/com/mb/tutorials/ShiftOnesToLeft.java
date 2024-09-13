package com.mb.tutorials;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

public class ShiftOnesToLeft {

    public static void main(String[] args) {
        int[] arr = {2, 1, 4, 1, 5, 1, 3, 1, 6, 1};  // Example input array
        shiftOnesToLeft(arr);
        System.out.println("Array after shifting 1's to the left: " + Arrays.toString(arr));
    }

    public static void shiftOnesToLeft(int[] arr) {
        int left = 0; // Pointer to keep track of the position to place 1's

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == 1) {
                // Swap arr[i] with arr[left]
                int temp = arr[left];
                arr[left] = arr[i];
                arr[i] = temp;
                
                left++; // Move the left pointer to the next position
            }
            System.out.println("Array after "+i+"th iteration: "+Arrays.toString(arr));
        }
    }
}
