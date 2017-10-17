import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private Game game;
    ArrayList<Socket> sockets = new ArrayList<>();
    private boolean running = true;
    private State state;

    enum State{
        Waiting,
        SettingShips
    }

    public Server(){
        state = State.Waiting;
    }

    public static final int PORT = 1000;

    public static void main(String[] args) {
        new Server().start();
    }

    private void readyToStart(){
        if(sockets.size() == 3){
            state = State.SettingShips;
        }
    }

    void left(Socket socket){
        System.out.println(socket + " has sent message that left");
        if(state == State.Waiting){
            sockets.remove(socket);
        }
    }

    private void start() {
        System.out.println("Starting server");
        try (ServerSocket serverSocket = new ServerSocket(PORT, 3)) {
            System.out.println("Started");
            // WAITING FOR ALL PLAYERS
            while (state == State.Waiting) {
                System.out.println("Waiting for players...");
                Socket s = serverSocket.accept();
                System.out.println("New player!" + s);
                sockets.add(s);
                System.out.println("Starting thread for him");
                PlayerSocket p = new PlayerSocket(this, s);
                p.start();
                readyToStart();
            }
            game = new Game();
            System.out.println("READY TO BEGIN");
        } catch (IOException e) {
            System.err.println("Could not listen on port " + 1000);
            System.exit(-1);

        }
    }
}
