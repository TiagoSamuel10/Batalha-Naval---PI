package Server;

import Common.Network;
import Common.Network.*;
import Common.PlayerBoard;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.*;

public class GameServer {

    private enum GameState{
        waitingForPlayers,
        waitingForShips,
        playing
    }

    private GameState state;
    private Game game;
    private Server server;
    private int count;
    private boolean gameStarted;
    private BConnection[] connections;

    public GameServer() throws IOException {

        state = GameState.waitingForPlayers;

        connections = new BConnection[3];

        server = new Server() {
            protected Connection newConnection () {
                // By providing our own connection implementation, we can store per
                // connection state without a connection ID to state look up.
                return new BConnection();
            }
        };

        Network.register(server);

        server.addListener(new Listener() {

            @Override
            public void connected(Connection connection) {
                if(state != GameState.waitingForPlayers){
                    connection.sendTCP(new IsFull());
                    connection.close();
                    return;
                }
                connections[count] = (BConnection)connection;
                count++;
                System.out.println("Count : " + count);
                if(count == 3){
                    try {
                        server.update(0);
                        server.update(1);
                        server.update(2);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    startTheGame();
                }
            }

            public void received (Connection c, Object object) {

                BConnection connection = (BConnection)c;

                if(object instanceof Register){
                    Register r = (Register) object;
                    String name = r.name;
                    if (name == null) {
                        return;
                    }
                    System.out.println("Connected " + name);
                    connection.name = r.name;
                    ConnectedPlayers connectedPlayers = new ConnectedPlayers();
                    connectedPlayers.names = getConnectedNames();
                    server.sendToAllTCP(connectedPlayers);
                }
                if(object instanceof int[][]){
                    PlayerBoard pb = new PlayerBoard((int[][]) object);
                    pb.nukeIt();
                    System.out.println(pb);
                    if(addToGame(pb)){
                        WhoseTurn whoseTurn = new WhoseTurn();
                        whoseTurn.id = 0;
                        server.sendToAllTCP(whoseTurn);
                        server.sendToAllTCP(new CanStart());
                    }
                }

            }

            public void disconnected (Connection c) {
                BConnection connection = (BConnection)c;
                //System.out.println("LEFT: " + connection.name);
                count--;
                connection.id = -1;
                if(state == GameState.waitingForPlayers){
                    System.out.println("Disconnected " + connection.name);
                    ConnectedPlayers connectedPlayers = new ConnectedPlayers();
                    connectedPlayers.names = getConnectedNames();
                    server.sendToAllTCP(connectedPlayers);
                }
                if(state == GameState.waitingForShips){
                    abortGame();
                }
            }
        });

        server.bind(Network.port);
        server.start();

        System.out.println("Server started");

    }

    private String[] getConnectedNames(){
        String[] string = new String[count];
        for(int i = 0; i < count; i++){
            string[i] = connections[i].name;
        }
        return string;
    }

    private boolean addToGame(PlayerBoard playerBoard){
        for(int i = 0; i < connections.length; i++){
            if(connections[i].id == -1){
                connections[i].id = i;
                game.setPlayerBoard(playerBoard, i);
                return i == connections.length - 1;
            }
        }
        return false;
    }

    private void abortGame() {
        state = GameState.waitingForPlayers;
        server.sendToAllTCP(new StartTheGame());
    }

    private void startTheGame() {
        for (int i = 0; i < connections.length; i++){
            System.out.println(connections[i].name);
        }
        game = new Game();
        server.sendToAllTCP(new StartTheGame());
        state = GameState.waitingForShips;

    }

    static class BConnection extends Connection {
        String name;
        int id;

        BConnection(){
            id = -1;
        }
    }

    public static void main(String[] args) throws IOException {
        new GameServer();
    }


}
