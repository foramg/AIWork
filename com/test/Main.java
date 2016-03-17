package com.test;

import com.apple.concurrent.Dispatch;

import java.util.*;


public class Main {

    static int[][] idealBoardMat = new int[][]{{0,1,2},{3,4,5},{6,7,8}};

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
        int[][] inputMatPrev = new int[3][3];
        Object[] paths = new Object[32];
        for(int i=0; i<32; i++){
            paths[i] = new ArrayList<>();
        }
        Object[] pathMats = new Object[32];
        for(int i=0; i<32; i++){
            pathMats[i] = new ArrayList();
        }

        solve(inputMat, paths, 0, pathMats);

    }

    static void solve(int[][] inputMat, Object[] paths, int pathNumber, Object[] pathMats){
        //this part needs to be repeated for each move
        List<Map> neighbors = findNeighbors(inputMat);
        //get zero position
        Map<String,Integer> zeroPosition = (Map)neighbors.get(0);
        neighbors.remove(0);

        List<Map> pathSoFar = null;
        Iterator<Map> iter = null;
        Map<String, Integer> iterItem = null;
        //check if any of the neighbors is already visited






        int m=0;
        int h=0;
        int lowestm = 10000;
        Map<String,Integer> lowestCostNeighbor = new HashMap<String,Integer>();
        int[][] inputMatPrev = new int[3][3];


        for (Map<String,Integer> neighborMap : neighbors
                ) {
            //save previous matrix
            for (int i = 0; i < 3; i++) {
                System.arraycopy(inputMat[i],0, inputMatPrev[i],0,3);
            }

            moveNeighbor(inputMat,zeroPosition,neighborMap);
            m = manhattan(inputMat, idealBoardMat);
            h = hamming(inputMat, idealBoardMat);
            if (h != 0 && (m) < lowestm){
                //find lowest m
                lowestm = (m);
                lowestCostNeighbor = neighborMap;
            } else if (h==0){
                return;
            }
            //move the neighbor back to where it was and move the zero position to where it was in order to
            //try a new neighbor and its cost
            for (int i = 0; i < 3; i++) {
                System.arraycopy(inputMatPrev[i],0, inputMat[i],0,3);
            }

        }
        //once we have the lowest cost neighbor, we move that neighbor to the empty slot and iterate
        lowestCostNeighbor.put("cost", m);
        ((ArrayList)pathMats[pathNumber]).add(inputMat);
        moveNeighbor(inputMat,zeroPosition,lowestCostNeighbor);
        //add the move to the paths
        ((ArrayList)paths[pathNumber]).add(lowestCostNeighbor);
        System.out.println(" row "+lowestCostNeighbor.get("row")+" column "+lowestCostNeighbor.get("column")
                +" value "+lowestCostNeighbor.get("value")+" m "+m+" h "+h);
        solve(inputMat, paths, pathNumber, pathMats);
    }


    static void pathMani(Object[] paths, int pathNumber, List<Map> neighbors, Object[] pathMats, int index){
        List<Map> pathSoFar = (List)paths[pathNumber];
        List pathMatSoFar = (List)pathMats[pathNumber];
        Iterator<Map> iter = neighbors.iterator();
        Map<String, Integer> iterItem = null;
        while(iter.hasNext())
        {
            iterItem = (Map)iter.next();
            for(Map pathItem : pathSoFar){
                if(iterItem.get("row") == pathItem.get("row") && iterItem.get("column") == pathItem.get("column") && iterItem.get("column") == pathItem.get("column")){
                    iter.remove();
                    break; //come out of for loop outer while loop should continue
                }
            }
        }
        if(neighbors.size() == 0)
        {
            //go back to the previous move and chose some other lowest option until we run out of options
            if(index > 0) {
                Map prevMap = pathSoFar.get(index - 1);
                int[][] prevMat = (int[][])pathMatSoFar.get(index - 1);

                pathMani();
            }
            else
            {
                pathNumber++;

            }

        }
        index --;



    }

    /**
        swap the neighbor for 0 and calculate hamming and manhattan
     */
    static void moveNeighbor(int[][] inputMat, Map<String, Integer> position1, Map<String, Integer> position2){
        inputMat[position1.get("row")][position1.get("column")] = position2.get("value");
        inputMat[position2.get("row")][position2.get("column")] = position1.get("value");
    }



    static List<Map> findNeighbors(int[][] currBoard){
        //find 0 on the board
        Map<String, Integer> rcpos;
        List<Map> neighbors = new ArrayList<Map>();
        for (int a = 0; a < 3; a++) {
            for (int b = 0; b < 3; b++) {
                if (currBoard[a][b] == 0){
                    rcpos = new HashMap<String, Integer>();
                    rcpos.put("row",a);
                    rcpos.put("column",b);
                    rcpos.put("value",0);
                    neighbors.add(rcpos);
                    if (ifExistsRightNeighbor(a,b,3)){
                        rcpos = new HashMap<String, Integer>();
                        rcpos.put("row", a);
                        rcpos.put("column", b+1);
                        rcpos.put("value", currBoard[a][b+1]);
                        neighbors.add(rcpos);
                    }
                    if (ifExistsLeftNeighbor(a,b,3)){
                        rcpos = new HashMap<String, Integer>();
                        rcpos.put("row", a);
                        rcpos.put("column", b-1);
                        rcpos.put("value", currBoard[a][b-1]);
                        neighbors.add(rcpos);
                    }
                    if (ifExistsTopNeighbor(a,b,3)){
                        rcpos = new HashMap<String, Integer>();
                        rcpos.put("row", a-1);
                        rcpos.put("column", b);
                        rcpos.put("value", currBoard[a-1][b]);
                        neighbors.add(rcpos);
                    }
                    if (ifExistsBottomNeighbor(a,b,3)){
                        rcpos = new HashMap<String, Integer>();
                        rcpos.put("row", a+1);
                        rcpos.put("column", b);
                        rcpos.put("value", currBoard[a+1][b]);
                        neighbors.add(rcpos);
                    }
                    break;
                }
            }
        }
        return neighbors;
    }

    static boolean ifExistsRightNeighbor(int a, int b, int N){
        if(b < (N-1)){
            return true;
        } else {
            return false;
        }
    }
    static boolean ifExistsLeftNeighbor(int a, int b, int N){
        if(b == 0){
            return false;
        } else {
            return true;
        }
    }
    static boolean ifExistsTopNeighbor(int a, int b, int N){
        if(a == 0)
            return false;
        else
            return true;
    }
    static boolean ifExistsBottomNeighbor(int a, int b, int N){
        if (a < (N-1))
            return true;
        else
            return false;
    }

    static int hamming(int[][] currBoardMat, int[][] idealBoardMat)
    {
        int h = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if(currBoardMat[i][j] != idealBoardMat[i][j])
                    h++;

            }
        }
        return h;
    }

    static int manhattan(int[][] currBoardMat, int[][] idealBoardMat){


        int[] coordinates = new int[2];
        int sum=0;
        for (int a = 0; a < 3; a++) {
            for(int b =0; b < 3; b++){
                if(currBoardMat[a][b] != idealBoardMat[a][b]) {
                    //find the row and column difference to calculate manhattan hieuristic
                    //for that you need to find the ideal position given a value
                    coordinates = getCoordinatesForBlocks(currBoardMat[a][b], idealBoardMat);
                    sum += Math.abs(coordinates[0] - a);
                    sum += Math.abs(coordinates[1] - b);
                }
            }
        }
        return sum;
    }

    static int[][]convertArrToMat3(int[] arr)
    {
        int a=0,b=0;
        int[][] retMat = new int[3][3];
        for (int i = 0; i < arr.length; i++) {
            retMat[a][b] = arr[i];
            if (b < 2)
                b++;
            else {
                a++;
                b=0;
            }
            //TODO: error handling can be done here to throw error if the number of rows and columns exceed 3
        }
        return retMat;
    }

    static int[] getCoordinatesForBlocks(int value, int[][] mat){
        int[] coord = new int[2];
        int a=0, b=0;
        for (a = 0; a < 3; a++) {
            for(b =0; b < 3; b++){
                if(mat[a][b] == value)
                    break;
            }
        }
        coord[0] = a;
        coord[1] = b;
        return coord;
    }


}
