package JavaFX;

import Common.Direction;
import Common.PlayerBoard;
import Common.Ship;
import Common.ShipPiece;
import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.awt.*;
import java.util.ArrayList;

import static Common.PlayerBoard.COLUMNS;
import static Common.PlayerBoard.LINES;
import static Common.PlayerBoard.inBounds;

public class ShipsBoardFX extends GraphBoardFX {

    ShipFX[] shipsFX;
    ShipFX selected;
    ArrayList<Point> tilesToDraw;
    boolean canPlace;
    boolean toRotate;
    boolean finished;

    ShipsBoardFX(int _w, int _h) {
        super(_w, _h);
        shipsFX = new ShipFX[10];
        tilesToDraw = new ArrayList<>();
        canPlace = false;
        toRotate = false;
        doShips();


        anim = new AnimationTimer()
        {
            long lastNano = System.nanoTime();
            final double perSec = 0;

            int x_max = COLUMNS * TileFX.TILE_SIZE;
            int y_max = LINES * TileFX.TILE_SIZE;

            public void handle(long currentNanoTime)
            {

                if(((currentNanoTime - lastNano) / 1000000000.0) > perSec ) {
                    gc.clearRect(0, 0,getWidth() ,getHeight() );
                    gc.drawImage(new Image("images/water_bg.jpg"), 0 , 0);
                    for (int l = 0; l < LINES; l++)
                        gc.strokeLine(0, l * TileFX.TILE_SIZE , x_max, l * TileFX.TILE_SIZE);
                    for (int c = 0; c < COLUMNS; c++)
                        gc.strokeLine(c * TileFX.TILE_SIZE, 0 , c * TileFX.TILE_SIZE, y_max);
                    for(ShipFX s : shipsFX)
                        s.draw(gc);
                    Paint paint = gc.getFill();
                    gc.setStroke(Color.BLUE);
                    if(selected != null)
                        gc.strokeRect(selected.x, selected.y, selected.width, selected.height);
                    gc.setFill(Color.rgb(3, 200, 100, 0.5));
                    if (!canPlace)
                        gc.setFill(Color.rgb(233, 0, 3, 0.5));
                    if(tilesToDraw.size() > 0)
                        for (Point p : tilesToDraw)
                            gc.fillRect(p.x * TileFX.TILE_SIZE, p.y * TileFX.TILE_SIZE, TileFX.TILE_SIZE, TileFX.TILE_SIZE);
                    gc.setFill(paint);
                    lastNano = currentNanoTime;
                }
            }
        };

    }

    void doShips(){
        Ship[] ships = Ship.getFreshShips();
        for(int i = 0; i < ships.length; i++) {
            shipsFX[i] = new ShipFX(ships[i].getSize());
            placeIt(shipsFX[i], i);
        }
    }

    void doShips(PlayerBoard _pb){
        pb = _pb;
        ArrayList<Ship> ships = pb.getShips();
        for(int i = 0; i < ships.size(); i++) {
            Ship s = ships.get(i);
            //System.out.println(s);
            switch(s.getDirection()){
                case VERTICAL:
                    doShipVertical(i, s);
                    break;
                case HORIZONTAL:
                    doShipHorizontal(i, s);
                    break;
            }
        }
    }

    private void doShipVertical(int i, Ship s) {
        shipsFX[i] = new ShipFX(s.getSize(), s.getLandC().x, s.getLandC().y,
                Direction.VERTICAL, true);
        shipsFX[i].placed = true;
    }

    private void doShipHorizontal(int i, Ship s) {
        shipsFX[i] = new ShipFX(s.getSize(), s.getLandC().x, s.getLandC().y,
                Direction.HORIZONTAL, true);
        shipsFX[i].placed = true;
    }

    void placeIt(ShipFX s, int i){
        int startX = TileFX.TILE_SIZE * COLUMNS + 5;
        s.setPosition(startX, i * TileFX.TILE_SIZE);
    }

    boolean fullOfShips(){
        return pb.fullOfShips();
    }

    void seeIfShipFXCanBePlaced(double x, double y) {
        tilesToDraw.clear();

        if (!(x < 0 || x > LINES * TileFX.TILE_SIZE || y < 0 || y > COLUMNS * TileFX.TILE_SIZE))
            if (selected != null) {

                int l = (int) y / TileFX.TILE_SIZE;
                int c = (int) x / TileFX.TILE_SIZE;

                Direction direction = selected.dir;
                if(toRotate)
                    direction = selected.dir.getRotated();

                Ship temp = new Ship(l, c, direction, Ship.ShipType.getShipType(selected.shipSize));
                canPlace = pb.canShipBeHere(temp);
                for (ShipPiece sp : temp.getPieces())
                    tilesToDraw.add(switchCoords(sp.getPointCoordinates()));
            }
    }

    Point switchCoords(Point p){
        return new Point(p.y, p.x);
    }

    boolean canPlace(double x, double y){
        seeIfShipFXCanBePlaced(x, y);
        return canPlace;
    }

    void placeShipFX(double x, double y){

        int l = (int) y / TileFX.TILE_SIZE;
        int c = (int) x / TileFX.TILE_SIZE;

        if(l > 9 || l < 0 || c > 9 || c < 0)
            return;

        if(canPlace && selected != null) {
            Direction direction = selected.dir;

            if(toRotate)
                direction = direction.getRotated();

            pb.placeShip(new Ship(l, c, direction, Ship.ShipType.getShipType(selected.shipSize)));
            selected.setPositionBoard(l, c);
            selected.placed = true;
            if(toRotate) {
                selected.dir = direction;
                selected.selectImage();
            }
            selected = null;
        }
    }


    public ShipFX checkAShip(double x, double y) {
        for(ShipFX s : shipsFX)
            if(s.contains(x, y))
                return s;
        return null;
    }

    void setSelected(ShipFX _selected) {
        selected = _selected;
    }

    public void removeShipFX(ShipFX s) {

        int oldL = s.y / TileFX.TILE_SIZE;
        int oldC = s.x / TileFX.TILE_SIZE;
        System.out.println("oldL: " + oldL);
        System.out.println("oldC: " + oldC);
        pb.removeShip(new Ship(oldL, oldC, s.dir, Ship.ShipType.getShipType(s.shipSize)));

    }
}
