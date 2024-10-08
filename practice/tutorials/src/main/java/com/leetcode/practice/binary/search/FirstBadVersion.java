package com.leetcode.practice.binary.search;

public class FirstBadVersion {

	public static void main(String[] args) {
			
		int n = 2126753390;
		System.out.println(firstBadVersion(n));
	}
	
	public static int firstBadVersion(int n) {
        
		int low = 1;
		int high = n;
		
		while(low <= high) {
			int mid = low + (high - low) / 2;
			if(!isBadVersion(mid) && isBadVersion(mid+1)) {
				return mid+1;
			}
			else if(isBadVersion(mid) && isBadVersion(mid+1)) {
				high = mid-1;
			}
			else if(!isBadVersion(mid) && !isBadVersion(mid+1)) {
				low = mid +1;
			}
		}
		return low;
    }
	
	public int firstBadVersionBetter(int n) {
        if(n==1) return n;

        int start =1;
        int end = n;
        int badVersion = 1;
        while(start <= end){
            int mid = start +(end-start)/2;
            if(isBadVersion(mid)){
                badVersion = mid;
                end = mid-1;
            }
            else start=mid+1;
        }
        return badVersion;
        
    }
	
	public static boolean isBadVersion(int version) {
		return version >= 1702766719; 
	}

}
