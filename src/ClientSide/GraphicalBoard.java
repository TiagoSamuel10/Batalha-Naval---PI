package ClientSide;

import Common.BoardTile;
import Common.PlayerBoard;
import Common.ShipPiece;
import Common.WaterTile;

import javax.swing.*;
import java.awt.*;

import static Common.PlayerBoard.COLUMNS;
import static Common.PlayerBoard.LINES;

class GraphicalBoard extends JPanel {

    static final int BORDER = 1;
    static final Dimension SIZE = new Dimension(
            LINES * (BoardTile.SIZE + BORDER),
            COLUMNS * (BoardTile.SIZE + BORDER)
    );

    String[][] currentBoard;

    void visibleForPlayer(){
        for (int l = 0; l < LINES; l++) {
            for (int c = 0; c < COLUMNS; c++) {
                if(currentBoard[l][c].getBytes()[0] == 'W'){
                    getComponent(l * COLUMNS + c).setBackground(WaterTile.COLOR_TO_SHOW);
                }
                else{
                    getComponent(l * COLUMNS + c).setBackground(ShipPiece.COLOR_TO_SHOW);
                }
            }
        }
    }

    GraphicalBoard (String[][] board){

        currentBoard = board;

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
                        graphTile.setColor(ShipPiece.ATTACKED_COLOR);
                        break;
                    case ShipPiece.ATTACKED_STRING:
                        graphTile.setColor(ShipPiece.ATTACKED_COLOR);
                        break;
                    case ShipPiece.NOT_ATTACKED_STRING:
                    case WaterTile.NOT_VISIBLE_STRING:
                        graphTile.setColor(BoardTile.NOT_VISIBLE_COLOR);
                        break;
                    case WaterTile.ATTACKED_OR_VISIBLE_STRING:
                        graphTile.setColor(WaterTile.ATTACKED_COLOR);
                        break;
                }
                add(graphTile);
            }
        }

        setSize(SIZE);
        setLocation(GameClient.GAME_BOARD_LOCATION);
    }

}
