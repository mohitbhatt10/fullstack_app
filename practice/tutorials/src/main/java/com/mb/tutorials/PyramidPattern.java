package com.mb.tutorials;

public class PyramidPattern {

	public static void main(String[] args) {
		
		printPyramidPattern(10);

	}
	
	public static void printPyramidPattern(int n) {
		
		int revRow = n;
		for(int i=1; i<=n ;i++) {
			for(int j=1;j<=n;j++) {
				if(j >= revRow) {
					System.out.print("*");
				}
				else {
					System.out.print(" ");
				}
			}
			for(int k=1;k<=n ;k++) {
				if(i>1 && k <i) {
					System.out.print("*");
				}
			}
			System.out.println();
			revRow--;
		}
	}
}
