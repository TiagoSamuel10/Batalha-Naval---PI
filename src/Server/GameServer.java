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

                boolean ready = false;

                connection.name = r.name;
                connection.address = r.address;
                System.out.println(connection.address);

                switch (state){
                    case waitingForPlayers:
                        count++;
                        if(count == 3){
                            ready = true;
                        }
                        break;
                    case waitingForShips:
                        // see if it's a new player
                        // or somebody that dropped
                        if(disconnectedWhenWaitingForShips){
                            boolean old = true;
                            for (BConnection c: playersThatStarted) {
                                if(c.address.equalsIgnoreCase(r.address)){
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

                connection.myID = count - 1;

                System.out.println("Connected " + connection.name);
                System.out.println("Connected myID becomes: " + connection.myID);

                ConnectedPlayers connectedPlayers = new ConnectedPlayers();
                connectedPlayers.names = getConnectedNames();
                server.sendToAllTCP(connectedPlayers);

                System.out.println("Count : " + count);
                System.out.println(Arrays.toString(server.getConnections()));

                if(ready){
                    sendReadyForShips();
                }

            }

            public void received (Connection c, Object object) {

                BConnection connection = (BConnection)c;

                if(object instanceof Register){
                    decideWhatToDo(connection, (Register) object);
                }
                if(object instanceof int[][]){
                    PlayerBoard pb = new PlayerBoard((int[][]) object);
                    //ADD TO GAME
                    //System.out.println("ADDING TO: " + connection.myID +
                    //        " WHICH IS " + connection.name);
                    game.setPlayerBoard(pb, connection.myID);
                    //IF WE'VE RECEIVED ALL, WE CAN START
                    if(game.canStart()){
                        WhoseTurn whoseTurn = new WhoseTurn();
                        whoseTurn.id = 0;
                        sendOthersBoards();
                        server.sendToAllTCP(whoseTurn);
                        server.sendToAllTCP(new CanStart());
                    }
                }

                if (object instanceof  AnAttackAttempt){
                    AnAttackAttempt attempt = (AnAttackAttempt) object;
                    //System.out.println(connection.myID % attempt.clientID);
                    //System.out.println(attempt.l+"\n"+ attempt.c);
                    boolean result = game.attack(
                            connection.myID % attempt.clientID,
                            attempt.l,
                            attempt.c);
                    AnAttackResponse response = new AnAttackResponse();
                    response.hitAnything = result;
                    response.newAttackedBoard = game.getPlayerBoard(
                            connection.myID % attempt.clientID).getToSendToPaint()
                    ;
                    if(result){
                        System.out.println("ATTACKED SOME SHIT");
                    }
                    else{
                        System.out.println("MISSED");
                    }
                    connection.sendTCP(response);
                }

            }

            public void disconnected (Connection c) {
                BConnection connection = (BConnection) c;
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

            private void sendOthersBoards(){
                for(int i = 0; i < playersThatStarted.length; i++){
                    EnemiesBoardsToPaint enemiesBoardsToPaint = new EnemiesBoardsToPaint();
                    int own = playersThatStarted[i].myID;
                    //System.out.println("OWN IS : " + own);
                    //System.out.println("PLAYER IS : " + playersThatStarted[i].name);
                    enemiesBoardsToPaint.board1 = game.getPlayerBoard((own + 1) % 3).getToSendToPaint();
                    enemiesBoardsToPaint.board2 = game.getPlayerBoard((own + 2) % 3).getToSendToPaint();
                    playersThatStarted[i].sendTCP(enemiesBoardsToPaint);
                }
            }
        });

        server.bind(Network.port);
        server.start();
        System.out.println("Server started");

    }

    private int findRightID(int mine, BConnection bConnection){
        for (Connection connection : server.getConnections()  ) {
            if(((BConnection) connection).myID == mine){
                return connection.getID();
            }
        }
        System.out.println("Could not find it");
        return -999;
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
            //System.out.println("PLAYER: " + playersThatStarted[i].name);
            //System.out.println("ID IS: " +  playersThatStarted[i].myID);
            i++;
        }
        game = new Game();
        server.sendToAllTCP(new StartTheGame());
        state = GameState.waitingForShips;
    }

    static class BConnection extends Connection {

        String name;
        int myID;
        String address;

        BConnection(){
            myID = -1;
        }
    }

    public static void main(String[] args) throws IOException {
        new GameServer();
    }


}
