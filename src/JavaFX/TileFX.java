package JavaFX;

import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.awt.*;

public abstract class TileFX {

    final static int TILE_SIZE = 50;

    int l;
    int c;
    boolean attacked;
    boolean toRotate;

    TileFX(int _l, int _c){
        l = _l;
        c = _c;
        attacked = false;
        toRotate = false;
    }

    abstract void drawForSelf(GraphicsContext gc);
    abstract void drawForOther(GraphicsContext gc);

}
