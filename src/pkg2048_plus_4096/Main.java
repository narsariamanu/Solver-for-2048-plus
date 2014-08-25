package pkg2048_plus_4096;
import java.util.*;
import java.io.*;
import java.lang.*;

/**
 * This is a program to run the game of 2048+4096.
 * The Game rules are same as that of the standard 2048.
 * With the "Fixed Blocks" added to the difficulty of the problem.
 * @author Manu
 */
public class Main {
    /**
     * Menu Driven Main Function.
     * @param args
     */
    public static void main(String[] args)
    {
        while(true){
            System.out.println("\nController is:\n"
                    + "w -> UP\n"
                    + "s -> DOWN\n"
                    + "a -> LEFT\n"
                    + "d -> RIGHT\n"
                    + "Press ENTER after pressing the desired key.");
            System.out.println("Choose your option of game mode:\n1.Manual Mode.\n2.Solver Mode.\n3.Exit");
            int ch=(new Scanner(System.in)).nextInt();
            if(ch==1)
                (new Manual()).start();
            else if(ch==2)
                (new Solver()).solve();
            else if(ch==3)
                break;
            else
                System.out.println("Wrong Choice.");
        }
        System.out.println("Thank You.");
    }
}