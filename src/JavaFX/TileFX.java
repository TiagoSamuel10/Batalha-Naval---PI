package JavaFX;

import Common.Direction;
import javafx.scene.image.Image;

public abstract class TileFX extends SpriteTileFX {

    final static int TILE_SIZE = 50;
    boolean attacked;
    boolean normalBoard;

    Image imageAttacked;
    Image imageToSelf;
    Image imageOthersHidden = new Image("images/fog.png");

    TileFX(int _l, int _c, Direction _dir){
        super(_l, _c, true, _dir);
        attacked = false;
        normalBoard = false;
    }

    /**
     *
     * @param other - Used by GraphBoards to tell the tile if they should draw
     * their image or a hidden image; FALSE - means show image; TRUE - means
     * hidden
     */
    void forNormalBoard(boolean other){
        setImageToDraw(imageToSelf);
        if(other)
            setImageToDraw(imageOthersHidden);
        normalBoard = other;
    }

    @Override
    public String toString() {
        return "T at" + l + ":" + c + "; attacked: " + attacked;
    }
}
