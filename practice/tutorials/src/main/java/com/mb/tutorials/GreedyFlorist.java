package com.mb.tutorials;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class GreedyFlorist {

	// Complete the getMinimumCost function below.
	static int getMinimumCost(int k, int[] c) {

		List<Integer> list = Arrays.stream(c).boxed().sorted(Collections.reverseOrder()).collect(Collectors.toList());

		int totalCost = 0;
		int friendMultiplier = 0;

		for (int i = 0; i < list.size(); i++) {
			// Calculate the cost for each flower and add it to the total cost
			totalCost += (friendMultiplier + 1) * list.get(i);

			// Update the friendMultiplier for the next friend
			friendMultiplier = (friendMultiplier + 1) % k;
		}

		return totalCost;

	}

	private static final Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) throws IOException {


		String[] nk = scanner.nextLine().split(" ");

		int n = Integer.parseInt(nk[0]);

		int k = Integer.parseInt(nk[1]);

		int[] c = new int[n];

		String[] cItems = scanner.nextLine().split(" ");
		scanner.skip("(\r\n|[\n\r\u2028\u2029\u0085])?");

		for (int i = 0; i < n; i++) {
			int cItem = Integer.parseInt(cItems[i]);
			c[i] = cItem;
		}

		int minimumCost = getMinimumCost(k, c);

		System.out.println(minimumCost);

		scanner.close();
	}
}
