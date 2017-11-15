package ClientSide;

import javax.swing.*;
import java.awt.*;

public class GraphTile extends JPanel {

    private int l;
    private int c;
    private Color toPaint;

    GraphTile(){
        this(new Color(0,0,0));
    }

    GraphTile(Color _toPaint){
        setColor(_toPaint);
    }

    void setL(int l) {
        this.l = l;
    }

    int getL() {
        return l;
    }

    void setC(int c) {
        this.c = c;
    }

    int getC() {
        return c;
    }

    void setColor(Color _toPaint){
        toPaint = _toPaint;
    }

    @Override
    public void paint(Graphics g) {
        setBackground(toPaint);
        super.paint(g);
    }
}
