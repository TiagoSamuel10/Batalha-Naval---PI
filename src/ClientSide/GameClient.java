package ClientSide;

import Common.BoardTile;
import Common.Network;
import Common.Network.*;
import Common.PlayerBoard;

import Server.Game;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

public class GameClient extends JFrame{

    final static Point GAME_BOARD_LOCATION = new Point(200,200);
    final static Dimension DIMENSION = new Dimension(1280, 720);
    private final static String TITLE = "GAME";
    private final static int BORDER_RIGHT_SIDE_WIDTH = 200;

    //FOR ONLINE
    private final boolean online = false;
    boolean shipsSet;
    private Client client;
    private String myName;
    private final String address = "82.154.150.145";

    // MAIN MENU
    private Button playButton;
    private JTextField nameField;

    //WAITING WINDOW
    private String[] namesArray;
    private JList<String> names;

    //SETTING BOATS WINDOW
    private Button goToGame;

    // Main Game Window
    private Button attackButton;
    private Button chatButton;
    private Button backToMenu;
    private JLabel playerTurn;

    //ATTACK PEOPLE
    private Button attack1;
    private Button attack2;
    private JPanel players;
    private BufferedImage[] bufferedImages = new BufferedImage[2];
    private JLabel[] labelsToImage = new JLabel[3];
    private GraphicalBoard[] all = new GraphicalBoard[3];

    //FRAME
    private Container container;

    GameClient() {

        container = getContentPane();
        setVisible(true);
        setSize(DIMENSION);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(TITLE);
        setResizable(false);
        setLayout(null);
        setLocationRelativeTo(null);
        setLocation(0,0);

        shipsSet = false;
        serverConfigurations();

        setMainMenu();
        setAttackWindow();
        setGameWindow();

        toMainMenu();
        //toPlaceShipsScreen();

        names = new JList<>();
        names.setModel(new DefaultListModel<>());
        names.setSize(500, 500);
        names.setLocation(100,100);

    }

    public static void main(String[] args){
        GameClient c = new GameClient();
    }

    private static InetAddress getMeIPV4() throws UnknownHostException {
        try {
            InetAddress closestOneFound = null;
            // GET AND ITERATE ALL NETWORK CARDS
            Enumeration networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) networkInterfaces.nextElement();
                // GO THROUGH ALL IP ADDRESSES OF THIS CARD...
                Enumeration inetAddresses = networkInterface.getInetAddresses();
                while(inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = (InetAddress) inetAddresses.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        if (inetAddress.isSiteLocalAddress()) {
                            //YEAH BOY, FOUND IT
                            return inetAddress;
                        } else if (closestOneFound == null) {
                            //FOUND ONE, MIGHT NOT BE WHAT WE WANT
                            //BUT LET'S STORE IT IN CASE WE DON'T FIND EXACTLY WHAT WE WANT
                            closestOneFound = inetAddress;
                        }
                    }
                }
            }
            if (closestOneFound != null) {
                // NOT IPV4, but will have to do
                return closestOneFound;
            }
            //FOUND NOTHING
            //WILL TRY THE JDK ONE
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                throw new UnknownHostException("The JDK INetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress;
        } catch (Exception e) {
            UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
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
        }
        return bufferedImage;
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

    //region serverstuff

    private void serverConfigurations(){
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
                /*
                Register register = new Register();
                register.name = myName;
                client.sendTCP(register);
                */
            }

            public void received (Connection connection, Object object) {
                if (object instanceof IsFull){
                    System.out.println(object);
                    return;
                }
                if (object instanceof StartTheGame){
                    toPlaceShipsScreen();
                }
                if (object instanceof Abort){
                    setMainMenu();
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
                if (object instanceof ConnectedPlayers){
                    ConnectedPlayers connectedPlayers = (ConnectedPlayers) object;
                    namesArray = connectedPlayers.names;
                    updateNames();
                    repaint();
                    validate();
                }
                if (object instanceof YourBoardToPaint){
                    all[0] = new GraphicalBoard(((YourBoardToPaint)object).board);
                }
                if (object instanceof EnemiesBoardsToPaint){
                    all[1] = new GraphicalBoard(((EnemiesBoardsToPaint)object).board1);
                    all[2] = new GraphicalBoard(((EnemiesBoardsToPaint)object).board2);
                }
            }
        });


        if(online) {
            new Thread("Connect") {
                public void run() {
                    try {
                        client.connect(5000, address, Network.port);
                        // Server communication after connection can go here, or in Listener#connected().
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        System.exit(1);
                    }
                }
            }.start();
        }
    }

    private void setCloseListener (final Runnable listener) {
        addWindowListener(new WindowAdapter() {
            public void windowClosed (WindowEvent evt) {
                listener.run();
            }
        });
    }

    //endregion

    private void toMainMenu(){

        container.removeAll();
        container.add(playButton);
        container.add(nameField);

        //THIS WILL DO FOR NOW

        String imagePath = "assets/images/BattleShip.png";

        JLabel background = new JLabel();
        background.setIcon(new ImageIcon(imagePath));
        background.setLayout(new FlowLayout());
        background.setSize(DIMENSION);
        background.setLocation(0,0);
        container.add(background);
        repaint();
        validate();
    }

    private void setMainMenu() {

        playButton = new Button("Start game");
        playButton.setLocation(500,500);
        playButton.setSize(100,50);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myName = nameField.getText();
                System.out.println(myName);
                Register r = new Register();
                r.name = myName;
                try {
                    r.adress = getMeIPV4().toString();
                } catch (UnknownHostException e1) {
                    e1.printStackTrace();
                }
                client.sendTCP(r);
                toWaitingWindow();
            }
        });

        nameField = new JTextField();
        nameField.setLocation(500, 450);
        nameField.setSize(100,25);
        nameField.setTransferHandler(null);
        nameField.addKeyListener(new KeyListener() {

            private final int maxChars = 10;

            @Override
            public void keyTyped(KeyEvent e) {
                if(nameField.getText().length() >= maxChars){
                    e.consume();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    private void toPlaceShipsScreen(){
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
                if(shipsSet && !online){
                    toGameWindow();
                }
                if(shipsSet && online){
                    client.sendTCP(shipsPlacing.getPlayerBoard().getToSend());
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

    private void updateNames(){
        DefaultListModel<String> model = (DefaultListModel<String>) names.getModel();
        model.removeAllElements();
        for(String s : namesArray){
            System.out.println("Received player from server list " + s);
            model.addElement(s);
        }
    }

    private void toWaitingWindow(){
        container.removeAll();
        add(names);
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
        me.visibleForPlayer();
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
        playerTurn.setLocation(100,20);

    }

    private void toAttackWindow(){
        container.removeAll();

        /*

        //TODO: Labels correctly

        attack1.setLabel("PLAYER " + "1");
        attack2.setLabel("PLAYER " + "2");

        bufferedImages[0] = createImage(all[player1 - 1]);
        labelsToImage[0].setIcon(new ImageIcon(bufferedImages[0]));


        if(turns.remaining() > 1){
            bufferedImages[1] = createImage(all[player2 - 1]);
            labelsToImage[1].setIcon(new ImageIcon(bufferedImages[1]));
        }

        */

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
}
