import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * O "tabuleiro" de cada jogador. Tem umas poucas de coisas.
 * <p>
 *     Uma lista de navios. Isto é para saber que navios pusemos cá
 * </p>
 * <p>
 *     Um Array 2D com BoardTile[][]. Isto é o tabuleiro mesmo em si. Como WaterTile e ShipPiece são derivados de(extends) de BoardTile, neste array podemos por, quer uma ou outra
 * </p>
 * <p>
 *     Um dicionario, basicamente, entre as peças de navio e o navio a que lhes pertençe
 * </p>
 * <p>
 *     Metodos para "disparar" contra um x e y
 * </p>
 * <p>
 *     Metodos para por, tirar, e ver se posso colocar um navio em (X,Y)
 * </p>
 *
 */

public class PlayerBoard extends JPanel {

    //GRAPHICAL

    private boolean gettingAttacked;
    private BoardTile lastHit;
    private boolean goAgain;
    private ArrayList<BoardTile> tiles;

    //LOGIC

    private final int _id;
    private BoardTile[][] boardTiles;
    static final int LINES = 10;
    static final int COLUMNS = 10;
    private ArrayList<Ship> ships;
    private HashMap<ShipPiece, Ship> shipsWithTiles;
    private boolean gameOver;


    // TODO add method to handle all ships

    PlayerBoard(int id) {
        //System.out.println("++1");
        gameOver = false;
        _id = id;
        boardTiles = new BoardTile[LINES][COLUMNS];
        fillWithWater();
        ships = new ArrayList<>();
        shipsWithTiles = new HashMap<>();
        tiles = new ArrayList<>();
    }

    private void fillWithWater() {
        for (int l = 0; l < LINES; l++) {
            for (int c = 0; c < COLUMNS; c++) {
                boardTiles[l][c] = new WaterTile(l, c);
            }
        }
    }

    //region attacked

    /*

    private void getAttacked(int x, int y) {
        goAgain = true;
        BoardTile tile = getTileAt(x, y);
        lastHit = tile;
        //NOT ATTACKED YET
        if (!tile.isVisible) {
            tile.setAttacked();
            // NOT A SHIP PIECE
            if (!tile.isPiece()) {
                System.out.println("NO");
                goAgain = false;
                return;
            }
            System.out.println("HIT A SHIP");
            Ship s = shipsWithTiles.get((ShipPiece) tile);
            if (s.isDestroyed()) {
                System.out.println("SHIP DESTROYED");
                shipDestroyed(s);
            }
        }
        repaint();
    }

    */

    void attack(BoardTile boardTile){
        //NOT ATTACKED YET
        if (!boardTile.isVisible) {
            boardTile.setAttacked();
            repaint();
            // NOT A SHIP PIECE
            if (!boardTile.isPiece()) {
                goAgain = false;
                return;
            }
            System.out.println("HIT A SHIP");
            Ship s = shipsWithTiles.get((ShipPiece) boardTile);
            if (s.isDestroyed()) {
                System.out.println("SHIP DESTROYED");
                shipDestroyed(s);
            }
        }
        repaint();
    }

    /*

    BoardTile attack(int x, int y) {
        getAttacked(x, y);
        if (goAgain) {
            checkGameOver();
        }
        return lastHit;
    }
    */

    //endregion

    private void checkGameOver() {
        for (Ship ship : ships) {
            if (!ship.isDestroyed()) {
                gameOver = false;
                return;
            }
        }
        gameOver = true;
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

    void removeShip(Ship toRemove) {
        for (ShipPiece piece : toRemove.getPieces()) {
            boardTiles[piece._x][piece._y] = new WaterTile(piece._x, piece._y);
            shipsWithTiles.remove(piece, toRemove);
        }
        ships.remove(toRemove);
    }

    void placeShip(Ship toAdd) {
        //System.out.println(ships.size());
        if(ships.size() >= Game.MAX_SHIPS){
            //System.err.println("TOO MANY SHIPS ALREADY");
            return;
        }
        //System.out.println(ships.size());
        ships.add(toAdd);
        for (ShipPiece piece : toAdd.getPieces()) {
            //System.out.println("PLACING " + piece + " PIECE AT: " + piece._x + " " + piece._y);
            boardTiles[piece._x][piece._y] = piece;
            shipsWithTiles.put(piece, toAdd);
            //System.out.println(shipsWithTiles.get(piece));
        }
        //System.out.println(ships.size());
        if(ships.size() == Game.MAX_SHIPS){
            addTiles();
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

    boolean isGameOver() {
        return gameOver;
    }

    public BoardTile getLastHit() {
        return lastHit;
    }

    boolean goAgain() {
        return goAgain;
    }

    public ArrayList<Ship> getShips() {
        return ships;
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
        setLocation(GUI.GAMEBOARD_LOCATION);
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (gettingAttacked) {
                    //System.out.println(1);
                    BoardTile boardTile = findTileAt(e.getPoint());
                    if(boardTile != null){
                        attack(boardTile);
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
