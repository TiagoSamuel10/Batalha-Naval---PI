package Water;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel implements MouseMotionListener, MouseListener{

    private static final long serialVersionUID = 1L;

    public static int mouseX, mouseY;

    private Timer looper;

    private final Color sky = new Color(105, 130, 220);

    private final Color water = new Color(100, 255, 235);

    private int num_springs = 200;

    private int y_offset = Window.HEIGHT / 4;

    private Spring[] springs = new Spring[num_springs];

    private final float spread = 0.25f;

    private final float gravity = 0.3f;

    private BufferedImage droplet;

    private Image rock;

    private List<Particle> particles = new ArrayList<Particle>();

    private List<Rock> rocks = new ArrayList<Rock>();

    public GamePanel() {
        looper = new Timer(1000 / 200, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                update();
                repaint();
            }});

        for(int n = 0; n < springs.length; n++) {

            float t = (float) n / (float) springs.length;

            springs[n] = new Spring(t * Window.WIDTH, y_offset - 10);

        }

        try {
            File file = new File("assets/images/Droplet.png");
            droplet = ImageIO.read(file);
            //droplet = ImageIO.read(GamePanel.class.getResource("/Droplet.png"));
            file = new File("assets/images/rock.png");
            rock = ImageIO.read(file);
            //rock = ImageIO.read(GamePanel.class.getResource("/rock.png"));
            rock = rock.getScaledInstance(rock.getWidth(null), rock.getHeight(null),
                    BufferedImage.TYPE_INT_ARGB);
        } catch (IOException e1) {
            e1.printStackTrace();
        }


        looper.start();
    }

    public void update() {

        for(int i = 0; i < springs.length; i++) springs[i].update();

        float[] leftDeltas = new float[springs.length];
        float[] rightDeltas = new float[springs.length];

        for(int j = 0; j < 8; j++) {

            for(int i = 0; i < springs.length; i++) {

                if(i > 0)
                {
                    leftDeltas[i] = spread * (springs[i].posy - springs[i - 1].posy);
                    springs[i - 1].speed += leftDeltas[i];
                }

                if(i < springs.length - 1) {

                    rightDeltas[i] = spread * (springs[i].posy - springs[i + 1].posy);
                    springs[i + 1].speed += rightDeltas[i];
                }
            }

            for(int i = 0; i < springs.length; i++) {

                if(i > 0)
                    springs[i - 1].posy += leftDeltas[i];
                if(i < springs.length - 1)
                    springs[i + 1].posy += rightDeltas[i];

            }

        }

        for(int i = 0; i < particles.size(); i++) {

            Particle p = particles.get(i);

            p.vely += gravity;

            p.x += p.velx;
            p.y += p.vely;

            p.orientation = (float) Math.atan2(p.vely, p.velx);

            if(p.x < 0 || p.x > Window.WIDTH || p.y > y_offset)
                particles.remove(i);

        }

        for(int i = 0; i < rocks.size(); i++)
            rocks.get(i).update();

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(sky);

        g.fillRect(0, 0, Window.WIDTH, Window.HEIGHT);

        g.setColor(water);

        Graphics2D g2 = (Graphics2D)g;

        for(int i = 0; i < springs.length - 1; i++) {

            int[] xPoints = new int[] {(int)springs[i].posx, (int)springs[i + 1].posx,
                    (int)springs[i+1].posx, (int)springs[i].posx};

            int[] yPoints = new int[] {(int)springs[i].posy, (int)springs[i + 1].posy,
                    Window.HEIGHT, Window.HEIGHT};

            /*GradientPaint gp = new GradientPaint(0, Window.HEIGHT, new Color(50, 90, 190),
                    0, 0, water);
                    */

            g2.setPaint(water);

            if(i == 1 || i == 2 || i == 3 || i == 4){
                System.out.println("E");
                g2.setColor(new Color(2, 25, 65));
            }

            g2.fillPolygon(xPoints, yPoints, 4);

        }

        for(Particle p: particles)
            p.render(g);

        for(int i = 0; i < rocks.size(); i++)
            rocks.get(i).render(g2);

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    public void splash(float x, float speed) {

        float bestDistanceSoFar = Window.WIDTH;

        int index = springs.length / 2;

        for(int i = 0; i < springs.length; i++) {

            float distance = Math.abs(springs[i].posx - x);

            if(distance < bestDistanceSoFar) {
                bestDistanceSoFar = distance;
                index = i;
            }
        }

        springs[index].speed = speed*20;

        for(int i = 0; i < 20; i++) {

            float velx = (float) (Math.random()*speed - speed / 2);

            float vely = (float)(-Math.random()*speed);

            particles.add(new Particle(springs[index].posx, springs[index].posy,
                    velx, vely, (float)Math.atan2(vely, velx), droplet));

        }


    }

    @Override
    public void mousePressed(MouseEvent e) {
        rocks.add(new Rock(e.getX(), e.getY(), rock, this));
    }

    public List<Rock> getRocks(){return rocks;}

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
    @Override
    public void mouseClicked(MouseEvent e) {
    }
    @Override
    public void mouseDragged(MouseEvent e) {
    }
}
