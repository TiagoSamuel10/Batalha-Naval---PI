package Common;

import java.awt.*;
import java.io.Serializable;

public abstract class BoardTile implements Serializable {

    public static final int SIZE = 50;
    public final static Color COLOR_NOT_VISIBLE = new Color(100,100,100);
    int x, y;
    boolean visible;
    boolean attacked;

    BoardTile(){
        attacked = false;
        visible = false;
    }

    public abstract boolean isPiece();

    public Color getNotVisibleColor(){
        return COLOR_NOT_VISIBLE;
    }

    public boolean canAttack(){
        return !attacked;
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

    void setAttacked(boolean val){
        attacked = val;
    }

    abstract String toSendString();

}
