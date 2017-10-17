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

public class Client extends JFrame{

    private ClientSocket clientSocket;
    private boolean readyToStart;

    private PlayerBoard me;
    private PlayerBoard[] others;

    final static Point GAMEBOARD_LOCATION = new Point(180,180);
    private final static Dimension DIMENSION = new Dimension(1200, 1080);
    private Container container;
    private final static String TITLE = "GAME";
    private final static int BORDER_RIGHT_SIDE_WIDTH = 200;

    public Client(){
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
            clientSocket = new ClientSocket(this, socket);
            clientSocket.start();
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
                clientSocket.sendMessage("WANT TO ENTER");
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

    // TODO: DEAL WITH NOT CLOSING


    @Override
    public void dispose() {
        try {
            clientSocket._socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.dispose();
    }
}
