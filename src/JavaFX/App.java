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
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import Common.Network.*;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.*;

public class App extends Application {

    //FOR OFFLINE
    private MyAI ai;
    private boolean vsAI;
    private SelfGraphBoardFX selfvsAI;

    //FOR ONLINE
    private Client client;
    private String myName;
    private static final String ADDRESS = "82.154.150.115";
    private PlayerBoard pb;

    private String soundFile = "assets/sound/play.mp3";
    private AudioClip soundPlayer = new AudioClip(new File(soundFile).toURI().toString());

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

    private Image mMPlayButtonImage = new Image("images/Botao_Start.png");
    private Button mMPlayButton;

    private Image mMAloneButtonImage = new Image("images/Botao_Solo_Play.png");
    private Button mMAloneButton;

    private Image mMExitButtonImage = new Image("images/Botao_Exit.png");
    private Button mMExit;

    private TextField mMnameInput;
    private Label mMServerText;

    private GridPane mMMiddle;

    //endregion

    //region SET SHIPS STUFF

    private HBox sSRoot;

    private ShipsBoardFX sSboard;

    private VBox sSRightStuff;

    private HBox sSPlaceIntructions;
    private VBox sSTips;

    private HBox sSReadyBox;
    private Button sSRandomButton;
    private Button sSReadyButton;

    private VBox sSInstructionsGame;
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
    private VBox aWvBox = new VBox(50);
    private VBox aWvBox2 = new VBox(50);

    private String aWshipSoundFile = "assets/sound/ship.mp3";
    private String aWwaterSoundFile = "assets/sound/water.mp3";

    private MediaPlayer aWShipSound = new MediaPlayer(new Media(new File(aWshipSoundFile).toURI().toString()));
    private MediaPlayer aWWaterSound = new MediaPlayer(new Media(new File(aWwaterSoundFile).toURI().toString()));

    //endregion

    private TextArea textArea;

    private Label cWl1;
    private Label cWl2;

    private ArrayList<EmptyGraphBoardFX> toAnimate = new ArrayList<>();

    private final static Rectangle2D SCREEN_RECTANGLE = Screen.getPrimary().getVisualBounds();

    //SCENES

    private Scene mainMenu;
    private Scene mainGame;
    private Scene setShips;
    private Scene attackScene;
    private Scene wonScene;
    private Scene chatScreen;
    private Scene waitingScreen;
    private Scene AIScene;

    private Stage theStage;

    public static void main(String[] args) {
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
                while (inetAddresses.hasMoreElements()) {
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
            UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN ADDRESS: " + e);
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
                    transitionTo(mainMenu);
                if (object instanceof Abort) {
                    //
                }

                if (object instanceof CanStart)
                    Platform.runLater(() -> transitionTo(mainGame));

                if (object instanceof WhoseTurn) {
                    WhoseTurn whoseTurn = (WhoseTurn) object;
                    Platform.runLater(() -> setTurnLabel(whoseTurn.name));
                }
                if (object instanceof ConnectedPlayers) {
                    System.out.println("CONNECTED PLAYERS");
                    ConnectedPlayers players = (ConnectedPlayers) object;
                    Platform.runLater(() -> {
                                textArea.clear();
                                for (String name : players.names)
                                    textArea.appendText(name + "\n");
                            }
                    );
                }

                if (object instanceof ReadyForShips)
                    Platform.runLater(() -> transitionTo(setShips));

                if (object instanceof OthersSpecs) {
                    OthersSpecs othersSpecs = (OthersSpecs) object;

                    Platform.runLater(() -> {

                        ene1.serverID = othersSpecs.ene1;
                        ene1.name = othersSpecs.ene1n;
                        ene1.labeln.setText(ene1.name);
                        cWl1.setText(ene1.name);

                        ene2.serverID = othersSpecs.ene2;
                        ene2.name = othersSpecs.ene2n;
                        ene2.labeln.setText(ene2.name);
                        cWl2.setText(ene2.name);

                    });

                }

                if (object instanceof YourBoardToPaint)
                    //System.out.println("MY BOARD TO PAINT");
                    Platform.runLater(() -> mGSelfBoard.updateTiles(((YourBoardToPaint) object).board));

                if (object instanceof EnemiesBoardsToPaint) {
                    //System.out.println("ENEMIES BOARDS TO PAINT");
                    Platform.runLater(() -> {
                        ene1.b.startTiles(((EnemiesBoardsToPaint) object).board1);
                        ene2.b.startTiles(((EnemiesBoardsToPaint) object).board2);
                    });
                }

                if (object instanceof EnemyBoardToPaint) {
                    Platform.runLater(() -> {
                        EnemyBoardToPaint ebp = (EnemyBoardToPaint) object;
                        updateEnemyBoard(ebp.id, ebp.newAttackedBoard);
                        System.out.println("ENEMY BOARD TO PAINT WITH INDEX " + ebp.id);
                    });
                }

                if (object instanceof AnAttackResponse) {
                    Platform.runLater(() -> {
                        lastAttacked.b.updateTiles(((AnAttackResponse) object).newAttackedBoard);
                        iCanAttack = ((AnAttackResponse) object).again;
                        doSounds(((AnAttackResponse) object).actualHit, ((AnAttackResponse) object).shipHit);
                    });
                }

                if (object instanceof YourTurn) {
                    Platform.runLater(() -> {
                        iCanAttack = true;
                        setTurnLabel("My TURN!!");
                    });
                }

                if (object instanceof YouDead) {
                    Platform.runLater(() -> {
                        lost("You died a horrible death. RIP you");
                        transitionTo(mainMenu);
                    });
                }

                if (object instanceof PlayerDied) {
                    Platform.runLater(() -> {
                        removeEnemy(((PlayerDied) object).who);
                    });
                }

                if (object instanceof YouWon) {
                    Platform.runLater(() -> {
                        Alert lost = new Alert(Alert.AlertType.CONFIRMATION);
                        lost.setContentText("YOU BEAT THEM ALL");
                        lost.showAndWait();
                        won();
                    });
                }

                if (object instanceof ChatMessage) {
                    Platform.runLater(() -> {
                        EnemyLocal toUpdate = ene1;
                        if (((ChatMessage) object).saidIt == ene2.serverID) {
                            toUpdate = ene2;
                        }
                        toUpdate.conversation.setText(toUpdate.conversation.getText() + ((ChatMessage) object).message);
                    });
                }
            }
        });

    }

    private void reset(){
        setShipsScene();
    }

    private void lost(String s) {
        Alert lost = new Alert(Alert.AlertType.INFORMATION);
        lost.setContentText(s);
        lost.showAndWait();
        reset();
    }


    private void won() {
        reset();
        transitionTo(wonScene);
    }

    private void removeEnemy(int who) {
        if (who == ene2.serverID)
            aWRoot.getChildren().remove(aWvBox2);
        else
            aWRoot.getChildren().remove(aWvBox);
    }

    private void updateEnemyBoard(int id, String[][] newAttackedBoard) {
        EnemyLocal toUpdate = ene1;

        if (id == ene2.serverID) {
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

        vsAI = false;

        theStage = primaryStage;
        setAllScenes();

        theStage.initStyle(StageStyle.UNDECORATED);
        theStage.setTitle("BS");
        theStage.setResizable(false);
        theStage.setMaximized(true);
        theStage.setScene(mainMenu);
        theStage.show();

        soundPlayer.setVolume(.2);
        soundPlayer.setCycleCount(AudioClip.INDEFINITE);
        soundPlayer.play();

        aWShipSound.setVolume(1);
        aWWaterSound.setVolume(.1);

    }

    private void setAllScenes() {
        setMainMenu();
        setMainGame();
        setShipsScene();
        setAttackScreen();
        setChatScreen();
        setWonScene();
        setAIScene();
    }

    /**
     * CALL BEFORE OTHER STUFF
     *
     * @param scene
     */
    private void transitionTo(Scene scene) {
        for (EmptyGraphBoardFX g : toAnimate)
            g.stopAnimating();
        toAnimate.clear();
        theStage.setScene(scene);
        if (scene == mainGame)
            toAnimate.add(mGSelfBoard);
        if (scene == attackScene) {
            toAnimate.add(ene1.b);
            toAnimate.add(ene2.b);
        }
        if (scene == setShips)
            toAnimate.add(sSboard);
        if (scene == AIScene) {
            toAnimate.add(selfvsAI);
            toAnimate.add(ai.b);
        }
        for (EmptyGraphBoardFX g : toAnimate)
            g.startAnimating();
    }

    private void setMainGame() {

        MGRoot = new BorderPane();
        MGRoot.setStyle("-fx-background-image: url(images/BattleShipBigger2.png);-fx-background-size: cover;");

        //GridPane gridPane = new GridPane();
        //gridPane.setStyle("-fx-background-color:cyan");

        MGCanvasHolder = new Group();

        mGSelfBoard = new SelfGraphBoardFX(500, 500);
        mGSelfBoard.startTiles(PlayerBoard.getRandomPlayerBoard().getToPaint());

        MGCanvasHolder.getChildren().add(mGSelfBoard);

        MGTop = new StackPane();

        mGcurrentPlayerText = new Label("IS PLAYING");
        mGcurrentPlayerText.setFont(new Font(30));
        MGTop.getChildren().add(mGcurrentPlayerText);

        MGRight = new VBox(50);

        MGAttackButton = new Button("TO ARMS");
        MGAttackButton.setOnMouseClicked(event -> transitionTo(attackScene));
        MGChatButton = new Button("CHAT");
        MGChatButton.setOnMouseClicked(event -> transitionTo(chatScreen));

        VBox.setVgrow(MGAttackButton, Priority.ALWAYS);
        VBox.setVgrow(MGChatButton, Priority.ALWAYS);

        MGRight.getChildren().addAll(MGAttackButton, MGChatButton);

        MGRoot.setRight(MGRight);
        MGRoot.setTop(MGTop);
        MGRoot.setCenter(MGCanvasHolder);

        mainGame = new Scene(MGRoot, SCREEN_RECTANGLE.getWidth(), SCREEN_RECTANGLE.getHeight());
    }

    private void setMainMenu() {

        mMRoot = new BorderPane();
        mMRoot.setStyle("-fx-background-image: url(images/BattleShip.png);-fx-background-size: cover;");

        mMPlayButton = new Button();
        mMPlayButton.setGraphic(new ImageView(mMPlayButtonImage));

        mMPlayButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                myName = mMnameInput.getText();
                if (myName.equals("")) {
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
                            client.connect(5000, ADDRESS, Network.port);
                        } catch (IOException e) {
                            connected = false;
                        }
                        return connected;
                    }
                };
                connect.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {
                        if (connect.getValue()) {
                            Register r = new Register();
                            r.name = myName;
                            try {
                                r.address = getMeIPV4().toString();
                            } catch (UnknownHostException e1) {
                                e1.printStackTrace();
                                r.address = "100:00";
                            }

                            BorderPane root = new BorderPane();
                            textArea = new TextArea();
                            textArea.setEditable(false);

                            root.setCenter(textArea);

                            waitingScreen = new Scene(root, SCREEN_RECTANGLE.getWidth(), SCREEN_RECTANGLE.getHeight());
                            transitionTo(waitingScreen);
                            client.sendTCP(r);
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("YEE");
                            alert.setHeaderText("WE GOT IN");
                            alert.setContentText("TEMP MESSAGE TO SAY WE GOT IN; WAIT NOW! DON'T PRESS ANY MORE SHIT");
                            alert.showAndWait();
                        } else {
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
        mMAloneButton.setOnAction(event -> {
                    vsAI = true;
                    theStage.setScene(setShips);
                }
        );
        mMAloneButton.setStyle("-fx-background-color: transparent;");

        mMExit = new Button();
        mMExit.setGraphic(new ImageView(mMExitButtonImage));
        mMExit.setOnAction(event ->
                Platform.exit()
        );
        mMExit.setStyle("-fx-background-color: transparent;");

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
        mMMiddle.setStyle("-fx-fill: true; -fx-alignment:bottom-center; -fx-padding: 50");

        //mMMiddle.getChildren().addAll(mMPlayButton,mMAloneButton, mMExit, mMServerText, mMnameInput);
        mMRoot.setCenter(mMMiddle);
        //mMMiddle.setStyle("-fx-background-color:cyan;");

        mainMenu = new Scene(mMRoot, SCREEN_RECTANGLE.getWidth(),
                SCREEN_RECTANGLE.getHeight()
        );
    }

    private void setTurnLabel(String name) {
        mGcurrentPlayerText.setText(name);
    }

    private void setShipsScene() {

        sSRoot = new HBox();
        sSRoot.setStyle("-fx-background-image: url(images/BattleShipBigger2.png);-fx-background-size: cover;");

        pb = new PlayerBoard();
        sSboard = new ShipsBoardFX(700, 500);

        sSboard.setPlayerBoard(pb);
        sSboard.startAnimating();

        sSRightStuff = new VBox();

        sSPlaceIntructions = new HBox();
        sSPlaceIntructions.setStyle("-fx-background-color: grey;");

        sSTips = new VBox();

        //TIPS
        sSTips.setSpacing(5);
        sSTips.getChildren().add(new Text("Hey!"));
        sSTips.getChildren().add(new Text(
                " +Left-Mouse to select a ship; Left-Mouse again to deselect it"));
        sSTips.getChildren().add(new Text(" +R while a ship is selected to rotate it"));
        sSTips.getChildren().add(new Text(" +Green means it can be placed there; Left-Mouse to do that"));
        sSTips.getChildren().add(new Text(" +Red means it can't be placed there"));

        sSPlaceIntructions.getChildren().addAll(sSTips);

        sSReadyBox = new HBox();
        sSReadyBox.setStyle("-fx-background-color: yellow;");

        sSRandomButton = new Button("Random");
        sSRandomButton.setFont(new Font(50));
        sSRandomButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                pb = PlayerBoard.getRandomPlayerBoard();
                sSboard.doShips(pb);
                sSboard.setSelected(null);
                //System.out.println(pb);
            }
        });

        sSReadyButton = new Button("Ready");
        sSReadyButton.setFont(new Font(50));

        sSReadyButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (sSboard.pb.fullOfShips()) {

                    sSReadyButton.setDisable(true);
                    sSRandomButton.setDisable(true);
                    sSboard.finished = true;

                    pb = sSboard.pb;

                    if (!vsAI) {
                        mGSelfBoard.setPlayerBoard(pb);
                        mGSelfBoard.startTiles(pb.getToPaint());
                        mGSelfBoard.updateTiles(pb.getToPaint());
                        APlayerboard p = new APlayerboard();
                        p.board = sSboard.pb.getToPaint();
                        client.sendTCP(p);
                    } else {
                        selfvsAI.startTiles(pb.getToPaint());
                        transitionTo(AIScene);
                    }
                }
            }
        });

        sSReadyBox.getChildren().addAll(sSRandomButton, sSReadyButton);

        sSInstructionsGame = new VBox();
        sSInstructionsGame.setStyle("-fx-background-color: grey;");
        sSInstructionsGame.setSpacing(5);
        sSInstructionsGame.getChildren().add(new Text("Instructions: "));
        sSInstructionsGame.getChildren().add(new Text(
                " +When you hit a ship piece, you get to go again"));
        sSInstructionsGame.getChildren().add(new Text(" +Missing means it is now somebody else's turn"));
        sSInstructionsGame.getChildren().add(new Text(" +If you destroy a ship, surrounding area will be shown"));

        sSRightStuff.getChildren().addAll(sSPlaceIntructions, sSReadyBox, sSInstructionsGame);

        sSRoot.getChildren().addAll(sSboard, sSRightStuff);
        sSRoot.setPadding(new Insets(25));
        sSRoot.setSpacing(10);

        sSRoot.setOnKeyPressed(new EventHandler<>() {
            @Override
            public void handle(KeyEvent event) {
                if (sSboard.selected != null && event.getCode() == KeyCode.R) {
                    sSboard.toRotate = !sSboard.toRotate;
                }
            }
        });
        sSboard.setOnMouseMoved(new EventHandler<>() {
            @Override
            public void handle(MouseEvent event) {
                if (!sSboard.finished) {
                    sSboard.seeIfShipFXCanBePlaced(event.getX(), event.getY());
                }
            }
        });
        sSboard.setOnMouseClicked(new EventHandler<>() {

            ShipFX current = null;
            boolean haveAShip = false;
            boolean toRemove = false;

            @Override
            public void handle(MouseEvent event) {

                if (!sSboard.finished) {

                    ShipFX result = sSboard.checkAShip(event.getX(), event.getY());

                    if (haveAShip && result == current) {
                        if (toRemove) {
                            sSboard.placeShipFX(event.getX(), event.getY());
                            toRemove = false;
                        }
                        haveAShip = false;
                        current = null;
                        sSboard.setSelected(null);
                        return;
                    }

                    //ALREADY HAVE 1, HIT WATER AND ALREADY PLACED
                    if (haveAShip && result == null && current.placed) {
                        if (sSboard.canPlace(event.getX(), event.getY())) {
                            sSboard.placeShipFX(event.getX(), event.getY());
                            haveAShip = false;
                            current = null;
                        }
                        return;
                    }

                    //ALREADY HAVE 1, HIT WATER AND NOT PLACED
                    if (haveAShip && result == null) {
                        if (sSboard.canPlace(event.getX(), event.getY())) {
                            sSboard.placeShipFX(event.getX(), event.getY());
                            haveAShip = false;
                            current = null;
                        }
                        return;
                    }

                    if (result != null && !haveAShip && result.placed) {
                        toRemove = true;
                        sSboard.removeShipFX(result);
                        sSboard.setSelected(result);
                        current = result;
                        haveAShip = true;
                        return;
                    }

                    if (result != null && !haveAShip) {
                        sSboard.setSelected(result);
                        current = result;
                        haveAShip = true;
                    }
                }
            }
        });





        setShips = new Scene(sSRoot, SCREEN_RECTANGLE.getWidth(),
                SCREEN_RECTANGLE.getHeight());
    }

    private void setAttackScreen() {

        ene1 = new EnemyLocal();
        ene2 = new EnemyLocal();
        lastAttacked = new EnemyLocal();
        iCanAttack = false;

        ene1.b = new GraphBoardFX();
        ene2.b = new GraphBoardFX();
        ene1.b.startTiles(PlayerBoard.getRandomPlayerBoard().getToPaint());
        ene2.b.startTiles(PlayerBoard.getRandomPlayerBoard().getToPaint());
        ene1.b.startAnimating();
        ene2.b.startAnimating();

        ene1.labeln = new Label("ENEMY 1");
        ene1.labeln.setFont(new Font("Verdana", 30));
        ene1.labeln.setTextFill(Color.rgb(0, 0, 0));

        ene2.labeln = new Label("ENEMY 2");
        ene2.labeln.setFont(new Font("Verdana", 30));
        ene2.labeln.setTextFill(Color.rgb(0, 0, 0));

        aWvBox = new VBox(10);
        aWvBox.getChildren().addAll(ene1.b, ene1.labeln);

        aWvBox2 = new VBox(10);
        aWvBox2.getChildren().addAll(ene2.b, ene2.labeln);

        Button back = new Button("BACK");
        back.setOnMouseClicked(event -> transitionTo(mainGame));

        aWRoot = new HBox(50);
        aWRoot.setStyle("-fx-background-image: url(images/BattleShipBigger2.png);-fx-background-size: cover;");

        aWRoot.getChildren().addAll(aWvBox, aWvBox2, back);

        ene1.b.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                lastAttacked = ene1;
                if (iCanAttack) {
                    iCanAttack = false;

                    Point p = ene1.b.pointCoordinates(event);

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

    private void setWonScene() {
        StackPane root = new StackPane();
        root.setStyle("-fx-background-image: url(images/BattleShipBigger2.png)");
        Button b = new Button("Go To Main Menu");
        b.setOnMouseClicked(event -> transitionTo(mainMenu));
        b.setStyle("-fx-alignment: center");
        root.getChildren().add(b);

        wonScene = new Scene(root, SCREEN_RECTANGLE.getWidth(), SCREEN_RECTANGLE.getHeight());
    }

    private void setChatScreen() {

        Button n = new Button("BACK");
        n.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                transitionTo(mainGame);
            }
        });

        cWl1 = new Label();
        cWl1.setFont(new Font(30));
        cWl2 = new Label();
        cWl2.setFont(new Font(30));

        VBox vBox1 = new VBox(20);
        VBox vBox2 = new VBox(20);

        ene1.conversation = new TextArea();
        ene1.conversation.setEditable(false);
        ene1.conversation.setWrapText(true);

        TextArea tf1 = new TextArea();
        tf1.setWrapText(true);
        tf1.setMinSize(tf1.getPrefWidth() * 2, tf1.getPrefHeight() * 2);
        tf1.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String message = tf1.getText();
                tf1.clear();
                ChatMessageFromClient c = new ChatMessageFromClient();
                ene1.conversation.appendText("ME: " + message);
                c.text = message;
                c.to = ene1.serverID;
                client.sendTCP(c);
            }
        });

        ene2.conversation = new TextArea();
        ene2.conversation.setEditable(false);
        ene2.conversation.setWrapText(true);

        TextArea tf2 = new TextArea();
        tf2.setWrapText(true);
        tf2.setMinSize(tf2.getPrefWidth(), tf2.getPrefHeight());
        tf2.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String message = tf2.getText();
                tf2.clear();
                ChatMessageFromClient c = new ChatMessageFromClient();
                ene2.conversation.appendText("ME: " + message);
                c.text = message;
                c.to = ene2.serverID;
                client.sendTCP(c);
            }
        });

        vBox1.getChildren().addAll(cWl1, ene1.conversation, tf1);
        vBox2.getChildren().addAll(cWl2, ene2.conversation, tf2);

        VBox.setVgrow(ene1.conversation, Priority.ALWAYS);
        VBox.setVgrow(ene2.conversation, Priority.ALWAYS);

        HBox hBox = new HBox(50);
        HBox.setHgrow(vBox1, Priority.ALWAYS);
        HBox.setHgrow(vBox2, Priority.ALWAYS);
        hBox.getChildren().addAll(vBox1, vBox2, n);

        chatScreen = new Scene(hBox, SCREEN_RECTANGLE.getWidth(), SCREEN_RECTANGLE.getHeight());

    }

    private void setAIScene() {

        selfvsAI = new SelfGraphBoardFX(500,500);
        ai = new MyAI();
        iCanAttack = true;

        Label label = new Label("YOU!");
        label.setFont(new Font("Verdana", 30));
        label.setTextFill(Color.ALICEBLUE);

        VBox forYou = new VBox(10);
        forYou.getChildren().addAll(selfvsAI, label);

        Label ene = new Label("ENEMY(AI)!");
        ene.setFont(new Font("Verdana", 30));
        ene.setTextFill(Color.ROSYBROWN);

        VBox forAI = new VBox(10);
        forAI.getChildren().addAll(ai.b, ene);

        Button back = new Button("BACK/FORFEIT");
        back.setOnMouseClicked(event -> { reset(); transitionTo(mainMenu); });

        HBox root = new HBox(50);
        root.setStyle("-fx-fill: true; -fx-alignment:center");
        root.setStyle("-fx-background-image: url(images/BattleShipBigger2.png);-fx-background-size: cover;");

        root.getChildren().addAll(forYou, forAI, back);

        ai.b.setOnMouseClicked(new EventHandler<>() {
            @Override
            public void handle(MouseEvent event) {
                if (iCanAttack) {
                    Point p = ai.b.pointCoordinates(event);
                    iCanAttack = ai.attacked(p);
                    doSounds(ai.board);
                    if(ai.board.isGameOver()) {
                        won();
                        return;
                    }
                    if (!iCanAttack)
                        aiTurn();
                }
            }
        });

        AIScene = new Scene(root, SCREEN_RECTANGLE.getWidth(), SCREEN_RECTANGLE.getHeight());

    }

    private void doSounds(PlayerBoard pb){
        doSounds(pb.actualNewHit(), pb.isShipHit());
    }


    private void doSounds(boolean actualHit, boolean shipHit) {
        aWShipSound.stop();
        aWWaterSound.stop();
        if(actualHit){
            if(shipHit)
                aWShipSound.play();
            else
                aWWaterSound.play();
        }
    }

    private void aiTurn() {
        Point p = ai.chooseAttack(pb.getAvailable());
        System.out.println("CHOSE " + p);
        boolean hit = false;
        boolean destroyed = false;
        if (pb.getAttacked(p.x, p.y)) {
            //ACTUAL HIT AND NOT AN ALREADY ATTACKED POSITION
            hit = pb.actualNewHit();
            destroyed = pb.lastShipDestroyed();
        }
        ai.thinkAboutNext(pb.getAvailable(), hit, destroyed);
        selfvsAI.updateTiles(pb.getToPaint());
        selfvsAI.setLast(p);
        if(hit && !pb.isGameOver()) {
            Task<Void> wait = new Task<> () {
                @Override
                protected Void call() throws Exception {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                    return null;
                }
            };
            wait.setOnSucceeded(event -> aiTurn());
            new Thread(wait).start();
        }
        else if(hit &&pb.isGameOver())
            lost("YOU LOST TO AI LOL!");
        else
            iCanAttack = true;
    }

    private static class EnemyLocal {
        private int serverID;
        private GraphBoardFX b;
        private String name;
        private Label labeln;
        private TextArea conversation;
        private EnemyLocal(){
            serverID = 0;
        }
    }

    private static class MyAI {

        GraphBoardFX b;
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
            b = new GraphBoardFX();
            b.startTiles(board.getToPaint());
            //gb = new GraphBoardFX(getToPaint());
        }

        //POINT WITH DIRECTION
        private Point pWD(Point p, Direction dir){
            return new Point(p.x + dir.getDirectionVector()[0], p.y + dir.getDirectionVector()[1]);
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
                //FILTER OUT ALREADY ATTACKED

                System.out.println("WAS AN OLD TARGET");

                //FAILED
                if(!hit) {
                    justBefore = firstHit;
                    int size = directionsToGo.size();

                    for(int i = size - 1; i >= 0; i--){
                        Point n = new Point(justBefore.x + directionsToGo.get(i).getDirectionVector()[0],
                                justBefore.y + directionsToGo.get(i).getDirectionVector()[1]);
                        if(!pos.contains(n))
                            directionsToGo.remove(i);
                    }
                    directionLooking = directionsToGo.get(0);
                }
                else
                    if(!pos.contains(pWD(justBefore, directionLooking))) {
                        justBefore = firstHit;
                        directionLooking = directionLooking.getOpposite();
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

        public boolean attacked(Point p) {
            boolean res = board.getAttacked(p.x, p.y);
            b.updateTiles(board.getToPaint());
            return res;
        }
    }

}


