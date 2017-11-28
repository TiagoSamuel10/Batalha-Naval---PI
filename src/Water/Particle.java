package Water;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Particle {

    public float x, y;
    public float velx, vely;
    public float orientation;
    private Image texture;
    private int width, height;

    public Particle(float x, float y, float velx, float vely, float orientation,
                    BufferedImage t) {
        this.x = x;
        this.y = y;
        this.velx = velx;
        this.vely = vely;
        this.orientation = orientation;

        width = t.getWidth();
        height = t.getHeight();

        texture = t.getScaledInstance(width / 2, height / 2,
                BufferedImage.TYPE_INT_ARGB);

    }

    public void render(Graphics g) {

        g.setColor(Color.BLUE);

        Graphics2D g2 = (Graphics2D)g;

        AffineTransform at = AffineTransform.getTranslateInstance(x, y);

        at.rotate(orientation - Math.PI / 2,
                width / 4, height / 4);

        g2.drawImage(texture, at, null);

    }

}
