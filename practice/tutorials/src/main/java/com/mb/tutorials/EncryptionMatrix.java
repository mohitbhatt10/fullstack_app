package com.mb.tutorials;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class EncryptionMatrix {

	public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        String s = bufferedReader.readLine();

        String result = encryption(s);

        System.out.println(result);

        bufferedReader.close();
    }
	
	public static String encryption(String s) {
	    // Write your code here
	        Double squareroot = Math.sqrt(new Double(String.valueOf(s.length())));
	        int rows = squareroot.intValue();
	        int column = squareroot.intValue();
	        int lengthCounter = 0;
	        String encodedString = "";
	        if (squareroot.intValue() < (new Double(Math.ceil(squareroot))).intValue()){
	            column++;
	        }
	        if(rows*column < s.length()){
	            rows++;
	        }
	        System.out.println("ROWS: "+rows+" COLUMNS: "+column);
	        Character[][] matrix = new Character[rows][column];
	        for(int i =0;i<rows; i++){
	            for(int j= 0;j<column && lengthCounter <= s.length()-1;j++){
	                matrix[i][j] = s.charAt(lengthCounter);
	                lengthCounter++;
	            }
	        }
	        for(int i =0;i<column; i++){
	            for(int j= 0;j<rows;j++){
	                System.out.print(matrix[j][i]);
	                if(matrix[j][i] != null){
	                    encodedString += matrix[j][i];
	                }
	            }
	            System.out.println();
	            encodedString += " ";
	            
	        }
	        return encodedString.trim(); 
	    }

}
