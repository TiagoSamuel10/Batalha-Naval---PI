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
    boolean placed;

    ShipFX(int _ShipSize, int _x, int _y, Direction _dir, boolean boardCoord){
        super(_x, _y, boardCoord, _dir);
        shipSize = _ShipSize;
        selectImage();
    }




    ShipFX(int _ShipSize){
        this(_ShipSize, 0,0, Direction.HORIZONTAL, false);
    }

    void selectImage(){
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
        setImageToDraw(giveImageBasedOnDirection(getImageToDraw()));
    }

}
