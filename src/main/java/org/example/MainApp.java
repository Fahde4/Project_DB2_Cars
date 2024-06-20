package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class MainApp extends Application{

    private boolean connected = false;
    private Label connectionStatusLabel = new Label();
    private GridPane dataTextArea = new GridPane();

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("KfZ Vergleichs- und Auswertesystem");

        Label welcomeLabel = new Label("Willkommen bei KfZ Vergleichs- und Auswertesystem");

        TextField userTextField = new TextField();
        userTextField.setPromptText("Benutzername");
        userTextField.getStyleClass().add("text-field");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Passwort");
        passwordField.getStyleClass().add("password-field");

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

        VBox loginLayout = new VBox(10, userTextField, passwordField, loginButton, connectionStatusLabel);
        loginLayout.setAlignment(Pos.CENTER);
        loginLayout.setMaxWidth(250); // Maximale Breite für das Layout setzen

        VBox welcomeLayout = new VBox(20, welcomeLabel, loginLayout);
        welcomeLayout.setAlignment(Pos.CENTER);

        Scene welcomeScene = new Scene(welcomeLayout, 400, 400);
        welcomeScene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        primaryStage.setScene(welcomeScene);
        primaryStage.show();
    }

    private void showMainScreen(Stage primaryStage) {
        BorderPane mainLayout = new BorderPane();

        connectionStatusLabel.setText("");

        HBox topLayout = new HBox(connectionStatusLabel);
        topLayout.setAlignment(Pos.TOP_LEFT);
        topLayout.setPadding(new Insets(10));

        Button connectButton = new Button("Verbinden");
        connectButton.setOnAction(e -> handleDatabaseConnection(connectButton));

        HBox rightLayout = new HBox(connectButton);
        rightLayout.setAlignment(Pos.TOP_RIGHT);
        rightLayout.setPadding(new Insets(10));

        HBox topContainer = new HBox(topLayout, rightLayout);
        topContainer.setSpacing(10);
        topContainer.setPadding(new Insets(10));
        topContainer.setPrefWidth(800);

        VBox leftLayout = new VBox(10);
        leftLayout.setPadding(new Insets(10));
        leftLayout.setAlignment(Pos.CENTER);

        Button button1 = new Button("Daten abrufen");
        button1.setOnAction(e -> retrieveDataFromDatabase());

        Button button2 = new Button("Hinzufügen");
        button2.setOnAction(e -> showAddDataDialog());

        Button button3 = new Button("Auswertung");
        button3.setOnAction(e -> showAnalysisWindow());

        leftLayout.getChildren().addAll(button1, button2, button3);

        mainLayout.setTop(topContainer);
        mainLayout.setLeft(leftLayout);

        VBox centerLayout = new VBox(20);
        centerLayout.setAlignment(Pos.CENTER);
        centerLayout.setPadding(new Insets(10));
        centerLayout.getChildren().add(dataTextArea);
        mainLayout.setCenter(centerLayout);

        Scene mainScene = new Scene(mainLayout, 1400, 900);
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
            connectionStatusLabel.setText("");
            connectButton.setText("Verbinden");
        }
    }

    private void retrieveDataFromDatabase() {
        if (!connected) {
            connectionStatusLabel.setText("Bitte zuerst mit der Datenbank verbinden.");
            return;
        }
        try {
            ResultSet resultSet = Queries.retrieveDataFromDatabase();
            displayData(resultSet);
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayData(ResultSet resultSet) throws SQLException {
        GridPane gridPane = new GridPane();
        gridPane.setGridLinesVisible(true);

        int columnCount = resultSet.getMetaData().getColumnCount();

        for (int i = 0; i < columnCount; i++) {
            ColumnConstraints column = new ColumnConstraints();
            column.setHgrow(Priority.ALWAYS);
            gridPane.getColumnConstraints().add(column);
        }
        for (int i = 1; i <= columnCount; i++) {
            Label headerLabel = new Label(resultSet.getMetaData().getColumnName(i));
            headerLabel.setStyle("-fx-font-weight: bold;");
            headerLabel.setFont(Font.font("Segoe UI", 12));
            GridPane.setHalignment(headerLabel, HPos.CENTER);
            GridPane.setValignment(headerLabel, VPos.CENTER);
            gridPane.add(headerLabel, i - 1, 0);
        }
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

        ScrollPane scrollPane = new ScrollPane(gridPane);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        Platform.runLater(() -> {
            VBox centerLayout = (VBox) ((BorderPane) dataTextArea.getScene().getRoot()).getCenter();
            if (centerLayout != null) {
                centerLayout.getChildren().add(scrollPane);
            }else {
                centerLayout.getChildren().clear();
            }
        });
    }


    private void showAddDataDialog() {

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Daten hinzufügen");

        VBox dialogVBox = new VBox(11);
        dialogVBox.setPadding(new Insets(11));
        dialogVBox.setAlignment(Pos.CENTER);

        TextField idField = new TextField();
        idField.setPromptText("ID");

        TextField brandField = new TextField();
        brandField.setPromptText("Marke");

        TextField modelField = new TextField();
        modelField.setPromptText("Modell");

        TextField motorisierungField = new TextField();
        motorisierungField.setPromptText("Motorisierung");

        TextField yearField = new TextField();
        yearField.setPromptText("Baujahr");

        TextField chassisField = new TextField();
        chassisField.setPromptText("Karosserieform");

        TextField psField = new TextField();
        psField.setPromptText("Leistung in PS");

        TextField fuelEconField = new TextField();
        fuelEconField.setPromptText("Kombinierter Verbrauch in Liter");

        TextField vMaxField = new TextField();
        vMaxField.setPromptText("Höchstgeschwindigkeit in Km/h");

        Label warningLabel = new Label();
        warningLabel.setStyle("-fx-text-fill: red;");

        Button saveButton = new Button("Speichern");
        saveButton.setOnAction(e -> {
            String id = idField.getText();
            String brand = brandField.getText();
            String model = modelField.getText();
            String motorisierung = motorisierungField.getText();
            String year =  yearField.getText();
            String chassis = chassisField.getText();
            String ps = psField.getText();
            String fuelEcon = fuelEconField.getText();
            String vMax = vMaxField.getText();

            if (id.isEmpty() || brand.isEmpty() || model.isEmpty() || motorisierung.isEmpty() || year.isEmpty() || chassis.isEmpty()|| ps.isEmpty()|| fuelEcon.isEmpty()||vMax.isEmpty()) {
                warningLabel.setText("Alle Felder müssen ausgefüllt werden.");
                return;
            }

            int id2parse;
            int year2parse;
            int ps2parse;
            int vMax2parse;

            try {
                id2parse = Integer.parseInt(id);
                year2parse = Integer.parseInt(year);
                ps2parse = Integer.parseInt(ps);
                vMax2parse = Integer.parseInt(vMax);
            } catch (NumberFormatException ex) {
                warningLabel.setText("Das das Feld muss eine Zahl sein.");
                return;
            }
            if (Queries.saveCarData(id, brand, model, motorisierung, year2parse, chassis, ps2parse, fuelEcon, vMax2parse)) {
                connectionStatusLabel.setText("Auto erfolgreich angelegt");
            } else {
                connectionStatusLabel.setText("Fehler beim Hinzufügen des Auto.");
            }
            dialog.close();
        });

        dialogVBox.getChildren().addAll(idField, brandField, modelField, motorisierungField, yearField, chassisField, psField, fuelEconField, vMaxField, saveButton, warningLabel);

        Scene dialogScene = new Scene(dialogVBox, 300, 500);
        dialog.setScene(dialogScene);
        dialog.showAndWait();
    }

    private void showAnalysisWindow() {
        Stage analysisStage = new Stage();
        analysisStage.setTitle("Auswertung");

        VBox analysisLayout = new VBox(12);
        analysisLayout.setPadding(new Insets(12));
        analysisLayout.setAlignment(Pos.CENTER);

        ToggleButton toggleButton1 = new ToggleButton("Verbrauch");
        ToggleButton toggleButton2 = new ToggleButton("Leistung");
        ToggleButton toggleButton3 = new ToggleButton("Verbrauch zu Leistung");

        HBox toggleButtonsBox = new HBox(10, toggleButton1, toggleButton2, toggleButton3);
        toggleButtonsBox.setAlignment(Pos.CENTER);

        ComboBox<String> brandComboBox = new ComboBox<>();
        brandComboBox.setPromptText("Wählen Sie eine Marke");
        brandComboBox.setItems(Queries.getBrandsFromDatabase());

        ComboBox<String> modelComboBox = new ComboBox<>();
        modelComboBox.setPromptText("Wählen Sie ein Modell");
        modelComboBox.setItems(Queries.getModellFromDatabase());

        ComboBox<String> formComboBox = new ComboBox<>();
        formComboBox.setPromptText("Wählen Sie eine Form");
        formComboBox.setItems(Queries.getFormFromDatabase());

        TextField yearField = new TextField();
        yearField.setPromptText("Jahr");

        ListView<String> resultsListView = new ListView<>();
        resultsListView.setPrefWidth(300);
        resultsListView.setPrefHeight(200);

        Button analyzeButton = new Button("Auswerten");
        analyzeButton.setOnAction(e -> {
            String brand = brandComboBox.getValue();
            String model = modelComboBox.getValue();
            String chassis = formComboBox.getValue();
            Integer year = null;
            String yearText = yearField.getText();
            if (!yearText.isEmpty()) {
                try {
                    year = Integer.parseInt(yearText);
                } catch (NumberFormatException ex) {
                    ex.printStackTrace();
                }
            }

            resultsListView.getItems().clear();

            List<String> results = new ArrayList<>();

            try {
                if (toggleButton1.isSelected()) {
                    ResultSet resultSet = Queries.getLowestConsumptionCar(brand, model, year, chassis);
                    results.addAll(collectResults(resultSet));
                }
                if (toggleButton2.isSelected()) {
                    ResultSet resultSet = Queries.getStrongestCar(brand, model, year, chassis);
                    results.addAll(collectResults(resultSet));
                }
                if (toggleButton3.isSelected()) {
                    ResultSet resultSet = Queries.getBestConsumptionToPowerRatio(brand, model, year, chassis);
                    results.addAll(collectResults(resultSet));
                }
                if (!toggleButton1.isSelected() && !toggleButton2.isSelected() && !toggleButton3.isSelected()) {
                    ResultSet resultSet = Queries.getSpecific(brand, model, year, chassis);
                    results.addAll(collectResults(resultSet));
                }

                // Display all collected results in the ListView
                resultsListView.getItems().addAll(results);

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
            analysisLayout.getChildren().addAll(toggleButtonsBox, brandComboBox, modelComboBox,
                    formComboBox, yearField, analyzeButton, resultsListView);

        Scene scene = new Scene(analysisLayout, 400, 400);
        analysisStage.setScene(scene);
        analysisStage.show();
    }
    private List<String> collectResults(ResultSet resultSet) throws SQLException {
        List<String> results = new ArrayList<>();
        while (resultSet.next()) {
            String marke = resultSet.getString("marke");
            String modell = resultSet.getString("modell");
            String motorisierung = resultSet.getString("motorisierung");
            int year = resultSet.getInt("baujahr");
            int ps = resultSet.getInt("leistungInPS");
            double verbrauch = resultSet.getDouble("verbrauchInLiter");

            String resultString = String.format("Marke: %s, Modell: %s, Motorisierung: %s, Baujahr: %d, PS: %d, Verbrauch: %.2f",
                    marke, modell, motorisierung, year, ps, verbrauch);

            results.add(resultString);
        }
        return results;
    }
    public static void main(String[] args) {
        launch(args);
    }
}
