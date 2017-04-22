package clientservergame;

import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.Scanner;
import javax.swing.JOptionPane;
class Client{


    private final int width;
    private final int height;
    public static char[][] board;
    private final char empty = '-';
    private int lastRow = -1;
    private int lastColumn = -1;



    public Client (int width, int height) {
        this.width = width;
        this.height = height;


        board = new char[height][];

        for(int i = 0; i < height; i++) {
            Arrays.fill(board[i] = new char[width], empty);
        }
    }


    public static void main(String args[])throws Exception{
        
        int ss = Integer.parseInt(JOptionPane.showInputDialog("Enter socket number : "));
        Socket s=new Socket("localhost",ss);
        System.out.println("Connected to " + s.getInetAddress() + " on port " + s.getPort());
        Client c = new Client (7,6);
        ObjectOutputStream dout=new ObjectOutputStream(s.getOutputStream());
        dout.flush();
        ObjectInputStream din=new ObjectInputStream(s.getInputStream());
        int input;
        
        String temp;
        do{
            try{
                temp = (String)din.readObject();
                if((temp).length()>2){
                    System.out.println(temp);
                    if(temp.contains("won")){
                        System.out.println("Closing the Game ...");
                        break;
                    }
                }
                else if(Integer.parseInt((String) temp) == 1){
                    System.out.println("Your Turn ...");
                    input = Integer.parseInt(JOptionPane.showInputDialog("Your Turn ...."));
                    dout.writeObject(input);
                }


            }

            catch(Exception e){
                System.err.println(e);
            }

        }while(true);

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