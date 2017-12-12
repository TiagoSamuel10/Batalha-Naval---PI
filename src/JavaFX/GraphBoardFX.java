package JavaFX;

import Common.Direction;
import Common.PlayerBoard;
import Common.ShipPiece;
import Common.WaterTile;
import javafx.animation.AnimationTimer;
import javafx.scene.input.MouseEvent;

import java.awt.*;
import java.util.Arrays;

import static Common.PlayerBoard.COLUMNS;
import static Common.PlayerBoard.LINES;

public class GraphBoardFX extends EmptyGraphBoardFX {

    TileFX[][] tiles;
    PlayerBoard pb;

    GraphBoardFX(){
        this(TileFX.TILE_SIZE * COLUMNS, TileFX.TILE_SIZE * LINES);
    }

    public GraphBoardFX(int _w, int _h) {
        super(_w, _h);
        gc = getGraphicsContext2D();
        tiles = new TileFX[LINES][COLUMNS];
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

    //TODO: PUT IT SOMEWHERE COMMON
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
                    tiles[l][c] = new ShipTileFX(sp.getShip().getSize(), sp.getIdInsideShip(), l, c, sp.getShip().getDirection());
                }
                else
                    tiles[l][c] = new WaterTileFX(l, c, Direction.VERTICAL);
            }
        }
    }

    void updateTiles(String[][] sent) {
        for (int l = 0; l < LINES; l++) {
            for (int c = 0; c < COLUMNS; c++) {
                TileFX t = tiles[l][c];
                boolean piece = true;
                switch (sent[l][c]){
                    case ShipPiece.ATTACKED_SHIP_DESTROYED_STRING:
                        ShipTileFX st = (ShipTileFX) t;
                        st.attacked = true;
                        st.shipDestroyed = true;
                        break;
                    case ShipPiece.ATTACKED_STRING:
                        st = (ShipTileFX) t;
                        st.attacked = true;
                        break;
                    case ShipPiece.NOT_ATTACKED_STRING:
                        st = (ShipTileFX) t;
                        st.attacked = false;
                        break;
                    case WaterTile.NOT_VISIBLE_STRING:
                        piece = false;
                        WaterTileFX wt = (WaterTileFX) t;
                        wt.attacked = false;
                        break;
                    case WaterTile.VISIBLE_STRING:
                        piece = false;
                        wt = (WaterTileFX) t;
                        wt.attacked = true;
                        break;
                }
                setImageForTile(t, piece);
            }
        }
    }

    void setImageForTile(TileFX t, boolean isPiece){
        if(isPiece){
            ShipTileFX st = (ShipTileFX) t;
            if(st.attacked)
                st.setImageToDraw(st.imageAttacked);
            else
                st.setImageToDraw(st.imageOthersHidden);
        }else {
            WaterTileFX wt = (WaterTileFX) t;
            if(wt.attacked)
                wt.setImageToDraw(WaterTileFX.IMAGE_ATTACKED);
            else
                wt.setImageToDraw(WaterTileFX.IMAGE_OTHERS_HIDDEN);
        }
    }

    @Override
    void startAnimating(){
        new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
                //updateTiles(pb.getToPaint());
                //gc.drawImage(BACKGROUND_WATER, 0,0);
                for (int l = 0; l < LINES; l++)
                    for (int c = 0; c < COLUMNS; c++) {
                        gc.drawImage(WaterTileFX.IMAGE_TO_SELF, c * TileFX.TILE_SIZE,
                                l * TileFX.TILE_SIZE);
                        tiles[l][c].draw(gc);
                    }

                for (int l = 0; l < LINES; l++)
                    for (int c = 0; c < COLUMNS; c++) {
                        gc.strokeRect(c * TileFX.TILE_SIZE,
                                l * TileFX.TILE_SIZE,
                                TileFX.TILE_SIZE,
                                TileFX.TILE_SIZE
                        );
                    }

            }
        }.start();
    }

    public void setPlayerBoard(PlayerBoard playerBoard) {
        pb = playerBoard;
    }
}
