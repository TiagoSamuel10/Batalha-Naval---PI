package Server;

import Common.Network;
import Common.Network.*;
import Common.PlayerBoard;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.*;
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
                return new BConnection();
            }
        };

        Network.register(server);

        server.addListener(new Listener() {

            private boolean isANewPlayer(){
                return true;
            }

            private void decideWhatToDo(BConnection connection, Register r){

                String name = r.name;
                if (name == null) {
                    return;
                }
                System.out.println(connection.getRemoteAddressTCP().getPort());
                System.out.println(connection.getRemoteAddressTCP().getAddress());
                connection.name = r.name;
                ConnectedPlayers connectedPlayers = new ConnectedPlayers();
                connectedPlayers.names = getConnectedNames();
                server.sendToAllTCP(connectedPlayers);


                System.out.println("Connected " + connection.name);

                switch (state){
                    case waitingForPlayers:
                    case waitingForShips:
                    case playing:
                }

                connections[count] = connection;
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
                System.out.println(Arrays.toString(server.getConnections()));

            }

            public void received (Connection c, Object object) {

                BConnection connection = (BConnection)c;

                if(object instanceof Register){
                    decideWhatToDo(connection, (Register) object);
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
