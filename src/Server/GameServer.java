package Server;

import Common.Network;
import Common.Network.*;
import Common.PlayerBoard;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import javax.swing.*;
import java.io.*;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class GameServer {

    private enum GameState{
        waitingForPlayers,
        waitingForShips,
        playing
    }

    private final static long TIME_TO_WAIT = 1000 * 60 ;
    private boolean timing;
    private long currentWaitedTime;
    private boolean disconnectWhileFull;
    private long started;

    private GameState state;
    private Game game;
    private Server server;
    private int count;
    private boolean gameStarted;

    private BConnection[] playersThatStarted;

    GameServer() throws IOException {

        state = GameState.waitingForPlayers;

        disconnectWhileFull = false;
        timing = false;
        currentWaitedTime = 0;
        playersThatStarted = new BConnection[3];

        /*

        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
                System.out.println("TimerTask executing counter is: " + currentWaitedTime);
                currentWaitedTime++;//increments the counter
            }
        };



        Timer timer = new Timer("MyTimer");//create a new Timer

        timer.scheduleAtFixedRate(timerTask, 0, 1000);//this line starts the timer at the same time its executed

        timing = true;

        started = System.currentTimeMillis();

        */

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

                System.out.println("Connected port" + connection.getRemoteAddressTCP().getPort());
                System.out.println("Connected address" + connection.getRemoteAddressTCP().getAddress());

                connection.name = r.name;

                System.out.println("Connected " + connection.name);

                switch (state){
                    case waitingForPlayers:
                        //normal
                        count++;
                        //ver se chegou a 3
                        if(count == 3){
                            startTheGame();
                        }
                    case waitingForShips:
                        // see if it's a new player
                        // or somebody that dropped
                    case playing:
                }



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
                    pb.nukeIt();
                    System.out.println(pb);
                    if(addToGame(connection, pb)){
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
                switch (state){
                    case waitingForShips:
                    case waitingForPlayers:
                }
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

    private void abortGame() {
        state = GameState.waitingForPlayers;
        server.sendToAllTCP(new StartTheGame());
    }

    private void startTheGame() {
        int i = 0;
        for(Connection connection : server.getConnections()){
            playersThatStarted[i] = (BConnection) connection;
            System.out.println(playersThatStarted[i].name);
            i++;
        }
        game = new Game();
        server.sendToAllTCP(new StartTheGame());
        state = GameState.waitingForShips;

    }

    static class BConnection extends Connection {
        String name;
        int id;
        InetAddress inetAddress;

        BConnection(){
            id = -1;
        }
    }

    public static void main(String[] args) throws IOException {
        new GameServer();
    }


}
