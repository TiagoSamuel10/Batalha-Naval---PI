package JavaFX;

import Common.Direction;
import Common.PlayerBoard;
import Common.Ship;
import Common.ShipPiece;
import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.ArrayList;

import static Common.PlayerBoard.COLUMNS;
import static Common.PlayerBoard.LINES;

public class GraphShipsBoardFX extends GraphBoardFX {

    ShipFX[] shipsFX;
    ShipFX selected;
    ArrayList<Point> tilesToDraw;
    boolean canPlace;
    public boolean toRotate;

    GraphShipsBoardFX(int _w, int _h) {
        super(_w, _h);
        shipsFX = new ShipFX[10];
        tilesToDraw = new ArrayList<>();
        canPlace = false;
        toRotate = false;
        doShips();
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
            System.out.println(s);
            switch(s.getDirection()){
                case UP:
                    doShipUP(i, s);
                    break;
                case DOWN:
                    doShipDown(i, s);
                    break;
                case RIGHT:
                    doShipRight(i, s);
                    break;
                case LEFT:
                    doShipLeft(i, s);
            }
        }
    }

    private void doShipUP(int i, Ship s) {
        ShipPiece last = s.getPieces()[0];
        for (ShipPiece shipPiece : s.getPieces())
            last = shipPiece;
        s.oppositeDirection();
        shipsFX[i] = new ShipFX(s.getSize(), last.getPointCoordinates().x, last.getPointCoordinates().y,
                s.getDirection(), true);
        shipsFX[i].placed = true;
    }

    private void doShipLeft(int i, Ship s) {
        ShipPiece last = s.getPieces()[0];
        for (ShipPiece shipPiece : s.getPieces())
            last = shipPiece;
        s.oppositeDirection();
        shipsFX[i] = new ShipFX(s.getSize(), last.getPointCoordinates().x, last.getPointCoordinates().y,
                s.getDirection(), true);
        shipsFX[i].placed = true;
    }

    private void doShipRight(int i, Ship s) {
        shipsFX[i] = new ShipFX(s.getSize(), s.getLandC().x, s.getLandC().y,
                Direction.RIGHT, true);
        shipsFX[i].placed = true;
    }

    private void doShipDown(int i, Ship s) {
        shipsFX[i] = new ShipFX(s.getSize(), s.getLandC().x, s.getLandC().y,
                Direction.DOWN, true);
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

    boolean canPlace(){
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

    @Override
    void startAnimating() {
        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                gc.clearRect(0, 0,700 ,700 );
                gc.drawImage(new Image("images/water_bg.jpg"), 0 , 0);
                for (int l = 0; l < LINES; l++)
                    for (int c = 0; c < COLUMNS; c++)
                        gc.strokeRect(c * TileFX.TILE_SIZE,
                                l * TileFX.TILE_SIZE,
                                TileFX.TILE_SIZE,
                                TileFX.TILE_SIZE
                        );
                for(ShipFX s : shipsFX)
                    s.draw(gc);
                gc.save();
                gc.setStroke(Color.BLUE);
                if(selected != null)
                    gc.strokeRect(selected.x, selected.y, selected.width, selected.height);
                gc.setFill(Color.rgb(3, 200, 100, 0.5));
                if (!canPlace)
                    gc.setFill(Color.rgb(233, 0, 3, 0.5));
                if(tilesToDraw.size() > 0)
                    for (Point p : tilesToDraw)
                        gc.fillRect(p.x * TileFX.TILE_SIZE, p.y * TileFX.TILE_SIZE, TileFX.TILE_SIZE, TileFX.TILE_SIZE);
                gc.restore();
            }
        }.start();
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
