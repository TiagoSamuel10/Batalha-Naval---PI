package JavaFX;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

public class Test extends Application{

    final int WIDTH = 600;
    final int HEIGHT = 400;

    double ballRadius = 40;
    double ballX = 100;
    double ballY = 200;
    double xSpeed = 4;

    public static void main(String[] args) {

        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        stage.setTitle("Basic JavaFX demo");

        Group root = new Group();
        Scene scene = new Scene(root, WIDTH, HEIGHT);

        Circle circle = new Circle();
        circle.setCenterX(ballX);
        circle.setCenterY(ballY);
        circle.setRadius(ballRadius);
        circle.setFill(Color.BLUE);
        root.getChildren().add(circle);

        stage.setScene(scene);
        stage.show();

        AnimationTimer animator = new AnimationTimer(){

            @Override
            public void handle(long arg0) {

                // UPDATE
                ballX += xSpeed;

                if (ballX + ballRadius >= WIDTH)
                {
                    ballX = WIDTH - ballRadius;
                    xSpeed *= -1;
                }
                else if (ballX - ballRadius < 0)
                {
                    ballX = 0 + ballRadius;
                    xSpeed *= -1;
                }

                // RENDER
               circle.setCenterX(ballX);
            }
        };

        animator.start();
    }
}
