package JavaFX;


import Common.Direction;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

class ShipFX extends SpriteTileFX {

    private final static Image ONE = new Image("images/1.png");
    private final static Image TWO = new Image("images/2.png");
    private final static Image THREE = new Image("images/3.png");
    private final static Image FOUR = new Image("images/4.png");

    private final static Image ONE_V = new Image("images/1_v.png");
    private final static Image TWO_V = new Image("images/2_v.png");
    private final static Image THREE_V = new Image("images/3_v.png");
    private final static Image FOUR_V = new Image("images/4_v.png");

    int shipSize;
    Direction dir;
    boolean placed;

    ShipFX(int _ShipSize, int _x, int _y, Direction _dir, boolean toRotate){
        super(_x, _y, false, toRotate);
        shipSize = _ShipSize;
        dir = _dir;
        selectImage();
    }

    ShipFX(int _ShipSize){
        this(_ShipSize, 0,0, Direction.RIGHT, false);
    }

    void selectImage(){
        switch (shipSize) {
            case 1:
                if(!toRotate)setImageToDraw(ONE);
                else setImageToDraw(ONE_V);
                break;
            case 2:
                if(!toRotate)setImageToDraw(TWO);
                else setImageToDraw(TWO_V);
                break;
            case 3:
                if(!toRotate)setImageToDraw(THREE);
                else setImageToDraw(THREE_V);
                break;
            case 4:
                if(!toRotate)setImageToDraw(FOUR);
                else setImageToDraw(FOUR_V);
                break;
        }
    }

}
