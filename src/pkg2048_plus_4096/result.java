package pkg2048_plus_4096;
import java.util.*;
import java.io.*;
import java.lang.*;
/**
 * This class is used to create User-Defined objects which carry the result
 * information.
 * The direction in which the movement is made along with the score after 
 * moving in that direction.
 * @author Manu
 */
public class result {
    private int score;
    private char dir;
    
    /**
     * Function to return the score.
     * @return Score after moving in the given direction.
     */
    int getScore(){
        return score;
    }
    
    /**
     * Function to return the direction in which we should move.
     * @return Direction in which we will move.
     */
    char getDirection(){
        return dir;
    }
    
    /**
     * Assigns value to the object.
     * @param sc Score after moving.
     * @param ch Direction to move in.
     */
    void set(int sc,char ch){
        score=sc;
        dir=ch;
    }
            
}
