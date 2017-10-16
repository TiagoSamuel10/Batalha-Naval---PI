import java.util.Arrays;
import java.util.Random;

/**
 * Um navio. A classe contêm umas poucas de coisas.
 * <p>Um X e Y iniciais(a "cabeça" do navio)
 * </p>
 * <p>
 *     O tipo(a dimensão(1,2,3,4)
 * </p>
 * <p>
 *     A direção em que está
 * </p>
 * <p>
 *     Um booleano a dizer se já foi destroído ou não
 * </p>
 * <p>
 *     Um Array de ShipPiece[]. Este array tem as peças que vamos por no campo.
 * </p>
 */

public class Ship implements Comparable<Ship> {

    private int startX;
    private int startY;
    private final ShipType _shipType;
    private final Direction _dir;
    private ShipPiece[] pieces;
    private static final Random r = new Random();

    enum ShipType {
        One,
        Two,
        Three,
        Four
    }

    Ship(int x, int y, Direction dir, ShipType st){
        startX = x;
        startY = y;
        _shipType = st;
        _dir = dir;
        pieces = new ShipPiece[st.ordinal() + 1];
    }

    ShipPiece[] getPieces(){
        if(_shipType == ShipType.One){
            pieces[0] = new ShipPiece(startX, startY);
            return pieces;
        }
        int[] vector = _dir.getDirectionVector();
        for (int i = 0; i < _shipType.ordinal() + 1; i++){
            pieces[i] = new ShipPiece(
                    startX + vector[0] * i,
                    startY + vector[1] * i)
            ;
        }
        return pieces;
    }

    public int getSize(){
        return _shipType.ordinal() + 1;
    }

    public boolean isAPieceAt(int x, int y){
        for (ShipPiece piece : pieces) {
            if (piece._x == x && piece._y == y) {
                return true;
            }
        }
        return false;
    }

    public boolean isAPiece(ShipPiece aPiece){
        for (ShipPiece piece : pieces) {
            if (aPiece == piece) {
                return true;
            }
        }
        return false;
    }

    private static Ship getOneRandomShip(PlayerBoard pb, ShipType size, Direction[] directions){
        Ship tempShip = new Ship(0,0,Direction.RIGHT, ShipType.One);
        boolean done = false;
        while(!done) {
            boolean didIt = false;
            int x = r.nextInt(PlayerBoard.LINES);
            int y = r.nextInt(PlayerBoard.COLUMNS);
            tempShip = new Ship(x, y, directions[r.nextInt(directions.length)], size);
            if (pb.canShipBeHere(tempShip)) {
                didIt = true;
                pb.placeShip(tempShip);
                done = true;
            }
            String did = (didIt) ? "Placed" : "Could not place";
            /*System.out.printf("%s a ship at x-%d and y-%d. The " +
                    "ship has %d size and it's dir is %s \n", did, x, y, tempShip.getSize(), tempShip._dir);*/
        }
        return tempShip;
    }

    static Ship[] getRandomShips(){
        ShipType[] types = new ShipType[]{
                ShipType.Four, ShipType.Three, ShipType.Three,
                ShipType.Two, ShipType.Two, ShipType.Two,
                ShipType.One,ShipType.One,ShipType.One,ShipType.One
        };
        Ship[] temp = new Ship[10];
        // 4
        // 3, 3
        // 2, 2, 2
        // 1, 1, 1, 1
        PlayerBoard tempBoard = new PlayerBoard(1);
        Direction[] directions = Direction.values();
        int i = 0;
        while(true){
            temp[i] = getOneRandomShip(tempBoard, types[i], directions);
            i++;
            if(i == types.length){
                //System.out.println("I is: " +  i);
                //tempBoard.lightItUp();
                //System.out.println(tempBoard);
                //System.out.println(Arrays.toString(temp));
                break;
            }
        }

        return temp;
    }

    boolean isDestroyed() {
        for (ShipPiece piece : pieces) {
            if (!piece.isVisible) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int compareTo(Ship other) {
        if(other.startX == startX && other.startY == startY){
            return 1;
        }
        return 0;
    }
}
