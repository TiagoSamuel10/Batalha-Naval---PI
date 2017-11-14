package ClientSide;

import javax.swing.*;
import java.awt.*;
import Common.*;

public class GraphTile extends JPanel {

    private BoardTile boardTile;

    private Color toPaint;

    public GraphTile(){
        this(new Color(0,0,0));
    }

    public GraphTile(Color _toPaint){
        setColor(_toPaint);
    }

    public void setColor(Color _toPaint){
        toPaint = _toPaint;
    }

    public GraphTile(BoardTile _boardTile){ boardTile = _boardTile;
    }

    public void oldPaint(Graphics g){
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

    @Override
    public void paint(Graphics g) {
        setBackground(toPaint);
        super.paint(g);
    }

}
