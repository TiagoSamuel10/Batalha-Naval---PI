package Common;


import java.awt.*;

public class WaterTile extends BoardTile {

    private final static Color ATTACKED_COLOR = new Color(10,60,150);
    private final static Color VISIBLE_COLOR = new Color(80,140,240);

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
        return VISIBLE_COLOR;
    }

    String details(){
        return "W";
    }
}
