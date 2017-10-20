import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class GraphicalBoard extends JPanel {

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
                graphTile.setLocation(bt._x * multiplier, bt._y * multiplier);
                graphTile.setSize(BoardTile.SIZE, BoardTile.SIZE);
                add(graphTile);
            }
        }
        setSize(SIZE);
        setLocation(Client.GAME_BOARD_LOCATION);
    }

}
