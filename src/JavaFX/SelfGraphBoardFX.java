package JavaFX;

public class SelfGraphBoardFX extends GraphBoardFX {

    SelfGraphBoardFX(int _w, int _h) {
        super(_w, _h);
    }

    void setImageForTile(TileFX t, boolean isPiece){
        if(isPiece){
            ShipTileFX st = (ShipTileFX) t;
            if(st.attacked)
                st.setImageToDraw(st.imageAttacked);
            else
                st.setImageToDraw(st.imageToSelf);
        }else {
            WaterTileFX wt = (WaterTileFX) t;
            if(wt.attacked)
                wt.setImageToDraw(WaterTileFX.IMAGE_ATTACKED);
            else
                wt.setImageToDraw(WaterTileFX.IMAGE_TO_SELF);
        }
    }
}
