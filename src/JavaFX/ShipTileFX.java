package JavaFX;

import Common.Direction;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class ShipTileFX extends TileFX {

    private final static Image ATTACKED = new Image("images/FOGO.png");
    private final static Image DESTROYED = new Image("images/fumeira.png");

    private final static Image ONE_ONE = new Image("images/1_1.png");

    private final static Image ONE_TWO = new Image("images/1_2.png");
    private final static Image TWO_TWO = new Image("images/2_2.png");

    private final static Image ONE_THREE = new Image("images/1_3.png");
    private final static Image TWO_THREE = new Image("images/2_3.png");
    private final static Image THREE_THREE = new Image("images/3_3.png");

    private final static Image ONE_FOUR = new Image("images/1_4.png");
    private final static Image TWO_FOUR = new Image("images/2_4.png");
    private final static Image THREE_FOUR = new Image("images/3_4.png");
    private final static Image FOUR_FOUR = new Image("images/4_4.png");

    boolean shipDestroyed;

    private int id;
    private int sSize;

    ShipTileFX(int _sSize, int _id, int _l, int _c, Direction _dir) {
        super(_l, _c, _dir);
        sSize = _sSize;
        id = _id;
        imageAttacked = ATTACKED;
        giveRightImageToShow();
        forNormalBoard(true);
    }

    void giveRightImageToShow(){
        switch (sSize){
            case 1:
                imageToSelf = giveImageBasedOnDirection(ONE_ONE);
                break;
            case 2:
                switch (id) {
                    case 0:
                        imageToSelf = giveImageBasedOnDirection(ONE_TWO);
                        break;
                    case 1:
                        imageToSelf = giveImageBasedOnDirection(TWO_TWO);
                        break;
                }
                break;
            case 3:
                switch (id) {
                    case 0:
                        imageToSelf = giveImageBasedOnDirection(ONE_THREE);
                        break;
                    case 1:
                        imageToSelf = giveImageBasedOnDirection(TWO_THREE);
                        break;
                    case 2:
                        imageToSelf = giveImageBasedOnDirection(THREE_THREE);
                        break;
                }
                break;
            case 4:
                switch (id) {
                    case 0:
                        imageToSelf = giveImageBasedOnDirection(ONE_FOUR);
                        break;
                    case 1:
                        imageToSelf = giveImageBasedOnDirection(TWO_FOUR);
                        break;
                    case 2:
                        imageToSelf = giveImageBasedOnDirection(THREE_FOUR);
                        break;
                    case 3:
                        imageToSelf = giveImageBasedOnDirection(FOUR_FOUR);
                        break;
                }
        }
        setImageToDraw(imageToSelf);
    }

    void attack(){
        attacked = true;
    }

    void shipDestroyed(){
        attacked = true;
        shipDestroyed = true;
        setImageToDraw(imageToSelf);
    }

    @Override
    void draw(GraphicsContext gc) {
        if(normalBoard){
            if(attacked) {
                gc.drawImage(WaterTileFX.IMAGE_ATTACKED, x, y);
                if (shipDestroyed) {
                    super.draw(gc);
                    gc.drawImage(DESTROYED, x, y);
                }
                else
                    gc.drawImage(ATTACKED, x, y);
            }

            else {
                gc.drawImage(WaterTileFX.IMAGE_TO_SELF, x, y);
                super.draw(gc);
            }
        }
        else{
            if(attacked) {
                gc.drawImage(WaterTileFX.IMAGE_ATTACKED, x, y);
                if (shipDestroyed) {
                    super.draw(gc);
                    gc.drawImage(DESTROYED, x, y);
                }
                else {
                    super.draw(gc);
                    gc.drawImage(ATTACKED, x, y);
                }
            }

            else {
                gc.drawImage(WaterTileFX.IMAGE_TO_SELF, x, y);
                super.draw(gc);
            }
        }
    }

    @Override
    public String toString() {
        return "ST at" + l + ":" + c + "; attacked: " + attacked;
    }
}
