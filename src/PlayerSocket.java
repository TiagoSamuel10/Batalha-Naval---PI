import java.io.*;
import java.net.Socket;

public class PlayerSocket extends Thread implements Runnable {

    private static int i = 0;

    private final int mine;
    private final Server _theServer;
    private Socket socket;
    private OutputStream out;
    private InputStream in;
    public volatile boolean running;

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
            this.out = socket.getOutputStream();
            this.in = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println(toString() + "has began");
        while (running) {
            try {
                BufferedReader fromClient = new BufferedReader(new InputStreamReader(in));
                String s = fromClient.readLine();
                System.out.println(toString() + "said " + s);
            } catch (IOException e) {
                running = false;
                //e.printStackTrace();
            }
        }
        //TODO: WARN THAT I DISCONNECTED?
        _theServer.left(socket);
        System.out.println(toString() + "has ended");
    }

    @Override
    public String toString() {
        return "Client number " + mine + " ";
    }

    public void close() {
        running = false;
    }
}
