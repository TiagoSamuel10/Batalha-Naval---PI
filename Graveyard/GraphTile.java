import javax.swing.*;
import java.awt.*;

public class GraphTile extends JPanel {

    static final int SIZE = 50;
    private BoardTile boardTile;

    public GraphTile(BoardTile bt){
        boardTile = bt;
    }

    @Override
    public String toString() {
        return boardTile.toString();
    }

    public BoardTile getBoardTile() {
        return boardTile;
    }

    public Point[] beetween(){
        Point to = new Point(getLocation().x + SIZE, getLocation().x + SIZE);
        return new Point[]{getLocation(), to};
    }

    public boolean insidePoint(Point point){
        boolean insideX = point.x > getLocation().x && point.x < getLocation().x + SIZE;
        boolean insideY = point.y > getLocation().y && point.y < getLocation().y + SIZE;
        return insideX && insideY;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (boardTile.isVisible){
            setBackground(boardTile.getHitColor());
        }
        else {
            setBackground(boardTile.getNotVisibleColor());
        }
    }
}
