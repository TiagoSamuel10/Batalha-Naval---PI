import javax.swing.*;
import java.awt.*;

abstract class BoardTile{

    private final static Color NOT_VISIBLE_COLOR = new Color(100,100,100);
    static final int SIZE = 50;

    Image image;
    int _x, _y;
    boolean isVisible;
    boolean attacked;

    abstract boolean isPiece();
    abstract Color getAttackedColor();
    abstract Color getVisibleColor();

    Color getNotVisibleColor(){
        return NOT_VISIBLE_COLOR;
    }

    BoardTile(){
        isVisible = false;
    }

    void setAttacked(){
        attacked = true;
        isVisible = true;
    }

}
