package Common;


import java.awt.*;

public class WaterTile extends BoardTile {

    public static final String NOT_VISIBLE_STRING = "W";
    public static final String VISIBLE_STRING = "WV";

    WaterTile(int _x, int _y){
        x = _x;
        y = _y;
    }

    @Override
    public String toString() {
        return "Water at " + this.x + "+" + this.y + " and is attacked " + attacked;
    }

    @Override
    public boolean isPiece() {
        return false;
    }

    @Override
    String toSendString() {
        if(canAttack())
            return NOT_VISIBLE_STRING;
        return VISIBLE_STRING;
    }
}
