package Common;

import java.awt.*;
import java.io.Serializable;

public abstract class BoardTile implements Serializable {

    public static final int SIZE = 50;
    public final static Color COLOR_NOT_VISIBLE = new Color(100,100,100);
    public Image image = null;
    int x, y;
    boolean isVisible;
    boolean attacked;

    BoardTile(){
        isVisible = false;
    }

    abstract boolean isPiece();

    public abstract Color getAttackedColor();

    public abstract Color getVisibleColor();

    abstract String details();

    public Color getNotVisibleColor(){
        return COLOR_NOT_VISIBLE;
    }

    public boolean isVisible() {
        return isVisible;
    }
    public boolean isAttacked() {
        return attacked;
    }

    public Point getPointCoordinates(){
        return new Point(x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (!(obj instanceof BoardTile))
            return false;
        BoardTile other = (BoardTile) obj;
        return x == other.x && y == other.y;
    }

    void setVisible(){
        isVisible = true;
    }

    void setAttacked(){
        attacked = true;
        setVisible();
    }

}
