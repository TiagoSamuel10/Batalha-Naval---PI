import java.io.*;
import java.net.Socket;

public class PlayerSocket extends Thread implements Runnable {

    private Socket socket;
    private OutputStream out;
    private InputStream in;
    public volatile boolean running;

    public PlayerSocket(Socket socket) {
        //System.out.println("1");
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
        while (running) {
            System.out.println(running);
            try {
                BufferedReader fromClient = new BufferedReader(new InputStreamReader(in));
                String s = fromClient.readLine();
                if (s != null) {
                    System.out.println(s);
                }
            } catch (IOException e) {
                running = false;
                //e.printStackTrace();
            }
        }
        System.out.println("OVER");
    }

    public void close() {
        running = false;
    }
}
