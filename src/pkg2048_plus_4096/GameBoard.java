package pkg2048_plus_4096;
import java.util.*;
import java.io.*;
import java.lang.*;
/**
 * This Class is used to implement the board of the game 2048-4096 auto-solver.
 * It consists of all the functions related to the board as used by the manual
 * mode.
 * @author Manu
 */
public class GameBoard {
    int BOARD_SIZE=4;
    private int score=0;
    private int[][] board=new int[4][4];
    private boolean[][] fixed=new boolean[4][4];
    
    /**
     * To return the Board to the caller.
     * @return 4x4 matrix (The Board)
     */
    int[][] getBoard(){
        return board;
    }
    
     /**
     * To return the position of the fixed blocks on the board.
     * @return 4x4 matrix having position of fixed blocks as true.
     */
    boolean[][] getFixed(){
        return fixed;
    }
    
    /**
     * To return the score of the board.
     * @return Score of the present scenario.
     */
    int getScore(){
        return score;
    }
    
    /**
     * The Function is used to ask the user what would be his next move based on
     * the present scenario of the board.
     * @param ch The Direction to move in.
     * @return Non-Zero number if blocks have been moved/merged.
     */
    int resolve(char ch){
        if(notDone()>0){
            switch(ch){
                case 'w':
                    if (fold(false,true)>0)return 100;
                    break;
                case 's':
                    if (fold(true, true)>0)return 100;
                    break;
                case 'a':
                    if (fold(false,false)>0)return 100;
                    break;
                case 'd':
                    if (fold(true,false)>0)return 100;
                    break;
            }
        }
        return 0;
    }
    
    /**
     * This is one of the most important function which merges different blocks 
     * into a single block.
     * Based on the move chosen the primary blocks to be filled are determined.
     * the neighbors of the blocks are checked and if a possibility of merging
     * is found, it is performed.
     * The Fixed blocks are checked with its neighbors if it is possible to 
     * remove the fixed block or not.
     * Score is updated.
     * Boolean variables are updated according to the following.
     * false,true  = up
     * true, true  = down
     * false,false = left
     * true, false = right
     * @param inv Boolean to decide the direction in which to move.
     * @param vert Boolean to decide the direction in which to move.
     * @return non-zero number if any of the blocks are moved/merged.
     */
    int fold(boolean inv, boolean vert){
        int didMove=0,point=0;
        int nextSpot,x,y,v,q,r;
        int[][] nb = new int[4][4];
        for(int i=0;i<4;i++){
            nextSpot=inv?3:0;
            for(int j=0;j<4;j++){
                v=vert?i:j;
                x=inv?3-v:v;
                v=vert?j:i;
                y=inv?3-v:v;
                q=vert?x:nextSpot;
                r=vert?nextSpot:y;
                if(fixed[y][x]==true){
                nb[y][x]=board[y][x];
                nextSpot=(vert?y:x);
                }
                if(fixed[r][q] && !fixed[y][x] && board[y][x]==board[r][q] && !(y==r && x==q))
                {
                    nb[r][q]=board[y][x];
                    didMove+=(inv?-1:1)*(vert?y-r:x-q);
                    point+=nb[r][q];
                    fixed[r][q]=false;
                }
                else if(board[y][x]>0 && !fixed[y][x]){
                    if(nb[r][q]<1){
                        nb[r][q]=board[y][x];
                        didMove+=(inv?-1:1)*(vert?y-r:x-q);
                    }else if(nb[r][q]==board[y][x]){
                        nb[r][q]*=2;
                        point+=nb[r][q];
                        nextSpot+=inv?-1:1;
                        didMove++;
                    }else{
                        nextSpot+=inv?-1:1;
                        q=vert?x:nextSpot;
                        r=vert?nextSpot:y;
                        nb[r][q]=board[y][x];
                        didMove+=(inv?-1:1)*(vert?y-r:x-q);
                    }
                }
            }
        }
        board=nb;
        score+=point;
        return didMove;
    }
    
    /**
     * Checks if the there are any vacant spaces on the board or if there are 
     * any possible moves present or not.
     * It also checks whether we have reaching the winning condition that is 
     * have made 2048.
     * @return -1 if 2048 formed else (number of moves + free block present or not)
     */
    int notDone(){
        int moves=0,x,y;
        for(y=0;y<4;y++){
            for(x=0;x<4;x++){
                if(x<3&&board[y][x]==board[y][x+1]&&!(fixed[y][x]&fixed[y][x+1]) ||
                        y<3&&board[y][x]==board[y+1][x]&&!(fixed[y][x]&fixed[y+1][x])) moves++;
                if(board[y][x]>2047) return -1;
            }
        }
        return hasFree()+moves;
    }
    
    /**
     * The method whether zero blocks are present on the whole board or not.
     * @return 1 if zero block is there else returns 0
     */
    int hasFree(){
        for(int[]y:board)
            for(int x:y)
                if(x<2)return 1;
        return 0;
    }
    
    /**
     * The Method is used to clone the board and the scores into another object.
     * @param original Object containing all the details of the board.
     */
    void clone2dArray(GameBoard original) {
        for(int i=0;i<original.board.length;++i) {
            board[i] = original.board[i].clone();
            fixed[i] = original.fixed[i].clone();
        }
        score=original.score;
    }
    
    /**
     * The method return a list of all the positions that are empty on the board.
     * @return List containing the empty positions.
     */
    public List<Integer> getEmptyCellIds() {
        List<Integer> cellList = new ArrayList<>();
        
        for(int i=0;i<4;++i) {
            for(int j=0;j<4;++j) {
                if(board[i][j]==0) {
                    cellList.add(4*i+j);
                }
            }
        }
        return cellList;
    }
    
    /**
     * The Method counts the number of empty cells found on the basis of the 
     * list created.
     * @return Number of cells found to be empty.
     */
    private Integer cache_emptyCells=null;
    public int getNumberOfEmptyCells() {
        if(cache_emptyCells==null) {
            cache_emptyCells = getEmptyCellIds().size();
        }
        return cache_emptyCells;
    }
    
    /**
     * Method to set a value to the empty position pointed by the values (i,j).
     * @param i X-Coordinate
     * @param j Y-Coordinate
     * @param value Value to be assigned.
     */
    public void setEmptyCell(int i, int j, int value) {
        if(board[i][j]==0) {
            board[i][j]=value;
            cache_emptyCells=null;
        }
    }
    
    /**
     * This method is used to add a new 2/4 to the board randomly in any of the 
     * available blocks.
     * The probability of adding a 4 to the board is 15% while that of 2 is 85%.
     */
    void addTwo(){    
        if (hasFree()<1) return;
        int x,y,num;
        do{
            x=(new Random()).nextInt(4);
            y=(new Random()).nextInt(4);
        }while(board[x][y]>0);
        num=(new Random()).nextInt(100);
        board[x][y]=(num<=85)?2:4;
    }
    
    /**
     * Print the values in their respective position on the board.
     */
    void drawBoard(){
        System.out.println("-------------------------------------------");
        for(int i=0;i<4;i++)
        {
            System.out.println();
            for(int j=0;j<4;j++)
                System.out.print((fixed[i][j]?"<":"")+board[i][j]+(fixed[i][j]?">":"")+"\t");
            System.out.println("\n");
        }
        System.out.println("Score: "+score);
        System.out.println("-------------------------------------------");
    }
    
    /**
     * This method is used to compare two instances of the board to check if 
     * they are equal or not.
     * @param currBoardArray First Board instance.
     * @param newBoardArray Second Board instance.
     * @return True if both are equal else returns false.
     */
    public boolean isEqual(int[][] currBoardArray, int[][] newBoardArray) {
    	boolean equal = true;
        for(int i=0;i<currBoardArray.length;i++) {
            for(int j=0;j<currBoardArray.length;j++) {
                if(currBoardArray[i][j]!= newBoardArray[i][j]) {
                    equal = false; 
                    return equal;
                }
            }
        }
        return equal;
    }
    
    /**
     * Method to take the initial condition of the board input by the user.
     */
    void loadBoard(){
        Scanner q=(new Scanner(System.in));
        for(int i=0;i<4;i++)
            for(int j=0;j<4;j++){
                board[i][j]=q.nextInt();
                fixed[i][j]=false;
            }
    }
    
    /**
     * This method is called to insert a fixed block in the board.
     * The function uses the random class and generates a fixed block with the
     * probability of 3%. 
     * @return Boolean: Whether Fixed block was inserted in the board or not.
     */
    boolean greyBlock()
    {
        int gr=(new Random()).nextInt(100);
        if(gr>97 && maxPow()>3 && maxPow()<10)
        {
            int p=0;
            while(p<3)
                p=(new Random()).nextInt(maxPow());
            if (hasFree()>0){
                int a[]=bestPoint(p);
                if(a[0]!=-1 && a[1]!=-1){
                    int[] neighbors = {-1,0,1};
                    int xx=a[0],yy=a[1];
                    do{
                        xx=a[0]+neighbors[(new Random()).nextInt(3)];
                        while(xx<0 || xx>=board.length) {
                            xx=a[0]+neighbors[(new Random()).nextInt(3)];
                        }
                        yy=a[1]+neighbors[(new Random()).nextInt(3)];
                        while(yy<0 || yy>=board.length) {
                            yy=a[1]+neighbors[(new Random()).nextInt(3)];
                        }
                    }while(board[xx][yy]>0);
                    if(countEmpty(a[0],a[1])!=0){
                        board[xx][yy]=(int)Math.pow(2, p);
                        fixed[xx][yy]=true;
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * The method chooses the 3x3 sub-matrix in which the fixed block should be inserted 
     * based on the present scenario of the board.
     * @param p Power of 2 to be added as fixed block.
     * @return (x,y) Position around which the fixed block should be inserted.
     */
    int[] bestPoint(int p)
    {
        int mX=0,mY=0,qq,min=11,empty,maxEmpty=0;
        for(int i=0;i<4;i++)
        {
            for(int j=0;j<4;j++)
            {
                if(board[i][j]>0 && !fixed[i][j])
                {
                    if(Math.pow(2,p)>board[i][j])
                        qq=(int)Math.pow(2,p)/board[i][j];
                    else
                        qq=board[i][j]/(int)Math.pow(2,p);
                    if (qq<=min)
                    {
                        empty=countEmpty(i,j);
                        if(empty>maxEmpty){
                            min=qq;
                            mX=i;
                            mY=j;
                            maxEmpty=empty;
                        }
                    }
                }
            }
        }
        int nope[]={-1,-1};
        int cood[]={mX,mY};
        if(maxEmpty>0)
            return cood;
        return nope;
    }
    
    /**
     * Counts the Number of empty i.e. zero blocks around a particular coordinate.
     * @param i X coordinate
     * @param j Y coordinate
     * @return Number of Empty or zero blocks.
     */
    int countEmpty(int i,int j)
    {
        int empty=0;
        int[] neighbors = {-1,0,1};
        for(int k:neighbors){
            int x=i+k;
            if(x<0 || x>=board.length) {
                continue;
            }
            for(int l:neighbors){
                int y = j+l;
                if(y<0 || y>=board.length) {
                    continue;
                }
                if(board[x][y]<2) {
                    ++empty;
                }
            }
        }
        return empty;
    }
    
    /**
     * It finds the maximum power of 2 presently available on the board.
     * The fixed blocks generated should always be less than the maximum available
     * value on the board.
     * @return Max. Power of 2 present on the board.
     */
    int maxPow(){
        int max=0;
        for(int i=0;i<4;i++){
            for(int j=0;j<4;j++){
                if(max<board[i][j]) max=board[i][j];
            }
        }
        int ctr=0;
        while(max>0)
        {
            ctr++;
            max/=2;
        }
        return ctr;
    }
    
    /**
     * The start function chooses whether to start a new game or take the matrix
     * input by the player.
     */
    void start(){
        System.out.println("Auto-Solver:\nChoose your option:\n1.Start a fresh Game.\n2.Take the scenario input row-wise.");
        int ch=(new Scanner(System.in)).nextInt();
        score=0;
        initial(ch);
    }
    
    /**
     * Initializes the board for the game according to the choice of the user.
     * Then the game is played.
     * @param ch Choice input by the user.
     */
    void initial(int ch)
    {
        int x;
        if(ch==1)
        {
            addTwo();
            addTwo();
        }
        else
            loadBoard();
    }
}
