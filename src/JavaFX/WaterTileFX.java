package JavaFX;

import Common.Direction;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

class WaterTileFX extends TileFX {

    final static Image IMAGE_TO_SELF = new Image("images/water.png");

    final static Image IMAGE_ATTACKED = new Image("images/water.png");
    final static Image IMAGE_OTHERS_HIDDEN = new Image("images/water.png");

    WaterTileFX(int _l, int _c, Direction _dir) {
        super(_l, _c, _dir);
    }

    @Override
    void draw(GraphicsContext gc) {
        //
    }
}
