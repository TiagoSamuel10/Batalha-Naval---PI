package JavaFX;

import Common.Direction;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

class WaterTileFX extends TileFX {

    final static Image IMAGE_TO_SELF = new Image("images/water.png");
    final static Image IMAGE_ATTACKED = new Image("images/water_d.png");

    WaterTileFX(int _l, int _c, Direction _dir) {
        super(_l, _c, _dir);
        imageToSelf = IMAGE_TO_SELF;
        imageAttacked = IMAGE_ATTACKED;
        setImageHidden(true);
    }

    @Override
    void draw(GraphicsContext gc) {
        if(attacked)
            gc.drawImage(imageAttacked, x, y);
        else
            super.draw(gc);
    }

    @Override
    public String toString() {
        return "WT at" + l + ":" + c + "; attacked: " + attacked;
    }
}
