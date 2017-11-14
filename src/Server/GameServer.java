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

    private final static long TIME_TO_WAIT = 1000 * 60 ;
    private boolean timing;
    private long currentWaitedTime;
    private boolean disconnectedWhenWaitingForShips;
    private long started;

    private GameState state;
    private Game game;
    private Server server;
    private int count;
    private boolean gameStarted;

    //WILL SAVE WHAT CONNECTIONS THE GAME STARTED WITH
    //SO IT'S POSSIBLE TO KNOW IF SOMEBODY WHO DROPPED IS RECONNECTING
    private BConnection[] playersThatStarted;

    GameServer() throws IOException {

        state = GameState.waitingForPlayers;

        disconnectedWhenWaitingForShips = false;
        timing = false;
        currentWaitedTime = 0;
        playersThatStarted = new BConnection[3];

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

                connection.name = r.name;
                connection.address = r.adress;
                System.out.println(connection.address);

                switch (state){
                    case waitingForPlayers:
                        count++;
                        if(count == 3){
                            sendReadyForShips();
                        }
                        break;
                    case waitingForShips:
                        // see if it's a new player
                        // or somebody that dropped
                        if(disconnectedWhenWaitingForShips){
                            boolean old = true;
                            for (BConnection c: playersThatStarted) {
                                if(c.address.equalsIgnoreCase(r.adress)){
                                    count++;
                                    System.out.println("RETURNED BOY");
                                    break;
                                }
                                old = false;
                            }
                            if(!old) {
                                connection.sendTCP(new IsFull());
                            }
                        }
                        else{
                            connection.sendTCP(new IsFull());
                        }
                        break;
                    case playing:
                        break;
                }


                connection.id = count - 1;

                System.out.println("Connected " + connection.name);

                ConnectedPlayers connectedPlayers = new ConnectedPlayers();
                connectedPlayers.names = getConnectedNames();
                server.sendToAllTCP(connectedPlayers);

                System.out.println("Count : " + count);
                System.out.println(Arrays.toString(server.getConnections()));

            }

            public void received (Connection c, Object object) {

                BConnection connection = (BConnection)c;

                if(object instanceof Register){
                    decideWhatToDo(connection, (Register) object);
                }
                if(object instanceof int[][]){
                    PlayerBoard pb = new PlayerBoard((int[][]) object);
                    //ADD TO GAME
                    game.setPlayerBoard(pb, ((BConnection) c).id);
                    //IF WE'VE RECEIVED ALL, WE CAN START
                    if(game.canStart()){
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
                switch (state){
                    case waitingForShips:
                        System.out.println("Disconnected " + connection.name);
                        ConnectedPlayers connectedPlayers = new ConnectedPlayers();
                        connectedPlayers.names = getConnectedNames();
                        server.sendToAllTCP(connectedPlayers);
                    case waitingForPlayers:
                        handleLeavingWhileShips();
                }
                System.out.println("Count : " + count);
                System.out.println(Arrays.toString(server.getConnections()));
            }
        });

        server.bind(Network.port);
        server.start();
        System.out.println("Server started");

    }

    private String[] getConnectedNames(){
        String[] string = new String[count];
        int i = 0;
        for (Connection connection : server.getConnections()  ) {
            string[i] = ((BConnection) connection).name;
            i++;
        }
        return string;
    }

    private boolean addToGame(BConnection connection, PlayerBoard playerBoard){
            if(connection.id == -1){
                game.setPlayerBoard(playerBoard, connection.id);
                return true;
            }
            return false;
    }

    private void handleLeavingWhileShips(){
        disconnectedWhenWaitingForShips = true;
    }

    private void abortGame() {
        state = GameState.waitingForPlayers;
        server.sendToAllTCP(new StartTheGame());
    }

    private void sendReadyForShips() {
        int i = 0;
        for(Connection connection : server.getConnections()){
            playersThatStarted[i] = (BConnection) connection;
            System.out.println("PLAYER: " + playersThatStarted[i].name);
            i++;
        }
        game = new Game();
        server.sendToAllTCP(new StartTheGame());
        state = GameState.waitingForShips;
    }

    static class BConnection extends Connection {
        String name;
        int id;
        String address;

        BConnection(){
            id = -1;
        }
    }

    public static void main(String[] args) throws IOException {
        new GameServer();
    }


}
