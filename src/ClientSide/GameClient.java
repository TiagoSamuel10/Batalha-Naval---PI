package ClientSide;

import Common.BoardTile;
import Common.Network;
import Common.Network.*;
import Common.PlayerBoard;

import Server.Game;
import Server.Turns;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class GameClient extends JFrame{

    //TESTS ONLY
    private Turns turns;
    private Game game;
    //

    //FOR ONLINE
    private final boolean online = true;
    private Client client;
    private String myName;
    private boolean canStart;
    private boolean notReceived;

    // BEFORE MENU
    private Button goButton;
    private JTextField nameField;

    // MAIN MENU
    private Button playButton;

    //SETTING BOATS WINDOW
    private Button goToGame;
    boolean shipsSet;

    // Main Game Window
    private Button attackButton;
    private Button chatButton;
    private Button backToMenu;
    private JLabel playerTurn;
    final static Point GAME_BOARD_LOCATION = new Point(200,200);

    //ATTACK PEOPLE
    private Button attack1;
    private Button attack2;
    private JPanel players;
    private BufferedImage[] bufferedImages = new BufferedImage[2];
    private JLabel[] labelsToImage = new JLabel[3];
    private GraphicalBoard[] all = new GraphicalBoard[3];
    ////

    final static Dimension DIMENSION = new Dimension(1200, 1080);
    private Container container;
    private final static String TITLE = "GAME";
    private final static int BORDER_RIGHT_SIDE_WIDTH = 200;

    public static void main(String[] args){
        GameClient c = new GameClient();
    }

    GameClient() {

        container = getContentPane();
        setVisible(true);
        setSize(DIMENSION);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(TITLE);
        setResizable(false);
        setLayout(null);
        shipsSet = false;
        canStart = false;

        //all[1] = new GraphicalBoard(Game.getRandomPlayerBoard());
        //all[2] = new GraphicalBoard(Game.getRandomPlayerBoard());

        String input = (String)JOptionPane.showInputDialog(null, "Your name:", "Choose a name", JOptionPane.QUESTION_MESSAGE,
                null, null, "");
        if (input == null || input.trim().length() == 0) System.exit(1);

        myName = input.trim();

        client = new Client();
        client.start();

        Network.register(client);

        setCloseListener(new Runnable() {
            public void run () {
                dispose();
                client.stop();
            }
        });

        client.addListener(new Listener() {
            public void connected (Connection connection) {
                Register register = new Register();
                register.name = myName;
                client.sendTCP(register);
            }

            public void received (Connection connection, Object object) {
                if (object instanceof IsFull){
                    System.out.println(object);
                    return;
                }
                if (object instanceof StartTheGame){
                    System.out.println("GAME IS ABOUT TO START");
                    canStart = true;
                    return;
                }
                if (object instanceof Abort){
                    setMainMenu();
                    canStart = false;
                }
                if (object instanceof CanStart){
                    toGameWindow();
                }
                if (object instanceof WhoseTurn){
                    WhoseTurn whoseTurn = (WhoseTurn) object;
                    System.out.println(whoseTurn.id);
                    if(whoseTurn.id != -1){
                        setPlayerTurn(whoseTurn.id);
                    }
                }
            }
        });

        //setMainMenu();
        //setAttackWindow();
        setGameWindow();
        placeShipsScreen();

        new Thread("Connect") {
            public void run () {
                try {
                    client.connect(5000, "192.168.56.1", Network.port);
                    // Server communication after connection can go here, or in Listener#connected().
                } catch (IOException ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
        }.start();
    }

    private void setCloseListener (final Runnable listener) {
        addWindowListener(new WindowAdapter() {
            public void windowClosed (WindowEvent evt) {
                listener.run();
            }
        });
    }

    private void setMainMenu() {
        container.removeAll();
        playButton = new Button("Start game");
        playButton.setLocation(500,500);
        playButton.setSize(100,50);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(canStart){
                    placeShipsScreen();
                }
            }
        });
        container.add(playButton);
    }

    private void placeShipsScreen(){
        container.removeAll();

        ShipsPlacing shipsPlacing = new ShipsPlacing(this);

        Button b = new Button("RANDOM");

        b.setLocation(900,700);
        b.setSize(100,50);

        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shipsSet = true;
                PlayerBoard pb = Game.getRandomPlayerBoard();
                shipsPlacing.setPlayerBoard(pb);
                shipsPlacing.removeShips();
                shipsPlacing.repaint();
                shipsPlacing.validate();
            }
        });


        goToGame = new Button("PLAY");
        goToGame.setLocation(900,500);
        goToGame.setSize(100,50);
        goToGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(shipsSet){
                    client.sendTCP(shipsPlacing.getPlayerBoard().getToSend());
                    all[0] = new GraphicalBoard(shipsPlacing.getPlayerBoard());
                    toWaitingWindow();
                    //getPlayerTurn();
                }
            }
        });

        add(goToGame);
        add(b);

        container.add(shipsPlacing);
        repaint();
    }

    private void toWaitingWindow(){
        container.removeAll();
        repaint();
        validate();
    }

    private void setPlayerTurn(int index){
        playerTurn.setText("Player " + index + " is playing now");
    }

    private void toGameWindow(){
        container.removeAll();
        container.add(attackButton);
        container.add(chatButton);
        container.add(backToMenu);
        GraphicalBoard me = all[0];
        me.lightItForNow();
        container.add(me);
        container.add(playerTurn);
        repaint();
        validate();
    }

    private void setGameWindow() {
        attackButton = new Button("Attack");
        attackButton.setSize(100,100);
        attackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //all[turns.getCurrent()].intoDarknessWeGo();
                //toAttackWindow();
            }
        });
        attackButton.setLocation(1200 - BORDER_RIGHT_SIDE_WIDTH, DIMENSION.height/2 - 400);

       chatButton = new Button("Chat");
       chatButton.setSize(100,100);
       chatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
        chatButton.setLocation(1200 - BORDER_RIGHT_SIDE_WIDTH, DIMENSION.height/2);

        backToMenu = new Button("Back to Menu");
        backToMenu.setSize(100,50);
        backToMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
        backToMenu.setLocation(100, 100);

        playerTurn = new JLabel();
        playerTurn.setSize(200, 100);
        playerTurn.setLocation(500,20);

    }

    private static BufferedImage createImage(JPanel panel) {
        int w = panel.getWidth();
        int h = panel.getHeight();
        BufferedImage bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        try {
            Graphics2D g = bufferedImage.createGraphics();
            panel.paint(g);
        }catch (Exception e){
            e.printStackTrace();
            PlayerBoard pb = ((GraphicalBoard) panel)._playerBoard;
            pb.lightItUp();
            System.out.println(pb);
        }
        return bufferedImage;
    }

    private void toAttackWindow(){
        container.removeAll();

        int player1 = turns.nextPlayerIndex() + 1;
        int player2 = turns.nextPlayerIndex() + 1;

        turns.nextPlayerIndex();

        attack1.setLabel("PLAYER " + player1);
        attack2.setLabel("PLAYER " + player2);

        bufferedImages[0] = createImage(all[player1 - 1]);
        labelsToImage[0].setIcon(new ImageIcon(bufferedImages[0]));

        if(turns.remaining() > 1){
            bufferedImages[1] = createImage(all[player2 - 1]);
            labelsToImage[1].setIcon(new ImageIcon(bufferedImages[1]));
        }

        container.add(players);
        repaint();
        validate();
    }

    private void setAttackWindow(){
        attack1 = new Button("PLAYER 2");
        attack1.setLocation(350, 600);
        attack1.setSize(150, 50);
        attack1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setBoardToAttack(Integer.valueOf(attack1.getLabel().substring(7)) - 1);
            }
        });

        attack2 = new Button("PLAYER 3");
        attack2.setLocation(750, 600);
        attack2.setSize(150, 50);
        attack2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setBoardToAttack(Integer.valueOf(attack2.getLabel().substring(7)) - 1);
            }
        });

        players = new JPanel(null);

        labelsToImage[0] = new JLabel();
        labelsToImage[1] = new JLabel();

        labelsToImage[0].setLocation(25, 50);
        labelsToImage[1].setLocation(650, 50);

        labelsToImage[0].setSize(GraphicalBoard.SIZE);
        labelsToImage[1].setSize(GraphicalBoard.SIZE);

        players.add(attack1);
        players.add(attack2);
        players.add(labelsToImage[0]);
        players.add(labelsToImage[1]);
        players.setLocation(0,0);
        players.setSize(DIMENSION);

    }

    private void setBoardToAttack(int who) {
        container.removeAll();
        repaint();
        validate();

        MouseListener mouseListener = new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {
                //System.out.println(e.getPoint());
                if(!online) {
                    if(getCoordinatesFromClick(e.getPoint()) != null) {
                        all[who]._playerBoard.getAttacked(getCoordinatesFromClick(e.getPoint()));
                        all[who].repaint();
                        if(all[who]._playerBoard.isGameOver()){
                            turns.removePlayer(who);
                            JOptionPane.showMessageDialog(container,
                                    "Destroyed the guy, poor " + (who + 1) );
                            int i = turns.getCurrent();
                            setBoardToAttack(turns.nextPlayerIndex());
                            turns.setLatestIndex(i);
                            return;
                        }
                        if(!all[who]._playerBoard.gotAPieceAttacked){
                            removeMouseListener(this);
                            turns.nextPlayerIndex();
                            JOptionPane.showMessageDialog(container,
                                    "Missed. Now player " + (turns.getCurrent() + 1) + " will take over");
                            toGameWindow();
                        }
                    }
                }
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
        };

        addMouseListener(mouseListener);

        Button backToMenu = new Button("Back to Server.Game");
        backToMenu.setSize(0,0);
        backToMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toGameWindow();
            }
        });


        add(backToMenu);

        add(all[who]);

        repaint();
        validate();
    }

    @Nullable
    static Point getCoordinatesFromClick(Point point){
        //188, 211
        //208, 231
        // 20 border?
        int minX = GAME_BOARD_LOCATION.x;
        int minY = GAME_BOARD_LOCATION.y;
        int gpWidth = GraphicalBoard.SIZE.width;
        int gpHeight = GraphicalBoard.SIZE.height;
        int maxX = minX + gpWidth;
        int maxY = minY + gpHeight;
        if(point.x > maxX || point.y > maxY){
            return null;
        }
        int defX = -1;
        for (int x = 0; x < PlayerBoard.LINES; x++) {
            /*
            System.out.println("-----------");
            System.out.println("X: "+ x);
            System.out.println("FIRST: " + (minX + x * (ClientSide.GraphicalBoard.BORDER + BoardTile.SIZE)));
            System.out.println("SECOND: " + (minX + x * (BoardTile.SIZE  + ClientSide.GraphicalBoard.BORDER) + BoardTile.SIZE));
            */
            if(point.x > minX + x * (GraphicalBoard.BORDER + BoardTile.SIZE) &&
                    point.x <= minX + x  * (BoardTile.SIZE + GraphicalBoard.BORDER) + BoardTile.SIZE){
                defX = x;
                break;
            }
        }
        int defY = -1;

        for (int y = 0; y < PlayerBoard.COLUMNS; y++) {
            /*
            System.out.println("-----------");
            System.out.println("Y: "+ y);
            System.out.println("FIRST: " + (minY + y * (ClientSide.GraphicalBoard.BORDER + BoardTile.SIZE)));
            System.out.println("SECOND: " + (minY + y * (BoardTile.SIZE  + ClientSide.GraphicalBoard.BORDER) + BoardTile.SIZE));
            */

            if(point.y > minY + y * (GraphicalBoard.BORDER + BoardTile.SIZE) &&
                    point.y <= minY + y  * (BoardTile.SIZE + GraphicalBoard.BORDER) + BoardTile.SIZE){
                defY = y;
                break;
            }
        }
        if(defX  == - 1|| defY == -1){
            return null;
        }
        return new Point(defX,defY);
    }
}
