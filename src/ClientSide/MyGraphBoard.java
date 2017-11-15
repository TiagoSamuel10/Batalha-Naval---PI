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
                GraphTile graphTile = new GraphTile();
                Point p = new Point(c * multiplier, l * multiplier);
                graphTile.setLocation(p);
                graphTile.setSize(BoardTile.SIZE, BoardTile.SIZE);
                switch (board[l][c]){
                    case ShipPiece.ATTACKED_SHIP_DESTROYED_STRING:
                        graphTile.setColor(ShipPiece.COLOR_ATTACKED_SHIP_DESTROYED);
                        break;
                    case ShipPiece.ATTACKED_STRING:
                        graphTile.setColor(ShipPiece.COLOR_ATTACKED);
                        break;
                    case ShipPiece.NOT_ATTACKED_STRING:
                        graphTile.setColor(ShipPiece.COLOR_TO_SHOW);
                        break;
                    case WaterTile.NOT_VISIBLE_STRING:
                        graphTile.setColor(WaterTile.COLOR_TO_SHOW);
                        break;
                    case WaterTile.ATTACKED_OR_VISIBLE_STRING:
                        graphTile.setColor(WaterTile.COLOR_ATTACKED_OR_VISIBLE);
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
