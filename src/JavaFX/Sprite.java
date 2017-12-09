package JavaFX;

import javafx.scene.Node;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.transform.Rotate;

import java.awt.*;

abstract class Sprite extends Node {

    private Image imageToDraw;

    int x;
    int y;

    double width;
    double height;

    void setImageToDraw(Image i){
        imageToDraw = i;
        width = i.getWidth();
        height = i.getWidth();
    }

    Image getImageToDraw(){
        return imageToDraw;
    }

    void draw(GraphicsContext gc){
        gc.drawImage(imageToDraw, x, y);
    }

    void setPosition(Point p){
        setPosition(p.x, p.y);
    }

    void setPosition(int _x, int _y){
        x = _x;
        y = _y;
    }

    void drawRotated(Image image, int angles, GraphicsContext gc){
        gc.save(); // saves the current state on stack, including the current transform

        Rotate r = new Rotate(angles, x + width/2 ,  y + height/2);
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
        gc.drawImage(image, x, y);

        gc.restore(); // back to original state (before rotation)
    }

}
