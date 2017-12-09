package JavaFX;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.transform.Rotate;

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

    Image imageToSelf;
    Image imageAttacked;
    Image imageOthersHidden;

    ShipTileFX(int sSize, int id, int _l, int _c) {
        super(_l, _c);
        giveImages(sSize, id);
    }

    private void giveImages(int sSize,int id){
        switch (sSize){
            case 1:
                imageToSelf = ONE_ONE;
                //imageAttacked = ONE_ONE_DESTROYED;
                break;
            case 2:
                switch (id) {
                    case 0:
                        imageToSelf = ONE_TWO;
                        //imageAttacked = ONE_TWO_DESTROYED;
                        break;
                    case 1:
                        imageToSelf = TWO_TWO;
                        //imageAttacked = TWO_TWO_DESTROYED;
                        break;
                }
                break;
            case 3:
                switch (id) {
                    case 0:
                        imageToSelf = ONE_THREE;
                        //imageAttacked = ONE_THREE_DESTROYED;
                        break;
                    case 1:
                        imageToSelf = TWO_THREE;
                        //imageAttacked = TWO_THREE_DESTROYED;
                        break;
                    case 2:
                        imageToSelf = THREE_THREE;
                        //imageAttacked = THREE_THREE_DESTROYED;
                        break;
                }
                break;
            case 4:
                switch (id) {
                    case 0:
                        imageToSelf = ONE_FOUR;
                        //imageAttacked = ONE_FOUR_DESTROYED;
                        break;
                    case 1:
                        imageToSelf = TWO_FOUR;
                        //imageAttacked = TWO_FOUR_DESTROYED;
                        break;
                    case 2:
                        imageToSelf = THREE_FOUR;
                        //imageAttacked = THREE_FOUR_DESTROYED;
                        break;
                    case 3:
                        imageToSelf = FOUR_FOUR;
                        //imageAttacked = FOUR_FOUR_DESTROYED;
                        break;
                }
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
