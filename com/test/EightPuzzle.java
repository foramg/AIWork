package com.test;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

public class EightPuzzle{

    int[][] goalBoardMat = new int[][]{{1,2,3},{4,5,6},{7,8,0}};
    int numberOfMovesMade = 0;
    int currCost = 0;
    int[][] currBoardMat = new int[3][3];
    List<Map> boardPathQueue = new ArrayList();
    static int numOfIterations = 0;

    EightPuzzle(int[][] ipCurrBoardMat)
    {
        currBoardMat = ipCurrBoardMat;
    }

    int manhattan(int[][] inputBoardMat){

        int[] coordinates = new int[2];
        int sum=0;
        for (int a = 0; a < 3; a++) {
            for(int b =0; b < 3; b++){
                if(inputBoardMat[a][b] != goalBoardMat[a][b]) {
                    //find the row and column difference to calculate manhattan hieuristic
                    //for that you need to find the ideal position given a value
                    coordinates = getCoordinatesForBlocks(inputBoardMat[a][b]);
                    sum += Math.abs(coordinates[0] - a);
                    sum += Math.abs(coordinates[1] - b);
                }
            }
        }
        return numberOfMovesMade+sum;
    }

    int[] getCoordinatesForBlocks(int value){
        int[] coord = new int[2];
        int a=0,b=0;
        for (a = 0; a < 3; a++) {
            for(b =0; b < 3; b++){
                if(goalBoardMat[a][b] == value)
                    break;
            }
        }
        coord[0] = a;
        coord[1] = b;
        return coord;
    }

    int hamming(int[][] inputBoardMat)
    {
        int h = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if(inputBoardMat[i][j] != goalBoardMat[i][j]){
                    h++;
                }
            }
        }
        return numberOfMovesMade+h;
    }

    boolean isItSameAsOnePrevious(int[][] pathElem, int[][] nextBoardMat){
        for (int a = 0; a < 3; a++) {
            for (int b = 0; b < 3; b++) {
                if (pathElem[a][b] != nextBoardMat[a][b]) {
                    return false;
                }
            }
        }
        return true;
    }

    boolean isItSameAsAnyPrevious(int[][] nextBoardMat){
        int[][] pathElem = null;
        if(boardPathQueue.size() == 0){
            return false;
        }

        //need to go reverse
        for(int i = boardPathQueue.size()-1; i >= 0; i--){
            pathElem = (int[][])((Map)boardPathQueue.get(i)).get("mat");
            if(!isItSameAsOnePrevious(pathElem, nextBoardMat) && i==0)
                return false;
            if(!isItSameAsOnePrevious(pathElem, nextBoardMat))
                continue;
            if(isItSameAsOnePrevious(pathElem, nextBoardMat))
                return true;
        }
        return true;
    }

    List<Map> findNeighbors(){
        //find 0 on the board
        Map<String, Integer> rcpos;
        List<Map> neighbors = new ArrayList<Map>();
        for (int a = 0; a < 3; a++) {
            for (int b = 0; b < 3; b++) {
                if (currBoardMat[a][b] == 0){
                    rcpos = new HashMap<String, Integer>();
                    rcpos.put("row",a);
                    rcpos.put("column",b);
                    rcpos.put("value",0);
                    neighbors.add(rcpos);
                    if (ifExistsRightNeighbor(a,b,3)){
                        rcpos = new HashMap<String, Integer>();
                        rcpos.put("row", a);
                        rcpos.put("column", b+1);
                        rcpos.put("value", currBoardMat[a][b+1]);
                        neighbors.add(rcpos);
                    }
                    if (ifExistsLeftNeighbor(a,b,3)){
                        rcpos = new HashMap<String, Integer>();
                        rcpos.put("row", a);
                        rcpos.put("column", b-1);
                        rcpos.put("value", currBoardMat[a][b-1]);
                        neighbors.add(rcpos);
                    }
                    if (ifExistsTopNeighbor(a,b,3)){
                        rcpos = new HashMap<String, Integer>();
                        rcpos.put("row", a-1);
                        rcpos.put("column", b);
                        rcpos.put("value", currBoardMat[a-1][b]);
                        neighbors.add(rcpos);
                    }
                    if (ifExistsBottomNeighbor(a,b,3)){
                        rcpos = new HashMap<String, Integer>();
                        rcpos.put("row", a+1);
                        rcpos.put("column", b);
                        rcpos.put("value", currBoardMat[a+1][b]);
                        neighbors.add(rcpos);
                    }
                    break;
                }
            }
        }
        return neighbors;
    }

    List<Map> sortNeighborsbyCost(List<Map> neighbors, Map zeroPosition){
        Map newMatMap = null;
        int[][] newMat = null;
        int minCost = 0;
        int moves = 0;
        int minMoves = 0;
        List<Map> sortedNeighbors = new ArrayList<Map>();
        for (Map<String,Integer> neighborMap : neighbors) {
            //newMat is the resulting matrix when the move is made to new position of the zero position
            newMat = move(neighborMap, zeroPosition);
            //find cost
            moves = manhattan(newMat)+hamming(newMat);
            newMatMap = new HashMap();
            newMatMap.put("cost",moves);
            newMatMap.put("mat", newMat);
            //newMatMap.put("neighbor", neighborMap);
            insertElement(sortedNeighbors, newMatMap);
        }
        return sortedNeighbors;
    }

    void insertElement(List<Map> sortedNeighbors, Map<String, Integer> newMat){
        int value = 0;
        if(sortedNeighbors.size() == 0){
            //System.out.println("insert element cost "+newMat.get("cost"));
            sortedNeighbors.add(newMat);
        }
        else
        {
            //find cost of each element
            //System.out.println("insert element cost "+newMat.get("cost"));
            for(int i = 0; i < sortedNeighbors.size(); i++){
                value = (Integer)(((Map)sortedNeighbors.get(i)).get("cost"));
                if(value < newMat.get("cost")){
                    continue;
                }
                else {
                    sortedNeighbors.add(i, newMat); //push the rest of the elements down
                    break;
                }
            }
        }
    }

    boolean reducesCost(int costOfMove){
        for(Map pastMoves: boardPathQueue){
            if(costOfMove > ((Integer)pastMoves.get("cost")).intValue()){
                return false;
            }
        }
        return true;
    }

    boolean ifExistsRightNeighbor(int a, int b, int N){
        if(b < (N-1)){
            return true;
        } else {
            return false;
        }
    }
    boolean ifExistsLeftNeighbor(int a, int b, int N){
        if(b == 0){
            return false;
        } else {
            return true;
        }
    }
    boolean ifExistsTopNeighbor(int a, int b, int N){
        if(a == 0)
            return false;
        else
            return true;
    }
    boolean ifExistsBottomNeighbor(int a, int b, int N){
        if (a < (N-1))
            return true;
        else
            return false;
    }

    void copyMatrix(int[][] a, int[][] b){
        for (int i = 0; i< 3; i++){
            for (int j = 0; j < 3; j++){
                b[i][j] = a[i][j];
            }
        }

    }

    int[][] move(Map<String, Integer> position1, Map<String, Integer> position2){
        int[][] nextBoardMat = new int[3][3];
        copyMatrix(currBoardMat, nextBoardMat);
        nextBoardMat[position1.get("row")][position1.get("column")] = position2.get("value");
        nextBoardMat[position2.get("row")][position2.get("column")] = position1.get("value");
        return nextBoardMat;
    }

    Map getQueuePosition(List<Map> sortedNeighbors, Map zeroPosition){
        Map retMap = new HashMap();


        //check if the move will result in repeat state

        for (Map lowestCostNeighbor: sortedNeighbors){
            //get matrix and cost
            int[][] lowestCostNeighborMat = (int[][])lowestCostNeighbor.get("mat");
            int lowestCost = (Integer)lowestCostNeighbor.get("cost");
            //System.out.println("get queue position cost"+lowestCost);

            if (!isItSameAsAnyPrevious(lowestCostNeighborMat)){
                retMap.put("mat", lowestCostNeighborMat);
                retMap.put("cost", lowestCost);
                if(!reducesCost(lowestCost)) {
                    continue;
                }
                return retMap;
            }
        }
        //if all neighbors are exhausted, try the lowest cost
        //go through another loop to ignore cost
        for (Map lowestCostNeighbor: sortedNeighbors){
            //get matrix and cost
            int[][] lowestCostNeighborMat = (int[][])lowestCostNeighbor.get("mat");
            int lowestCost = (Integer)lowestCostNeighbor.get("cost");

            if (!isItSameAsAnyPrevious(lowestCostNeighborMat)){
                retMap.put("mat", lowestCostNeighborMat);
                retMap.put("cost", lowestCost);
                return retMap;
            }
        }


        //move back one and try again
        Map backOne = (Map)boardPathQueue.get(boardPathQueue.size()-1);
        return backOne;



    }

    boolean isGoalPosition(int[][] inputMat){
        for(int a = 0; a < 3; a++){
            for(int b=0; b < 3; b++){
                if(inputMat[a][b] != goalBoardMat[a][b]){
                    return false;
                }
            }
        }
        return true;
    }

    void solve(){
        //first find zero position
        //then find neighbors
        //choose neighbor with lowest cost
        //check next state
        //check if next state same as prev state
        //if yes then choose another neighbor which is lowest cost
        //maintain path so far as states in queue so it is easy to switch to past states to avoid loops
        //also the past states need to store the total cost
        //the neighbors are a map of row column value
        List<Map>neighbors = findNeighbors();
        Map zeroPosition = neighbors.get(0);
        neighbors.remove(0);
        //the result is a list of maps. each map contains the matrix, the neighbor row, col, value and the cost
        List<Map> sortedNeighbors = sortNeighborsbyCost(neighbors, zeroPosition);
        //get the neighbors by lowest cost
        //recursively try each neighbor until the goal is reached depth first search
        Map retMap = getQueuePosition(sortedNeighbors, zeroPosition);
        int[][] position = (int[][])retMap.get("mat");
        for(int a=0; a<3; a++){
            System.out.println(position[a][0]+" "+position[a][1]+" "+position[a][2]);
        }
        System.out.println("------------------");
        if(null != position) {
            if (!isGoalPosition(position)) {
                copyMatrix(position, currBoardMat);
                boardPathQueue.add(retMap);
                numberOfMovesMade++;
                currCost += (Integer)retMap.get("cost");
                numOfIterations++;
                if(numOfIterations == 10)
                   return;
                solve();

                //TODO if you abondon a tree branch, how do we get rid of the cost of the whole branch?

            } else {
                return;
            }
        }

    }

}
