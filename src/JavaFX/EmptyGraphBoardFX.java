package JavaFX;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import static Common.PlayerBoard.COLUMNS;
import static Common.PlayerBoard.LINES;

public class EmptyGraphBoardFX extends Canvas {

    final static Image BACKGROUND_WATER = new Image("images/water_bg.jpg");
    GraphicsContext gc;

    EmptyGraphBoardFX(int _w, int _h) {
        super(_w, _h);
        gc = getGraphicsContext2D();
    }

    void startAnimating() {
        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                getGraphicsContext2D().drawImage(BACKGROUND_WATER, 0, 0);
                for (int l = 0; l < LINES; l++)
                    for (int c = 0; c < COLUMNS; c++)
                        gc.strokeRect(c * TileFX.TILE_SIZE,
                                (l) * TileFX.TILE_SIZE,
                                TileFX.TILE_SIZE,
                                TileFX.TILE_SIZE
                        );
            }
        }.start();
    }
}
