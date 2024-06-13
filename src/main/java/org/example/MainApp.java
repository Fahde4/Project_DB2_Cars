package org.example;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
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

        // Login-Bildschirm
        Label welcomeLabel = new Label("Willkommen bei der Autosuche!");

        // Benutzername und Passwort-Felder
        TextField userTextField = new TextField();
        userTextField.setPromptText("Benutzername");
        userTextField.getStyleClass().add("text-field");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Passwort");
        passwordField.getStyleClass().add("password-field");

        // Login-Button
        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            String username = userTextField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                connectionStatusLabel.setText("Bitte füllen Sie alle Felder aus.");
            } else if (DatabaseConnection.validateLogin(username, password)) {
                showMainScreen(primaryStage);
            } else {
                connectionStatusLabel.setText("Ungültige Anmeldedaten. Bitte versuchen Sie es erneut.");
            }
        });

        // Layout für die Eingabefelder und den Login-Button
        VBox loginLayout = new VBox(10, userTextField, passwordField, loginButton, connectionStatusLabel);
        loginLayout.setAlignment(Pos.CENTER);
        loginLayout.setMaxWidth(250); // Maximale Breite für das Layout setzen

        // Gesamtes Layout für den Willkommens- und Login-Bereich
        VBox welcomeLayout = new VBox(20, welcomeLabel, loginLayout);
        welcomeLayout.setAlignment(Pos.CENTER);

        Scene welcomeScene = new Scene(welcomeLayout, 800, 600);
        welcomeScene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
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

        Button button2 = new Button("Hinzufügen");
        Button button3 = new Button("Button 3");
        leftLayout.getChildren().addAll(button1, button2, button3);

        mainLayout.setTop(topContainer);
        mainLayout.setLeft(leftLayout);

        // Box für die Datenanzeige
        VBox centerLayout = new VBox(20);
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
                connectButton.setText("Trenne die Verbindung");
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
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true); // Zeige Gitterlinien

        int columnCount = resultSet.getMetaData().getColumnCount();

        // Set column constraints for the GridPane
        for (int i = 0; i < columnCount; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setHgrow(Priority.ALWAYS); // Säulenbreite an den verfügbaren Platz anpassen
            gridPane.getColumnConstraints().add(column);
        }

        // Add column headers
        for (int i = 1; i <= columnCount; i++) {
            Label headerLabel = new Label(resultSet.getMetaData().getColumnName(i));
            headerLabel.setStyle("-fx-font-weight: bold;");
            headerLabel.setFont(Font.font("Segoe UI", 12));
            GridPane.setHalignment(headerLabel, HPos.CENTER);
            GridPane.setValignment(headerLabel, VPos.CENTER);
            gridPane.add(headerLabel, i - 1, 0);
        }

        // Add data rows
        int rowIndex = 1;
        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                String cellData = resultSet.getString(i);
                Label cellLabel = new Label(cellData);
                cellLabel.setStyle("-fx-padding: 5px;");
                cellLabel.setFont(Font.font("Segoe UI", 12));
                GridPane.setHalignment(cellLabel, HPos.CENTER);
                GridPane.setValignment(cellLabel, VPos.CENTER);
                gridPane.add(cellLabel, i - 1, rowIndex);
            }
            rowIndex++;
        }

        // Wrap the GridPane in a ScrollPane for better viewing
        ScrollPane scrollPane = new ScrollPane(gridPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        // Clear the center layout and add the scroll pane
        VBox centerLayout = (VBox) ((BorderPane) dataTextArea.getScene().getRoot()).getCenter();
        centerLayout.getChildren().clear();
        centerLayout.getChildren().add(scrollPane);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
