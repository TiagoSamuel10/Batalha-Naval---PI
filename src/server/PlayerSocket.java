package server;

import java.net.Socket;

public class PlayerSocket {

    private Server _server;
    private Socket _socket;

    public PlayerSocket(Server server, Socket socket){
        _server = server;
        _socket = socket;
    }



}
