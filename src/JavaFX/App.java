package JavaFX;

import Common.Direction;
import Common.Network;
import Common.PlayerBoard;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import Common.Network.*;

import javax.swing.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class App extends Application{

    //FOR OFFLINE
    private MyAI ai;

    //FOR ONLINE
    private Client client;
    private String myName;
    private final String address = "localhost";
    private PlayerBoard pb;

    //region MAIN MENU STUFF
    
    private BorderPane mMRoot;
    private final static String MM_IMAGE_BACKGROUND_PATH = "images/BattleShipBigger.png";
    private final static Image MM_IMAGE_BACKGROUND = new Image(MM_IMAGE_BACKGROUND_PATH);
    private final static BackgroundImage MM_BACKGROUND = new BackgroundImage(MM_IMAGE_BACKGROUND,
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

    private Image mMExitButtonImage = new Image("images/exit_medium.png");
    private Button mMExit;

    private TextField mMnameInput;
    private Label mMServerText;

    private GridPane mMMiddle;

    //endregion

    //region SET SHIPS STUFF

    private HBox sSRoot;

    private ShipsBoardFX sSboard;

    private VBox sSRightStuff;

    private HBox sSShipsStatus;
    private VBox sSTips;

    private HBox sSReadyBox;
    private Button sSRandomButton;
    private Button sSReadyButton;

    private VBox sSPlayersReady;
    private Text sSPlayer1Ready;
    private Text sSPlayer2Ready;

    //endregion

    //region MAIN GAME STUFF

    private BorderPane MGRoot;

    private Group MGCanvasHolder;
    private SelfGraphBoardFX mGSelfBoard;

    private StackPane MGTop;
    private Label mGcurrentPlayerText;

    private VBox MGRight;
    private Circle MGShips;
    private Button MGAttackButton;
    private Button MGChatButton;

    //endregion

    //region ATTACK WINDOW STUFF

    private boolean iCanAttack;
    private EnemyLocal lastAttacked;
    private EnemyLocal ene1;
    private EnemyLocal ene2;
    private HBox aWRoot;

    //endregion

    private ArrayList<EmptyGraphBoardFX> toAnimate = new ArrayList<>();

    private final static Rectangle2D SCREEN_RECTANGLE = Screen.getPrimary().getVisualBounds();

    //SCENES

    private Scene mainMenu;
    private Scene mainGame;
    private Scene setShips;
    private Scene attackScene;

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
                if (object instanceof Abort){
                    //
                }

                if (object instanceof CanStart) 
                    Platform.runLater( () -> transitionTo(mainGame));
                
                if (object instanceof WhoseTurn) {
                    WhoseTurn whoseTurn = (WhoseTurn) object;
                    Platform.runLater( () -> setTurnLabel(whoseTurn.name));
                }
                if (object instanceof ConnectedPlayers) {
                    System.out.println("CONNECTED PLAYERS");
                    ConnectedPlayers players = (ConnectedPlayers) object;
                    //TODO: DO WAITING WINDOW
                }

                if (object instanceof ReadyForShips)
                    Platform.runLater( () -> transitionTo(setShips));

                if (object instanceof OthersSpecs) {
                    //TODO: LABELS WITH THE CORRECT NAMES
                    OthersSpecs othersSpecs = (OthersSpecs) object;

                    Platform.runLater( () -> {

                        ene1.serverID = othersSpecs.ene1;
                        ene1.alive = true;
                        ene1.name = othersSpecs.ene1n;

                        ene2.serverID = othersSpecs.ene2;
                        ene2.alive = true;
                        ene2.name = othersSpecs.ene2n;

                    });

                }

                if (object instanceof YourBoardToPaint)
                    //System.out.println("MY BOARD TO PAINT");
                    Platform.runLater( () -> mGSelfBoard.updateTiles(((YourBoardToPaint) object).board));

                if (object instanceof EnemiesBoardsToPaint) {
                    //System.out.println("ENEMIES BOARDS TO PAINT");
                    Platform.runLater( () -> {
                                ene1.b.startTiles(((EnemiesBoardsToPaint) object).board1);
                                ene2.b.startTiles(((EnemiesBoardsToPaint) object).board2);
                            });
                    //TODO: LABELS WITH THE CORRECT NAMES
                }

                if (object instanceof EnemyBoardToPaint) {
                    Platform.runLater( () -> {
                                EnemyBoardToPaint ebp = (EnemyBoardToPaint) object;
                                updateEnemyBoard(ebp.id, ebp.newAttackedBoard);
                                System.out.println("ENEMY BOARD TO PAINT WITH INDEX " + ebp.id);
                    });
                }

                if (object instanceof AnAttackResponse) {
                    Platform.runLater( () -> {
                        lastAttacked.b.updateTiles(((AnAttackResponse) object).newAttackedBoard);
                        iCanAttack = ((AnAttackResponse) object).again;
                    });
                }

                if (object instanceof YourTurn) {
                    Platform.runLater( () -> {
                        iCanAttack = true;
                        setTurnLabel("My TURN!!");
                    });
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
        System.exit(0);
    }

    //endregion

    @Override
    public void start(Stage primaryStage) throws Exception {

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
        setAttackScreen();
    }

    /**
     * CALL BEFORE OTHER STUFF
     * @param scene
     */
    private void transitionTo(Scene scene) {
        for (EmptyGraphBoardFX g : toAnimate)
            g.stopAnimating();
        toAnimate.clear();
        theStage.setScene(scene);
        if(scene == mainGame){
            toAnimate.add(mGSelfBoard);
        }
        if(scene == attackScene){
            toAnimate.add(ene1.b);
            toAnimate.add(ene2.b);
        }
        if(scene == setShips){
            toAnimate.add(sSboard);
        }
        for (EmptyGraphBoardFX g : toAnimate)
            g.startAnimating();
    }

    private void setMainGame() {

        MGRoot = new BorderPane();
        MGRoot.setStyle("-fx-background-color:white");

        //GridPane gridPane = new GridPane();
        //gridPane.setStyle("-fx-background-color:cyan");

        MGCanvasHolder = new Group();
        MGCanvasHolder.setStyle("-fx-background-color:cyan");

        mGSelfBoard = new SelfGraphBoardFX(500, 500);

        MGCanvasHolder.getChildren().add(mGSelfBoard);

        MGTop = new StackPane();
        MGTop.setStyle("-fx-background-color:red");

        mGcurrentPlayerText = new Label("IS PLAYING");
        MGTop.getChildren().add(mGcurrentPlayerText);

        MGRight = new VBox();
        MGRight.setStyle("-fx-background-color:green");
        MGRight.setSpacing(50);

        MGShips = new Circle();
        MGShips.setRadius(50);
        MGAttackButton = new Button("TO ARMS");
        MGAttackButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                transitionTo(attackScene);
            }
        });
        MGChatButton = new Button("CHAT");

        MGRight.getChildren().addAll(MGShips, MGAttackButton, MGChatButton);

        MGRoot.setRight(MGRight);
        MGRoot.setTop(MGTop);
        MGRoot.setCenter(MGCanvasHolder);

        mainGame = new Scene(MGRoot, SCREEN_RECTANGLE.getWidth(), SCREEN_RECTANGLE.getHeight());
    }

    private void setMainMenu(){

        mMRoot = new BorderPane();
        mMRoot.setPrefSize(SCREEN_RECTANGLE.getWidth(), SCREEN_RECTANGLE.getHeight());
        mMRoot.setBackground(new Background(MM_BACKGROUND));
        mMRoot.setStyle("-fx-background-image: url(images/BattleShipBigger2.png);-fx-background-repeat-style: strech;");

        mMPlayButton = new Button();
        mMPlayButton.setGraphic(new ImageView(mMPlayButtonImage));

        mMPlayButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                myName = mMnameInput.getText();
                if(myName.equals("")){
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Ups!");
                    alert.setHeaderText("Name was null!");
                    alert.setContentText("Name can't be null, we need to know who you are :(");
                    alert.showAndWait();
                    return;
                }
                theStage.setTitle(myName);
                Task<Boolean> connect = new Task<>() {
                    @Override
                    protected Boolean call() throws Exception {
                        Boolean connected = true;
                        try {
                            client.connect(5000, address, Network.port);
                        }
                        catch (IOException e) {
                            connected = false;
                        }
                        return connected;
                    }
                };
                connect.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {
                        if(connect.getValue()) {
                            Register r = new Register();
                            r.name = myName;
                            try {
                                r.address = getMeIPV4().toString();
                            } catch (UnknownHostException e1) {
                                e1.printStackTrace();
                                r.address = "100:00";
                            }
                            client.sendTCP(r);
                            //transitionTo(setShips);
                        }else{
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Noo!");
                            alert.setHeaderText("Can't play when you can't connect to server :(");
                            alert.setContentText("Maybe...Go play alone? \nOr you could try again (:");
                            alert.showAndWait();
                        }
                    }
                });

                new Thread(connect).start();
            }
        });
        mMPlayButton.setStyle("-fx-background-color: transparent;");

        mMAloneButton = new Button();
        mMAloneButton.setGraphic(new ImageView(mMAloneButtonImage));
        mMAloneButton.setOnAction(event ->
            theStage.setScene(mainGame)
        );
        mMAloneButton.setStyle("-fx-background-color: transparent;");

        mMExit = new Button();
        mMExit.setGraphic(new ImageView(mMExitButtonImage));
        mMExit.setOnAction(event ->
                Platform.exit()
        );
        mMExit.setStyle("-fx-background-color: transparent;");

        //TODO: BETTER TEXT
        mMServerText = new Label();

        mMnameInput = new TextField("Name!");
        mMnameInput.textProperty().addListener(new ChangeListener<>() {

            final int maxLength = 10;

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (mMnameInput.getText().length() > maxLength) {
                    String s = mMnameInput.getText().substring(0, maxLength);
                    mMnameInput.setText(s);
                }
            }
        });

        mMMiddle = new GridPane();
        mMMiddle.add(mMPlayButton, 0, 2);
        mMMiddle.add(mMAloneButton, 1, 2);
        mMMiddle.add(mMExit, 1, 3);
        mMMiddle.add(mMnameInput, 0, 1);
        mMMiddle.add(mMServerText, 0, 0);

        //mMMiddle.getChildren().addAll(mMPlayButton,mMAloneButton, mMExit, mMServerText, mMnameInput);
        mMMiddle.setAlignment(Pos.BOTTOM_CENTER);
        mMRoot.setCenter(mMMiddle);
        //mMMiddle.setStyle("-fx-background-color:cyan;");

        mainMenu = new Scene(mMRoot, SCREEN_RECTANGLE.getWidth(),
                SCREEN_RECTANGLE.getHeight()
        );
    }

    private void setTurnLabel(String name) {
        mGcurrentPlayerText.setText(name + " IS ATTACKING!!");
    }

    private void setShipsScene(){

        sSRoot = new HBox();
        sSRoot.setStyle("-fx-background-image: url(images/BattleShipBigger2.png)");

        pb = new PlayerBoard();
        sSboard = new ShipsBoardFX(700,500);

        sSboard.setPlayerBoard(pb);
        sSboard.startAnimating();

        sSRightStuff = new VBox();
        sSRightStuff.setStyle("-fx-background-color: red");

        sSShipsStatus = new HBox();
        sSShipsStatus.setStyle("-fx-background-color: green;");
        sSTips = new VBox();

        //TIPS
        sSTips.setSpacing(5);
        sSTips.getChildren().add(new Text("Hey!"));
        sSTips.getChildren().add(new Text(
                " +Left-Mouse to select a ship; Left-Mouse again to deselect it"));
        sSTips.getChildren().add(new Text(" +R while a ship is selected to rotate it"));
        sSTips.getChildren().add(new Text(" +Green means it can be placed there; Left-Mouse to do that"));
        sSTips.getChildren().add(new Text(" +Red means it can't be placed there"));

        sSShipsStatus.getChildren().addAll(sSTips);

        sSReadyBox = new HBox();
        sSReadyBox.setStyle("-fx-background-color: yellow;");

        sSRandomButton = new Button("Random");
        sSRandomButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                pb = PlayerBoard.getRandomPlayerBoard();
                sSboard.doShips(pb);
                //System.out.println(pb);
            }
        });

        sSReadyButton = new Button("Ready");

        sSReadyButton.setOnMouseClicked(new EventHandler<MouseEvent>() {


            @Override
            public void handle(MouseEvent event) {
                if(sSboard.pb.fullOfShips()) {

                    sSReadyButton.setDisable(true);
                    sSRandomButton.setDisable(true);

                    pb = sSboard.pb;

                    //System.out.println(pb);

                    mGSelfBoard.setPlayerBoard(pb);
                    mGSelfBoard.startTiles(pb.getToPaint());
                    mGSelfBoard.updateTiles(pb.getToPaint());
                    mGSelfBoard.startAnimating();
                    client.sendTCP(sSboard.pb.getToPaint());

                }
            }
        });


        sSReadyBox.getChildren().addAll(sSRandomButton, sSReadyButton);

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

        sSRoot.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(sSboard.selected != null && event.getCode() == KeyCode.R){
                    sSboard.toRotate = !sSboard.toRotate;
                }
            }
        });
        sSboard.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                sSboard.seeIfShipFXCanBePlaced(event.getX(), event.getY());
            }
        });
        sSboard.setOnMouseClicked(new EventHandler<MouseEvent>() {

            ShipFX current = null;
            boolean haveAShip = false;
            boolean toRemove = false;

            @Override
            public void handle(MouseEvent event) {

                ShipFX result = sSboard.checkAShip(event.getX(), event.getY());

                if(haveAShip && result == current){
                    if(toRemove) {
                        sSboard.placeShipFX(event.getX(), event.getY());
                        toRemove = false;
                    }
                    haveAShip = false;
                    current = null;
                    sSboard.setSelected(null);
                    return;
                }

                //ALREADY HAVE 1, HIT WATER AND ALREADY PLACED
                if(haveAShip && result == null && current.placed){
                    if(sSboard.canPlace(event.getX(), event.getY())) {
                        sSboard.placeShipFX(event.getX(), event.getY());
                        haveAShip = false;
                        current = null;
                    }
                    return;
                }

                //ALREADY HAVE 1, HIT WATER AND NOT PLACED
                if (haveAShip && result == null) {
                    if(sSboard.canPlace(event.getX(), event.getY())) {
                        sSboard.placeShipFX(event.getX(), event.getY());
                        haveAShip = false;
                        current = null;
                    }
                    return;
                }

                if (result != null &&!haveAShip && result.placed) {
                    toRemove = true;
                    sSboard.removeShipFX(result);
                    sSboard.setSelected(result);
                    current = result;
                    haveAShip = true;
                    return;
                }

                if (result != null &&!haveAShip) {
                    sSboard.setSelected(result);
                    current = result;
                    haveAShip = true;
                }
            }
        });

        setShips = new Scene(sSRoot, SCREEN_RECTANGLE.getWidth(),
                SCREEN_RECTANGLE.getHeight());
    }

    private void setAttackScreen(){

        ene1 = new EnemyLocal();
        ene2 = new EnemyLocal();
        lastAttacked = new EnemyLocal();
        iCanAttack = false;

        ene1.b = new GraphBoardFX();
        ene2.b = new GraphBoardFX();

        VBox vBox = new VBox(50);
        vBox.getChildren().addAll(ene1.b, new Button(ene1.name));

        VBox vBox2 = new VBox(50);
        vBox2.getChildren().addAll(ene2.b ,new Button(ene2.name));

        Button back = new Button("BACK");
        back.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                transitionTo(mainGame);
            }
        });

        aWRoot = new HBox(200);
        aWRoot.setAlignment(Pos.CENTER);
        aWRoot.setPrefSize(SCREEN_RECTANGLE.getWidth(), SCREEN_RECTANGLE.getHeight());
        aWRoot.setStyle("-fx-background-image: url(images/BattleShipBigger2.png)");

        aWRoot.getChildren().addAll(vBox, vBox2, back);


        ene1.b.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                lastAttacked = ene1;

                if (iCanAttack) {
                    iCanAttack = false;

                    Point p = ene1.b.pointCoordinates(event);
                    System.out.println(p);

                    AnAttackAttempt anAttackAttempt = new AnAttackAttempt();
                    anAttackAttempt.l = p.x;
                    anAttackAttempt.c = p.y;
                    anAttackAttempt.toAttackID = ene1.serverID;
                    anAttackAttempt.otherID = ene2.serverID;

                    client.sendTCP(anAttackAttempt);
                }
            }
        });


        ene2.b.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                lastAttacked = ene2;
                if (iCanAttack) {
                    iCanAttack = false;

                    Point p = ene2.b.pointCoordinates(event);
                    System.out.println(p);

                    AnAttackAttempt anAttackAttempt = new AnAttackAttempt();
                    anAttackAttempt.l = p.x;
                    anAttackAttempt.c = p.y;
                    anAttackAttempt.toAttackID = ene2.serverID;
                    anAttackAttempt.otherID = ene1.serverID;

                    client.sendTCP(anAttackAttempt);
                }
            }
        });

        attackScene = new Scene(aWRoot, SCREEN_RECTANGLE.getWidth(), SCREEN_RECTANGLE.getHeight());

    }

    private static class EnemyLocal {
        private int serverID;
        private GraphBoardFX b;
        private boolean alive = true;
        private String name;
        private ArrayList<String> conversation;
        private EnemyLocal(){
            serverID = 0;
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
            return board.getToPaint();
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


