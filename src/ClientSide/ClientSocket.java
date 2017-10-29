package ClientSide;

import Common.PlayerBoard;

import java.io.*;
import java.net.Socket;

public class ClientSocket extends Thread {

    private Client _client;
    Socket _socket;
    private PrintWriter toServer;
    private BufferedReader fromServer;

    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;

    volatile boolean running = true;

    ClientSocket(Client client, Socket socket){
        _client = client;
        _socket = socket;
        getStreams();
    }

    private void getStreams() {
        try {
            //toServer = new PrintWriter(_socket.getOutputStream(), true);
            //fromServer = new BufferedReader(new InputStreamReader(_socket.getInputStream()));
            objectOutputStream = new ObjectOutputStream(_socket.getOutputStream());
            objectInputStream = new ObjectInputStream(_socket.getInputStream());
            //System.out.println("Done streams on client");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sendMessage(String message){
        toServer.println(message);
    }

    @Override
    public void run() {
        System.out.println(toString() + "has began");
        while (running) {
            try {
                //String s = objectInputStream.readUTF();
                //System.out.println("ClientSide.Client received " + s);
                Object o = objectInputStream.readObject();
                PlayerBoard pb = (PlayerBoard) o;
                System.out.println(pb);
            } catch (IOException e) {
                running = false;
                //e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        System.out.println("ClientSide.Client "+ "died");

    }

    @Override
    public String toString() {
        return _client.toString();
    }
}
