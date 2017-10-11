import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class GraphBoard extends JPanel{

    private int _x;
    private int _y;
    private PlayerBoard playerBoard;
    boolean canPlay;
    private ArrayList<GraphTile> graphTiles;
    BoardTile lastHit;

    public GraphBoard(PlayerBoard pb){
        setLayout(null);
        playerBoard = pb;
        canPlay = false;
        graphTiles = new ArrayList<>();
        for (int x = 0; x < PlayerBoard.LINES; x++) {
            for (int y = 0; y < PlayerBoard.COLUMNS; y++) {
                BoardTile bt = pb.getTileAt(x,y);
                GraphTile gt = new GraphTile(bt);
                graphTiles.add(gt);
                gt.setLocation(bt._x * (GraphTile.SIZE + 5), bt._y*(GraphTile.SIZE + 5));
                gt.setSize(GraphTile.SIZE,GraphTile.SIZE);
                //gt.setBackground(new Color(23 * bt._x,22 * bt._x,21 * bt._y));
                //gt.setBackground(bt.getNotVisibleColor());
                add(gt);
            }
        }
        setSize(PlayerBoard.LINES * (GraphTile.SIZE + 5),PlayerBoard.COLUMNS * (GraphTile.SIZE+ 5));
        setLocation(GUI.GAMEBOARD_LOCATION);
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(canPlay) {
                    System.out.println(findTileAt(e.getX(), e.getY()));
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }

    @Nullable
    private BoardTile findTileAt(int x, int y) {
        System.out.println("X: " + x);
        System.out.println("Y: " + y);
            Point p = new Point(x, y);
            for (GraphTile graphTile : graphTiles) {
                //System.out.println(graphTile.getLocation());
                if(graphTile.insidePoint(p)){
                    lastHit  = graphTile.getBoardTile();
                    return lastHit;
                }
            }
        return null;
    }

    public void setCanPlay(boolean canPlay) {
        this.canPlay = canPlay;
    }
}
