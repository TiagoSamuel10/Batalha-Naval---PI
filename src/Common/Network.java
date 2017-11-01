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
		kryo.register(PlayerBoard.class);
        kryo.register(BoardTile[][].class);
        kryo.register(BoardTile[].class);
        //kryo.register(ArrayList.class);
        kryo.register(int[][].class);
        kryo.register(int[].class);
        kryo.register(int.class);
        kryo.register(Ship.class);
        kryo.register(Ship[].class);
        kryo.register(ShipPiece[].class);
        kryo.register(Ship.ShipType.class);
        kryo.register(ShipPiece.class);
        kryo.register(Direction.class);
        kryo.register(WaterTile.class);
        kryo.register(BoardTile.class);
		//
        kryo.register(IsFull.class);
        kryo.register(StartTheGame.class);
        kryo.register(Abort.class);
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

	public static class IsFull{
        @Override
        public String toString() {
            return "Server is full";
        }
    }
}
