package Common;


import java.awt.*;

public class WaterTile extends BoardTile {

    public final static Color COLOR_ATTACKED_OR_VISIBLE = new Color(0,0,255);

    public final static Color COLOR_TO_SHOW = new Color(0,200,200);

    public static final String NOT_VISIBLE_STRING = "W";
    public static final String ATTACKED_OR_VISIBLE_STRING = "WV";

    WaterTile(int _x, int _y){
        x = _x;
        y = _y;
    }

    @Override
    public String toString() {
        return "Water at " + this.x + "+" + this.y + " and is attacked " + attacked;
    }

    @Override
    boolean isPiece() {
        return false;
    }

    @Override
    String toSendString() {
        if(canAttack())
            return NOT_VISIBLE_STRING;
        return ATTACKED_OR_VISIBLE_STRING;
    }
}
