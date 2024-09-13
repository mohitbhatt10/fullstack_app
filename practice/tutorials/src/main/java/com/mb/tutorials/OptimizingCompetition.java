package com.mb.tutorials;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class OptimizingCompetition {
	public static void main(String args[] ) throws Exception {

	      Scanner sc = new Scanner(System.in);
	      int N = sc.nextInt();
	      int[] W = new int[N];
	      Map<Integer,Integer> map = new HashMap<>();
	      
	      int max = 0;
	      
	      for(int i=0;i<N;i++){
	         W[i] = sc.nextInt();
	      }

	      for(int i=0;i<N-1;i++){
	    	 map.clear(); 
	         for(int j=i+1; j<N; j++){
	        	if(i==j) {
	        		continue;
	        	} 
	        	//System.out.println("Wi="+W[i]+" Wj="+W[j]); 
	            int weightSum = W[i]+W[j];
	            if(map.containsKey(weightSum)){
	               map.put(weightSum, map.get(weightSum)+1);
	            }
	            else{
	               map.put(weightSum,1);
	            }
	         }
	         int tempMax = map.values().stream().max(Integer::compareTo).orElse(0);
	         if(max<tempMax) {
	        	 max = tempMax;
	         }
	      }

	      System.out.println(max);
	      sc.close();
	   }
}
