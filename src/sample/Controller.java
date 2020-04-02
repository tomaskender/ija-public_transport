package sample;

import data.implementations.CONFIG;
import data.interfaces.Coordinate;
import data.interfaces.Stop;
import data.interfaces.Street;
import data.interfaces.Vehicle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class Controller {
    public Button pauseButton;
    public TextField timeLabel;
    @FXML
    AnchorPane field;


    private Map<Vehicle,Circle> vehicles = new HashMap<>();
    boolean isPaused = true;

    @FXML
    public void LoadStreets(){
        for (Street street : CONFIG.streets.values()){
            field.getChildren().add(new Line(street.getBegin().getX(),street.getBegin().getY(),street.getEnd().getX(),street.getEnd().getY()));

        }
    }

    @FXML
    public void LoadStops(){
        for (Stop stop : CONFIG.stops.values()){
            Circle circle = new Circle();
            circle.setCenterX(stop.getCoordinate().getX());
            circle.setCenterY(stop.getCoordinate().getY());
            circle.setRadius(5);
            field.getChildren().add(circle);
        }
    }

    @FXML
    public void SetVehicle(Vehicle v, Coordinate pos) {
        if(vehicles.containsKey(v)) {
            vehicles.get(v).setCenterX(pos.getX());
            vehicles.get(v).setCenterY(pos.getY());
        } else {
            Circle c = new Circle();
            c.setCenterX(pos.getX());
            c.setCenterY(pos.getY());
            c.setRadius(10);
            c.setFill(Color.GREEN);
            field.getChildren().add(c);
            vehicles.put(v, c);
        }
    }

    @FXML
    public void RemoveVehicle(Vehicle v) {
        field.getChildren().remove(vehicles.get(v));
        vehicles.remove(v);
    }

    @FXML
    public void initialize() {
        timeLabel.textProperty().bind(new SimpleObjectProperty<>(CONFIG.CURRENT_TIME).asString());
    }

    @FXML
    void onPauseClicked(ActionEvent actionEvent) {
        isPaused = !isPaused;
        if(isPaused)
            pauseButton.setText("▶");
        else
            pauseButton.setText("⏸");
    }
}

