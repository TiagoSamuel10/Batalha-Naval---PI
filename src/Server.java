import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    public Server(){
        new Thread(new TheServer()).start();
    }

    public static final int PORT = 1000;

    public static void main(String[] args) {
        new Server();
    }

    private static class TheServer implements Runnable {

        private Game game;
        ArrayList<Socket> sockets = new ArrayList<>();

        @Override
        public void run() {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                while (sockets.size() != 3) {
                    Socket s = serverSocket.accept();
                    sockets.add(s);
                    System.out.println(s);
                    PlayerSocket p = new PlayerSocket(s);
                    p.start();
                }
                game = new Game();
                System.out.println("Waiting for moves");
            } catch (IOException e) {
                System.err.println("Could not listen on port " + 1000);
                System.exit(-1);
            }
        }
    }
}
