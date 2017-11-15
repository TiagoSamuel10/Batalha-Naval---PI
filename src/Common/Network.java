package Common;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import Common.*;

// This class is a convenient place to keep things common to both the client and server.
public class Network {

	public static final int port = 5555;

	// This registers objects that are going to be sent over the network.
	public static void register(EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		kryo.register(Register.class);
		kryo.register(ChatMessage.class);
		//
        kryo.register(int[][].class);
        kryo.register(int[].class);
        kryo.register(int.class);
        kryo.register(String.class);
        kryo.register(String[].class);
        kryo.register(String[][].class);
		//
        kryo.register(IsFull.class);
        kryo.register(StartTheGame.class);
        kryo.register(Abort.class);
        kryo.register(CanStart.class);
        kryo.register(WhoseTurn.class);
        kryo.register(ConnectedPlayers.class);
        kryo.register(YourBoardToPaint.class);
        kryo.register(EnemiesBoardsToPaint.class);
        kryo.register(AnAttackAttempt.class);
        kryo.register(AnAttackResponse.class);
	}

    public static class EnemiesBoardsToPaint{
        public String[][] board1;
        public String[][] board2;
    }

    public static class AnAttackAttempt{
	    public int clientID;
	    public int l;
	    public int c;
    }

    public static class AnAttackResponse{
	    public boolean hitAnything;
    }

	public static class YourBoardToPaint{
	    public String[][] board;
    }

    public static class WhoseTurn{
	    public int id;
    }

	public static class Abort{
    }

	public static class StartTheGame{
    }

	public static class Register {
		public String name;
		public String address;
	}

	public static class ChatMessage {
		public String text;
	}

	public static class ConnectedPlayers{
	    public String[] names;
    }

	public static class CanStart{
    }

	public static class IsFull{
        @Override
        public String toString() {
            return "Server is full";
        }
    }
}
