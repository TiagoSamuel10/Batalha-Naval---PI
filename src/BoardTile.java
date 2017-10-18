import javax.swing.*;
import java.awt.*;

public abstract class BoardTile extends JPanel{

    private final static Color NOT_VISIBLE_COLOR = new Color(100,100,100);
    static final int SIZE = 50;

    Image image;
    int _x, _y;
    boolean isVisible;
    boolean attacked;

    abstract boolean isPiece();
    abstract Color getAttackedColor();
    abstract Color getVisibleColor();

    Color getNotVisibleColor(){
        return NOT_VISIBLE_COLOR;
    }

    public BoardTile(){
        isVisible = false;
    }

    void setAttacked(){
        attacked = true;
        isVisible = true;
    }

    @Override
    public void paint(Graphics g) {
        if (isVisible){
            if(attacked){
                setBackground(getAttackedColor());
            }
            else {
                setBackground(getVisibleColor());
            }
        }
        else {
            setBackground(getNotVisibleColor());
        }
        super.paint(g);
    }

}
