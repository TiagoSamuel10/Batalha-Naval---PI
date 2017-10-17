import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * BoardTile é um dado quadrado do campo de batalha; Tem o seu x e o seu y
 * <p>
 *     Porquê Abstracto? Não queremos criar coisas do tipo BoardTile; queremos ou criar {@link WaterTile} ou um pedaço de navio {@link ShipPiece}
 * </p>
 * O {@link #isPiece()} é abstracto e depois o {@link WaterTile} vai implementá-lo para retornar false, enquanto o {@link ShipPiece}
 * vai faze-lo returnar true;
 * O {@link #gotHit()} é só para ajudar neste caso
 * <p>
 *     o isVisible é se já foi disparado contra;
 * </p>
 */

public abstract class BoardTile extends JPanel{

    static final int SIZE = 50;

    Image image;
    int _x, _y;
    boolean isVisible;

    abstract boolean isPiece();
    abstract String gotHit();
    abstract Color getNotVisibleColor();
    abstract Color getHitColor();

    public BoardTile(){
        isVisible = false;
        addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String property = evt.getPropertyName();

                if ("isVisible".equals(property)) {
                    getRootPane().repaint();
                    getParent().repaint();
                }
            }
        });
    }

    void setAttacked(){
        isVisible = true;
    }

    //TODO is there a better way to do this?

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
        if (isVisible){
            setBackground(getHitColor());
        }
        else {
            setBackground(getNotVisibleColor());
        }
        super.paint(g);
    }

}
