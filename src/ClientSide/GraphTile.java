package ClientSide;

import javax.swing.*;
import java.awt.*;
import Common.*;

public class GraphTile extends JPanel {

    private BoardTile boardTile;

    public GraphTile(BoardTile _boardTile){ boardTile = _boardTile;
    }

    @Override
    public void paint(Graphics g) {
        if (boardTile.isVisible()){
            if(boardTile.isAttacked()){
                setBackground(boardTile.getAttackedColor());
            }
            else {
                setBackground(boardTile.getVisibleColor());
            }
        }
        else {
            setBackground(boardTile.getNotVisibleColor());
        }
        super.paint(g);
    }

}
