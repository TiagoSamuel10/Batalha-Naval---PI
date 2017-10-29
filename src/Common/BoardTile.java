package Common;

import java.awt.*;
import java.io.Serializable;

public abstract class BoardTile implements Serializable {

    private final static Color NOT_VISIBLE_COLOR = new Color(100,100,100);
    public static final int SIZE = 50;

    public Image image;
    int _x, _y;
    boolean isVisible;
    boolean attacked;

    abstract boolean isPiece();
    public abstract Color getAttackedColor();
    public abstract Color getVisibleColor();

    public Color getNotVisibleColor(){
        return NOT_VISIBLE_COLOR;
    }

    BoardTile(){
        isVisible = false;
    }

    public boolean isVisible() {
        return isVisible;
    }
    public boolean isAttacked() {
        return attacked;
    }

    public Point getCoord(){
        return new Point(_x, _y);
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
