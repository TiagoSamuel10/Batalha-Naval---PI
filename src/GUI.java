import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GUI extends JFrame implements WindowListener {

    private boolean readyToStart;
    private PlayerSocket thisSocket;
    private PrintWriter toServer;
    private BufferedReader fromServer;
    //private Scanner sc;

    final static Point GAMEBOARD_LOCATION = new Point(180,180);
    private final static Dimension DIMENSION = new Dimension(1200, 1080);
    private Container container;
    private final static String TITLE = "GAME";
    private final static int BORDER_RIGHT_SIDE_WIDTH = 200;

    // TODO turns

    public GUI(){
        container = getContentPane();
        setVisible(true);
        setSize(DIMENSION);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(TITLE);
        setResizable(false);
        setLayout(null);
        setMainMenu();
        readyToStart = false;
        try {
            Socket socket = new Socket("localhost", 1000);
            toServer = new PrintWriter(socket.getOutputStream(), true);
            fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setMainMenu() {
        Button playButton = new Button("Start game");
        playButton.setLocation(500,500);
        playButton.setSize(100,50);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toServer.println("I'm trying to enter");
                JOptionPane.showMessageDialog(container,"Not enough players.");
            }
        });
        add(playButton);
    }

    private void setGameWindow() {
        container.removeAll();
        Button attackButton = new Button("Attack");
        attackButton.setSize(100,100);
        attackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAttackWindow();
            }
        });
        attackButton.setLocation(1200 - BORDER_RIGHT_SIDE_WIDTH, DIMENSION.height/2 - 400);
        add(attackButton);

        Button chatButton = new Button("Chat");
        chatButton.setSize(100,100);
        chatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
        chatButton.setLocation(1200 - BORDER_RIGHT_SIDE_WIDTH, DIMENSION.height/2);
        add(chatButton);

        Button backToMenu = new Button("Back to Menu");
        backToMenu.setSize(100,50);
        backToMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
        backToMenu.setLocation(100, 100);
        add(backToMenu);

        //PlayerBoard graphBoard = game.getPlayerBoards()[0];
        //add(graphBoard);
        repaint();
        validate();

    }

    private void setAttackWindow(){
        container.removeAll();
        repaint();
        validate();
        Button b1 = new Button("PLAYER 2");
        b1.setSize(100,50);
        b1.setLocation(250,500);
        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setBoardToAttack();
            }
        });

        Button b2 = new Button("PLAYER 3");
        b2.setSize(100,50);
        b2.setLocation(500,500);

        JPanel players = new JPanel();
        players.add(b1);
        players.add(b2);
        players.setLocation(0,0);
        players.setSize(DIMENSION);

        container.add(players);

    }

    private void setBoardToAttack() {
        container.removeAll();
        repaint();
        validate();
        //PlayerBoard graphBoard = game.getPlayerBoards()[0];
        //graphBoard.setGettingAttacked(true);
        //add(graphBoard);

        Button b2 = new Button("PLAYER 3");
        b2.setSize(100,50);
        b2.setLocation(1000,1000);
        b2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //game.getPlayerBoards()[0].getAttacked(4,5);
                //game.getPlayerBoards()[0].lightItUp();
            }
        });

        //add(b2);

        repaint();
        validate();
    }

    //region WindowListener

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {
        //thisSocket.close();
    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    //endregion WindowListener
}
