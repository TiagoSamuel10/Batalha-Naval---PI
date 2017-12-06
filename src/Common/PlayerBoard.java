package Common;

import java.awt.*;
import java.io.Serializable;
import java.util.ArrayList;

public class PlayerBoard implements Serializable {

    public static final int LINES = 10;
    public static final int COLUMNS = 10;
    final static int NUMBER_OF_BOATS = 10;
    private boolean gameOver;

    private String[][] toPaint;

    private ArrayList<ShipPiece> pieces;
    private BoardTile[][] boardTiles;
    private int[][] toSend;
    private int shipN = 0;
    private boolean lastShipDestroyed;

    public PlayerBoard() {
        toSend = new int[LINES][COLUMNS];
        gameOver = false;
        boardTiles = new BoardTile[LINES][COLUMNS];
        pieces = new ArrayList<>();
        toPaint = new String[LINES][COLUMNS];
        fillWithWater();
    }

    public PlayerBoard(int[][] sent){
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

    public String[][] getToSendToPaint(){
        //System.out.println("--------------");
        String[][] board = new String[LINES][COLUMNS];
        BoardTile bt;
        for (int l = 0; l < LINES; l++) {
            for (int c = 0; c < COLUMNS; c++) {
                bt = getTileAt(l, c);
                if(bt.isPiece()){
                    ShipPiece sp = (ShipPiece) bt;
                    //System.out.println(sp.details());
                    //System.out.println(sp.ship.isDestroyed());
                    if(sp.isAttacked() && sp.ship.isDestroyed()){
                        board[l][c] = ShipPiece.ATTACKED_SHIP_DESTROYED_STRING;
                        //System.out.println("DESTROYED HERE");
                    }
                    if(sp.isAttacked() && !sp.ship.isDestroyed()){
                        board[l][c] = ShipPiece.ATTACKED_STRING;
                        //System.out.println("NOT DESTROYED");
                    }
                    if(!sp.isAttacked()){
                        board[l][c] = ShipPiece.NOT_ATTACKED_STRING;
                        //System.out.println("NOT VIS");
                    }
                }
                else{
                    if(bt.isAttacked()){
                        board[l][c] = WaterTile.ATTACKED_OR_VISIBLE_STRING;
                    }
                    else{
                        board[l][c] = WaterTile.NOT_VISIBLE_STRING;
                    }
                }
                //System.out.println(board[l][c]);
            }
        }
        //System.out.println(Arrays.deepToString(board));
        //seeAllShips();
        return board;

    }

    public int[][] getToSend(){
        return toSend;
    }

    private void transformBack(int[][] sent){
        ArrayList<Point> toSkip = new ArrayList<>(LINES * COLUMNS);

        /*

        for (int l = 0; l < LINES; l++) {
            System.out.println(Arrays.toString(sent[l]));
        }

        */


        int count = 0;

        for (int l = 0; l < LINES; l++) {
            for (int c = 0; c < COLUMNS; c++) {
                if (toSkip.contains(new Point(l, c))){
                    //already found it
                    continue;
                }
                //IT'S A PIECE
                if(aPieceInTheArray(sent, l, c)){
                    //look in directions
                    int x = l;
                    int y = c + 1;
                    int size = 1;
                    boolean foundHorizontal = false;

                    //HORIZONTAL

                    while(inBounds(x, y)){
                        if(aPieceInTheArray(sent, x, y)){
                            size++;
                            foundHorizontal = true;
                            toSkip.add(new Point(x, y));
                            y++;
                        }
                        else{
                            break;
                        }
                    }

                    //VERTICAL

                    x = l + 1;
                    y = c;

                    if(!foundHorizontal){
                        while(inBounds(x, y)){
                            if(aPieceInTheArray(sent, x, y)){
                                size++;
                                toSkip.add(new Point(x, y));
                                x++;
                            }
                            else{
                                break;
                            }
                        }
                    }
                    Ship.ShipType st = Ship.ShipType.getShipType(size);
                    Direction d = Direction.DOWN;
                    if(foundHorizontal){
                        d = Direction.RIGHT;
                    }
                    Ship tempShip = new Ship(l, c, d, st);
                    tempShip.getPieces();
                    //System.out.println(tempShip);
                    placeShip(tempShip);
                    count += size;
                }

            }
        }
    }

    public static PlayerBoard getRandomPlayerBoard(){
        PlayerBoard pb = new PlayerBoard();
        pb.placeShips(Ship.getRandomShips());
        //pb.allPieces();
        return pb;
    }

    private boolean aPieceInTheArray(int[][] check, int x, int y){
        return check[x][y] > 0;
    }

    private void fillWithWater() {
        for (int l = 0; l < LINES; l++) {
            for (int c = 0; c < COLUMNS; c++) {
                boardTiles[l][c] = new WaterTile(l, c);
                toSend[l][c] = 0;
                toPaint[l][c] = WaterTile.NOT_VISIBLE_STRING;
            }
        }
    }

    //region attacked

    public boolean getAttacked(int x, int y) {
        //NOT ATTACKED YET
        if(inBounds(x, y)) {
            BoardTile boardTile = getTileAt(x, y);
            if (boardTile.canAttack()) {
                boardTile.setAttacked();
                // NOT A SHIP PIECE
                if (!boardTile.isPiece()) {
                    toPaint[x][y] = WaterTile.ATTACKED_OR_VISIBLE_STRING;
                    return false;
                }
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
        }
        else{
            return false;
        }
        return true;
    }

    public ArrayList<Point> getAvailable(){
        ArrayList<Point> points = new ArrayList<>();
        for (int l = 0; l < LINES; l++) {
            for (int c = 0; c < COLUMNS; c++) {
                if (!boardTiles[l][c].visible)
                    points.add(new Point(l, c));
            }
        }
        return points;
    }

    //endregion

    public boolean isGameOver(){
        return pieces.isEmpty();
    }

    private void shipDestroyed(Ship s) {
        for (ShipPiece piece : s.getPieces()) {
            Point[] points = getSurroundingPoints(piece.x, piece.y);
            for (Point point : points) {
                if (inBounds(point.x, point.y)) {
                    getTileAt(point.x, point.y).setAttacked();
                    toPaint[point.x][point.y] = WaterTile.ATTACKED_OR_VISIBLE_STRING;
                }
            }
        }
    }

    public void nukeIt(){
        for (int l = 0; l < LINES; l++) {
            for (int c = 0; c < COLUMNS; c++) {
                boardTiles[l][c].setAttacked();
            }
        }
    }

    void lightItUp() {
        for (int l = 0; l < LINES; l++) {
            for (int c = 0; c < COLUMNS; c++) {
                boardTiles[l][c].visible = true;
            }
        }
    }

    String details(){
        String s = "    ";
        for (int i = 0; i < COLUMNS; i++) {
            s += i + "    ";
        }
        s += "\n";
        for (int i = 0; i < LINES; i++) {
            s += i;
            for (int c = 0; c < COLUMNS; c++) {
                s += "    " + boardTiles[i][c].details();
            }
            s += "\n";
        }
        return s;
    }

    @Override
    public String toString() {
        String s = "    ";
        for (int i = 0; i < COLUMNS; i++) {
            s += i + 1 + "    ";
        }
        s += "\n";
        for (int i = 0; i < LINES; i++) {
            s += (char) (i + 65);
            for (int c = 0; c < COLUMNS; c++) {
                s += "    " + boardTiles[i][c].toString();
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

    public boolean placeShip(Ship toAdd) {
        if(canShipBeHere(toAdd)) {
            shipN++;
            for (ShipPiece piece : toAdd.getPieces()) {
                //System.out.println("PLACING " + piece.getClass().getSimpleName() + " AT: " + piece.x + " " + piece.y);
                boardTiles[piece.x][piece.y] = piece;
                pieces.add(piece);
                toSend[piece.x][piece.y] = shipN;
                toPaint[piece.x][piece.y] = ShipPiece.NOT_ATTACKED_STRING;
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
            //System.out.println(piece.toString());
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

    BoardTile getTileAt(int x, int y) {
        if(inBounds(x, y)){
            return boardTiles[x][y];
        }
        return null;
    }

    public static boolean inBounds(int x, int y) {
        return (x < LINES && x >= 0) && (y < COLUMNS && y >= 0);
    }

    public void removeShip(Ship ship) {

        for (ShipPiece piece : ship.getPieces()) {
            //System.out.println("REMOVING " + piece.getClass().getSimpleName() + " AT: " + piece.x + " " + piece.y);
            boardTiles[piece.x][piece.y] = new WaterTile(piece.x, piece.y);
            pieces.remove(piece);
            toSend[piece.x][piece.y] = 0;
        }

    }

    public boolean lastShipDestroyed(){
        return lastShipDestroyed;
    }

    public boolean fullOfShips(){
        System.out.println(pieces.size());
        return pieces.size() == 20;
    }
}
