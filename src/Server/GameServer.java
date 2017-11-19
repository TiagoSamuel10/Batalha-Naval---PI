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

    private final static long TIME_TO_WAIT = 1000 * 60 ;
    private boolean timing;
    private long currentWaitedTime;


    private long started;

    private GameState state;
    private Game game;
    private Server server;
    private int currentPlayer;

    //WILL SAVE WHAT CONNECTIONS THE GAME STARTED WITH
    //SO IT'S POSSIBLE TO KNOW IF SOMEBODY WHO DROPPED IS RECONNECTING
    private BConnection[] players;
    private boolean disconnectedWhenWaitingForShips;
    private int idOfReturned;
    private int count;
    private int nextID;

    public GameServer() throws IOException {

        state = GameState.waitingForPlayers;

        disconnectedWhenWaitingForShips = false;
        timing = false;
        currentWaitedTime = 0;
        players = new BConnection[3];

        server = new Server() {
            protected Connection newConnection () {
                return new BConnection();
            }
        };

        Network.register(server);

        server.addListener(new Listener() {

            private boolean isANewPlayer(String address){
                for (BConnection c: players) {
                    if(c.address.equalsIgnoreCase(address)){
                        idOfReturned = c.myID;
                        return true;
                    }
                }
                return false;
            }

            private void handleRegister(BConnection connection, Register r){

                connection.name = r.name;
                connection.address = r.address;
                connection.myID = nextID;

                switch (state){
                    case waitingForPlayers:
                        players[nextID] = connection;
                        count++;
                        nextID++;
                        break;
                    case waitingForShips:
                        // see if it's a new player
                        // or somebody that dropped
                        if(disconnectedWhenWaitingForShips){
                            if(!isANewPlayer(r.address)) {
                                connection.sendTCP(new IsFull());
                            }
                            else{
                                players[idOfReturned] = connection;
                                count++;
                            }
                        }
                        else{
                            connection.sendTCP(new IsFull());
                        }
                        break;
                    case playing:
                        break;
                }

                //System.out.println(connection.address);
                //System.out.println("Connected " + connection.name);
                //System.out.println("Connected myID becomes: " + connection.myID);

                ConnectedPlayers connectedPlayers = new ConnectedPlayers();
                connectedPlayers.names = getConnectedNames();
                server.sendToAllTCP(connectedPlayers);

                System.out.println("Count : " + count);

                if(count == 3){
                    sendReadyForShips();
                }

                printConnections();

            }

            public void received (Connection c, Object object) {

                BConnection connection = (BConnection)c;

                if(object instanceof Register){
                    handleRegister(connection, (Register) object);
                }
                if(object instanceof int[][]){
                    PlayerBoard pb = new PlayerBoard((int[][]) object);
                    //ADD TO GAME
                    //System.out.println("ADDING TO: " + connection.myID +
                    //        " WHICH IS " + connection.name);
                    game.setPlayerBoard(pb, connection.myID);
                    //IF WE'VE RECEIVED ALL, WE CAN START
                    if(game.canStart()){

                        sendOthersIDs();
                        sendOthersBoards();

                        WhoseTurn whoseTurn = new WhoseTurn();
                        whoseTurn.name = players[0].name;
                        server.sendToAllTCP(whoseTurn);

                        players[0].sendTCP(new YourTurn());
                        server.sendToAllTCP(new CanStart());
                    }
                }

                if (object instanceof  AnAttackAttempt){
                    AnAttackAttempt attempt = (AnAttackAttempt) object;

                    System.out.println(connection.name + " IS ATTACKING " +
                            players[attempt.toAttackID].name);

                    boolean canGoAgain = game.attack(
                            attempt.toAttackID,
                            attempt.l,
                            attempt.c);

                    String[][] attackedOne = game.getPlayerBoard(attempt.toAttackID).getToSendToPaint();

                    AnAttackResponse response = new AnAttackResponse();
                    response.again = canGoAgain;
                    response.newAttackedBoard = attackedOne;
                    connection.sendTCP(response);

                    //TO THE GUY NOT ATTACKED

                    EnemyBoardToPaint eb = new EnemyBoardToPaint();
                    eb.newAttackedBoard = attackedOne;
                    eb.id = attempt.otherID;

                    players[attempt.otherID].sendTCP(eb);

                    //TO THE ATTACKED
                    YourBoardToPaint attacked = new YourBoardToPaint();
                    attacked.board = attackedOne;
                    players[attempt.toAttackID].sendTCP(attacked);

                    if(!canGoAgain){
                        currentPlayer = (currentPlayer + 1) % 3;
                        WhoseTurn whoseTurn = new WhoseTurn();
                        whoseTurn.name = players[currentPlayer].name;
                        sendToAllExcept(currentPlayer, whoseTurn);
                        players[currentPlayer].sendTCP(new YourTurn());
                    }
                    else{
                        System.out.println("HIT");
                        if(game.isGameOverFor(attempt.toAttackID)){
                            System.out.println("MAN DOWN!");
                            if(game.gameIsOver()){
                                System.out.println("YOU WON BOY!");
                            }
                        }
                    }
                }
            }

            private void sendOthersIDs() {
                for(int i = 0; i < players.length; i++){
                    GameIDs ids = new GameIDs();
                    ids.ene1 = (i + 1) % 3;
                    ids.ene2 = (i + 2) % 3;
                    players[i].sendTCP(ids);
                }
            }

            private void sendToAllExcept(int i, Object object){
                players[(i + 4) % 3].sendTCP(object);
                players[(i + 5) % 3].sendTCP(object);
            }

            public void disconnected (Connection c) {
                BConnection connection = (BConnection) c;
                System.out.println("Disconnected " + connection.name);
                count--;
                switch (state){
                    case waitingForPlayers:
                        ConnectedPlayers connectedPlayers = new ConnectedPlayers();
                        connectedPlayers.names = getConnectedNames();
                        server.sendToAllTCP(connectedPlayers);
                        nextID = connection.myID;
                        break;
                    case waitingForShips:
                        handleLeavingWhileShips();
                        break;
                }
                System.out.println("Count : " + count);
                printConnections();
            }

            private void sendOthersBoards(){
                for(int i = 0; i < players.length; i++){
                    EnemiesBoardsToPaint enemiesBoardsToPaint = new EnemiesBoardsToPaint();
                    //int own = players[i].myID;
                    enemiesBoardsToPaint.board1 = game.getPlayerBoard((i + 1) % 3).getToSendToPaint();
                    enemiesBoardsToPaint.board2 = game.getPlayerBoard((i + 2) % 3).getToSendToPaint();
                    players[i].sendTCP(enemiesBoardsToPaint);
                }
            }

            private void printConnections(){
                for (BConnection connection : players) {
                    if(connection != null) {
                        System.out.println(connection.name + " has ID:" + connection.myID +
                                " and address:" + connection.address);
                    }
                }
            }


        });

        server.bind(Network.port);
        server.start();
        System.out.println("Server started");

    }

    private String[] getConnectedNames(){
        String[] string = new String[count];
        for (int i = 0; i < count; i++) {
            string[i] = players[i].name;
        }
        return string;
    }

    private void handleLeavingWhileShips(){
        disconnectedWhenWaitingForShips = true;
    }

    private void abortGame() {
        state = GameState.waitingForPlayers;
        server.sendToAllTCP(new ReadyForShips());
    }

    private void sendReadyForShips() {
        game = new Game();
        server.sendToAllTCP(new ReadyForShips());
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
