import java.awt.*;
import java.io.Serializable;

class ShipPiece extends BoardTile implements Serializable{

    private final static Color ATTACKED_COLOR = new Color(20,20,20);
    private final static Color ATTACKED_COLOR_SHIP_DESTROYED = new Color(150,10,11);
    private final static Color VISIBLE_COLOR = new Color(100,100,100);

    Ship _ship;

    ShipPiece(Ship ship, int x, int y) {
        _x = x;
        _y = y;
        _ship = ship;
    }

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
    Color getAttackedColor() {
        if(_ship.isDestroyed()){
            return ATTACKED_COLOR_SHIP_DESTROYED;
        }
        return ATTACKED_COLOR;
    }

    @Override
    Color getVisibleColor() {
        return VISIBLE_COLOR;
    }
}
