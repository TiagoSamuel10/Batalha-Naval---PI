package Common;


import java.awt.*;

public class WaterTile extends BoardTile {

    public final static Color ATTACKED_COLOR = new Color(10,60,150);
    public final static Color COLOR_TO_SHOW = new Color(80,140,240);

    public static final String NOT_VISIBLE_STRING = "W";
    public static final String ATTACKED_OR_VISIBLE_STRING = "WV";

    WaterTile(int _x, int _y){
        x = _x;
        y = _y;
    }

    @Override
    public String toString() {
        if(isVisible){
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

    @Override
    public Color getAttackedColor() {
        return ATTACKED_COLOR;
    }

    @Override
    public Color getVisibleColor() {
        return COLOR_TO_SHOW;
    }

    String details(){
        return "W";
    }
}
