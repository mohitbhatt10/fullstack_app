package com.mb.tutorials;

import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class GridChallenge {

	
	
	public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        //BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(System.getenv("OUTPUT_PATH")));

        int t = Integer.parseInt(bufferedReader.readLine().trim());

        IntStream.range(0, t).forEach(tItr -> {
            try {
                int n = Integer.parseInt(bufferedReader.readLine().trim());

                List<String> grid = IntStream.range(0, n).mapToObj(i -> {
                    try {
                        return bufferedReader.readLine();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                })
                    .collect(toList());

                String result = Result.gridChallenge(grid);
                
                System.out.println(result);
                //bufferedWriter.write(result);
                //bufferedWriter.newLine();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        bufferedReader.close();
        //bufferedWriter.close();
    }

}

class Result {

    /*
     * Complete the 'gridChallenge' function below.
     *
     * The function is expected to return a STRING.
     * The function accepts STRING_ARRAY grid as parameter.
     */
	/*
	 * 
	5
	ebacd
	fghij
	olmkn
	trpqs
	xywuv
	*/
    public static String gridChallenge(List<String> grid) {
        
        int n = grid.size();
        List<String> orderedList = new ArrayList<>(n);
        
        for(String str : grid){
            char[] charArray = str.toCharArray();
            Arrays.sort(charArray);
            orderedList.add(String.copyValueOf(charArray));
        }
        
        for(int i = 0;i<orderedList.get(0).length();i++ ){
            for(int j = 0 ; j<n-1 ;j++){
                if(Character.compare(orderedList.get(j).charAt(i), orderedList.get(j+1).charAt(i)) <=0){
                    continue;
                } 
                else {
                    return "NO";
                }
            }
        }
        return "YES";

    }

}
