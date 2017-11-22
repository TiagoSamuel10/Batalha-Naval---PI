package Server;

import Common.Conversations;
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
        playing,
        playing2left
    }

    private final static long TIME_TO_WAIT = 1000 * 60 ;
    private long currentWaitedTime;

    private GameState state;
    private Conversations conversations;
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
                        if(count == 3){
                            sendReadyForShips();
                        }else{
                            sendConnections();
                        }
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
                    case playing2left:
                        break;
                }

                //System.out.println(connection.address);
                //System.out.println("Connected " + connection.name);
                //System.out.println("Connected myID becomes: " + connection.myID);


                System.out.println("Count : " + count);

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

                        sendOthersDetails();
                        sendOthersBoards();

                        state = GameState.playing;

                        WhoseTurn whoseTurn = new WhoseTurn();
                        whoseTurn.name = players[0].name;
                        server.sendToAllTCP(whoseTurn);

                        players[0].sendTCP(new YourTurn());
                        server.sendToAllTCP(new CanStart());
                    }
                }

                if (object instanceof  AnAttackAttempt){
                    processAttack(connection, (AnAttackAttempt) object);
                }

                if (object instanceof ChatMessageFromClient){
                    ChatMessageFromClient m = (ChatMessageFromClient) object;
                    newChatMessage(connection.myID, m.to, m.text);
                }
            }

            private void newChatMessage(int from, int to, String message){
                int c = conversations.getConversationIDWithIDs(from, to);
                conversations.appendToConversation(from, c, message);
                Conversations.Line line = conversations.getLastLineFromConversation(c);
                ChatMessage chats = new ChatMessage();
                chats.saidIt = from;
                chats.message = line.decode(players[from].name);
                System.out.println("SENDING FROM: " + chats.saidIt + " TO: " + to + " CONTENT: " + chats.message);
                players[to].sendTCP(chats);
            }

            private void processAttack(BConnection connection, AnAttackAttempt a){

                System.out.println(connection.name + " IS ATTACKING " +
                        players[a.toAttackID].name);

                boolean canGoAgain = game.attack(
                        a.toAttackID,
                        a.l,
                        a.c
                );

                String[][] attackedOne = game.getPlayerBoard(a.toAttackID).getToSendToPaint();

                AnAttackResponse response = new AnAttackResponse();
                response.again = canGoAgain;
                response.newAttackedBoard = attackedOne;


                // TO THE ATTACKED GUY

                YourBoardToPaint attacked = new YourBoardToPaint();
                attacked.board = attackedOne;


                switch (state){
                    case playing:
                        //TO THE GUY THAT ATTACKED
                        connection.sendTCP(response);

                        //TO THE GUY NOT ATTACKED

                        EnemyBoardToPaint eb = new EnemyBoardToPaint();
                        eb.newAttackedBoard = attackedOne;
                        eb.id = a.toAttackID;
                        players[a.otherID].sendTCP(eb);

                        //TO THE ATTACKED
                        players[a.toAttackID].sendTCP(attacked);

                        if(!canGoAgain){
                            currentPlayer = (currentPlayer + 1) % 3;
                            WhoseTurn whoseTurn = new WhoseTurn();
                            whoseTurn.name = players[currentPlayer].name;
                            sendToAllExcept(currentPlayer, whoseTurn);
                            players[currentPlayer].sendTCP(new YourTurn());
                        }
                        else{
                            System.out.println("HIT");
                            if(game.isGameOverFor(a.toAttackID)){
                                System.out.println("MAN DOWN!");
                                state = GameState.playing2left;
                                players[a.toAttackID].sendTCP(new YouDead());
                                PlayerDied playerDied = new PlayerDied();
                                playerDied.who = a.toAttackID;
                                sendToAllExcept(a.toAttackID, playerDied);
                            }
                        }


                        break;
                    case playing2left:

                        connection.sendTCP(response);

                        players[a.toAttackID].sendTCP(attacked);

                        if(!canGoAgain){
                            currentPlayer = a.toAttackID;
                            WhoseTurn whoseTurn = new WhoseTurn();
                            whoseTurn.name = players[currentPlayer].name;
                            sendToAllExcept(currentPlayer, whoseTurn);
                            players[currentPlayer].sendTCP(new YourTurn());
                        }
                        else{
                            System.out.println("HIT");
                            if(game.isGameOverFor(a.toAttackID)){
                                //GAME IS OVER
                                players[a.toAttackID].sendTCP(new YouDead());
                                players[currentPlayer].sendTCP(new YouWon());
                                state = GameState.waitingForPlayers;
                            }
                        }

                        break;
                }


            }

            private void sendOthersDetails() {
                for(int i = 0; i < players.length; i++){
                    OthersSpecs send = new OthersSpecs();
                    send.ene1 = (i + 1) % 3;
                    send.ene2 = (i + 2) % 3;

                    send.ene1n = players[(i + 1) % 3].name;
                    send.ene2n = players[(i + 2) % 3].name;

                    players[i].sendTCP(send);
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
                if(count < 0){
                    count = 0;
                }
                switch (state){
                    case waitingForPlayers:
                        sendConnections();
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

            private void sendConnections(){
                ConnectedPlayers connectedPlayers = new ConnectedPlayers();
                connectedPlayers.names = getConnectedNames();
                if(connectedPlayers.names != null) {
                    server.sendToAllTCP(connectedPlayers);
                }
            }

            private String[] getConnectedNames(){
                if(count > 0) {
                    String[] string = new String[count];
                    for (int i = 0; i < count; i++) {
                        string[i] = players[i].name;
                    }

                    return string;
                }
                return null;
            }

        });

        server.bind(Network.port);
        server.start();
        System.out.println("Server started");

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
        conversations = new Conversations();
        Conversations conversations = new Conversations();
        server.sendToAllTCP(new ReadyForShips());
        state = GameState.waitingForShips;
    }

    private void sendConversation(int requester){
        //TODO
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
