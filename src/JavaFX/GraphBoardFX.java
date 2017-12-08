package JavaFX;

import Common.PlayerBoard;
import Common.ShipPiece;
import Common.WaterTile;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import static Common.PlayerBoard.COLUMNS;
import static Common.PlayerBoard.LINES;

public class GraphBoardFX extends Canvas {

    final static Image BACKGROUND_WATER = new Image("images/water_bg.jpg");
    TileFX[][] tiles;
    GraphicsContext gc;
    PlayerBoard pb;

    GraphBoardFX(int _w, int _h){
        super(_w, _h);
        gc = getGraphicsContext2D();
        tiles = new TileFX[LINES][COLUMNS];
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
                    tiles[l][c] = new ShipTileFX(sp.getShip().getSize(), sp.getIdInsideShip(), l, c);
                }
                else
                    tiles[l][c] = new WaterTileFX(l, c);
            }
        }
    }

    void updateTiles(String[][] sent){
        for (int l = 0; l < LINES; l++) {
            for (int c = 0; c < COLUMNS; c++) {
                //System.out.println("HAVE " + board[l][c]);
                switch (sent[l][c]){
                    case ShipPiece.ATTACKED_SHIP_DESTROYED_STRING:
                        ShipTileFX st = (ShipTileFX) tiles[l][c];
                        st.attacked = true;
                        st.shipDestroyed = true;
                        break;
                    case ShipPiece.ATTACKED_STRING:
                        st = (ShipTileFX) tiles[l][c];
                        st.attacked = true;
                        break;
                    case ShipPiece.NOT_ATTACKED_STRING:
                        st = (ShipTileFX) tiles[l][c];
                        st.attacked = false;
                        break;
                    case WaterTile.NOT_VISIBLE_STRING:
                        WaterTileFX wt = (WaterTileFX) tiles[l][c];
                        wt.attacked = false;
                        break;
                    case WaterTile.VISIBLE_STRING:
                        wt = (WaterTileFX) tiles[l][c];
                        wt.attacked = true;
                        break;
                }
            }
        }
    }

    void startAnimating(){
        new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
                gc.drawImage(BACKGROUND_WATER, 0,0);
                for (int l = 0; l < LINES; l++) {
                    for (int c = 0; c < COLUMNS; c++)
                        tiles[l][c].drawForOther(gc);
                }
            }
        }.start();
    }



}
