package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainApp extends Application{

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Auto Such- und Vergleichssystem");

        Label label = new Label("Willkommen bei der Autosuche!");
        StackPane root = new StackPane();
        root.getChildren().add(label);

        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
