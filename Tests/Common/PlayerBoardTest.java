package Common;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static Common.PlayerBoard.COLUMNS;
import static Common.PlayerBoard.LINES;

class  PlayerBoardTest {

    PlayerBoard pb = new PlayerBoard();


    @Test
    void testAssert(){
        assert (true);
    }

    @Test
    void ships() {
        Ship toAdd = new Ship(1, 1, Direction.LEFT, Ship.ShipType.Four);
        boolean returned = pb.placeShip(toAdd);
        assert (!returned);

        for (ShipPiece shipPiece : toAdd.getPieces()) {
            assert (pb.freeAt(shipPiece.x, shipPiece.y));
        }

        toAdd = new Ship(6, 6, Direction.RIGHT, Ship.ShipType.Three);
        assert (pb.canShipBeHere(toAdd));
        returned = pb.placeShip(toAdd);
        assert (returned);
        assert (!pb.freeAt(6, 6));

        toAdd = new Ship(6, 0, Direction.LEFT, Ship.ShipType.One);
        assert (pb.canShipBeHere(toAdd));
        returned = pb.placeShip(toAdd);
        assert (returned);

        toAdd = new Ship(6, 6, Direction.UP, Ship.ShipType.One);
        assert (!pb.canShipBeHere(toAdd));
        returned = pb.placeShip(toAdd);
        assert (!returned);
        assert (!pb.freeAt(6, 6));

        toAdd = new Ship(9, 9, Direction.UP, Ship.ShipType.Two);
        assert (pb.canShipBeHere(toAdd));
        returned = pb.placeShip(toAdd);
        assert (returned);

        toAdd = new Ship(7, 9, Direction.UP, Ship.ShipType.Two);
        assert (!pb.canShipBeHere(toAdd));

        //9,9 -> 2, cima
        //6,0 -> 1, esquerda
        //6,6 -> 3, direita

        // SEE IF THERE ARE 20 PIECES

        for(int w = 0; w < 1000; w++) {

            pb = PlayerBoard.getRandomPlayerBoard();

            int i = 0;
            for (int l = 0; l < LINES; l++) {
                for (int c = 0; c < COLUMNS; c++) {
                    if (pb.getTileAt(l, c).isPiece()) {
                        i++;
                    }
                }
            }
            assert (i == 20);
        }

        pb = PlayerBoard.getRandomPlayerBoard();


        pb.getAttacked(0,8);
        pb.getAttacked(0,9);
        pb.getAttacked(0,7);
        pb.getAttacked(0,6);
        pb.getAttacked(0,5);
        pb.getAttacked(0,4);
        pb.getAttacked(0,3);
        pb.getAttacked(0,2);
        pb.getAttacked(0,1);
        pb.getAttacked(0,0);

        pb.getAttacked(1,8);
        pb.getAttacked(2,9);
        pb.getAttacked(3,7);
        pb.getAttacked(4,6);
        pb.getAttacked(5,5);
        pb.getAttacked(6,4);
        pb.getAttacked(7,3);
        pb.getAttacked(8,2);
        pb.getAttacked(9,1);

        pb.getAttacked(1,1);
        pb.getAttacked(2,2);
        pb.getAttacked(3,3);
        pb.getAttacked(4,4);
        pb.getAttacked(5,5);
        pb.getAttacked(6,6);
        pb.getAttacked(7,7);
        pb.getAttacked(8,8);
        pb.getAttacked(9,9);


        pb.getAttacked(5,8);
        pb.getAttacked(5,9);
        pb.getAttacked(5,7);
        pb.getAttacked(5,6);
        pb.getAttacked(4,5);
        pb.getAttacked(5,4);
        pb.getAttacked(5,3);
        pb.getAttacked(5,2);


        System.out.println(pb);

        pb = new PlayerBoard(pb.getToSendToPaint());

        System.out.println("---------------------------------");
        System.out.println("---------------------------------");
        System.out.println("---------------------------------");

        System.out.println(pb);


    }

}