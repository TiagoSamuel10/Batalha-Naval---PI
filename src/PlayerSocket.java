import java.io.*;
import java.net.Socket;

public class PlayerSocket extends Thread {

    private static int i = 0;

    private final int mine;
    private final Server _theServer;
    Socket socket;

    private ObjectOutputStream objectOutputStream;
    private OutputStream out;
    private InputStream in;
    private ObjectInputStream objectInputStream;

    volatile boolean running;


    public PlayerSocket(Server theServer, Socket socket) {
        i++;
        mine = i;
        _theServer = theServer;
        this.socket = socket;
        running = true;
        getStreams();
    }

    private void getStreams() {
        try {
            //this.out = socket.getOutputStream();
            //this.in = socket.getInputStream();
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
            //System.out.println("Done streams on server");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sendObject(Object object){
        try {
            objectOutputStream.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sendMessage(String message){
        new PrintWriter(out, true).println(message);
    }

    @Override
    public void run() {
        System.out.println(toString() + "has began");
        while (running) {
            try {
                String s = objectInputStream.readUTF();
                System.out.println("Server received " + s);
            } catch (IOException e) {
                running = false;
                //e.printStackTrace();
            }
        }
        //TODO: WARN THAT I DISCONNECTED?
        _theServer.left(socket);
        i--;
        System.out.println(toString() + "has ended");
    }

    @Override
    public String toString() {
        return "Player socket " + mine + " ";
    }

    public void close() {
        running = false;
    }
}
