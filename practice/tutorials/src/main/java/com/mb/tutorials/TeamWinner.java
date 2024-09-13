package com.mb.tutorials;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class TeamWinner {
	public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int N = scanner.nextInt();
        String queue = scanner.next();
        
        // Find the position of the last person standing
        int position = findLastPersonPosition(N);
        
        // Output the team of the last person
        System.out.println(queue.charAt(position - 1));
        scanner.close();
    }

    private static int findLastPersonPosition(int N) {
        int highestPowerOfTwo = Integer.highestOneBit(N);
        return 1 + 2 * (N - highestPowerOfTwo);
    }
}