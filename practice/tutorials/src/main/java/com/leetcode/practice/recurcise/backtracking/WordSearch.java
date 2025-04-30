package com.leetcode.practice.recurcise.backtracking;

public class WordSearch {

	public static void main(String[] args) {
		
		char[][] board = {{'A','B','C','E'}
						 ,{'S','F','C','S'},
						  {'A','D','E','E'}};
		String word = "ABCCED";
		
		WordSearch obj = new WordSearch();
		
		System.out.println(obj.exist(board, word));
	}
	
	public boolean exist(char[][] board, String word) {
    
		int rows = board.length;
		int columns = board[0].length;
		int wordLength = word.length();
		
		boolean[][] visited = new boolean[rows][columns];
		
		if(rows == 1 && columns ==1 && wordLength == 1 && board[0][0] == word.charAt(0)) {
			return true;
		}
		
		for(int i=0 ;i< rows; i++) {
			for(int j=0; j< columns; j++) {
				if (backtrack(i,j, 0 , board, word, visited)) {
					return true;
				}
			}
		}
		
		return false;
		
    }
	
	public boolean backtrack(int i, int j, int index, char[][] board, String word, boolean[][] visited) {
		//Base case
		if(index == word.length()) {
			return true;
		}
		
		if (i < 0 || j < 0 ||
				i >= board.length || j >= board[0].length ||
				visited[i][j] || board[i][j] != word.charAt(index)) {
            return false;
        }
		
		visited[i][j] = true;

        boolean found = backtrack(i + 1, j, index + 1,board, word, visited) || // Right check
                        backtrack(i - 1, j, index + 1,board, word, visited) || // Up Check
                        backtrack(i, j + 1, index + 1,board, word, visited) || // Down Check
                        backtrack(i, j - 1, index + 1,board, word, visited);   // Left Check

        visited[i][j] = false;
        
        return found;
	}
}
