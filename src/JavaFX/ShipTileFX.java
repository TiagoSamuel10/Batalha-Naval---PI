package JavaFX;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class ShipTileFX extends TileFX {

    Image destroyedImage;
    boolean isDestroyed;

    ShipTileFX(Image _image, int _l, int _c){
        super(_image, _l, _c);
        destroyedImage = new Image("images/2_2.png");
    }
}
