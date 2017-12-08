package JavaFX;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class WaterTileFX extends TileFX {

    final static Image IMAGE_TO_SELF = new Image("images/water.png");

    final static Image IMAGE_ATTACKED = new Image("images/water.png");
    final static Image IMAGE_OTHERS_HIDDEN = new Image("images/water.png");

    WaterTileFX(int _l, int _c) {
        super(_l, _c);
    }

    @Override
    void drawForSelf(GraphicsContext gc) {
        /*
        if(attacked)
            gc.drawImage(IMAGE_ATTACKED, l * TILE_SIZE, c * TILE_SIZE);
        else
            gc.drawImage(IMAGE_TO_SELF, l * TILE_SIZE, c * TILE_SIZE);
            */
    }

    @Override
    void drawForOther(GraphicsContext gc) {
        /*
        if(attacked)
            gc.drawImage(IMAGE_ATTACKED, l * TILE_SIZE, c * TILE_SIZE);
        else
            gc.drawImage(IMAGE_OTHERS_HIDDEN, l * TILE_SIZE, c * TILE_SIZE);
            */
    }
}
