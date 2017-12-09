package JavaFX;

import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.awt.*;

public abstract class TileFX extends Sprite{

    final static int TILE_SIZE = 50;
    boolean attacked;
    boolean toRotate;

    int l;
    int c;

    TileFX(int _l, int _c){
        l = _l;
        c = _c;
        setPosition(_l * TILE_SIZE, _c *TILE_SIZE);
        attacked = false;
        toRotate = false;
    }

}
