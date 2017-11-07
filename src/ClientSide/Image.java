package ClientSide;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URL;

public class Image extends JFrame {

    public static void main(String[] args) {
        Image i = new Image();
    }


    JButton botao;
    JLabel label;


    public Image() {

        //LOAD DA IMAGEM

        ClassLoader classLoader = getClass().getClassLoader();
        String file = classLoader.getResource("images/BattleShip.png").getPath();

        setTitle("BattleShip");
        setSize(1280, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setLayout(new BorderLayout());
        JLabel background = new JLabel();
        background.setIcon(new ImageIcon(file));
        background.setLayout(new FlowLayout());
        background.setSize(1300,760);
        background.setLocation(0,0);
        label = new JLabel("Botao2");
        botao = new JButton("Botao1");
        background.add(label);
        background.add(botao);
        getContentPane().add(background);
        repaint();
        validate();
    }
}


