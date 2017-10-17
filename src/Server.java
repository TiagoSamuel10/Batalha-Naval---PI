import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private Game game;
    ArrayList<PlayerSocket> sockets = new ArrayList<>();
    private boolean running = true;
    private State state;

    private PrintWriter toClients;

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
            for (PlayerSocket playerSocket:
             sockets) {
                if(socket == playerSocket.socket){
                    sockets.remove(playerSocket);
                }
            }
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
                System.out.println("Starting thread for him");
                PlayerSocket p = new PlayerSocket(this, s);
                p.start();
                sockets.add(p);
                readyToStart();
            }
            game = new Game();
            //sendToAll("READY TO START");
            sendToAll(game.getPlayerBoards()[0]);
        } catch (IOException e) {
            System.err.println("Could not listen on port " + PORT);
            System.exit(-1);

        }
    }

    private void sendToAll(Object object){
        for (PlayerSocket playerSocket:
                sockets) {
            playerSocket.sendObject(object);
        }
    }

    private void sendToAll(String message){
        for (PlayerSocket playerSocket:
             sockets) {
            playerSocket.sendMessage("READY TO PLAY");
        }
    }
}
