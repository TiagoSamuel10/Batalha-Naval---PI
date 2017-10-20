import javax.swing.*;
import java.awt.*;

public class GraphTile extends JPanel {

    private BoardTile _boardTile;

    public GraphTile(BoardTile boardTile){
        _boardTile = boardTile;
    }

    @Override
    public void paint(Graphics g) {
        if (_boardTile.isVisible){
            if(_boardTile.attacked){
                setBackground(_boardTile.getAttackedColor());
            }
            else {
                setBackground(_boardTile.getVisibleColor());
            }
        }
        else {
            setBackground(_boardTile.getNotVisibleColor());
        }
        super.paint(g);
    }

}
