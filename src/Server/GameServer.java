package Server;

import Common.Network;
import Common.Network.*;
import Common.NetworkChat;
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

    private Game game;
    private Server server;
    private BConnection[] connections;
    private int count;

    public GameServer() throws IOException {

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
                System.out.println(count);
                if(count == 3) {
                    connection.sendTCP(new IsFull());
                    connection.close();
                    System.out.println(Arrays.toString(server.getConnections()));
                    return;
                }
                count++;
            }

            public void received (Connection c, Object object) {

                BConnection connection = (BConnection)c;

                if(object instanceof Register){
                    Register r = (Register) object;
                    String name = r.name;
                    if (name == null) {
                        return;
                    }
                    System.out.println("Name is " + name);
                    connection.name = r.name;
                }

            }

            public void disconnected (Connection c) {
                BConnection connection = (BConnection)c;
                System.out.println("LEFT: " + connection.name);
                count--;
                if (connection.name != null) {
                    // Announce to everyone that someone (with a registered name) has left.
                }
            }
        });

        server.bind(Network.port);
        server.start();

        System.out.println("Server started");

    }

    static class BConnection extends Connection {
        String name;
        int id;
    }

    public static void main(String[] args) throws IOException {
        new GameServer();
    }


}
