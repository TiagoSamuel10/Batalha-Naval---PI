import java.awt.*;

/**
 * Um pedaço de navio. Tem um X e Y
 */

class ShipPiece extends BoardTile implements Comparable<ShipPiece> {

    private final static Color notVisibleColor = new Color(100,100,100);
    private final static Color hitColor = new Color(30,30,30);

    Ship _ship;

    ShipPiece(Ship ship, int x, int y) {
        _x = x;
        _y = y;
        _ship = ship;
    }

    @Override
    public String toString() {
        return (isVisible)?"X":"?";
    }

    @Override
    boolean isPiece() {
        return true;
    }

    @Override
    public int compareTo(ShipPiece o) {
        if(o._y == _y && o._x == _x){
            return 1;
        }
        return 0;
    }

    @Override
    String gotHit() {
        return "Hit ship piece at " + _x + ":" + _y;
    }

    @Override
    Color getNotVisibleColor() {
        return notVisibleColor;
    }

    @Override
    Color getHitColor() {
        return hitColor;
    }
}
