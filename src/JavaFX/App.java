package JavaFX;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Cell;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;

import static Common.PlayerBoard.COLUMNS;
import static Common.PlayerBoard.LINES;

public class App extends Application {

    Scene mainMenu;
    Scene mainGame;
    private final static Rectangle2D SCREEN_RECTANGLE = Screen.getPrimary().getVisualBounds();

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        setAllScenes(primaryStage);

        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("BS");
        primaryStage.setResizable(false);
        primaryStage.setMaximized(true);
        primaryStage.setScene(mainMenu);
        primaryStage.show();

    }

    private void setAllScenes(Stage stage){
        setMainMenu(stage);
        setMainGame(stage);
    }

    private void setMainGame(Stage stage) {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color:white");

        GridPane gridPane = new GridPane();
        gridPane.setStyle("-fx-background-color:cyan");

        StackPane top = new StackPane();
        top.setStyle("-fx-background-color:red");

        Text text = new Text("IS PLAYING");
        top.getChildren().add(text);

        VBox right = new VBox();
        right.setStyle("-fx-background-color:green");
        right.setSpacing(50);

        Circle ships = new Circle();
        ships.setRadius(50);
        Button button = new Button("TO ARMS");
        Button chat = new Button("CHAT");

        VBox.setVgrow(chat, Priority.ALWAYS);
        VBox.setVgrow(button, Priority.ALWAYS);
        VBox.setVgrow(ships, Priority.ALWAYS);

        chat.setPrefHeight(Integer.MAX_VALUE);
        button.setPrefHeight(Integer.MAX_VALUE);

        right.getChildren().addAll(ships, button, chat);

        root.setRight(right);
        root.setTop(top);
        root.setCenter(gridPane);

        mainGame = new Scene(root, SCREEN_RECTANGLE.getWidth(), SCREEN_RECTANGLE.getHeight());

        Platform.runLater(() -> {
            for (int l = 0; l < LINES; l++) {
                for (int c = 0; c < COLUMNS; c++) {
                    Image i = new Image("images/boat_temp3.png");
                    ImageView iv = new ImageView(i);
                    gridPane.add(iv, l, c);
                }
            }
        });
    }

    private void setMainMenu(Stage stage){

        BorderPane root = new BorderPane();
        root.setPrefSize(SCREEN_RECTANGLE.getWidth(), SCREEN_RECTANGLE.getHeight());

        String imagePath = "images/BattleShipBigger.png";
        Image image = new Image(imagePath);

        BackgroundImage bg = new BackgroundImage(image,
                BackgroundRepeat.REPEAT,
                BackgroundRepeat.REPEAT,
                null,
                new BackgroundSize(image.getWidth(), image.getHeight(), false, false, true, true));

        //root.setBackground(new Background(bg));
        root.setStyle("-fx-background-color: white");

        Image playImage = new Image("images/start_medium.png");

        Button play = new Button();
        play.setGraphic(new ImageView(playImage));
        play.setOnAction(event ->
                System.out.println("COOL DUDE")
        );
        play.setStyle("-fx-background-color: transparent;");

        Image aloneImage = new Image("images/alone_medium.png");

        Button alone = new Button();
        alone.setGraphic(new ImageView(aloneImage));
        alone.setOnAction(event -> {
            stage.setScene(mainGame);
        });
        alone.setStyle("-fx-background-color: transparent;");

        Image exitImage = new Image("images/exit_medium.png");

        Button exit = new Button();
        exit.setGraphic(new ImageView(exitImage));
        exit.setOnAction(event ->
                Platform.exit()
        );
        exit.setStyle("-fx-background-color: transparent;");


        StackPane middle = new StackPane();
        middle.setStyle("-fx-background-color:cyan;");
        middle.getChildren().addAll(play,alone,exit);
        root.setCenter(middle);

        mainMenu = new Scene(root, SCREEN_RECTANGLE.getWidth(),
                SCREEN_RECTANGLE.getHeight()
        );

        Platform.runLater(() -> {
            alone.setTranslateX(play.getWidth() + 25);
            exit.setTranslateX(play.getWidth()/2);
            exit.setTranslateY(play.getHeight() + 25);
        });
    }

    class ImagePane extends Pane {
        // size an image by placing it in a pane.
        ImagePane(String imageLoc) {
            this(imageLoc, "-fx-background-size: cover; -fx-background-repeat: no-repeat;");
        }

        // size an image by placing it in a pane.
        ImagePane(String imageLoc, String style) {
            this(new SimpleStringProperty(imageLoc), new SimpleStringProperty(style));
        }

        // size a replacable image in a pane and add a replaceable style.
        ImagePane(StringProperty imageLocProperty, StringProperty styleProperty) {
            styleProperty().bind(
                    new SimpleStringProperty("-fx-background-image: url(\"")
                            .concat(imageLocProperty)
                            .concat(new SimpleStringProperty("\");"))
                            .concat(styleProperty)
            );
        }
    }
}
