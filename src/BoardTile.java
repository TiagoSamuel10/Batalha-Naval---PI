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

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof BoardTile))
            return false;
        BoardTile other = (BoardTile) obj;
        return _x == other._x && _y == other._y;
    }

    void setAttacked(){
        attacked = true;
        isVisible = true;
    }

}
