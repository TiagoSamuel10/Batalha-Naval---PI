import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.Socket;

public class Client extends JFrame{

    //TESTS ONLY
    private Turns turns;
    //

    private final boolean online = false;
    private ClientSocket clientSocket;
    private boolean readyToStart;

    // MAIN MENU
    private Button playButton;

    // Main Game Window
    private Button attackButton;
    private Button chatButton;
    private Button backToMenu;
    private JLabel playerTurn;

    //ATTACK PEOPLE
    private Button attack1;
    private Button attack2;
    private JPanel players;
    private BufferedImage[] bufferedImages = new BufferedImage[3];
    private JLabel[] labelsToImage = new JLabel[3];
    private GraphicalBoard[] all = new GraphicalBoard[3];
    ////

    final static Point GAME_BOARD_LOCATION = new Point(200,200);
    private final static Dimension DIMENSION = new Dimension(1200, 1080);
    private Container container;
    private final static String TITLE = "GAME";
    private final static int BORDER_RIGHT_SIDE_WIDTH = 200;

    public static void main(String[] args){
        Client c = new Client();
    }

    public Client(){
        container = getContentPane();
        setVisible(true);
        setSize(DIMENSION);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(TITLE);
        setResizable(false);
        setLayout(null);
        readyToStart = false;
        if(online) {
            try {
                Socket socket = new Socket("localhost", 1234);
                clientSocket = new ClientSocket(this, socket);
                clientSocket.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            turns = new Turns();
            turns.addPlayer(0);
            turns.addPlayer(1);
            turns.addPlayer(2);
            all[0] = new GraphicalBoard(Game.getRandomPlayerBoard());
            all[1] = new GraphicalBoard(Game.getRandomPlayerBoard());
            all[2] = new GraphicalBoard(Game.getRandomPlayerBoard());
        }

        setGameWindow();
        setAttackWindow();
        setMainMenu();


    }

    private void setMainMenu() {
        container.removeAll();
        playButton = new Button("Start game");
        playButton.setLocation(500,500);
        playButton.setSize(100,50);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toGameWindow();
            }
        });
        container.add(playButton);
    }

    private void toGameWindow(){
        container.removeAll();
        container.add(attackButton);
        container.add(chatButton);
        container.add(backToMenu);
        GraphicalBoard me = all[turns.getCurrent()];
        me.lightItForNow();
        container.add(me);
        playerTurn.setText("Player " + (turns.getCurrent() + 1) + " is playing now");
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
                all[turns.getCurrent()].intoDarknessWeGo();
                toAttackWindow();
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

        playerTurn = new JLabel("Player " + (turns.getCurrent() + 1)+ " is playing now");
        playerTurn.setSize(200, 100);
        playerTurn.setLocation(500,20);

    }

    private static BufferedImage createImage(JPanel panel) {
        int w = panel.getWidth();
        int h = panel.getHeight();
        BufferedImage bufferedImage = new BufferedImage(w , h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bufferedImage.createGraphics();
        panel.paint(g);
        return bufferedImage;
    }

    private void toAttackWindow(){
        container.removeAll();

        int player1 = turns.nextPlayerIndex() + 1;
        int player2 = turns.nextPlayerIndex() + 1;

        turns.nextPlayerIndex();

        attack1.setLabel("PLAYER " + player1);
        attack2.setLabel("PLAYER " + player2);

        labelsToImage[0].setIcon(new ImageIcon(bufferedImages[player1 - 1]));
        labelsToImage[0].setLocation(25, 50);
        labelsToImage[0].setSize(GraphicalBoard.SIZE);

        if(turns.remaining() > 1){
            labelsToImage[1].setSize(GraphicalBoard.SIZE);
            labelsToImage[1].setIcon(new ImageIcon(bufferedImages[player2 - 1]));
            labelsToImage[1].setLocation(600, 50);
        }


        //players.add(labelsToImage[0]);
        //players.add(labelsToImage[1]);

        container.add(players);
        players.repaint();
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

        bufferedImages[0] = createImage(all[0]);
        bufferedImages[1] = createImage(all[1]);
        bufferedImages[2] = createImage(all[2]);

        labelsToImage[0] = new JLabel(new ImageIcon(bufferedImages[1]));
        labelsToImage[1] = new JLabel(new ImageIcon(bufferedImages[2]));

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
                        bufferedImages[who] = createImage(all[who]);
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

        Button backToMenu = new Button("Back to Game");
        backToMenu.setSize(100,50);
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
    private Point getCoordinatesFromClick(Point point){
        //188, 211
        //208, 231
        // 20 border?
        int minX = GAME_BOARD_LOCATION.x + 20;
        int minY = GAME_BOARD_LOCATION.y + 30;
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
            System.out.println("FIRST: " + (minX + x * (GraphicalBoard.BORDER + BoardTile.SIZE)));
            System.out.println("SECOND: " + (minX + x * (BoardTile.SIZE  + GraphicalBoard.BORDER) + BoardTile.SIZE));
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
            System.out.println("FIRST: " + (minY + y * (GraphicalBoard.BORDER + BoardTile.SIZE)));
            System.out.println("SECOND: " + (minY + y * (BoardTile.SIZE  + GraphicalBoard.BORDER) + BoardTile.SIZE));
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

    // TODO: DEAL WITH NOT CLOSING

    @Override
    public void dispose() {
        if(online) {
            try {
                clientSocket._socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.dispose();
    }
}
