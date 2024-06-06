package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainApp extends Application{

    private boolean connected = false;
    private Label connectionStatusLabel = new Label();
    private TextArea dataTextArea = new TextArea();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Auto Such- und Vergleichssystem");

        // Willkommensbildschirm
        Label welcomeLabel = new Label("Willkommen bei der Autosuche!");
        Button proceedButton = new Button("Weiter");
        proceedButton.setOnAction(e -> showMainScreen(primaryStage));

        VBox welcomeLayout = new VBox(20, welcomeLabel, proceedButton);
        welcomeLayout.setAlignment(Pos.CENTER);

        Scene welcomeScene = new Scene(welcomeLayout, 800, 600);
        primaryStage.setScene(welcomeScene);
        primaryStage.show();
    }

    private void showMainScreen(Stage primaryStage) {
        // Hauptfenster
        BorderPane mainLayout = new BorderPane();

        // Verbindungsstatus
        connectionStatusLabel.setText(""); // Verbindungsstatus initial leer

        HBox topLayout = new HBox(connectionStatusLabel);
        topLayout.setAlignment(Pos.TOP_LEFT);
        topLayout.setPadding(new Insets(10));

        // Button oben rechts
        Button connectButton = new Button("Verbinden");
        connectButton.setOnAction(e -> handleDatabaseConnection(connectButton));

        HBox rightLayout = new HBox(connectButton);
        rightLayout.setAlignment(Pos.TOP_RIGHT);
        rightLayout.setPadding(new Insets(10));

        HBox topContainer = new HBox(topLayout, rightLayout);
        topContainer.setSpacing(10);
        topContainer.setPadding(new Insets(10));
        topContainer.setPrefWidth(800);

        // Linke Buttons
        VBox leftLayout = new VBox(10);
        leftLayout.setPadding(new Insets(10));
        leftLayout.setAlignment(Pos.CENTER);  // Zentriere die Buttons vertikal

        Button button1 = new Button("Daten abrufen");
        button1.setOnAction(e -> retrieveDataFromDatabase());

        Button button2 = new Button("Button 2");
        Button button3 = new Button("Button 3");
        leftLayout.getChildren().addAll(button1, button2, button3);

        mainLayout.setTop(topContainer);
        mainLayout.setLeft(leftLayout);

        // Box für die Datenanzeige
        VBox centerLayout = new VBox(10);
        centerLayout.setAlignment(Pos.CENTER);
        centerLayout.setPadding(new Insets(10));
        centerLayout.getChildren().add(dataTextArea);
        mainLayout.setCenter(centerLayout);

        Scene mainScene = new Scene(mainLayout, 800, 600);
        // Lade die CSS-Datei
        mainScene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        primaryStage.setScene(mainScene);
    }

    private void handleDatabaseConnection(Button connectButton) {
        if (!connected) {
            if (DatabaseConnection.isConnected()) {
                connected = true;
                connectionStatusLabel.setText("Verbindung zur Datenbank erfolgreich");
                connectButton.setText("Log-off");
            } else {
                connectionStatusLabel.setText("Verbindung zur Datenbank fehlgeschlagen");
            }
        } else {
            connected = false;
            connectionStatusLabel.setText(""); // Verbindung trennen, Status leeren
            connectButton.setText("Verbinden");
        }
    }

    private void retrieveDataFromDatabase() {
        if (!connected) {
            connectionStatusLabel.setText("Bitte zuerst mit der Datenbank verbinden.");
            return;
        }
        try {
            Connection connection = DatabaseConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM dbo.thrait02_Autos");

            // Daten anzeigen
            displayData(resultSet);

            resultSet.close();
            statement.close();
            connection.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Hier könntest du eine Fehlermeldung anzeigen, wenn die Daten nicht abgerufen werden können
        }
    }

    private void displayData(ResultSet resultSet) throws SQLException {
        // Daten im Textfeld anzeigen
        StringBuilder data = new StringBuilder();
        while (resultSet.next()) {
            // Hier werden alle Spaltenwerte einer Zeile aneinandergehängt
            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                data.append(resultSet.getString(i)).append("\t");
            }
            data.append("\n");
        }
        dataTextArea.setText(data.toString());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
