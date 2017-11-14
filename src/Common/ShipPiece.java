package Common;

import java.awt.*;

public class ShipPiece extends BoardTile {

    public final static Color ATTACKED_COLOR = new Color(20,20,20);
    public final static Color ATTACKED_COLOR_SHIP_DESTROYED = new Color(150,10,11);
    public final static Color COLOR_TO_SHOW = new Color(100,100,100);

    public static final String NOT_ATTACKED_STRING = "P";
    public static final String ATTACKED_STRING = "PA";
    public static final String ATTACKED_SHIP_DESTROYED_STRING = "PD";

    Ship ship;

    ShipPiece(Ship _ship, int _x, int _y) {
        this.x = _x;
        this.y = _y;
        ship = _ship;
    }

    /*

    @Override
    public String toString() {
        return "Ship at " + x + "; " +y;
    }

    */

    @Override
    public String toString() {
        if(isVisible){
            if(attacked){
                return "HS";
            }
            return "S";
        }
        return "?";
    }

    @Override
    boolean isPiece() {
        return true;
    }

    @Override
    public Color getAttackedColor() {
        if(ship.isDestroyed()){
            return ATTACKED_COLOR_SHIP_DESTROYED;
        }
        return ATTACKED_COLOR;
    }

    @Override
    public Color getVisibleColor() {
        return COLOR_TO_SHOW;
    }

    String details(){
        return "S";
    }
}
