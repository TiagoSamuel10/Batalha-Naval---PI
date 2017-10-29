package ClientSide;

import Common.BoardTile;
import Common.PlayerBoard;

import javax.swing.*;
import java.awt.*;

class GraphicalBoard extends JPanel {

    static final int BORDER = 1;
    static final Dimension SIZE = new Dimension(
            PlayerBoard.LINES * (BoardTile.SIZE + BORDER),
            PlayerBoard.COLUMNS * (BoardTile.SIZE + BORDER)
    );

    PlayerBoard _playerBoard;

    void lightItForNow(){
        _playerBoard.lightItUp();
    }

    void intoDarknessWeGo(){
        _playerBoard.lightsOut();
    }

    GraphicalBoard(PlayerBoard playerBoard){
        _playerBoard = playerBoard;
         addTiles();
    }

    private void addTiles(){
        setLayout(null);
        int multiplier = (BoardTile.SIZE + BORDER);
        for (int x = 0; x < PlayerBoard.LINES; x++) {
            for (int y = 0; y < PlayerBoard.COLUMNS; y++) {
                BoardTile bt = _playerBoard.getTileAt(x, y);
                GraphTile graphTile = new GraphTile(bt);
                Point p = new Point(bt.getCoord());
                p.setLocation(p.x * multiplier, p.y * multiplier);
                graphTile.setLocation(p);
                graphTile.setSize(BoardTile.SIZE, BoardTile.SIZE);
                add(graphTile);
            }
        }
        setSize(SIZE);
        setLocation(GameClient.GAME_BOARD_LOCATION);
    }

}
