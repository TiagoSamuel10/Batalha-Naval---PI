package Common;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

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
		//
        kryo.register(IsFull.class);
        kryo.register(StartTheGame.class);
        kryo.register(Abort.class);
        kryo.register(CanStart.class);
        kryo.register(WhoseTurn.class);
        kryo.register(String[].class);
        kryo.register(ConnectedPlayers.class);
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
