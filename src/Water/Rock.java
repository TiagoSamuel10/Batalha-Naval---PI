package Water;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;

public class Rock {

    private Image texture;

    public float x, y;

    public float speed;

    private final float gravity = 0.3f;

    private final int y_offset = Window.HEIGHT / 2;

    private GamePanel gp;

    private boolean splash = false;

    public Rock(int cx, int cy, Image t, GamePanel g) {
        x = cx;
        y = cy;
        speed = 0;
        gp = g;
        texture = t;
    }

    public void update() {

        speed += gravity;

        y += speed;

        if(y > y_offset - 20 && !splash) {
            gp.splash(x, speed);
            splash = true;
        }

        if(y > y_offset + 20)
            speed /= 2;

        if(y > Window.HEIGHT) {
            gp.getRocks().remove(this);
        }


    }

    public void render(Graphics2D g2) {
        float alpha = ((y - y_offset) / y_offset) / 2;

        if(splash && alpha <= 1) {

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    0.5f - alpha));
        }


        g2.drawImage(texture, (int)x, (int)y, null);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
    }

}
