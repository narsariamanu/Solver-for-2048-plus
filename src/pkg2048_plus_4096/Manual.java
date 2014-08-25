package pkg2048_plus_4096;
import java.util.*;
import java.lang.*;
import java.io.*;
/**
 * This is the program used to play the game 2048+4096 in the manual mode.
 * author @manu
 */
class Manual {
    /*public static void main(String[] a){
        (new Manual()).start();    //object created and called.
    }
    */
    private int[][] board=new int[4][4];
    boolean[][] fixed=new boolean[4][4];
    int score;
    
    /**
     * The start function chooses whether to start a new game or take the matrix input by the player.
     */
    void start(){
        System.out.println("Manual-Play:\nChoose your option:\n1.Start a fresh Game.\n2.Take the scenario input row-wise.");
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
        do{
            drawBoard(); //printing the board.
            resolve();  //Making Moves.
            if(!greyBlock())
                addTwo();
        }while((x=notDone())>0);
        drawBoard();
        //Printing the end result.
        if(x<0)
            System.out.println("========================\nYou WIN.\n========================");
        else
            System.out.println("========================\nYou LOSE.\n========================");
    }
    
    /**
     * This method is called to insert a fixed block in the board.
     * The function uses the random class and generates a fixed block with the
     * probability of 3%. 
     * @return Boolean: Whether Fixed block was inserted in the board or not.
     */
    boolean greyBlock()
    {
        //System.out.println("GREY CALL");
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
     * Checks if the there are any vacant spaces on the board or if there are 
     * any possible moves present or not.
     * It also checks whether we have reaching the winning condition that is 
     * have made 2048.
     * @return -1 if 2048 formed else (number of moves + free block present or not)
     */
    int notDone(){
        int moves,x,y;
        for(moves=y=0;y<4;y++){
            for(x=0;x<4;x++){
                if(x<3&&board[y][x]==board[y][x+1]&&!(fixed[y][x]&fixed[y][x+1]) ||
                        y<3&&board[y][x]==board[y+1][x]&&!(fixed[y][x]&fixed[y+1][x])) moves++;
                if(board[y][x]>2047) return -1;
            }
        }
        return hasFree()+moves;
    }
    
    /**
     * The Function is used to ask the user what would be his next move based on
     * the present scenario of the board.
     * A loop is used to keep a track of the invalid moves.
     */
    void resolve(){
        if(notDone()>0){
            do{
                System.out.print("Enter Your Move:  ");
                switch((new Scanner(System.in)).nextLine().charAt(0)){
                    case 'w':
                        System.out.println("-------------------------------------------\nChosen: UP");
                        if (fold(false,true)>0)return;
                        break;
                    case 's':
                        System.out.println("-------------------------------------------\nChosen: DOWN");
                        if (fold(true, true)>0)return;
                        break;
                    case 'a':
                        System.out.println("-------------------------------------------\nChosen: LEFT");
                        if (fold(false,false)>0)return;
                        break;
                    case 'd':
                        System.out.println("-------------------------------------------\nChosen: RIGHT");
                        if (fold(true,false)>0)return;
                        break;
                }
            } while(true);
        }
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
     * Method to take the initial condition of the board input by the user.
     */
    void loadBoard(){
        System.out.println("Enter The 4x4 Matrix:");
        Scanner q=(new Scanner(System.in));
        for(int i=0;i<4;i++)
            for(int j=0;j<4;j++){
                board[i][j]=q.nextInt();
                fixed[i][j]=false;
            }
    }
    
    /**
     * Print the values in their respective position on the board.
     */
    void drawBoard(){
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
}