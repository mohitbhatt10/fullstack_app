package com.mb.tutorials;

import java.util.*;

public class TeamWinnerByString {
	public static void main(String args[]) throws Exception {

		Scanner sc = new Scanner(System.in);
		int n = sc.nextInt();
		String memberString = sc.next();

		do {
			memberString = removeOddChars(memberString);
			System.out.println(memberString);
		} while (memberString.length() != 1);

		System.out.print(memberString);
	}

	private static String removeOddChars(String s) {
		String temp = "";
		for (int i = 0; i < s.length(); i++) {
			if (i % 2 != 0) {
				temp += s.charAt(i);
			}
		}
		return temp;
	}
}