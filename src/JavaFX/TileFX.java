package JavaFX;

public abstract class TileFX extends SpriteTileFX {

    final static int TILE_SIZE = 50;
    boolean attacked;

    TileFX(int _l, int _c, boolean toRotate){
        super(_l, _c, true, toRotate);
        attacked = false;
    }
}
