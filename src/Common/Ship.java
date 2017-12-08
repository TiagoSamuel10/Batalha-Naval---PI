package Common;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class Ship implements Serializable{

    private static final Random r = new Random();
    ShipType _shipType;
    int startL;
    int startC;
    Direction dir;

    boolean alreadyCalculated;
    ShipPiece[] pieces;

    Ship(int x, int y, Direction _dir, ShipType st){
        _shipType = st;
        setPoint(new Point(x, y));
        pieces = new ShipPiece[st.value];
        dir = _dir;
        alreadyCalculated = false;
    }

    //region SPECIAL
    Ship() {
    }
    //endregion


    public Direction getDirection() {
        return dir;
    }

    private static Ship getOneRandomShip(PlayerBoard pb, ShipType size, Direction[] directions){
        Ship tempShip = new Ship(0,0,Direction.DOWN, ShipType.One);
        boolean didIt = false;
        while(!didIt) {
            int x = r.nextInt(PlayerBoard.LINES);
            int y = r.nextInt(PlayerBoard.COLUMNS);
            tempShip = new Ship(x, y, directions[r.nextInt(directions.length)], size);
            if (pb.canShipBeHere(tempShip)) {
                didIt = true;
                pb.placeShip(tempShip);
            }
            String did = (didIt) ? "Placed" : "Could not place";
            /*System.out.printf("%s a ship at x-%d and y-%d. The " +
                    "ship has %d size and it's dir is %s \n", did, x, y, tempShip.getSize(), tempShip.dir);*/
        }
        return tempShip;
    }

    public static Ship[] getFreshShips(){
        return new Ship[]{
                new Ship(0,0,Direction.DOWN,ShipType.Four),
                new Ship(0,0,Direction.DOWN,ShipType.Three),
                new Ship(0,0,Direction.DOWN,ShipType.Three),
                new Ship(0,0,Direction.DOWN,ShipType.Two),
                new Ship(0,0,Direction.DOWN,ShipType.Two),
                new Ship(0,0,Direction.DOWN,ShipType.Two),
                new Ship(0,0,Direction.DOWN,ShipType.One),
                new Ship(0,0,Direction.DOWN,ShipType.One),
                new Ship(0,0,Direction.DOWN,ShipType.One),
                new Ship(0,0,Direction.DOWN,ShipType.One)
        };
    }

    static Ship[] getRandomShips(){
        ShipType[] types = new ShipType[]{
                ShipType.Four, ShipType.Three, ShipType.Three,
                ShipType.Two, ShipType.Two, ShipType.Two,
                ShipType.One,ShipType.One,ShipType.One,ShipType.One
        };
        Ship[] temp = new Ship[PlayerBoard.NUMBER_OF_BOATS];
        // 4
        // 3, 3
        // 2, 2, 2
        // 1, 1, 1, 1
        PlayerBoard tempBoard = new PlayerBoard();
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

    public void setPoint(Point point){
        startL = point.x;
        startC = point.y;
    }

    public void changeDirection(){
        dir = dir.getRotated();
    }

    ShipPiece[] getPieces(){
        if(alreadyCalculated) {
            return pieces;
        }
        computePieces();
        return pieces;
    }

    private void computePieces(){
        alreadyCalculated = true;
        int[] vector = new int[]{0,0};
        if(dir != null){
            vector = dir.getDirectionVector();
        }
        for (int i = 0; i < getSize(); i++){
            pieces[i] = new ShipPiece(
                    this,
                    i,
                    startL + vector[0] * i,
                    startC + vector[1] * i)
            ;
        }
    }

    public int getSize(){
        return _shipType.value;
    }

    boolean isDestroyed() {
        for (ShipPiece piece : pieces) {
            if (piece.canAttack()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        String s = "[";
        s += "Ship at " + startL + "+" + startC + " dir: " + dir +
                ", shipType: " + _shipType + "\n";
        for (ShipPiece sp : pieces) {
            s += "+ " + sp.details() + "\n";
        }
        return s;
    }

    enum ShipType {

        One,
        Two,
        Three,
        Four;

        static {
            One.value = 1;
            Two.value = 2;
            Three.value = 3;
            Four.value = 4;
        }

        private int value;

        static ShipType getShipType(int value){
            for(ShipType shipType : ShipType.values()){
                if(shipType.value == value){
                    return shipType;
                }
            }
            return null;
        }

    }



}

//SPECIAL CASE
class ConstructorShip extends Ship {

    private ArrayList<ShipPiece> temp;

    ConstructorShip(int _l, int _c){
        super();
        startL = _l;
        startC = _c;
        temp = new ArrayList<>();
    }

    void addPiece(ShipPiece sp){
        temp.add(sp);
    }

    Ship getShip(){
        _shipType = Ship.ShipType.getShipType(temp.size());
        //System.out.println(temp.size());
        pieces = new ShipPiece[temp.size()];
        Ship s = new Ship(startL, startC, dir, _shipType);
        int i = 0;
        for (ShipPiece shipPiece:temp) {
            shipPiece.ship = s;
            pieces[i] = shipPiece;
            i++;
        }
        s.alreadyCalculated = true;
        s.pieces = this.pieces;
        return s;
    }

    void setDirection(Direction _dir){
        dir = _dir;
    }
}
