package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;

public class Main extends Application {

    private final TemperatureConverter converter = new TemperatureConverter();
    private final TemperatureUnitDAO unitDAO = new TemperatureUnitDAO();
    private final TempRecordDAO recordDAO = new TempRecordDAO();

    private ComboBox<TemperatureUnit> fromUnitBox;
    private ComboBox<TemperatureUnit> toUnitBox;
    private TextField inputField;
    private Label resultLabel;
    private ListView<String> historyList;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Temperature Converter");

        Label inputLabel = new Label("Enter value:");
        inputField = new TextField();

        fromUnitBox = new ComboBox<>();
        toUnitBox = new ComboBox<>();
        loadUnits();

        Button convertButton = new Button("Convert");
        convertButton.setOnAction(e -> handleConvert());

        resultLabel = new Label("Result: ");

        historyList = new ListView<>();
        loadHistory();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        grid.add(inputLabel, 0, 0);
        grid.add(inputField, 1, 0);
        grid.add(new Label("From:"), 0, 1);
        grid.add(fromUnitBox, 1, 1);
        grid.add(new Label("To:"), 0, 2);
        grid.add(toUnitBox, 1, 2);
        grid.add(convertButton, 1, 3);
        grid.add(resultLabel, 1, 4);

        VBox root = new VBox(15);
        root.setPadding(new Insets(15));
        root.setAlignment(Pos.TOP_CENTER);
        root.getChildren().addAll(grid, new Label("History:"), historyList);

        Scene scene = new Scene(root, 400, 500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void loadUnits() {
        try {
            List<TemperatureUnit> units = unitDAO.getAllUnits();
            fromUnitBox.getItems().addAll(units);
            toUnitBox.getItems().addAll(units);
            if (!units.isEmpty()) {
                fromUnitBox.getSelectionModel().select(0);
                toUnitBox.getSelectionModel().select(1 < units.size() ? 1 : 0);
            }
        } catch (SQLException e) {
            resultLabel.setText("DB error loading units: " + e.getMessage());
        }
    }

    private void loadHistory() {
        try {
            List<TempRecord> records = recordDAO.getAllRecords();
            for (TempRecord r : records) {
                historyList.getItems().add(
                        r.getInputValue() + " -> " + r.getOutputValue()
                );
            }
        } catch (SQLException e) {
            historyList.getItems().add("Error loading history: " + e.getMessage());
        }
    }

    private void handleConvert() {
        try {
            double input = Double.parseDouble(inputField.getText());
            TemperatureUnit from = fromUnitBox.getValue();
            TemperatureUnit to = toUnitBox.getValue();

            double result = convert(input, from.getName(), to.getName());
            resultLabel.setText("Result: " + result);

            TempRecord record = new TempRecord(input, from.getId(), result, to.getId());
            int newId = recordDAO.insertRecord(record);

            historyList.getItems().add(0, input + " -> " + result);

        } catch (NumberFormatException ex) {
            resultLabel.setText("Please enter a valid number");
        } catch (SQLException ex) {
            resultLabel.setText("DB error: " + ex.getMessage());
        }
    }

    private double convert(double value, String from, String to) {
        if (from.equals(to)) return value;

        double celsius;
        switch (from) {
            case "Celsius": celsius = value; break;
            case "Fahrenheit": celsius = converter.fahrenheitToCelsius(value); break;
            case "Kelvin": celsius = converter.kelvinToCelsius(value); break;
            default: celsius = value;
        }

        switch (to) {
            case "Celsius": return celsius;
            case "Fahrenheit": return converter.celsiusToFahrenheit(celsius);
            case "Kelvin": return celsius + 273.15;
            default: return celsius;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}