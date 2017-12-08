package JavaFX;

import Water.Particle;
import Water.Window;
import javafx.animation.AnimationTimer;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;

public class GraphBoardFX extends Canvas {

    ArrayList <TileFX> pieces;
    GraphicsContext gc;

    GraphBoardFX(int _w, int _h){
        super(_w, _h);
        gc = getGraphicsContext2D();
        pieces = new ArrayList<>();

        new AnimationTimer()
        {
            public void handle(long currentNanoTime)
            {
                for(TileFX st : pieces) {
                    st.draw(gc);
                }
            }
        }.start();

    }

    public void associatePlayerBoard(){

    }

    public void addShipTile(ShipTileFX shipTileFX) {
        pieces.add(shipTileFX);
    }
}
