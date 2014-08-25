package pkg2048_plus_4096;
import java.io.*;
import java.lang.*;
import java.util.*;
/**
 * The class is used to implement the Solver mode of the game 2048+4096.
 * It consists of all the methods used to implement the Solver.
 * The solver is based on the the AlphaBeta Pruning Algorithm.
 * @author Manu
 */
public class Solver {
    //static int wo=0,no=0; //For Debugging puporses.
    /*public static void main(String[] args)
    {
        //for(int i=0;i<25;i++)
            (new Solver()).solve();
        //System.out.println("WIN:LOSS::"+wo+":"+no);        
    }*/
    
    /**
     * The Method is used to initialize a new game as per the user choice using 
     * the GameBoard object.
     * The best Possible move is found by calling functions from this method.
     * In case the best the possible move is not a valid move, the loop/circuit 
     * breaking conditions are defined here.
     */
    void solve()
    {
        int x;
        boolean loop=false;
        GameBoard game=new GameBoard();
        game.start();
        int depth=7;
        char ch='?',lastValid='?';
        int ctr=0,greyLoop=0;
        do{
            if(!loop)
                ch=findMove(game,depth);
            else
            {
                loop=false;
                ctr=0;
            }
            game.drawBoard();
            GameBoard previous=new GameBoard();
            previous.clone2dArray(game);
            game.resolve(ch);
            if(!previous.isEqual(game.getBoard(),previous.getBoard()))
            {
                if(!game.greyBlock())
                    game.addTwo();
                lastValid=ch;
            }
            else {
                ctr++;
                greyLoop++;
                if(ctr==25){
                    loop=true;
                    ch=circuitBreaker(game,lastValid);
                    lastValid=ch;
                }
                if(greyLoop==100){
                    game.addTwo();
                    greyLoop=0;
                }
            }
        }while((x=game.notDone())>0);
        game.drawBoard();
        if(x<0)
        {
            //wo++;
            System.out.println("========================\nSolver WIN.\n========================");
        }
        else
        {
            //no++;
            System.out.println("========================\nSolver LOSE.\n========================");
        }
    }
    
    /**
     * The Function initiates the alphabeta algorithm process to find the best 
     * possible move. The Depth is chosen to 7 according to the corelation 
     * between the value of depth(That is number of future steps to be checked)
     * and the time complexity.
     * Though a better result can be obtained by increasing the value of depth.
     * @param play GameBoard object having a copy of an instance of the board.
     * @param depth Depth upto which the Algorithm to be implemented.
     * @return Best possible Direction to be moved in.
     */
    char findMove(GameBoard play, int depth){
        result FinalResult= alphabeta(play, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        return FinalResult.getDirection();
    }
    
    /**
     * This method is the recursive implementation of the AlphaBeta Pruning Algorithm.
     * It forms the basis of the Auto Solver method.
     * @param play Instance of the board object.
     * @param depth Depth upto which to be checked.
     * @param alpha 
     * @param beta
     * @param isPlayer Whether its the turn of Player or Computer,
     * @return Result object having the Max/Min Score and the direction in which 
     * the movement has been made.
     */
    result alphabeta(GameBoard play,int depth,int alpha,int beta, boolean isPlayer)
    {
        result finalR=new result();
        int bestScore=0;
        char bestDirection='w';
        char directions[]={'w','a','s','d'};
        if(play.notDone()<1){ //base case.
            if(play.notDone()==-1)
                bestScore=Integer.MAX_VALUE;    
            else if(play.notDone()==0)
                bestScore=Integer.MIN_VALUE;
        }
        else if(depth==0) { //base case
            bestScore=heuristicScore(play.getScore(),play.getNumberOfEmptyCells(),calculateClusteringScore(play.getBoard(),play.getFixed()));
        }
        else{
            if(isPlayer){
                for(char direction : directions) {
                    GameBoard newBoard=new GameBoard();
                    newBoard.clone2dArray(play);
                    int moved=newBoard.resolve(direction);
                    if(moved<=0 && newBoard.isEqual(play.getBoard(),newBoard.getBoard())) 
                    {
                        //System.out.println("moved");
                        continue;
                    }
                    result currentResult = alphabeta(newBoard, depth-1, alpha, beta, false);
                    int currentScore=(currentResult.getScore());
                    if(currentScore>alpha) { //maximize score
                        alpha=currentScore;
                        bestDirection=direction;
                    }
                    if(beta<=alpha) {
                        break;
                    }
                }
                bestScore = alpha;
            }
            else {
                List<Integer> moves = play.getEmptyCellIds();
                int[] possibleValues = {2, 4};
                int i,j;
                abloop: for(Integer cellId : moves) {
                    i = cellId/play.BOARD_SIZE;
                    j = cellId%play.BOARD_SIZE;
                    for(int value : possibleValues) {
                        GameBoard newBoard=new GameBoard();
                        newBoard.clone2dArray(play);                     
                        newBoard.setEmptyCell(i, j, value);
                        result currentResult = alphabeta(newBoard, depth-1, alpha, beta, true);
                        int currentScore=(currentResult.getScore());
                        if(currentScore<beta) { //minimize best score
                            beta=currentScore;
                        }
                        if(beta<=alpha) {
                            break abloop;
                        }
                    }
                }
                bestScore = beta;
                if(moves.isEmpty()) {
                    bestScore=0;
                }
            }
        }
        finalR.set(bestScore,bestDirection);
        return finalR;
    }
        
    /**
     * This Method returns the value to determine the best possible move taking 
     * into consideration the score of the board, the number of empty cells, & 
     * a value based on how clustered the board is.
     * @param actualScore Score obtained by the board.
     * @param numberOfEmptyCells Number of empty cells.
     * @param clusteringScore value depending on the state of the board.
     * @return maximum possible score.
     */
    private static int heuristicScore(int actualScore, int numberOfEmptyCells, int clusteringScore) {
        int score = (int) (actualScore+Math.log(actualScore)*numberOfEmptyCells -clusteringScore);
        return Math.max(score, Math.min(actualScore, 1));
    }
    
    /**
     * Calculates a score that measures how clustered the board is.
     * By clustered we mean that the how many adjacent blocks have value that 
     * cannot be merged.
     * @param boardArray Instance of the board.
     * @param fixed Matrix having the position of fixed blocks.
     * @return Average difference in the value of the block with respect to its neighbours.
     */
    private static int calculateClusteringScore(int[][] boardArray, boolean[][] fixed) {
        int clusteringScore=0;
        int[] neighbors = {-1,0,1};
        for(int i=0;i<boardArray.length;++i) {
            for(int j=0;j<boardArray.length;++j) {
                if(boardArray[i][j]==0) {
                    continue; //ignore empty cells
                }
                //for every pixel find the distance from each neightbors
                int numOfNeighbors=0;
                int sum=0;
                for(int k : neighbors) {
                    int x=i+k;
                    if(x<0 || x>=boardArray.length) {
                        continue;
                    }
                    for(int l : neighbors) {
                        int y = j+l;
                        if(y<0 || y>=boardArray.length) {
                            continue;
                        }                        
                        if(boardArray[x][y]>0) {
                            ++numOfNeighbors;
                            sum+=Math.abs(boardArray[i][j]-boardArray[x][y]);
                        }
                    }
                }
                /*if(fixed[i][j])
                {
                    sum-=2048*numOfNeighbors;
                }*/
                clusteringScore+=sum/numOfNeighbors; //Average differnce in the value of the blocks with respect to its neighbours.
            }
        }
        return clusteringScore;
    }
    
    /**
     * When the heuristic Score suggests a move that is not possible, we enter 
     * an infinite loop. As soon as this loop is detected we call this method.
     * This method returns the Move which gives the maximum Score irrespective
     * of whether its User's turn or the computers.
     * @param play instance of the board.
     * @param oldMove last Move which was performed on the board to get this particular instance of the board.
     * @return Direction in which we score the maximum points.
     */
    char circuitBreaker(GameBoard play,char oldMove)
    {
        int max=0;
        char bestMove='?';
        char dir[]={'w','a','s','d'};
        for(char direc:dir){
            if(oldMove!=direc){
                GameBoard turnW=new GameBoard();
                turnW.clone2dArray(play);
                turnW.resolve(direc);
                //int fScore=heuristicScore(turnW.getScore(),turnW.getNumberOfEmptyCells(),calculateClusteringScore(turnW.getBoard()));
                if(turnW.getScore()>max)
                {
                    max=turnW.getScore();
                    bestMove=direc;
                }
                else if(turnW.getScore()==max)
                    bestMove=direc;
            }
        }
        //System.out.println("CircuitBreaker Is:"+bestMove);
        return bestMove;
    }
}

