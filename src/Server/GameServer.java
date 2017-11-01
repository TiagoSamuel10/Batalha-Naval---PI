package Server;

import Common.Network;
import Common.Network.*;
import Common.NetworkChat;
import Common.PlayerBoard;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

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
                System.out.println(count);
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
                    //System.out.println("Name is " + name);
                    connection.name = r.name;
                }
                if(object instanceof int[][]){
                    PlayerBoard pb = new PlayerBoard((int[][]) object);
                    pb.lightItUp();
                    System.out.println(pb);
                }

            }

            public void disconnected (Connection c) {
                BConnection connection = (BConnection)c;
                //System.out.println("LEFT: " + connection.name);
                count--;
                if(state == GameState.waitingForPlayers){
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
    }

    public static void main(String[] args) throws IOException {
        new GameServer();
    }


}
