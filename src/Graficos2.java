import com.esotericsoftware.jsonbeans.Test;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Graficos2 extends JFrame {
        public static void main(String[] args) throws IOException {
            Graficos2 g = new Graficos2();
            g.setVisible(true);
            g.setSize(1200,100);
            BufferedImage buttonIcon = ImageIO.read(new File("C:\\Users\\pedro\\Desktop\\Start.png"));
            JButton button = new JButton(new ImageIcon(buttonIcon));
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setContentAreaFilled(false);
            g.add(button);

        }
}
