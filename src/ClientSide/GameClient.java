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
    private JPanel players;
    private int localIDAttacking;
    private boolean inAttackWindow;
    private boolean iCanAttack;

    private Button backToGame;

    //GRAPHICS

    private GraphicalBoard me;

    private GraphicalBoard ene1;
    private BufferedImage ene1Buffered;
    private JLabel ene1ImageToShow;

    private GraphicalBoard ene2;
    private BufferedImage ene2Buffered;
    private JLabel ene2ImageToShow;
    
    //FRAME
    private Container container;

    public static void main(String[] args){
        GameClient c = new GameClient();
    }

    public GameClient() {

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
        setChooseAttackWindow();
        setGameWindow();

        if(online) {
            inAttackWindow = false;
            toMainMenu();
        }
        else{
            toPlaceShipsScreen();
        }
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
                if (object instanceof YourBoardToPaint){
                    System.out.println("MY BOARD TO PAINT");
                    remove(me);
                    me = new MyGraphBoard(((YourBoardToPaint)object).board);
                    container.add(me);
                    repaint();
                }
                if (object instanceof EnemiesBoardsToPaint){
                    System.out.println("ENEMIES BOARDS TO PAINT");
                    ene1 = new GraphicalBoard(((EnemiesBoardsToPaint)object).board1);
                    ene2 = new GraphicalBoard(((EnemiesBoardsToPaint)object).board2);
                    setLabels();
                }

                if (object instanceof EnemyBoardToPaint){
                    System.out.println("ENEMY BOARD TO PAINT");
                    EnemyBoardToPaint enemyBoardToPaint = (EnemyBoardToPaint) object;
                    updateEnemyBoard(enemyBoardToPaint.id, enemyBoardToPaint.newAttackedBoard);
                }

                if (object instanceof AnAttackResponse){
                    AnAttackResponse response = (AnAttackResponse) object;
                    updateEnemyBoard(localIDAttacking, response.newAttackedBoard);
                }

                if (object instanceof YourTurn){
                    iCanAttack = true;
                    setTurnLabel("ME");
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
                    client.sendTCP(shipsPlacing.getPlayerBoard().getToSend());
                    me = new MyGraphBoard(shipsPlacing.getPlayerBoard().getToSendToPaint());
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
        switch (whose) {
            case 1:
                container.remove(ene1);
                ene1 = new GraphicalBoard(toPaint);
                if(inAttackWindow){
                    container.add(ene1);
                }
                break;
            case 2:
                container.remove(ene2);
                ene2 = new GraphicalBoard(toPaint);
                if(inAttackWindow){
                    container.add(ene2);
                }
                break;
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

    private void setLabels(){
        boolean first = false;
        for (String s : namesArray) {
            if (s.equals(myName)) {
                continue;
            }
            if (!first) {
                attack1.setLabel(s);
                first = true;
                continue;
            }
            attack2.setLabel(s);
        }
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
        playerTurn.setSize(200, 100);
        playerTurn.setLocation(100,20);

    }

    private void toChooseAttackWindow(){
        container.removeAll();

        ene1Buffered = createImage(ene1);
        ene1ImageToShow.setIcon(new ImageIcon(ene1Buffered));

        ene2Buffered = createImage(ene2);
        ene2ImageToShow.setIcon(new ImageIcon(ene2Buffered));

        container.add(players);
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
                localIDAttacking = 1;
            }
        });

        attack2 = new Button("PLAYER 2");
        attack2.setLocation(750, 600);
        attack2.setSize(150, 50);
        attack2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toAttackingWindow(ene2);
                localIDAttacking = 2;
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

        players = new JPanel(null);

        ene1ImageToShow = new JLabel();
        ene2ImageToShow = new JLabel();

        ene1ImageToShow.setLocation(25, 50);
        ene2ImageToShow.setLocation(650, 50);

        ene1ImageToShow.setSize(GraphicalBoard.SIZE);
        ene2ImageToShow.setSize(GraphicalBoard.SIZE);

        players.add(attack1);
        players.add(attack2);
        players.add(ene1ImageToShow);
        players.add(ene2ImageToShow);
        players.add(backToGame);
        players.setLocation(0,0);
        players.setSize(DIMENSION);

    }

    private Component findComponentAt(MouseEvent e) {
        return findComponentAt(e.getPoint());
    }

    private void toAttackingWindow(GraphicalBoard graphicalBoard) {
        container.removeAll();
        repaint();
        validate();

        inAttackWindow = true;

        //TODO: NOT LET PLAYER SPAM CLICK

        MouseListener mouseListener = new MouseListener() {

            private float toWait = 1;

            @Override
            public void mouseClicked(MouseEvent e) {
                if(iCanAttack) {
                    Component found = findComponentAt(e);
                    if (found instanceof GraphTile) {
                        GraphTile gFound = (GraphTile) found;
                        AnAttackAttempt anAttackAttempt = new AnAttackAttempt();
                        anAttackAttempt.c = gFound.getC();
                        anAttackAttempt.l = gFound.getL();
                        anAttackAttempt.clientID = localIDAttacking;
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
                inAttackWindow = false;
                toGameWindow();
            }
        });


        add(backToMenu);
        add(graphicalBoard);

        repaint();
        validate();
    }
}
