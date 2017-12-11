package JavaFX;

import Common.Direction;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class ShipTileFX extends TileFX {

    boolean shipDestroyed;

    private final static Image ONE_ONE = new Image("images/1_1.png");
    //private final static Image ONE_ONE_DESTROYED = new Image("images/1_1_d.png");

    private final static Image ONE_TWO = new Image("images/1_2.png");
    //private final static Image ONE_TWO_DESTROYED = new Image("images/1_2_d.png");
    private final static Image TWO_TWO = new Image("images/2_2.png");
    //private final static Image TWO_TWO_DESTROYED = new Image("images/2_2_d.png");

    private final static Image ONE_THREE = new Image("images/1_3.png");
    //private final static Image ONE_THREE_DESTROYED = new Image("images/1_3.png");
    private final static Image TWO_THREE = new Image("images/2_3.png");
    //private final static Image TWO_THREE_DESTROYED = new Image("images/2_3_d.png");
    private final static Image THREE_THREE = new Image("images/3_3.png");
    //private final static Image THREE_THREE_DESTROYED = new Image("images/3_3_d.png");

    private final static Image ONE_FOUR = new Image("images/1_4.png");
    //private final static Image ONE_FOUR_DESTROYED = new Image("images/1_4_d.png");
    private final static Image TWO_FOUR = new Image("images/2_4.png");
    //private final static Image TWO_FOUR_DESTROYED = new Image("images/2_4_d.png");
    private final static Image THREE_FOUR = new Image("images/3_4.png");
    //private final static Image THREE_FOUR_DESTROYED = new Image("images/3_4_d.png");
    private final static Image FOUR_FOUR = new Image("images/4_4.png");
    //private final static Image FOUR_FOUR_DESTROYED = new Image("images/4_4_d.png");

    //VERTICAL

    private final static Image ONE_ONE_V = new Image("images/1_1_v.png");

    private final static Image ONE_TWO_V = new Image("images/1_2_v.png");
    //private final static Image ONE_TWO_DESTROYED = new Image("images/1_2_d.png");
    private final static Image TWO_TWO_V = new Image("images/2_2_v.png");
    //private final static Image TWO_TWO_DESTROYED = new Image("images/2_2_d.png");

    private final static Image ONE_THREE_V = new Image("images/1_3_v.png");
    //private final static Image ONE_THREE_DESTROYED = new Image("images/1_3.png");
    private final static Image TWO_THREE_V = new Image("images/2_3_v.png");
    //private final static Image TWO_THREE_DESTROYED = new Image("images/2_3_d.png");
    private final static Image THREE_THREE_V = new Image("images/3_3_v.png");
    //private final static Image THREE_THREE_DESTROYED = new Image("images/3_3_d.png");

    private final static Image ONE_FOUR_V = new Image("images/1_4_v.png");
    //private final static Image ONE_FOUR_DESTROYED = new Image("images/1_4_d.png");
    private final static Image TWO_FOUR_V = new Image("images/2_4_v.png");
    //private final static Image TWO_FOUR_DESTROYED = new Image("images/2_4_d.png");
    private final static Image THREE_FOUR_V = new Image("images/3_4_v.png");
    //private final static Image THREE_FOUR_DESTROYED = new Image("images/3_4_d.png");
    private final static Image FOUR_FOUR_V = new Image("images/4_4_v.png");
    //private final static Image FOUR_FOUR_DESTROYED = new Image("images/4_4_d.png");

    Image imageToSelf;
    Image imageAttacked;
    Image imageOthersHidden;

    ShipTileFX(int sSize, int id, int _l, int _c, Direction _dir) {
        super(_l, _c, _dir);
        giveImages(sSize, id);
    }

    private void giveImages(int sSize,int id){
        switch (sSize){
            case 1:
                imageToSelf = giveImageBasedOnDirection(ONE_ONE);
                //imageAttacked = ONE_ONE_DESTROYED;
                break;
            case 2:
                switch (id) {
                    case 0:
                        imageToSelf = giveImageBasedOnDirection(ONE_TWO);
                        //imageAttacked = ONE_TWO_DESTROYED;
                        break;
                    case 1:
                        imageToSelf = giveImageBasedOnDirection(TWO_TWO);
                        //imageAttacked = TWO_TWO_DESTROYED;
                        break;
                }
                break;
            case 3:
                switch (id) {
                    case 0:
                        imageToSelf = giveImageBasedOnDirection(ONE_THREE);
                        //imageAttacked = ONE_THREE_DESTROYED;
                        break;
                    case 1:
                        imageToSelf = giveImageBasedOnDirection(TWO_THREE);
                        //imageAttacked = TWO_THREE_DESTROYED;
                        break;
                    case 2:
                        imageToSelf = giveImageBasedOnDirection(THREE_THREE);
                        //imageAttacked = THREE_THREE_DESTROYED;
                        break;
                }
                break;
            case 4:
                switch (id) {
                    case 0:
                        imageToSelf = giveImageBasedOnDirection(ONE_FOUR);
                        //imageAttacked = ONE_FOUR_DESTROYED;
                        break;
                    case 1:
                        imageToSelf = giveImageBasedOnDirection(TWO_FOUR);
                        //imageAttacked = TWO_FOUR_DESTROYED;
                        break;
                    case 2:
                        imageToSelf = giveImageBasedOnDirection(THREE_FOUR);
                        //imageAttacked = THREE_FOUR_DESTROYED;
                        break;
                    case 3:
                        imageToSelf = giveImageBasedOnDirection(FOUR_FOUR);
                        //imageAttacked = FOUR_FOUR_DESTROYED;
                        break;
                }
        }
    }

    @Override
    void draw(GraphicsContext gc) {
        super.draw(gc);
    }
}
