package ClientSide;

import Common.BoardTile;
import Common.Network;
import Common.Network.*;
import Common.PlayerBoard;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;

public class GameClient extends JFrame{

    private enum WindowE{
        MainMenu,
        Waiting,
        Ships,
        MainGame,
        ChoosingEnemy,
        Attacking
    }

    private WindowE window;

    final static Point GAME_BOARD_LOCATION = new Point(75,75);
    final static Dimension DIMENSION = new Dimension(1280, 720);
    private final static String TITLE = "GAME";
    private final static int BORDER_RIGHT_SIDE_WIDTH = 300;

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
    private JPanel playersPanel;
    private MyMouse mouseListener;
    private boolean iCanAttack;

    private Button backToGame;

    //GRAPHICS

    private GraphicalBoard me;

    private EnemyLocal lastAttacked;

    private EnemyLocal ene1;
    private EnemyLocal ene2;
    
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
            toMainMenu();
            ene1 = new EnemyLocal();
            ene2 = new EnemyLocal();
        }
        else{
            toMainGameWindow();
        }

        setChooseAttackWindow();
        setMainGameWindow();

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
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                // GO THROUGH ALL IP ADDRESSES OF THIS CARD...
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while(inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
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
                if (object instanceof Abort){
                    setMainMenu();
                }
                if (object instanceof CanStart){
                    System.out.println("STARTING THE GAME");
                    toMainGameWindow();
                }
                if (object instanceof WhoseTurn){
                    WhoseTurn whoseTurn = (WhoseTurn) object;
                    System.out.println(whoseTurn.name);
                    iCanAttack = false;
                    setTurnLabel(whoseTurn.name);
                }
                if (object instanceof ConnectedPlayers){
                    System.out.println("CONNECTED PLAYERS");
                    ConnectedPlayers connectedPlayers = (ConnectedPlayers) object;
                    namesArray = connectedPlayers.names;
                    updateNames();
                    toWaitingWindow();
                }

                if (object instanceof ReadyForShips){
                    //System.out.println("TO SHIP SCREEN");
                    toPlaceShipsScreen();
                }
                if (object instanceof OthersSpecs){
                    OthersSpecs othersSpecs = ((OthersSpecs) object);

                    ene1.serverID = othersSpecs.ene1;
                    ene1.alive = true;
                    ene1.name = othersSpecs.ene1n;

                    ene2.serverID = othersSpecs.ene2;
                    ene2.alive = true;
                    ene2.name = othersSpecs.ene2n;

                    //System.out.println(myName + " has as 1(index): " + ene1.serverID);
                    //System.out.println(myName + " has as 2(index): " + ene2.serverID);

                }

                if (object instanceof YourBoardToPaint){
                    //System.out.println("MY BOARD TO PAINT");
                    container.remove(me);
                    me = new MyGraphBoard(((YourBoardToPaint)object).board);
                    container.add(me);
                    repaint();
                    validate();
                }
                if (object instanceof EnemiesBoardsToPaint){
                    //System.out.println("ENEMIES BOARDS TO PAINT");
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
                    System.out.println("Player died");
                    removeEnemyBoard(((PlayerDied) object).who);
                }

                if (object instanceof YouWon){
                    JOptionPane.showMessageDialog(container, "You WON!");
                }

                if (object instanceof ChatMessage){
                    int said = ((ChatMessage) object).saidIt;
                    String name = (ene1.serverID == said)?ene1.name : ene2.name;
                    System.out.println("RECEIVED MESSAGE: " + ((ChatMessage) object).message + "\n FROM ID: " +
                            said + "LOCALLY THAT IS: " + name);

                    EnemyLocal ene = ene1;
                    if(said == ene2.serverID){
                        ene = ene2;
                    }
                    ene.conversation.add(((ChatMessage) object).message);

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

        window = WindowE.MainMenu;

        container.removeAll();

        container.add(playButton);

        nameField.grabFocus();
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

        window = WindowE.Ships;

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
                    //toMainGameWindow();
                }
            }
        });

        container.add(goToGame);
        container.add(b);
        container.add(shipsPlacing);
        repaint();
        validate();
    }

    //endregion

    private void updateEnemyBoard(int whose, String[][] toPaint){
        EnemyLocal toUpdate = ene1;

        if(whose == ene2.serverID){
            toUpdate = ene2;
        }

        container.remove(toUpdate.b);
        toUpdate.b = new GraphicalBoard(toPaint);

        if(window == WindowE.Attacking){
            container.add(toUpdate.b);
        }

        repaint();
        validate();
    }

    private void updateEnemyBoard(EnemyLocal enemyLocal, String[][] toPaint){
        container.remove(enemyLocal.b);
        enemyLocal.b = new GraphicalBoard(toPaint);
        if(window == WindowE.Attacking){
            container.add(enemyLocal.b);
        }
        repaint();
        validate();
    }

    private void removeEnemyBoard(int who){
        EnemyLocal enemyLocal = ene1;
        if(who == ene2.serverID){
            enemyLocal = ene2;
        }
        enemyLocal.image.setLocation(GAME_BOARD_LOCATION.x + 100 , GAME_BOARD_LOCATION.y + 50);
        enemyLocal.attack.setLocation(enemyLocal.image.getLocation().x, enemyLocal.image.getLocation().y + 50);
        playersPanel.remove(enemyLocal.attack);
        playersPanel.remove(enemyLocal.image);
        playersPanel.remove(enemyLocal.b);
        enemyLocal.alive = false;
        container.remove(playersPanel);
        if(window == WindowE.Attacking){
            removeMouseListener(mouseListener);
            System.out.println("HERE" + "died " + enemyLocal+ "... and from server " +
                    who);
            toChooseAttackWindow();
        }
    }

    private void toWaitingWindow(){
        window = WindowE.Waiting;
        container.removeAll();
        container.add(names);
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
        ene1.attack.setLabel(ene1.name);
        ene2.attack.setLabel(ene2.name);
    }

    private void setMainGameWindow() {

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
                JList<String> list = new JList<>(new String[] {"Show", "Send"});
                JOptionPane.showMessageDialog(
                        null, list, "Choose one action", JOptionPane.PLAIN_MESSAGE);


                JList<String> listId = new JList<>(new String[] {ene1.name, ene2.name});
                JOptionPane.showMessageDialog(
                        null, listId, "To whom?", JOptionPane.PLAIN_MESSAGE);

                EnemyLocal selected = ene2;
                if(listId.getSelectedValue().equalsIgnoreCase(ene1.name)){
                    selected = ene1;
                }

                if (list.getSelectedValue().equalsIgnoreCase("Show")){

                    System.out.println(selected.conversation);
                }
                else{
                    String message = JOptionPane.showInputDialog(container, "What to send");

                    ChatMessageFromClient c = new ChatMessageFromClient();
                    selected.conversation.add(message);
                    c.text = message;
                    c.to = selected.serverID;
                    client.sendTCP(c);
                }
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

    private void toMainGameWindow(){
        window = WindowE.MainGame;
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
        ene1.attack = new Button("PLAYER 1");
        ene1.attack.setLocation(350, 600);
        ene1.attack.setSize(150, 50);
        ene1.attack.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toAttackingWindow(ene1);
            }
        });

        ene2.attack = new Button("PLAYER 2");
        ene2.attack.setLocation(750, 600);
        ene2.attack.setSize(150, 50);
        ene2.attack.addActionListener(new ActionListener() {
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
                toMainGameWindow();
            }
        });

        playersPanel = new JPanel(null);

        ene1.image = new JLabel();
        ene2.image = new JLabel();

        ene1.image.setLocation(25, 50);
        ene2.image.setLocation(650, 50);

        ene1.image.setSize(GraphicalBoard.SIZE);
        ene2.image.setSize(GraphicalBoard.SIZE);

        playersPanel.add(ene1.attack);
        playersPanel.add(ene2.attack);
        playersPanel.add(ene1.image);
        playersPanel.add(ene2.image);
        playersPanel.add(backToGame);
        playersPanel.setLocation(0,0);
        playersPanel.setSize(DIMENSION);

        mouseListener = new MyMouse();

    }

    private void toChooseAttackWindow(){

        window = WindowE.ChoosingEnemy;

        container.removeAll();

        if(ene1.alive) {
            ene1.buffered = createImage(ene1.b);
            ene1.image.setIcon(new ImageIcon(ene1.buffered));
        }

        if(ene2.alive){
            if(ene2.b != null){
                ene2.buffered = createImage(ene2.b);
                ene2.image.setIcon(new ImageIcon(ene2.buffered));
            }
            else{
                System.out.println("IS NULL");
            }
        }

        container.add(playersPanel);
        repaint();
        validate();
    }

    private Component findComponentAt(MouseEvent e) {
        return findComponentAt(e.getPoint());
    }

    private void toAttackingWindow(EnemyLocal ene) {

        window = WindowE.Attacking;

        container.removeAll();

        lastAttacked = ene;

        mouseListener.ene = ene;

        addMouseListener(mouseListener);

        Button backToMenu = new Button("Back to Game");
        backToMenu.setSize(100,50);
        backToMenu.setLocation(700,100);
        backToMenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toMainGameWindow();
                removeMouseListener(mouseListener);
            }
        });

        add(backToMenu);
        add(ene.b);

        repaint();
        validate();
    }

    private class MyMouse implements MouseListener{

        EnemyLocal ene;

        @Override
        public void mouseClicked(MouseEvent e) {
            if (iCanAttack) {
                Component found = findComponentAt(e);
                if (found instanceof GraphTile) {
                    iCanAttack = false;
                    GraphTile gFound = (GraphTile) found;
                    AnAttackAttempt anAttackAttempt = new AnAttackAttempt();
                    anAttackAttempt.c = gFound.getC();
                    anAttackAttempt.l = gFound.getL();
                    anAttackAttempt.toAttackID = ene.serverID;

                    EnemyLocal other = ene1;

                    if (ene.equals(ene1)) {
                        //System.out.println("HERE");
                        other = ene2;
                    }

                    anAttackAttempt.otherID = other.serverID;

                    //System.out.println(myName + " sending other as index: " +  other.serverID);
                    //System.out.println(myName + " attacking other as index: " +  ene.serverID);

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
    }

    private static class EnemyLocal {
        private int serverID;
        private GraphicalBoard b;
        private BufferedImage buffered;
        private JLabel image;
        private boolean alive = true;
        private String name;
        private Button attack;
        private ArrayList<String> conversation;
        private EnemyLocal(){
            conversation = new ArrayList<>();
        }
    }
}
