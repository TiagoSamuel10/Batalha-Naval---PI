package Common;

import Server.Game;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static Common.PlayerBoard.COLUMNS;
import static Common.PlayerBoard.LINES;

class PlayerBoardTest {

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
            assert (pb.freeAt(shipPiece._x, shipPiece._y));
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

        System.out.println(pb.details());

        // SEE IF THERE ARE 20 PIECES

        for(int w = 0; w < 10000; w++) {

            pb = Game.getRandomPlayerBoard();

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
    }

}