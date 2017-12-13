package JavaFX;

import Common.Direction;
import Common.PlayerBoard;
import Common.ShipPiece;
import Common.WaterTile;
import javafx.animation.AnimationTimer;
import javafx.animation.Timeline;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.util.Arrays;

import static Common.PlayerBoard.COLUMNS;
import static Common.PlayerBoard.LINES;

public class GraphBoardFX extends EmptyGraphBoardFX {

    TileFX[][] tiles;
    PlayerBoard pb;
    Point last;

    GraphBoardFX(){
        this(TileFX.TILE_SIZE * COLUMNS, TileFX.TILE_SIZE * LINES);
    }

    public GraphBoardFX(int _w, int _h) {
        super(_w, _h);
        tiles = new TileFX[LINES][COLUMNS];
        gc.setLineWidth(1);
        last = null;

        anim = new AnimationTimer()
        {

            int x_max = COLUMNS * TileFX.TILE_SIZE;
            int y_max = LINES * TileFX.TILE_SIZE;

            long lastNano = System.nanoTime();
            final double perSec = 1;

            public void handle(long currentNanoTime)
            {
                if((currentNanoTime - lastNano) / 1000000000.0 > perSec ) {
                    for (int l = 0; l < LINES; l++)
                        for (int c = 0; c < COLUMNS; c++)
                            tiles[l][c].draw(gc);
                    for (int l = 0; l < LINES; l++)
                        gc.strokeLine(0, l * TileFX.TILE_SIZE , x_max, l * TileFX.TILE_SIZE);
                    for (int c = 0; c < COLUMNS; c++)
                        gc.strokeLine(c * TileFX.TILE_SIZE, 0 , c * TileFX.TILE_SIZE, y_max);
                    if(last != null) {
                        Paint p = gc.getStroke();
                        double gcW = gc.getLineWidth();
                        gc.setLineWidth(4);
                        gc.setStroke(Color.rgb(255, 50,50 , 0.5));
                        gc.strokeRect(last.y * TileFX.TILE_SIZE, last.x * TileFX.TILE_SIZE, TileFX.TILE_SIZE, TileFX.TILE_SIZE);
                        gc.setStroke(p);
                        gc.setLineWidth(gcW);
                    }
                    lastNano = currentNanoTime;
                }
            }
        };
    }

    /**
     * @param event
     * @return a point with coordenates (L, C) -> L - Line; C -> Column
     */

    Point pointCoordinates(MouseEvent event){
        //BECAUSE ON SCREEN IS THE OTHER WAY AROUND
        int l = (int) event.getY() / TileFX.TILE_SIZE;
        int c = (int) event.getX() / TileFX.TILE_SIZE;

        if(l > 9 || l < 0 || c > 9 || c < 0)
            return null;

        return new Point(l, c);
    }

    private boolean aPieceInTheArray(String[][] sent, int l, int c) {
        return sent[l][c].equalsIgnoreCase(ShipPiece.ATTACKED_STRING) ||
                sent[l][c].equalsIgnoreCase(ShipPiece.NOT_ATTACKED_STRING) ||
                sent[l][c].equalsIgnoreCase(ShipPiece.ATTACKED_SHIP_DESTROYED_STRING);
    }

    void startTiles(String[][] sent){

        pb = new PlayerBoard(sent);

        for (int l = 0; l < LINES; l++) {
            for (int c = 0; c < COLUMNS; c++) {
                if(pb.getTileAt(l, c).isPiece()) {
                    ShipPiece sp = (ShipPiece) pb.getTileAt(l, c);
                    //System.out.println(sp.getShip().getDirection());
                    addShipTileFX(l, c, sp);
                }
                else
                    addWaterTileFX(l, c, (WaterTile)pb.getTileAt(l, c));
            }
        }
    }

    void addShipTileFX(int l, int c, ShipPiece sp){
        tiles[l][c] = new ShipTileFX(sp.getShip().getSize(), sp.getIdInsideShip(), l, c, sp.getShip().getDirection());
    }

    void addWaterTileFX(int l, int c, WaterTile wt){
        tiles[l][c] = new WaterTileFX(l, c, Direction.VERTICAL);
    }

    //WRONG SOMEWHERE
    void updateTiles(String[][] sent) {

        for (int l = 0; l < LINES; l++) {
            for (int c = 0; c < COLUMNS; c++) {
                TileFX t = tiles[l][c];
                switch (sent[l][c]){
                    case ShipPiece.ATTACKED_SHIP_DESTROYED_STRING:
                        ShipTileFX st = (ShipTileFX) t;
                        st.attack();
                        st.shipDestroyed = true;
                        break;
                    case ShipPiece.ATTACKED_STRING:
                        st = (ShipTileFX) t;
                        st.attack();
                        break;
                    case ShipPiece.NOT_ATTACKED_STRING:
                        st = (ShipTileFX) t;
                        st.attacked = false;
                        break;
                    case WaterTile.NOT_VISIBLE_STRING:
                        WaterTileFX wt = (WaterTileFX) t;
                        wt.attacked = false;
                        break;
                    case WaterTile.VISIBLE_STRING:
                        wt = (WaterTileFX) t;
                        wt.attacked = true;
                        wt.setImageHidden(false);
                        break;
                }
            }
        }
    }

    public void setPlayerBoard(PlayerBoard playerBoard) {
        pb = playerBoard;
    }

    public void setLast(Point p) {
        last = p;
    }
}
