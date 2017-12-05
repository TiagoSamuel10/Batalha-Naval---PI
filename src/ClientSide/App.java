package ClientSide;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Cell;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.net.URL;

public class App extends Application {

    Scene mainMenu;

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        GridPane interior = new GridPane();
        interior.setAlignment(Pos.BOTTOM_CENTER);

        String imagePath = "images/BattleShipBigger.png";
        Image image = new Image(imagePath);

        BorderPane root = new BorderPane();
        root.setCenter(interior);
        BackgroundImage bg = new BackgroundImage(image,
                BackgroundRepeat.REPEAT,
                BackgroundRepeat.REPEAT,
                null,
                new BackgroundSize(image.getWidth(), image.getHeight(), false, false, true, true));

        root.setBackground(new Background(bg));

        Image playImage = new Image("images/start_medium.png");

        Button play = new Button();
        play.setGraphic(new ImageView(playImage));
        play.setOnAction(event ->
                System.out.println("COOL DUDE")
        );
        play.setStyle("-fx-background-color: transparent;");

        GridPane.setConstraints(play, 0, 0);

        Image aloneImage = new Image("images/solo_small.png");

        Button alone = new Button();
        alone.setGraphic(new ImageView(aloneImage));
        alone.setOnAction(event ->
                System.out.println("COOL DUDE")
        );
        alone.setStyle("-fx-background-color: transparent;");

        GridPane.setConstraints(alone, 2, 0);

        Image exitImage = new Image("images/exit_medium.png");

        Button exit = new Button();
        exit.setGraphic(new ImageView(exitImage));
        exit.setOnAction(event ->
                Platform.exit()
        );
        exit.setStyle("-fx-background-color: transparent;");

        GridPane.setConstraints(exit, 1, 1);

        interior.getChildren().addAll(play, alone, exit);

        mainMenu = new Scene(root, Screen.getPrimary().getVisualBounds().getWidth(),
                Screen.getPrimary().getVisualBounds().getHeight()
        );

        primaryStage.initStyle(StageStyle.UNDECORATED);
        primaryStage.setTitle("BS");
        primaryStage.setResizable(false);
        primaryStage.setMaximized(true);
        primaryStage.setScene(mainMenu);
        primaryStage.show();

    }
}
