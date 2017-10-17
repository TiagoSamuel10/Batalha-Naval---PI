import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class PlayerBoard extends JPanel implements Serializable {

    //GRAPHICAL

    private boolean gettingAttacked;
    private BoardTile lastHit;
    private boolean goAgain;
    private ArrayList<BoardTile> tiles;

    //LOGIC

    private int index = 0;

    final static int NUMBER_OF_BOATS = 10;
    private final int _id;
    private BoardTile[][] boardTiles;
    static final int LINES = 10;
    static final int COLUMNS = 10;
    private Ship[] ships;
    private HashMap<ShipPiece, Ship> shipsWithTiles;
    private boolean gameOver;
    private ArrayList<ShipPiece> pieces;


    // TODO add method to handle all ships

    PlayerBoard(int id) {
        gameOver = false;
        _id = id;
        boardTiles = new BoardTile[LINES][COLUMNS];
        fillWithWater();
        ships = new Ship[NUMBER_OF_BOATS];
        tiles = new ArrayList<>();
        pieces = new ArrayList<>();
    }

    private void fillWithWater() {
        for (int l = 0; l < LINES; l++) {
            for (int c = 0; c < COLUMNS; c++) {
                boardTiles[l][c] = new WaterTile(l, c);
            }
        }
    }

    //region attacked

    void getAttacked(int x, int y) {
        //NOT ATTACKED YET
        BoardTile boardTile = getTileAt(x, y);
        if (!boardTile.isVisible) {
            boardTile.setAttacked();
            repaint();
            // NOT A SHIP PIECE
            if (!boardTile.isPiece()) {
                goAgain = false;
                return;
            }
            pieces.remove((ShipPiece) boardTile);
            Ship ship = ((ShipPiece) boardTile)._ship;
            if (ship.isDestroyed()) {
                System.out.println("SHIP DESTROYED");
                shipDestroyed(ship);
            }
        }
        repaint();
    }

    void getAttacked(BoardTile boardTile){
        getAttacked(boardTile._x, boardTile._y);
    }

    /*

    BoardTile getAttacked(int x, int y) {
        getAttacked(x, y);
        if (goAgain) {
            checkGameOver();
        }
        return lastHit;
    }
    */

    //endregion

    boolean isGameOver(){
        return gameOver;
    }

    private void checkGameOver() {
        gameOver = pieces.isEmpty();
    }

    private void shipDestroyed(Ship s) {
        for (ShipPiece piece : s.getPieces()) {
            Point[] points = getSurroundingPoints(piece._x, piece._y);
            for (Point point : points) {
                if (inBounds(point.x, point.y)) {
                    getTileAt(point.x, point.y).setAttacked();
                }
            }
        }
    }

    void lightItUp() {
        for (int l = 0; l < LINES; l++) {
            for (int c = 0; c < COLUMNS; c++) {
                boardTiles[l][c].setAttacked();
                repaint();
            }
        }
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
        for (Ship ship : toAdd) {
            placeShip(ship);
        }
        addTiles();
    }

    void placeShip(Ship toAdd) {
        for (ShipPiece piece : toAdd.getPieces()) {
            //System.out.println("PLACING " + piece + " PIECE AT: " + piece._x + " " + piece._y);
            boardTiles[piece._x][piece._y] = piece;
            //shipsWithTiles.put(piece, toAdd);
            //System.out.println(shipsWithTiles.get(piece));
        }
    }

    //region CanPlaceShip

    boolean canShipBeHere(Ship toAdd) {
        for (ShipPiece piece : toAdd.getPieces()) {
            //System.out.println(piece.toString());
            boolean isInBounds = inBounds(piece._x, piece._y);
            if (!isInBounds) {
                //System.out.println("NO BOUNDS");
                return false;
            }
            boolean isNotAdjacentOrOnTop = checkSurroundings(piece._x, piece._y);
            if (!isNotAdjacentOrOnTop) {
                //System.out.println("ADJACENT");
                return false;
            }
        }
        return true;
    }

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

    private boolean checkSurroundings(int x, int y) {
        Point[] points = getSurroundingPoints(x, y);
        for (Point point : points) {
            if (inBounds(point.x, point.y)) {
                if (!isAPieceAt(point.x, point.y)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isAPieceAt(int x, int y) {
        return !getTileAt(x, y).isPiece();
    }

    //endregion

    BoardTile getTileAt(int x, int y) {
        return boardTiles[x][y];
    }

    @Contract(pure = true)
    private boolean inBounds(int x, int y) {
        return (x < LINES && x >= 0) && (y < COLUMNS && y >= 0);
    }

    public BoardTile getLastHit() {
        return lastHit;
    }

    boolean goAgain() {
        return goAgain;
    }

    //region GRAPHICAL PART

    private void addTiles(){
        setLayout(null);
        gettingAttacked = false;
        for (int x = 0; x < PlayerBoard.LINES; x++) {
            for (int y = 0; y < PlayerBoard.COLUMNS; y++) {
                BoardTile bt = getTileAt(x, y);
                bt.setLocation(bt._x * (BoardTile.SIZE + 5), bt._y * (BoardTile.SIZE + 5));
                bt.setSize(BoardTile.SIZE, BoardTile.SIZE);
                //bt.setBackground(new Color(23 * bt._x,22 * bt._x,21 * bt._y));
                //System.out.println(bt.isPiece());
                tiles.add(bt);
                add(bt);
            }
        }
        setSize(LINES * (BoardTile.SIZE + 5), COLUMNS * (BoardTile.SIZE + 5));
        setLocation(Client.GAMEBOARD_LOCATION);
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (gettingAttacked) {
                    //System.out.println(1);
                    BoardTile boardTile = findTileAt(e.getPoint());
                    if(boardTile != null){
                        getAttacked(boardTile);
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

    }

    //endregion

    @Nullable
    private BoardTile findTileAt(Point point){
        for (BoardTile tile : tiles) {
            //System.out.println(graphTile.getLocation());
            if (tile.insidePoint(point)) {
                lastHit = tile;
                //tile.setAttacked();
                return lastHit;
            }
        }
        return null;
    }

    @Nullable
    private BoardTile findTileAt(int x, int y) {
        Point p = new Point(x, y);
        return findTileAt(p);
    }

    void setGettingAttacked(boolean gettingAttacked) {
        this.gettingAttacked = gettingAttacked;
    }
}
