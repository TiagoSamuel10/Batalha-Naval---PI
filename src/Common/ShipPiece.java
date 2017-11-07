package Common;

import java.awt.*;

class ShipPiece extends BoardTile {

    private final static Color ATTACKED_COLOR = new Color(20,20,20);
    private final static Color ATTACKED_COLOR_SHIP_DESTROYED = new Color(150,10,11);
    private final static Color VISIBLE_COLOR = new Color(100,100,100);

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
        return VISIBLE_COLOR;
    }

    String details(){
        return "S";
    }
}
