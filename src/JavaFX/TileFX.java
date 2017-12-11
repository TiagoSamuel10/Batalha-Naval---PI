package JavaFX;

import Common.Direction;

public abstract class TileFX extends SpriteTileFX {

    final static int TILE_SIZE = 50;
    boolean attacked;

    TileFX(int _l, int _c, Direction _dir){
        super(_l, _c, true, _dir);
        attacked = false;
    }
}
