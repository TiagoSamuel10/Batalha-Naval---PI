package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    public static void main(String[] args){

        Server s = new Server();

    }

    boolean listening = true;

    private static final int MAX_PLAYERS = 3;



    public Server(){

        int port = 4444;

        try (ServerSocket serverSocket = new ServerSocket(port)){
            System.out.println("RUNNING");
            while(listening){
                Socket clientSocket = serverSocket.accept();
                System.out.println("ACCEPTED TO SERVER");
            }
        }catch (Exception e){
            System.out.println("NOP " + e.toString());
        }
        System.out.println("STOPPED");

    }

}
