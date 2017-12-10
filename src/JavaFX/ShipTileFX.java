package JavaFX;

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

    private int s;

    ShipTileFX(int sSize, int id, int _l, int _c, boolean toRotate) {
        super(_l, _c, toRotate);
        giveImages(sSize, id);
        sSize = s;
    }

    private void giveImages(int sSize,int id){
        switch (sSize){
            case 1:
                if(!toRotate) imageToSelf = ONE_ONE;
                else imageToSelf = ONE_ONE_V;
                //imageAttacked = ONE_ONE_DESTROYED;
                break;
            case 2:
                switch (id) {
                    case 0:
                        if(!toRotate) imageToSelf = ONE_TWO;
                        else imageToSelf = ONE_TWO_V;
                        //imageAttacked = ONE_TWO_DESTROYED;
                        break;
                    case 1:
                        if(!toRotate) imageToSelf = TWO_TWO;
                        else imageToSelf = TWO_TWO_V;
                        //imageAttacked = TWO_TWO_DESTROYED;
                        break;
                }
                break;
            case 3:
                switch (id) {
                    case 0:
                        if(!toRotate) imageToSelf = ONE_THREE;
                        else imageToSelf = ONE_THREE_V;
                        //imageAttacked = ONE_THREE_DESTROYED;
                        break;
                    case 1:
                        if(!toRotate) imageToSelf = TWO_THREE;
                        else imageToSelf = TWO_THREE_V;
                        //imageAttacked = TWO_THREE_DESTROYED;
                        break;
                    case 2:
                        if(!toRotate) imageToSelf = THREE_THREE;
                        else imageToSelf = THREE_THREE_V;
                        //imageAttacked = THREE_THREE_DESTROYED;
                        break;
                }
                break;
            case 4:
                switch (id) {
                    case 0:
                        if(!toRotate) imageToSelf = ONE_FOUR;
                        else imageToSelf = ONE_FOUR_V;
                        //imageAttacked = ONE_FOUR_DESTROYED;
                        break;
                    case 1:
                        if(!toRotate) imageToSelf = TWO_FOUR;
                        else imageToSelf = TWO_FOUR_V;
                        //imageAttacked = TWO_FOUR_DESTROYED;
                        break;
                    case 2:
                        if(!toRotate) imageToSelf = THREE_FOUR;
                        else imageToSelf = THREE_FOUR_V;
                        //imageAttacked = THREE_FOUR_DESTROYED;
                        break;
                    case 3:
                        if(!toRotate) imageToSelf = FOUR_FOUR;
                        else imageToSelf = FOUR_FOUR_V;
                        //imageAttacked = FOUR_FOUR_DESTROYED;
                        break;
                }
        }
    }

    @Override
    void draw(GraphicsContext gc) {
        super.draw(gc);
        if(toRotate && s == 1)
            System.out.println("++");
    }
}
