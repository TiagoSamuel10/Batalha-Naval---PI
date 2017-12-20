package Common;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class PlayerBoard implements Serializable {

    public static final int LINES = 10;
    public static final int COLUMNS = 10;
    final static int NUMBER_OF_BOATS = 10;

    private String[][] toPaint;

    private ArrayList<Ship> ships;

    private ArrayList<ShipPiece> pieces;
    private BoardTile[][] boardTiles;
    private boolean lastShipDestroyed;
    private boolean actualHit;
    private boolean shipHit;






    public ArrayList<Ship> getShips(){
        return ships;
    }

    public PlayerBoard() {
        boardTiles = new BoardTile[LINES][COLUMNS];
        pieces = new ArrayList<>();
        toPaint = new String[LINES][COLUMNS];
        ships = new ArrayList<>();
        fillWithWater();
    }

    public PlayerBoard(String[][] sent){
        this();
        transformBack(sent);
    }




    void seeAllShips(){
        Ship old = null;
        for (ShipPiece piece: pieces) {
            if (!piece.ship.equals(old)) {
                System.out.println(piece.ship);
                old = piece.ship;
            }
        }
    }

    public String[][] getToPaint() {
        return toPaint;
    }

    //region TransformBack!

    //FROM String[][] go get the ship
    private Ship constructShip(ArrayList<Point> toSkip, String[][] sent, int l, int c){
        return initializeShipConstruction(toSkip, sent, l, c).getShip();
    }

    private ConstructorShip initializeShipConstruction(ArrayList<Point> toSkip, String[][] sent, int l, int c){

        ConstructorShip ship = new ConstructorShip(l, c);

        int[] vec = Direction.HORIZONTAL.getDirectionVector();
        //HORIZONTAL
        if(inBounds(l + vec[0], c + vec[1]) && aPieceInTheArray(sent, l + vec[0], c + vec[1])) {
            ship.setDirection(Direction.HORIZONTAL);
            return buildIt(toSkip, sent, l, c, ship, Direction.HORIZONTAL, 0);
        }
        //VERTICAL OR JUST 1 PIECE-SHIP
        ship.setDirection(Direction.VERTICAL);
        return buildIt(toSkip, sent, l, c, ship, Direction.VERTICAL, 0);
    }

    private ConstructorShip buildIt(ArrayList<Point> toSkip, String[][] sent, int l, int c, ConstructorShip ship, Direction _dir, int i) {

        ShipPiece sp  = new ShipPiece(null, i, l, c);
        if (sent[l][c].equalsIgnoreCase(ShipPiece.NOT_ATTACKED_STRING)) {
            sp = new ShipPiece(null, i, l, c);
        }
        else {
            sp.setAttacked(true);
        }
        toSkip.add(new Point(l, c));
        ship.addPiece(sp);

        int[] vec = _dir.getDirectionVector();
        Point newP = new Point(l + vec[0], c + vec[1]);

        if (inBounds(newP) && aPieceInTheArray(sent, newP.x, newP.y)) {
            i++;
            ship = buildIt(toSkip, sent, newP.x, newP.y, ship, _dir, i);
        }
        return ship;
    }

    private boolean inBounds(Point p){
        return inBounds(p.x, p.y);
    }

    public static boolean aPieceInTheArray(String[][] sent, int l, int c) {
        return sent[l][c].equalsIgnoreCase(ShipPiece.ATTACKED_STRING) ||
                sent[l][c].equalsIgnoreCase(ShipPiece.NOT_ATTACKED_STRING) ||
                sent[l][c].equalsIgnoreCase(ShipPiece.ATTACKED_SHIP_DESTROYED_STRING);
    }

    private void transformBack(String[][] sent){

        ArrayList<Point> toSkip = new ArrayList<>();

        for (int l = 0; l < LINES; l++)
            for (int c = 0; c < COLUMNS; c++){
                if (toSkip.contains(new Point(l, c))) {
                    //already found it
                    continue;
                }
                if(aPieceInTheArray(sent, l, c)){
                    Ship s = constructShip(toSkip, sent, l, c);
                    placeShip(s);
                }
                else{
                    //WATER
                    boardTiles[l][c] = new WaterTile(l, c);
                    if(sent[l][c].equalsIgnoreCase(WaterTile.VISIBLE_STRING)){
                        boardTiles[l][c].setAttacked(true);
                    }
                }
        }
    }

    //endregion

    public static PlayerBoard getRandomPlayerBoard(){
        PlayerBoard pb = new PlayerBoard();
        pb.placeShips(Ship.getRandomShips());
        //pb.allPieces();
        return pb;
    }

    private void fillWithWater() {
        for (int l = 0; l < LINES; l++) {
            for (int c = 0; c < COLUMNS; c++) {
                boardTiles[l][c] = new WaterTile(l, c);
                toPaint[l][c] = WaterTile.NOT_VISIBLE_STRING;
            }
        }
    }

    //region attacked

    public boolean getAttacked(int x, int y) {
        //NOT ATTACKED YET
        if(inBounds(x, y)) {
            BoardTile boardTile = getTileAt(x, y);
            actualHit = true;
            if (boardTile.canAttack()) {
                boardTile.setAttacked(true);
                // NOT A SHIP PIECE
                if (!boardTile.isPiece()) {
                    shipHit = false;
                    toPaint[x][y] = WaterTile.VISIBLE_STRING;
                    return false;
                }
                shipHit = true;
                toPaint[x][y] = ShipPiece.ATTACKED_STRING;
                pieces.remove((ShipPiece) boardTile);
                Ship ship = ((ShipPiece) boardTile).ship;
                lastShipDestroyed = false;
                if (ship.isDestroyed()) {
                    lastShipDestroyed = true;
                    //System.out.println("SHIP DESTROYED");
                    shipDestroyed(ship);
                }
                return true;
            }
            else
                actualHit = false;
        }
        else{
            return false;
        }
        return true;
    }

    //endregion

    public ArrayList<Point> getAvailable(){
        ArrayList<Point> points = new ArrayList<>();
        for (int l = 0; l < LINES; l++) {
            for (int c = 0; c < COLUMNS; c++) {
                if (boardTiles[l][c].canAttack())
                    points.add(new Point(l, c));
            }
        }
        return points;
    }

    public boolean isGameOver(){
        return pieces.isEmpty();
    }

    private void shipDestroyed(Ship s) {
        for (ShipPiece piece : s.getPieces()) {
            Point[] points = getSurroundingPoints(piece.x, piece.y);
            for (Point point : points) {
                if (inBounds(point.x, point.y)) {
                    getTileAt(point.x, point.y).setAttacked(true);
                    toPaint[point.x][point.y] = WaterTile.VISIBLE_STRING;
                }
            }
        }
        for (ShipPiece piece : s.getPieces())
            toPaint[piece.x][piece.y] = ShipPiece.ATTACKED_SHIP_DESTROYED_STRING;
    }

    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < LINES; i++) {
            for (int c = 0; c < COLUMNS; c++) {
                s += boardTiles[i][c]+ "\n";
            }
            s += "\n";
        }
        return s;
    }

    void placeShips(Ship[] toAdd){
        //int i = 0;
        for (Ship ship : toAdd) {
            //ships[i] = ship;
            //i++;
            placeShip(ship);
        }
    }

    public static void printDoubleArray(String[][] array){
        String s = "";
        for (String [] arry : array) {
            s += "\n";
            for (String e : arry)
                s += "," + e;
        }
        System.out.println(s);
    }

    public boolean placeShip(Ship toAdd) {
        if(canShipBeHere(toAdd)) {
            ships.add(toAdd);
            for (ShipPiece piece : toAdd.getPieces()) {
                //System.out.println("PLACING " + piece.getClass().getSimpleName() + " AT: " + piece.x + " " + piece.y);
                boardTiles[piece.x][piece.y] = piece;
                pieces.add(piece);
                toPaint[piece.x][piece.y] = piece.toSendString();
            }
            return true;
        }
        return false;
    }

    //region CanPlaceShip

    private Point[] getSurroundingPoints(int x, int y) {
        Point[] points = new Point[8];
        points[0] = new Point(x + 1, y);
        points[1] = new Point(x + 1, y + 1);
        points[2] = new Point(x + 1, y - 1);
        points[3] = new Point(x - 1, y);
        points[4] = new Point(x - 1, y + 1);
        points[5] = new Point(x - 1, y - 1);
        points[6] = new Point(x, y + 1);
        points[7] = new Point(x, y - 1);
        return points;
    }

    public boolean canShipBeHere(Ship toAdd) {
        for (ShipPiece piece : toAdd.getPieces()) {
            //System.out.println(piece.getPointCoordinates());
            boolean isInBounds = inBounds(piece.x, piece.y);
            if (!isInBounds) {
                //System.out.println("NO BOUNDS");
                return false;
            }
            boolean isNotAdjacent = checkSurroundings(piece.x, piece.y);
            if (!isNotAdjacent || !freeAt(piece.x, piece.y)) {
                //System.out.println("ADJACENT");
                return false;
            }
        }
        return true;
    }

    private boolean checkSurroundings(int x, int y) {
        Point[] points = getSurroundingPoints(x, y);
        for (Point point : points) {
            if (inBounds(point.x, point.y)) {
                if (!freeAt(point.x, point.y)) {
                    return false;
                }
            }
        }
        return true;
    }

    boolean freeAt(int x, int y) {
        if(inBounds(x, y)){
            return !getTileAt(x, y).isPiece();
        }
        return true;
    }

    //endregion

    public BoardTile getTileAt(int x, int y) {
        if(inBounds(x, y)){
            return boardTiles[x][y];
        }
        return null;
    }

    public static boolean inBounds(int x, int y) {
        return (x < LINES && x >= 0) && (y < COLUMNS && y >= 0);
    }

    public void removeShip(Ship ship) {
        ships.remove(ship);
        for (ShipPiece piece : ship.getPieces()) {
            //System.out.println("REMOVING " + piece.getClass().getSimpleName() + " AT: " + piece.x + " " + piece.y);
            boardTiles[piece.x][piece.y] = new WaterTile(piece.x, piece.y);
            pieces.remove(piece);
        }
    }

    public boolean lastShipDestroyed(){
        return lastShipDestroyed;
    }

    public boolean actualNewHit(){
        return actualHit;
    }

    public boolean fullOfShips(){
        System.out.println(pieces.size());
        return pieces.size() == 20;
    }

    public boolean isShipHit() {
        return shipHit;
    }
}
