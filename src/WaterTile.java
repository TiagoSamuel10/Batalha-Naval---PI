import java.awt.*;

/**
 * Bloco de água num X e Y
 */

public class WaterTile extends BoardTile {

    private final static Color notVisibleColor = new Color(100,100,100);
    private final static Color hitColor = new Color(50,100,200);

    public WaterTile(int x, int y){
        _x = x;
        _y = y;
    }

    @Override
    public String toString() {
        return (isVisible)?"W":"?";
    }

    @Override
    boolean isPiece() {
        return false;
    }

    @Override
    String gotHit() {
        return "Hit water at " + _x + ":" + _y;
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
