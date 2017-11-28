package Water;

import javax.swing.JFrame;

public class Window extends JFrame{

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private GamePanel GP = new GamePanel();

    public static final int WIDTH = 800, HEIGHT = 600;

    public Window() {
        setTitle("Water Effect");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        add(GP);
        addMouseMotionListener(GP);
        addMouseListener(GP);
        setVisible(true);

    }


    public static void main(String[] args) {
        new Window();
    }

}

