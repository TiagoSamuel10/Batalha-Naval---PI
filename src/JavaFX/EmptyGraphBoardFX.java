package JavaFX;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import static Common.PlayerBoard.COLUMNS;
import static Common.PlayerBoard.LINES;

class EmptyGraphBoardFX extends Canvas {

    final static Image BACKGROUND_WATER = new Image("images/water_bg.jpg");
    GraphicsContext gc;

    AnimationTimer anim;




    EmptyGraphBoardFX(int _w, int _h) {
        super(_w, _h);
        gc = getGraphicsContext2D();

        anim = new AnimationTimer()
        {
            long lastNano = System.nanoTime();
            final double perSec = 1;

            int x_max = COLUMNS * TileFX.TILE_SIZE;
            int y_max = LINES * TileFX.TILE_SIZE;

            public void handle(long currentNanoTime)
            {
                if((currentNanoTime - lastNano) / 1000000000.0 > perSec ) {
                    gc.drawImage(BACKGROUND_WATER, 0, 0);
                    for (int l = 0; l < LINES; l++)
                        gc.strokeLine(0, l * TileFX.TILE_SIZE , x_max, l * TileFX.TILE_SIZE);
                    for (int c = 0; c < COLUMNS; c++)
                        gc.strokeLine(c * TileFX.TILE_SIZE, 0 , c * TileFX.TILE_SIZE, y_max);
                    lastNano = currentNanoTime;
                }
            }
        };

    }

    void startAnimating() {
        anim.start();
    }

    void stopAnimating(){
        anim.stop();
    }
}
