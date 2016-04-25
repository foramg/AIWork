package com.test;

import java.util.*;
import com.test.EightPuzzle;


public class Main {

    static int[][] idealBoardMat = new int[][]{{1,2,3},{4,5,6},{7,8,0}};

    public static void main(String[] args) {

        /**
        step 1: Take input as a space separated string of 9 numbers
        0 represents empty spot
        step 2: convert input to matrix
        step 3: find neighbors
        step 4: try swapping zero with each of the neighbours and calculate hamilton and manhattan
        Step 5: choose the neighbor with least value for hamilton and manhattan
        step 5: update the matrix
        */
        int[] inpArr = new int[9];
        for (int i = 0; i < args.length; i++) {
            inpArr[i] = Integer.parseInt(args[i]);
        }


        int[][] inputMat = new int[][]{{0,1,3},{4,2,5},{7,8,6}};


        EightPuzzle ep = new EightPuzzle(inputMat);

        ep.solve();

    }




}
