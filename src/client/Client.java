package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    public static void main(String[] args){

        String hostName = "PC";
        int portNumber = 4444;

        try( Socket kkSocket = new Socket(hostName, portNumber)) {
            PrintWriter toServer =
                    new PrintWriter(kkSocket.getOutputStream(), true);
            BufferedReader fromServer =
                    new BufferedReader(
                            new InputStreamReader(kkSocket.getInputStream()));
            BufferedReader stdIn =
                    new BufferedReader(
                            new InputStreamReader(System.in));
            while(stdIn.readLine() != null) {
                toServer.println(stdIn.readLine());
            }
        }catch (Exception e){
            System.out.println("Not right!");
        }

    }


}
