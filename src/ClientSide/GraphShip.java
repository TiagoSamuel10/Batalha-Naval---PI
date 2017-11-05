package ClientSide;

import Common.BoardTile;
import Common.Ship;

import javax.swing.*;
import java.awt.*;

class GraphShip extends JPanel{

    private Ship _ship;
    private Dimension horizontal;
    private Dimension vertical;

    boolean alreadyPlaced;

    private Dimension current;

    Ship getShip(){
        return _ship;
    }

    void changeShipPosition(Point point){
        _ship.setNewCoord(point);
    }

    private GraphShip(Ship ship){
        alreadyPlaced = false;
        _ship = ship;
        horizontal = new Dimension(BoardTile.SIZE, _ship.getSize() * BoardTile.SIZE);
        vertical = new Dimension(_ship.getSize() * BoardTile.SIZE, BoardTile.SIZE);
        setBackground(Color.BLACK);
        setSize(horizontal);
        current = horizontal;
    }

    void rotate(){
        if(current == horizontal){
            setSize(vertical);
            current = vertical;
        }
        else{
            setSize(horizontal);
            current = horizontal;
        }
        _ship.changeDirection();
    }

    static GraphShip[] getAll(){
        Ship[] ships = Ship.getFreshShips();
        //4
        //3,3
        //2,2,2
        //1,1,1,1
        GraphShip[] graphShips = new GraphShip[ships.length];
        for(int i = 0; i < ships.length; i++) {
            graphShips[i] = layItForMe(new GraphShip(ships[i]), i);
        }
        return graphShips;
    }

    private static GraphShip layItForMe(GraphShip graphShip, int number) {

        int x = 0;
        int y = 0;

        if(number >= 0 && number <= 3){
            x += 800;
            y += 100;
            if(number > 0){
                x+= 100;
            }
            if(number > 1){
                x+= 100;
            }
            if(number > 2){
                x+= 100;
            }
        }

        else if(number > 3 && number <= 6){
            x += 800;
            y += 120 + 4 * BoardTile.SIZE;
            if(number > 4){
                x+= 100;
            }
            if(number > 5){
                x+= 100;
            }
        }

        else{
            x += 800;
            y += 140 + 6 * BoardTile.SIZE;
            if(number > 7){
                x+= 100;
            }
            if(number > 8){
                x+= 100;
            }
        }

        graphShip.setLocation(x, y);

        return graphShip;

    }

}
