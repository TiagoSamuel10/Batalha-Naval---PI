package JavaFX;


import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;

import java.awt.*;

import static JavaFX.TileFX.TILE_SIZE;

class ShipFX extends Sprite{

    private final static Image ONE = new Image("images/1.png");
    private final static Image TWO = new Image("images/2.png");
    private final static Image THREE = new Image("images/3.png");
    private final static Image FOUR = new Image("images/4.png");

    private int shipSize;
    boolean toRotate;

    ShipFX(int _ShipSize, int _x, int _y){
        shipSize = _ShipSize;
        selectImage();
        setPosition(_x, _y);
        toRotate = true;
    }

    ShipFX(int _ShipSize){
        this(_ShipSize, 0,0);
    }

    private void selectImage(){
        switch (shipSize) {
            case 1:
                setImageToDraw(ONE);
                break;
            case 2:
                setImageToDraw(TWO);
                break;
            case 3:
                setImageToDraw(THREE);
                break;
            case 4:
                setImageToDraw(FOUR);
                break;
        }
    }

    @Override
    void draw(GraphicsContext gc) {
        if(toRotate)
            super.draw(gc);
        else
            drawRotated(getImageToDraw(), 90, gc);
    }

}
