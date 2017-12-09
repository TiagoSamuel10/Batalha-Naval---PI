package JavaFX;

import Common.BoardTile;
import Common.PlayerBoard;
import Common.Ship;
import javafx.animation.AnimationTimer;

import static Common.PlayerBoard.COLUMNS;
import static Common.PlayerBoard.LINES;

public class GraphShipsBoardFX extends GraphBoardFX {

    ShipFX[] shipsFX;

    GraphShipsBoardFX(int _w, int _h) {
        super(_w, _h);
        shipsFX = new ShipFX[10];
    }

    @Override
    void startAnimating() {
        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                for (int l = 0; l < LINES; l++)
                    for (int c = 0; c < COLUMNS; c++)
                        gc.strokeRect(c * TileFX.TILE_SIZE,
                                (l) * TileFX.TILE_SIZE,
                                TileFX.TILE_SIZE,
                                TileFX.TILE_SIZE
                        );
                for(ShipFX s : shipsFX)
                    ;
                    //s.draw(gc);
            }
        }.start();
    }
}
