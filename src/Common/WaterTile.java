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
        if(visible){
            if(attacked){
                return "HW";
            }
            return "W";
        }
        return "?";
    }



    @Override
    boolean isPiece() {
        return false;
    }

    String details(){
        return "W";
    }
}
