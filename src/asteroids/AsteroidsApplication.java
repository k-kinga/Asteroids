package asteroids;

import javafx.application.Application;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;


public class AsteroidsApplication extends Application {

    public static int WIDTH = 600;
    public static int HEIGHT = 400;

    public static void main(String[] args) {
        launch(AsteroidsApplication.class);
    }


    @Override
    public void start(Stage window) throws Exception {
        Map<KeyCode, Boolean> keysPressed = new HashMap<>();
        ArrayList<Character> asteroids = new ArrayList<>();
        List<Projectile> projectiles = new ArrayList<>();

        //creating the pane
        Pane pane = new Pane();
        pane.setPrefSize(WIDTH, HEIGHT);

        //adding the ship
        Ship ship = new Ship(300, 200);
        pane.getChildren().add(ship.getCharacter());

        //creating the asteroids
        for (int i = 0; i < 5; i++) {
            Random rnd = new Random();
            asteroids.add(new Asteroid(rnd.nextInt(200), rnd.nextInt(200)));
            pane.getChildren().add(asteroids.get(i).getCharacter());
        }

        //setting the scene
        Scene scene = new Scene(pane);

        scene.setOnKeyPressed((event) -> {
            keysPressed.put(event.getCode(), true);
        });

        scene.setOnKeyReleased((event) -> {
            keysPressed.put(event.getCode(), false);
        });

        AtomicInteger points = new AtomicInteger();
        Text text = new Text(10, 20, "Points: 0");
        pane.getChildren().add(text);

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (keysPressed.getOrDefault(KeyCode.LEFT, false)) {
                    ship.turnLeft();
                }

                if (keysPressed.getOrDefault(KeyCode.RIGHT, false)) {
                    ship.turnRight();
                }

                if (keysPressed.getOrDefault(KeyCode.UP, false)) {
                    ship.accelerate();
                }

                if (keysPressed.getOrDefault(KeyCode.SPACE, false) && projectiles.size() < 3) {
                    Projectile projectile = new Projectile((int) ship.getCharacter().getTranslateX(), (int) ship.getCharacter().getTranslateY());
                    projectile.getCharacter().setRotate(ship.getCharacter().getRotate());
                    projectiles.add(projectile);
                    projectile.accelerate();
                    projectile.setMovement(projectile.getMovement().normalize().multiply(3));

                    pane.getChildren().add(projectile.getCharacter());
                }

                ship.move();
                if(asteroids.size()<1){
                    stop();
                    showResultsGameWon();
                }
                asteroids.stream().forEach((asteroid) -> {
                    asteroid.move();
                    if (ship.collide(asteroid)) {
                        stop();
                        showResultsGameLost();
                    }
                });

                projectiles.forEach(asteroid -> asteroid.move());
                projectiles.forEach(projectile -> {
                    asteroids.forEach(asteroid -> {
                        if (projectile.collide(asteroid)) {
                            projectile.setAlive(false);
                            asteroid.setAlive(false);
                            if (!projectile.isAlive()) {
                                text.setText("Points: " + points.addAndGet(1000));
                            }
                        }
                    });
                });

                projectiles.stream()
                        .filter(projectile -> !projectile.isAlive())
                        .forEach(projectile -> pane.getChildren().remove(projectile.getCharacter()));
                projectiles.removeAll(projectiles.stream()
                        .filter(projectile -> !projectile.isAlive())
                        .collect(Collectors.toList()));

                asteroids.stream()
                        .filter(asteroid -> !asteroid.isAlive())
                        .forEach(asteroid -> pane.getChildren().remove(asteroid.getCharacter()));
                asteroids.removeAll(asteroids.stream()
                        .filter(asteroid -> !asteroid.isAlive())
                        .collect(Collectors.toList()));

                if (Math.random() < 0.005) {
                    Asteroid asteroid = new Asteroid(WIDTH, HEIGHT);
                    if (!asteroid.collide(ship)) {
                        asteroids.add(asteroid);
                        pane.getChildren().add(asteroid.getCharacter());
                    }
                }

            }
        }.start();

        window.setScene(scene);
        window.setTitle("Asteroids!");
        window.show();

    }

    private void showResultsGameLost() {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        VBox dialogVbox = new VBox(20);
        dialogVbox.getChildren().add(new Text("You've lost!"));
        Scene dialogScene = new Scene(dialogVbox, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private void showResultsGameWon() {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        VBox dialogVbox = new VBox(20);
        dialogVbox.getChildren().add(new Text("You've won!"));
        Scene dialogScene = new Scene(dialogVbox, 300, 200);
        dialog.setScene(dialogScene);
        dialog.show();
    }

}

