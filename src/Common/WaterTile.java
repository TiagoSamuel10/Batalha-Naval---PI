package Common;

import java.awt.*;

/**
 * Bloco de água num X e Y
 */

public class WaterTile extends BoardTile {

    private final static Color attackedColor = new Color(10,60,150);
    private final static Color visibleColor = new Color(80,140,240);


    public WaterTile(int x, int y){
        _x = x;
        _y = y;
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
        return attackedColor;
    }

    @Override
    public Color getVisibleColor() {
        return visibleColor;
    }
}
