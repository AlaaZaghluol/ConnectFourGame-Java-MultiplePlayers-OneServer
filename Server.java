package clientservergame;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
class Server implements Serializable{


    // The board variables //
    private final int width;
    private final int height;

    private final char[][] board;
    private final char empty = '-';

    private int lastRow = -1;
    private int lastColumn = -1;

    // ArrayList contains strings for each player, each player have a different string //
    static ArrayList<String> players = new ArrayList<String>();

    // Constructor , Filling the board with '-' , which means it's empty //
    public Server (int width, int height) {
        this.width = width;
        this.height = height;

        board = new char[height][];

        for(int i = 0; i < height; i++) {
            Arrays.fill(board[i] = new char[width], empty);
        }
    }



    public static void main(String args[])throws Exception{

        // Creation of the board //
        Server server = new Server(7, 6);
        System.out.println("Server Started Running ...");

        try
        {
            while (true){

                // Creation of 3 server sockets for each client to connect //
                ServerSocket s1 = new ServerSocket(3333);
                ServerSocket s2 = new ServerSocket(3334);
                ServerSocket s3 = new ServerSocket(3335);

                // Waiting for each client to connect before starting the game //
                Socket s=s1.accept();
                System.out.println("Client 1 Connected.");
                players.add("x");
                Socket ss=s2.accept();
                System.out.println("Client 2 Connected.");
                players.add("O");
                Socket sss=s3.accept();
                System.out.println("Client 3 Connected.");
                players.add("*");

                // Initialize the maximum number of moves which is " Width of board * Height of board " //
                int moves = 6 * 7;

                // player vaariable indicates the turns //
                int player;

                // Initalizing the input and output streams for each client //
                ObjectOutputStream dout1=new ObjectOutputStream(s.getOutputStream());
                dout1.flush();
                ObjectInputStream din1=new ObjectInputStream(s.getInputStream());
                ObjectOutputStream dout2=new ObjectOutputStream(ss.getOutputStream());
                dout2.flush();
                ObjectInputStream din2=new ObjectInputStream(ss.getInputStream());
                ObjectOutputStream dout3=new ObjectOutputStream(sss.getOutputStream());
                dout2.flush();
                ObjectInputStream din3=new ObjectInputStream(sss.getInputStream());

                // The variable input indicates the input recieved from the user //
                // Starts with -1 value as a garbage value //
                int input = -1 ;
                // counter will indicate turns and pass the value to variable "player" //
                int counter = 0;

                // The game starts in this loop //
                do {
                    // set the player turn //
                    player = counter;

                    // send the empty board to clients & print the board itself//
                    System.out.println("Sending Board To Clients");
                    dout1.writeObject(server.toString());
                    dout2.writeObject(server.toString());
                    dout3.writeObject(server.toString());
                    System.out.println(server);


                    // 1. get player character from the ArrayList to write the value to the board //
                    // IF player 0 turn get "X", if player 1 turn get "O" , if player 2 turn get "*" //
                    char badge = players.get(player).charAt(0);

                    // Waiting for input from player that has the turn //
                    if (player == 0){
                        dout1.writeObject("1");
                        input = -1 ;
                        do {
                            input = (int)din1.readObject();
                        }while(input == -1);}
                    if (player == 1){
                        dout2.writeObject("1");
                        input = -1 ;
                        do {
                            input = (int)din2.readObject();
                        }while(input == -1);}
                    if (player == 2){
                        dout3.writeObject("1");
                        input = -1 ;
                        do {
                            input = (int)din3.readObject();
                        }while(input == -1);}


                    // 2. After receiving the player input, input it to the board //
                    server.makeTurn(badge, input);

                    // 3. Check for winning conditions //
                    if (server.hasWinningCombination()) {
                        System.out.println("Player " + badge + " has won!");
                        dout1.writeObject("Player " + badge + " has won!");
                        dout2.writeObject("Player " + badge + " has won!");
                        dout3.writeObject("Player " + badge + " has won!");
                        return;
                    }



                    // 4. Change the player to the next one //
                    if(counter != 2) {
                        counter++;
                    }
                    else {
                        counter = 0;
                    }

                    // 5. Decrease number of total moves //
                    --moves;

                    // IF the the board is full, exit the game //
                    if (moves == 0) {
                        break;
                    }


                } while (true);

                System.out.println("Game over, nobody has won.");

                s1.close();
                s2.close();
                s3.close();
            }
        }
        catch(IOException e)
        {
            System.out.println(e);
        }

    }

    public void makeTurn(char badge, int input) {
        do {
            // 1. ask for a column
            System.out.println("\nPlayer " + badge + " turn: ");

            // 2. check the input
            if (input < 0 || input >= width) {
                System.out.println("Please enter a number between 0 and " + (width - 1));
                continue;
            }

            // 3. place a badge
            for (int i = height - 1; i >= 0; i--) {
                if (board[i][input] == empty) {
                    board[lastRow = i][lastColumn = input] = badge;
                    return;
                }
            }

            // Can't find a spot
            System.out.println("Column " + input + " is full.");

        } while (true);
    }

    public boolean hasWinningCombination() {
        if (lastColumn == -1) {
            // no moves have been made!
            return false;
        }

        char badge = board[lastRow][lastColumn];
        String winningCombination = String.format("%c%c%c%c", badge, badge, badge, badge);

        return horizontal().contains(winningCombination) ||
                vertical().contains(winningCombination) ||
                diagonal().contains(winningCombination) ||
                backDiagonal().contains(winningCombination);
    }

    private String horizontal() {
        return new String(board[lastRow]);
    }

    private String vertical() {
        StringBuilder sb = new StringBuilder(height);
        for (int i = 0; i < height; i++) {
            sb.append(board[i][lastColumn]);
        }

        return sb.toString();
    }

    private String diagonal() {
        StringBuilder sb = new StringBuilder(height);
        for (int i = 0; i < height; i++) {
            int j = lastColumn + lastRow - i;
            if (0 <= j && j < width) {
                sb.append(board[i][j]);
            }
        }

        return sb.toString();
    }

    private String backDiagonal() {
        StringBuilder sb = new StringBuilder(height);
        for (int i = 0; i < height; i++) {
            int j = lastColumn - lastRow + i;
            if (0 <= j && j < width) {
                sb.append(board[i][j]);
            }
        }

        return sb.toString();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                stringBuilder.append(board[j][i]);
                stringBuilder.append(" ");
            }

            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

}

