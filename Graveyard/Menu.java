import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Menu extends JFrame{

    private static Dimension dimension = new Dimension(1000, 1000);
    private Container container;
    private Game game;
    private GraphBoard[] graphBoards;

    public Menu(){
        graphBoards = new GraphBoard[3];
        game = new Game();
        container = getContentPane();
        setToDefaultMenu();
    }

    private void backToMenu(){
        container.removeAll();
        //setLayout(new GridLayout(2,1));
        //JPanel jPanel = new JPanel(new GridLayout(1,1));
        JPanel jPanel = new JPanel();
        Button b = new Button("Play");
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                //System.out.println(actionEvent.getSource());
                StartAGame();
            }
        };
        b.addActionListener(actionListener);
        b.setLocation(300,300);
        jPanel.add(b);
        container.add(jPanel);
        //container.add(b);
        validate();
    }

    private void setToDefaultMenu(){
        setSize(dimension);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);
        //container.setLayout(new GridLayout(2,1));
        setResizable(false);
        //setExtendedState(JFrame.MAXIMIZED_BOTH);
        //setUndecorated(true);
        backToMenu();
    }

    private void StartAGame() {
        container.removeAll();
        //container.setLayout(new BorderLayout());
        Button button = new Button("BACK TO MENU");
        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backToMenu();
            }
        };
        button.addActionListener(actionListener);
        button.setLocation(dimension.width - 20, dimension.height - 20);
        container.add(button);
        //container.add(button, BorderLayout.PAGE_START);
        //JPanel sidePanel = new JPanel(new GridLayout(2, 1));
        JPanel sidePanel = new JPanel();
        Button attack = new Button("ATTACK");
        ActionListener actionListener1 = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doAttack();
            }
        };
        attack.addActionListener(actionListener1);
        sidePanel.add(attack);
        Button chat = new Button("Chat");
        sidePanel.add(chat);
        container.add(sidePanel, BorderLayout.LINE_END);
        graphBoards[0] = new GraphBoard(game.getPlayerBoards()[0]);
        container.add(graphBoards[0], BorderLayout.CENTER);
        validate();
        //pack();
    }

    public void doAttack(){
        container.removeAll();
        container.setLayout(new BorderLayout());
        JPanel players = new JPanel(new GridLayout(1, 2));
        Button b1 = new Button("PLAYER 2");
        b1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showBoardToAttack();
            }
        });
        Button b2 = new Button("PLAYER 3");
        players.add(b1);
        players.add(b2);
        Button b3 = new Button("Back");
        b3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StartAGame();
            }
        });
        container.add(b3, BorderLayout.SOUTH);
        container.add(players, BorderLayout.CENTER);
        validate();
        //pack();
    }

    private void showBoardToAttack() {
        container.removeAll();
        //container.setLayout(new BorderLayout());
        container.add(graphBoards[0], BorderLayout.CENTER);
        container.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                //game.attack(0, 0, graphBoards[0].findComponentAt(e.getX(), e.getY()));
                //System.out.println(graphBoards[0].findComponentAt(e.getX(), e.getY()).isPiece());
                graphBoards[0].repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        Button b = new Button("Back");
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doAttack();
            }
        });
        container.add(b, BorderLayout.PAGE_START);
        validate();
    }

    public enum GameState{
        MainMenu,
        InGame,
        Attacking,
    }

}
