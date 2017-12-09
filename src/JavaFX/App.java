package JavaFX;

import Common.Direction;
import Common.Network;
import Common.PlayerBoard;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import Common.Network.*;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Random;

public class App extends Application{

    //FOR OFFLINE
    private MyAI ai;

    //FOR ONLINE
    private final static boolean ONLINE = false;
    boolean shipsSet;
    private Client client;
    private String myName;
    private final String address = "localhost";
    private PlayerBoard pb;

    private boolean iCanAttack;
    
    //region MAIN MENU STUFF
    
    private BorderPane mMRoot;
    private final static String MM_IMAGE_BACKGROUND_PATH = "images/BattleShipBigger.png";
    private final static Image MM_IMAGE_BACKGROUND = new Image(MM_IMAGE_BACKGROUND_PATH);
    private final static BackgroundImage MG_BACKGROUND = new BackgroundImage(MM_IMAGE_BACKGROUND, 
            BackgroundRepeat.REPEAT, 
            BackgroundRepeat.REPEAT,
            null,
            new BackgroundSize(MM_IMAGE_BACKGROUND.getWidth(), MM_IMAGE_BACKGROUND.getHeight(), 
                    false, false, true, true)
    );

    private Image mMPlayButtonImage = new Image("images/start_medium.png");
    private Button mMPlayButton;

    private Image mMAloneButtonImage = new Image("images/alone_medium.png");
    private Button mMAloneButton;

    private Image exitButtonImage = new Image("images/exit_medium.png");
    private Button exit;
    
    private StackPane MMMiddle ;
    
    //endregion

    //region SET SHIPS STUFF

    private HBox sSRoot;

    private GraphShipsBoardFX sSboard;

    private VBox sSRightStuff;

    private HBox sSShipsStatus;
    private Text sSShipsStatusAllSet;
    private Text sSShipsStatusShipsSet;

    private HBox sSReadyBox;
    private Button sSRandomButton;
    private CheckBox sSReady;
    private Button sSReadyButton;

    private VBox sSPlayersReady;
    private Text sSPlayer1Ready;
    private Text sSPlayer2Ready;

    //endregion
    
    //region MAIN GAME STUFF

    private BorderPane MGRoot;

    private Group MGCanvasHolder;
    private SelfGraphBoardFX MGSelfBoard;

    private StackPane MGTop;
    private Text mGcurrentPlayerText;

    private VBox MGRight;
    private Circle MGShips;
    private Button MGAttackButton;
    private Button MGChatButton;

    //endregion
    
    //GRAPHICS

    private SelfGraphBoardFX me;

    private EnemyLocal lastAttacked;

    private EnemyLocal ene1;
    private EnemyLocal ene2;

    private final static Rectangle2D SCREEN_RECTANGLE = Screen.getPrimary().getVisualBounds();

    private Scene mainMenu;
    private Scene mainGame;
    private Scene setShips;
    private Stage theStage;

    public static void main(String[] args){
        launch(args);
    }

    //region IPV4FIND

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
                        if (inetAddress.isSiteLocalAddress())
                            //YEAH BOY, FOUND IT
                            return inetAddress;
                        else if (closestOneFound == null)
                            //FOUND ONE, MIGHT NOT BE WHAT WE WANT
                            //BUT LET'S STORE IT IN CASE WE DON'T FIND EXACTLY WHAT WE WANT
                            closestOneFound = inetAddress;
                    }
                }
            }
            if (closestOneFound != null)
                // NOT IPV4, but will have to do
                return closestOneFound;
            //FOUND NOTHING
            //WILL TRY THE JDK ONE
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null)
                throw new UnknownHostException("The JDK INetAddress.getLocalHost() method unexpectedly returned null.");
            return jdkSuppliedAddress;
        } catch (Exception e) {
            UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
    }

    //endregion

    //region SERVER/CLIENT STUFF

    private void serverConfigurations() {

        client = new Client();
        client.start();

        Network.register(client);

        client.addListener(new Listener() {
            public void connected(Connection connection) {
            }

            public void received(Connection connection, Object object) {
                if (object instanceof IsFull)
                    System.out.println(object);
                if (object instanceof Abort){}

                if (object instanceof CanStart) 
                    transitionTo(mainGame);
                
                if (object instanceof WhoseTurn) {
                    WhoseTurn whoseTurn = (WhoseTurn) object;
                    System.out.println(whoseTurn.name);
                    setTurnLabel(whoseTurn.name);
                }
                if (object instanceof ConnectedPlayers) {
                    System.out.println("CONNECTED PLAYERS");
                    ConnectedPlayers players = (ConnectedPlayers) object;
                    //TODO: DO WAITING WINDOW
                }

                if (object instanceof ReadyForShips) {}
                    //TODO: DO SHIPS WINDOW

                if (object instanceof OthersSpecs) {
                    OthersSpecs othersSpecs = (OthersSpecs) object;

                    ene1.serverID = othersSpecs.ene1;
                    ene1.alive = true;
                    ene1.name = othersSpecs.ene1n;

                    ene2.serverID = othersSpecs.ene2;
                    ene2.alive = true;
                    ene2.name = othersSpecs.ene2n;

                }

                if (object instanceof YourBoardToPaint)
                    //System.out.println("MY BOARD TO PAINT");
                    MGSelfBoard.updateTiles(((YourBoardToPaint) object).board);

                if (object instanceof EnemiesBoardsToPaint) {
                    //System.out.println("ENEMIES BOARDS TO PAINT");
                    //TODO: SIZE?
                    ene1.b = new GraphBoardFX(500,500);
                    ene2.b = new GraphBoardFX(500,500);

                    ene1.b.startTiles(((EnemiesBoardsToPaint) object).board1);
                    ene2.b.startTiles(((EnemiesBoardsToPaint) object).board2);
                    //TODO: LABELS WITH THE CORRECT NAMES
                }

                if (object instanceof EnemyBoardToPaint) {
                    EnemyBoardToPaint ebp = (EnemyBoardToPaint) object;
                    updateEnemyBoard(ebp.id, ebp.newAttackedBoard);
                    System.out.println("ENEMY BOARD TO PAINT WITH INDEX " + ebp.id);
                }

                if (object instanceof AnAttackResponse) {
                    lastAttacked.b.updateTiles(((AnAttackResponse) object).newAttackedBoard);
                    iCanAttack = ((AnAttackResponse) object).again;
                }

                if (object instanceof YourTurn) {
                    iCanAttack = true;
                    setTurnLabel("My TURN!!");
                }

                if (object instanceof YouDead) {
                    //TODO: WARN ABOUT DEATH; AND STUFF
                }

                if (object instanceof PlayerDied) {
                    //TODO: REMOVE BOARDS
                    System.out.println("Player died");
                    //removeEnemyBoard(((Network.mMplayerDied) object).who);
                }

                if (object instanceof YouWon) {
                    //TODO: YOU WON
                }

                if (object instanceof ChatMessage) {
                    ///TODO: CHAT
                }
            }
        });


        if (ONLINE) {
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

    private void updateEnemyBoard(int id, String[][] newAttackedBoard) {
        EnemyLocal toUpdate = ene1;

        if(id == ene2.serverID){
            toUpdate = ene2;
        }
        toUpdate.b.updateTiles(newAttackedBoard);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        client.stop();
    }

    //endregion

    @Override
    public void start(Stage primaryStage) throws Exception {

        shipsSet = false;
        iCanAttack = false;

        serverConfigurations();

        theStage = primaryStage;
        setAllScenes();

        theStage.initStyle(StageStyle.UNDECORATED);
        theStage.setTitle("BS");
        theStage.setResizable(false);
        theStage.setMaximized(true);
        theStage.setScene(mainMenu);
        theStage.show();

    }

    private void setAllScenes(){
        setMainMenu();
        setMainGame();
        setShipsScene();
    }

    private void transitionTo(Scene scene) {
        theStage.setScene(scene);
    }

    private void setMainGame() {

        MGRoot = new BorderPane();
        MGRoot.setStyle("-fx-background-color:white");

        //GridPane gridPane = new GridPane();
        //gridPane.setStyle("-fx-background-color:cyan");

        MGCanvasHolder = new Group();
        MGCanvasHolder.setStyle("-fx-background-color:cyan");

        MGSelfBoard = new SelfGraphBoardFX(512, 512);
        pb = PlayerBoard.getRandomPlayerBoard();

        MGSelfBoard.startTiles(pb.getToSendToPaint());
        MGSelfBoard.updateTiles(pb.getToSendToPaint());
        MGSelfBoard.startAnimating();
        MGSelfBoard.setPlayerBoard(pb);

        MGCanvasHolder.getChildren().add(MGSelfBoard);

        MGTop = new StackPane();
        MGTop.setStyle("-fx-background-color:red");

        mGcurrentPlayerText = new Text("IS PLAYING");
        MGTop.getChildren().add(mGcurrentPlayerText);

        MGRight = new VBox();
        MGRight.setStyle("-fx-background-color:green");
        MGRight.setSpacing(50);

        MGShips = new Circle();
        MGShips.setRadius(50);
        MGAttackButton = new Button("TO ARMS");
        MGChatButton = new Button("CHAT");

        MGRight.getChildren().addAll(MGShips, MGAttackButton, MGChatButton);

        MGRoot.setRight(MGRight);
        MGRoot.setTop(MGTop);
        MGRoot.setCenter(MGCanvasHolder);

        mainGame = new Scene(MGRoot, SCREEN_RECTANGLE.getWidth(), SCREEN_RECTANGLE.getHeight());
        mainGame.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.A){
                pb.getAttacked(new Random().nextInt(10), new Random().nextInt(10));
            }
        });
    }

    private void setMainMenu(){

        mMRoot = new BorderPane();
        mMRoot.setPrefSize(SCREEN_RECTANGLE.getWidth(), SCREEN_RECTANGLE.getHeight());
        mMRoot.setStyle("-fx-background-color: white");
        //mMRoot.setBackground(new Background(bg));

        mMPlayButton = new Button();
        mMPlayButton.setGraphic(new ImageView(mMPlayButtonImage));
        mMPlayButton.setOnAction(event ->
                transitionTo(setShips)
        );
        mMPlayButton.setStyle("-fx-background-color: transparent;");

        mMAloneButton = new Button();
        mMAloneButton.setGraphic(new ImageView(mMAloneButtonImage));
        mMAloneButton.setOnAction(event -> {
            theStage.setScene(mainGame);
        });
        mMAloneButton.setStyle("-fx-background-color: transparent;");

        exit = new Button();
        exit.setGraphic(new ImageView(exitButtonImage));
        exit.setOnAction(event ->
                Platform.exit()
        );
        exit.setStyle("-fx-background-color: transparent;");
        
        MMMiddle = new StackPane();
        MMMiddle.setStyle("-fx-background-color:cyan;");
        MMMiddle.getChildren().addAll(mMPlayButton,mMAloneButton,exit);
        mMRoot.setCenter(MMMiddle);

        mainMenu = new Scene(mMRoot, SCREEN_RECTANGLE.getWidth(),
                SCREEN_RECTANGLE.getHeight()
        );

        Platform.runLater(() -> {
            mMAloneButton.setTranslateX(mMPlayButton.getWidth() + 25);
            exit.setTranslateX(mMPlayButton.getWidth()/2);
            exit.setTranslateY(mMPlayButton.getHeight() + 25);
        });
    }
    
    private void setTurnLabel(String name) {
        mGcurrentPlayerText.setText(name + " IS ATTACKING!!");
    }

    private void setShipsScene(){
        sSRoot = new HBox();
        sSRoot.setStyle("-fx-background-color: white");

        pb = new PlayerBoard();
        sSboard = new GraphShipsBoardFX(700,700);
        sSboard.setPlayerBoard(pb);
        sSboard.startAnimating();

        sSRightStuff = new VBox();
        sSRightStuff.setStyle("-fx-background-color: red");

        sSShipsStatus = new HBox();
        sSShipsStatus.setStyle("-fx-background-color: green;");
        sSShipsStatusAllSet = new Text("");
        sSShipsStatusShipsSet = new Text("");

        sSShipsStatus.getChildren().addAll(sSShipsStatusAllSet, sSShipsStatusShipsSet);

        sSReadyBox = new HBox();
        sSReadyBox.setStyle("-fx-background-color: yellow;");

        sSRandomButton = new Button("Random");
        sSReady = new CheckBox();

        sSReadyButton = new Button("Ready");

        sSReadyBox.getChildren().addAll(sSRandomButton, sSReady, sSReadyButton);

        sSPlayersReady = new VBox();
        sSPlayersReady.setStyle("-fx-background-color: blue");

        sSPlayer1Ready = new Text("A");
        sSPlayer2Ready = new Text("ADD");

        sSPlayersReady.getChildren().addAll(sSPlayer1Ready, sSPlayer2Ready);
        sSPlayersReady.setPadding(new Insets(5));

        sSRightStuff.getChildren().addAll(sSShipsStatus, sSReadyBox, sSPlayersReady);

        sSRoot.getChildren().addAll(sSboard, sSRightStuff);
        sSRoot.setPadding(new Insets(50, 50, 200,200));
        sSRoot.setSpacing(200);

        setShips = new Scene(sSRoot, SCREEN_RECTANGLE.getWidth(),
                SCREEN_RECTANGLE.getHeight());
    }

    private static class EnemyLocal {
        private int serverID;
        private GraphBoardFX b;
        private BufferedImage buffered;
        private JLabel image;
        private boolean alive = true;
        private String name;
        private java.awt.Button attack;
        private ArrayList<String> conversation;
        private EnemyLocal(){
            conversation = new ArrayList<>();
        }
    }

    private static class MyAI {

        GraphBoardFX gb;
        PlayerBoard board;

        private boolean searching;
        private boolean betweenTwo;

        private Point firstHit;
        private Point justBefore;
        private ArrayList<Direction> directionsToGo;
        private Direction directionLooking;

        MyAI(){
            searching = false;
            betweenTwo = false;
            justBefore = new Point(0,0);
            firstHit = new Point(0,0);
            directionsToGo = new ArrayList<>();
            board = PlayerBoard.getRandomPlayerBoard();
            //gb = new GraphBoardFX(getToPaint());
        }

        private String[][] getToPaint(){
            return board.getToSendToPaint();
        }

        private boolean inBounds(Point p){
            return PlayerBoard.inBounds(p.x, p.y);
        }

        private void thinkAboutNext(ArrayList<Point> pos, boolean hit, boolean destroyedIt) {

            //WASN'T SEARCHING BEFORE
            //SEE IF HIT
            //IF HIT, PREPARE THE NEXT ATTACKS ALREADY
            if (!searching && hit) {
                betweenTwo = false;
                System.out.println("WAS A NEW TARGET");
                if(!destroyedIt) {
                    int[] d = Direction.DOWN.getDirectionVector();
                    Point n = new Point(justBefore.x + d[0], justBefore.y + d[1]);
                    if (inBounds(n) && pos.contains(n))
                        directionsToGo.add(Direction.DOWN);

                    d = Direction.UP.getDirectionVector();
                    n = new Point(justBefore.x + d[0], justBefore.y + d[1]);
                    if (inBounds(n) && pos.contains(n))
                        directionsToGo.add(Direction.UP);

                    d = Direction.LEFT.getDirectionVector();
                    n = new Point(justBefore.x + d[0], justBefore.y + d[1]);
                    if (inBounds(n) && pos.contains(n))
                        directionsToGo.add(Direction.LEFT);

                    d = Direction.RIGHT.getDirectionVector();
                    n = new Point(justBefore.x + d[0], justBefore.y + d[1]);
                    if (inBounds(n) && pos.contains(n))
                        directionsToGo.add(Direction.RIGHT);

                    directionLooking = directionsToGo.get(directionsToGo.size() - 1);
                }
            }
            else if (searching) {
                System.out.println("WAS AN OLD TARGET");
                //FAILED
                if(!hit && !betweenTwo){
                    System.out.println("MISSED; CHANGING DIRECTION");
                    justBefore = firstHit;
                    directionsToGo.remove(directionsToGo.size() - 1);
                    directionLooking = directionsToGo.get(directionsToGo.size() - 1);
                    System.out.println(directionLooking);
                }
                //SEARCHING ALREADY
                //BETWEEN TWO BUT FAILED
                else if (betweenTwo && !hit) {
                    System.out.println("I KNOW THE RIGHT ONE");
                    directionLooking = directionLooking.getOpposite();
                    justBefore = firstHit;
                }
                //IF HIT THAT MEANS IT'S EITHER THIS WAY OR THE OPPOSITE
                else{
                    System.out.println("BETWEEN TWO");
                    betweenTwo = true;
                }
            }
            if(hit) {
                searching = !destroyedIt;
            }
        }

        private Point chooseAttack(ArrayList<Point> pos){
            Point p;
            if(!searching){
                //GET A POINT NOT TRIED YET
                int r = new Random().nextInt(pos.size());
                p = pos.get(r);
                firstHit = p;
            }
            else{

                System.out.println(directionLooking);

                p = new Point(justBefore.x + directionLooking.getDirectionVector()[0],
                        justBefore.y + directionLooking.getDirectionVector()[1]);

            }
            justBefore = p;
            return p;
        }

    }

}


