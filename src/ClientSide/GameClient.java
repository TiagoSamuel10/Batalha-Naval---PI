package ClientSide;

import Common.BoardTile;
import Common.Network;
import Common.Network.*;
import Common.PlayerBoard;

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

    final static Point GAME_BOARD_LOCATION = new Point(75,75);
    final static Dimension DIMENSION = new Dimension(1280, 720);
    private final static String TITLE = "GAME";
    private final static int BORDER_RIGHT_SIDE_WIDTH = 150;

    //FOR ONLINE
    private final boolean online = true;
    boolean shipsSet;
    private Client client;
    private String myName;
    private final String address = "localhost";

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
    private JPanel playersPanel;
    private boolean inAttackWindow;
    private boolean iCanAttack;

    private Button backToGame;

    //GRAPHICS

    private GraphicalBoard me;

    private EnemyLocal lastAttacked;

    private EnemyLocal ene1;
    private EnemyLocal ene2;

    /*

    private GraphicalBoard ene1;
    private BufferedImage ene1Buffered;
    private JLabel ene1ImageToShow;

    private GraphicalBoard ene2;
    private BufferedImage ene2Buffered;
    private JLabel ene2ImageToShow;

    */
    
    //FRAME
    private Container container;

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
        setLocationRelativeTo(null);
        setLocation(0,0);

        shipsSet = false;
        iCanAttack = false;
        serverConfigurations();

        setMainMenu();

        if(online) {
            inAttackWindow = false;
            toMainMenu();
            ene1 = new EnemyLocal();
            ene2 = new EnemyLocal();
        }
        else{
            toPlaceShipsScreen();
        }

        setChooseAttackWindow();
        setGameWindow();

        names = new JList<>();
        names.setModel(new DefaultListModel<>());
        names.setSize(500, 500);
        names.setLocation(100,100);
    }

    //region other stuff

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

    //endregion

    //region Server Stuff

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
                }
                if (object instanceof ReadyForShips){
                    System.out.println("TO SHIP SCREEN");
                    toPlaceShipsScreen();
                }
                if (object instanceof Abort){
                    setMainMenu();
                }
                if (object instanceof CanStart){
                    System.out.println("STARTING THE GAME");
                    toGameWindow();
                }
                if (object instanceof WhoseTurn){
                    WhoseTurn whoseTurn = (WhoseTurn) object;
                    System.out.println(whoseTurn.name);
                    iCanAttack = false;
                    setTurnLabel(whoseTurn.name);
                }
                if (object instanceof ConnectedPlayers){
                    ConnectedPlayers connectedPlayers = (ConnectedPlayers) object;
                    namesArray = connectedPlayers.names;
                    updateNames();
                    repaint();
                    validate();
                }

                if (object instanceof OthersSpecs){
                    OthersSpecs othersSpecs = ((OthersSpecs) object);


                    //ene1.localID = 1;
                    ene1.serverID = othersSpecs.ene1;
                    ene1.alive = true;
                    ene1.name = othersSpecs.ene1n;

                    //ene2.localID = 2;
                    ene2.serverID = othersSpecs.ene2;
                    ene2.alive = true;
                    ene2.name = othersSpecs.ene2n;

                    System.out.println(myName + " has as 1(index): " + ene1.serverID);
                    System.out.println(myName + " has as 2(index): " + ene2.serverID);

                }

                if (object instanceof YourBoardToPaint){
                    System.out.println("MY BOARD TO PAINT");
                    remove(me);
                    me = new MyGraphBoard(((YourBoardToPaint)object).board);
                    container.add(me);
                    repaint();
                }
                if (object instanceof EnemiesBoardsToPaint){
                    System.out.println("ENEMIES BOARDS TO PAINT");
                    ene1.b = new GraphicalBoard(((EnemiesBoardsToPaint) object).board1);
                    ene2.b = new GraphicalBoard(((EnemiesBoardsToPaint) object).board2);
                    setLabels();
                }

                if (object instanceof EnemyBoardToPaint){
                    EnemyBoardToPaint enemyBoardToPaint = (EnemyBoardToPaint) object;
                    updateEnemyBoard(enemyBoardToPaint.id, enemyBoardToPaint.newAttackedBoard);
                    System.out.println("ENEMY BOARD TO PAINT WITH INDEX " + enemyBoardToPaint.id);
                }

                if (object instanceof AnAttackResponse){
                    AnAttackResponse response = (AnAttackResponse) object;
                    updateEnemyBoard(lastAttacked, response.newAttackedBoard);
                    iCanAttack = response.again;
                }

                if (object instanceof YourTurn){
                    iCanAttack = true;
                    setTurnLabel("ME");
                }

                if (object instanceof YouDead){
                    JOptionPane.showMessageDialog(container, "You've died");
                    toMainMenu();
                }

                if (object instanceof PlayerDied){
                    removeEnemyBoard(((PlayerDied) object).who);
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

    //region MainMenu

    private void setMainMenu() {

        playButton = new Button("Start game");
        playButton.setLocation(500,500);
        playButton.setSize(100,50);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myName = nameField.getText();
                if(myName.equals("")){
                    //TODO: WARN THE PLAYER
                    return;
                }
                System.out.println(myName);
                Register r = new Register();
                r.name = myName;
                try {
                    r.address = getMeIPV4().toString();
                } catch (UnknownHostException e1) {
                    e1.printStackTrace();
                }
                client.sendTCP(r);
                setTitle(getTitle() + myName);
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


    private void toMainMenu(){

        container.removeAll();
        container.add(playButton);

        nameField.requestFocus();
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

    //endregion

    //region PlaceShips

    private void toPlaceShipsScreen(){
        container.removeAll();
        ShipsPlacing shipsPlacing = new ShipsPlacing(this);

        Button b = new Button("RANDOM");

        b.setLocation(700,500);
        b.setSize(100,50);

        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shipsSet = true;
                PlayerBoard pb = PlayerBoard.getRandomPlayerBoard();
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
                    me = new MyGraphBoard(shipsPlacing.getPlayerBoard().getToSendToPaint());
                    client.sendTCP(shipsPlacing.getPlayerBoard().getToSend());
                    //System.out.println(Arrays.deepToString(shipsPlacing.getPlayerBoard().getToSend()));
                    //toGameWindow();
                }
            }
        });

        add(goToGame);
        add(b);

        container.add(shipsPlacing);
        repaint();
    }

    //endregion

    private void updateEnemyBoard(int whose, String[][] toPaint){
        if(whose == ene1.serverID){
            container.remove(ene1.b);
            ene1.b = new GraphicalBoard(toPaint);
            if(inAttackWindow){
                container.add(ene1.b);
            }
        }
        else if(whose == ene2.serverID){
            container.remove(ene2.b);
            ene2.b = new GraphicalBoard(toPaint);
            if(inAttackWindow){
                container.add(ene2.b);
            }
        }
        repaint();
    }

    private void updateEnemyBoard(EnemyLocal enemyLocal, String[][] toPaint){
        container.remove(enemyLocal.b);
        enemyLocal.b = new GraphicalBoard(toPaint);
        if(inAttackWindow){
            container.add(enemyLocal.b);
        }
        repaint();
    }

    private void removeEnemyBoard(int who){
        if(who == ene1.serverID){
            container.remove(ene1.b);
            playersPanel.remove(ene1.b);
            ene1.alive = false;
        }if(who == ene1.serverID){
            container.remove(ene2.b);
            playersPanel.remove(ene2.b);
            ene2.alive = false;
        }
        repaint();
    }

    private void toWaitingWindow(){
        container.removeAll();
        add(names);
        repaint();
        validate();
    }

    private void updateNames(){
        DefaultListModel<String> model = (DefaultListModel<String>) names.getModel();
        model.removeAllElements();
        for(String s : namesArray){
            System.out.println("Received player from server list " + s);
            model.addElement(s);
        }
    }

    private void setTurnLabel(String what){
        playerTurn.setText(what);
    }

    private void setLabels(){
        attack1.setLabel(ene1.name);
        attack2.setLabel(ene2.name);
    }

    private void setGameWindow() {

        attackButton = new Button("Attack");
        attackButton.setSize(100,100);
        attackButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toChooseAttackWindow();
            }
        });
        attackButton.setLocation(DIMENSION.width - BORDER_RIGHT_SIDE_WIDTH, DIMENSION.height/2 - 200);

        chatButton = new Button("Chat");
        chatButton.setSize(100,100);
        chatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
        chatButton.setLocation(DIMENSION.width - BORDER_RIGHT_SIDE_WIDTH, DIMENSION.height/2 + 100);

        backToMenu = new Button("Back to Menu");
        backToMenu.setSize(100,50);
        backToMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            }
        });
        backToMenu.setLocation(10, 10);

        playerTurn = new JLabel();
        playerTurn.setSize(250, 100);
        playerTurn.setLocation(300,10);

    }

    private void toGameWindow(){
        container.removeAll();
        container.add(attackButton);
        container.add(chatButton);
        container.add(backToMenu);
        container.add(playerTurn);
        container.add(me);
        repaint();
        validate();
    }

    private void setChooseAttackWindow(){
        attack1 = new Button("PLAYER 1");
        attack1.setLocation(350, 600);
        attack1.setSize(150, 50);
        attack1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toAttackingWindow(ene1);
            }
        });

        attack2 = new Button("PLAYER 2");
        attack2.setLocation(750, 600);
        attack2.setSize(150, 50);
        attack2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toAttackingWindow(ene2);
            }
        });

        backToGame = new Button("Back to Game");
        backToGame.setLocation(700, 10);
        backToGame.setSize(100,50);
        backToGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toGameWindow();
            }
        });

        playersPanel = new JPanel(null);

        ene1.image = new JLabel();
        ene2.image = new JLabel();

        ene1.image.setLocation(25, 50);
        ene2.image.setLocation(650, 50);

        ene1.image.setSize(GraphicalBoard.SIZE);
        ene2.image.setSize(GraphicalBoard.SIZE);

        playersPanel.add(attack1);
        playersPanel.add(attack2);
        playersPanel.add(ene1.image);
        playersPanel.add(ene2.image);
        playersPanel.add(backToGame);
        playersPanel.setLocation(0,0);
        playersPanel.setSize(DIMENSION);

    }

    private void toChooseAttackWindow(){
        container.removeAll();

        ene1.buffered = createImage(ene1.b);
        ene1.image.setIcon(new ImageIcon(ene1.buffered));

        ene2.buffered = createImage(ene2.b);
        ene2.image.setIcon(new ImageIcon(ene2.buffered));

        container.add(playersPanel);
        repaint();
        validate();
    }

    private Component findComponentAt(MouseEvent e) {
        return findComponentAt(e.getPoint());
    }

    private void toAttackingWindow(EnemyLocal ene) {
        container.removeAll();

        inAttackWindow = true;
        lastAttacked = ene;

        MouseListener mouseListener = new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if(iCanAttack) {
                    Component found = findComponentAt(e);
                    if (found instanceof GraphTile) {
                        iCanAttack = false;
                        GraphTile gFound = (GraphTile) found;
                        AnAttackAttempt anAttackAttempt = new AnAttackAttempt();
                        anAttackAttempt.c = gFound.getC();
                        anAttackAttempt.l = gFound.getL();
                        anAttackAttempt.toAttackID = ene.serverID;

                        EnemyLocal other = ene1;

                        if(ene.equals(ene1)){
                            System.out.println("HERE");
                            other = ene2;
                        }

                        anAttackAttempt.otherID = other.serverID;

                        System.out.println(myName + " sending other as index: " +  other.serverID);
                        System.out.println(myName + " attacking other as index: " +  ene.serverID);

                        client.sendTCP(anAttackAttempt);
                    }
                }
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
        };

        addMouseListener(mouseListener);

        Button backToMenu = new Button("Back to Game");
        backToMenu.setSize(100,50);
        backToMenu.setLocation(700,100);
        backToMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lastAttacked = ene;
                inAttackWindow = false;
                toGameWindow();
                removeMouseListener(mouseListener);
            }
        });


        add(backToMenu);
        add(ene.b);

        repaint();
        validate();
    }

    private static class EnemyLocal {
        //private int localID;
        private int serverID;
        private GraphicalBoard b;
        private BufferedImage buffered;
        private JLabel image;
        private boolean alive;
        private String name;
    }
}
