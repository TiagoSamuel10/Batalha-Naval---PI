package JavaFX;

import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.awt.*;

public class TileFX {

    Image image;
    int l;
    int c;

    public TileFX(Image _image, int _l, int _c){
        image = _image;
        l = _l;
        c = _c;
    }

    void draw(GraphicsContext gc){
        gc.drawImage(image, l * 30, c * 30);
    }


}
