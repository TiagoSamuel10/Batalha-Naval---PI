package ClientSide;

import Common.BoardTile;
import Common.ShipPiece;
import Common.WaterTile;

import java.awt.*;

import static Common.PlayerBoard.COLUMNS;
import static Common.PlayerBoard.LINES;

class MyGraphBoard extends GraphicalBoard {

    MyGraphBoard(String[][] board) {
        setLayout(null);
        int multiplier = (BoardTile.SIZE + BORDER);

        for (int l = 0; l < LINES; l++) {
            for (int c = 0; c < COLUMNS; c++) {
                System.out.println("HAVE " + board[l][c]);
                GraphTile graphTile = new GraphTile();
                Point p = new Point(c * multiplier, l * multiplier);
                graphTile.setLocation(p);
                graphTile.setSize(BoardTile.SIZE, BoardTile.SIZE);
                switch (board[l][c]){
                    case ShipPiece.ATTACKED_SHIP_DESTROYED_STRING:
                        graphTile.setColor(ShipPiece.COLOR_ATTACKED_SHIP_DESTROYED);
                        System.out.println("S ATTACKED DESTROYED");
                        break;
                    case ShipPiece.ATTACKED_STRING:
                        graphTile.setColor(ShipPiece.COLOR_ATTACKED);
                        System.out.println("S ATTACKED");
                        break;
                    case ShipPiece.NOT_ATTACKED_STRING:
                        graphTile.setColor(ShipPiece.COLOR_TO_SHOW);
                        System.out.println("S NOT ATTACKED");
                        break;
                    case WaterTile.NOT_VISIBLE_STRING:
                        graphTile.setColor(WaterTile.COLOR_TO_SHOW);
                        System.out.println("W NOT ATTACKED");
                        break;
                    case WaterTile.ATTACKED_OR_VISIBLE_STRING:
                        graphTile.setColor(WaterTile.COLOR_ATTACKED_OR_VISIBLE);
                        System.out.println("W ATTACKED OR VISIBLE");
                        break;
                }
                graphTile.setL(l);
                graphTile.setC(c);
                add(graphTile);
            }
        }

        setSize(SIZE);
        setLocation(GameClient.GAME_BOARD_LOCATION);
    }
}
