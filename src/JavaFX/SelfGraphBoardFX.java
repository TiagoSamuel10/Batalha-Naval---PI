package JavaFX;

import javafx.animation.AnimationTimer;

import static Common.PlayerBoard.COLUMNS;
import static Common.PlayerBoard.LINES;

public class SelfGraphBoardFX extends GraphBoardFX {

    SelfGraphBoardFX(int _w, int _h) {
        super(_w, _h);
    }

    @Override
    void startAnimating() {
        new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
                gc.drawImage(BACKGROUND_WATER, 0,0);
                for (int l = 0; l < LINES; l++) {
                    for (int c = 0; c < COLUMNS; c++)
                        tiles[l][c].drawForSelf(gc);
                }
            }

        }.start();
    }
}
