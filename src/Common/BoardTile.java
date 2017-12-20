package Common;

import java.awt.*;
import java.io.Serializable;

public abstract class BoardTile implements Serializable {

    int x, y;
    boolean visible;
    boolean attacked;

    BoardTile(){
        attacked = false;
        visible = false;
    }






    public abstract boolean isPiece();

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
