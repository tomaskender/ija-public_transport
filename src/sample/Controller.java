package sample;

import data.enums.StreetState;
import data.implementations.CONFIG;
import data.interfaces.Coordinate;
import data.interfaces.Stop;
import data.interfaces.Street;
import data.interfaces.Vehicle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class Controller {
    public Button pauseButton;
    public TextField timeLabel;
    public Slider simSpeedSlider;
    public Label simSpeedLabel;

    public Label busLineId;
    public Label busState;

    public ChoiceBox streetBusinessSelector;
    @FXML
    AnchorPane field;


    private Map<Vehicle,Circle> vehicles = new HashMap<>();
    public boolean isPaused = true;
    boolean initTimeHasBeenSet = false;
    Pair<Street,Line> highlightedStreet = null;
    Pair<Vehicle, Circle> highlightedVehicle = null;

    @FXML
    public void initialize() {
        timeLabel.setText(CONFIG.CURRENT_TIME.toString());
        UpdateSimSpeedLabel();
        simSpeedSlider.setValue(CONFIG.DELTA);

        simSpeedSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(
                    ObservableValue<? extends Number> observableValue,
                    Number oldValue,
                    Number newValue) {
                CONFIG.DELTA = newValue.intValue();
                UpdateSimSpeedLabel();
            }
        });

        for(StreetState s:StreetState.values())
            streetBusinessSelector.getItems().add(s.toString());
        streetBusinessSelector.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                if(highlightedStreet != null) {
                    String newState = streetBusinessSelector.getItems().get((Integer) number2).toString();
                    highlightedStreet.getKey().SetStreetState(StreetState.valueOf(newState));
                }
            }
        });
    }

    @FXML
    public void LoadStreets(){
        for (String street : CONFIG.streets.keySet()){
            Line streetObj = new Line(CONFIG.streets.get(street).getBegin().getX(),
                                    CONFIG.streets.get(street).getBegin().getY(),
                                    CONFIG.streets.get(street).getEnd().getX(),
                                    CONFIG.streets.get(street).getEnd().getY());
            streetObj.setStrokeWidth(5);

            streetObj.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if(highlightedStreet != null) {
                        highlightedStreet.getValue().setStroke(Color.BLACK);
                    }
                    streetObj.setStroke(Color.ORANGE);
                    highlightedStreet = null;
                    streetBusinessSelector.setValue(CONFIG.streets.get(street).getStreetState().toString());
                    streetBusinessSelector.setDisable(false);
                    highlightedStreet = new Pair<>(CONFIG.streets.get(street),streetObj);
                }
            });
            field.getChildren().add(streetObj);

        }
    }

    @FXML
    public void LoadStops(){
        for (String stop : CONFIG.stops.keySet()){
            Circle circle = new Circle();
            Text text = new Text(stop);
            circle.setCenterX(CONFIG.stops.get(stop).getCoordinate().getX());
            circle.setCenterY(CONFIG.stops.get(stop).getCoordinate().getY());
            circle.setRadius(5);
            text.setX(CONFIG.stops.get(stop).getCoordinate().getX() + 5);
            text.setY(CONFIG.stops.get(stop).getCoordinate().getY() - 10);
            field.getChildren().addAll(circle, text);
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
            c.setFill(v.getLine().getMapColor());
            c.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if(highlightedVehicle != null) {
                        highlightedVehicle.getValue().setFill(highlightedVehicle.getKey().getLine().getMapColor());
                        highlightedVehicle.getValue().setStroke(highlightedVehicle.getKey().getLine().getMapColor());
                        highlightedVehicle.getValue().setStrokeWidth(1);
                    }
                    c.setStroke(Color.BLACK);
                    c.setStrokeWidth(5);
                    highlightedVehicle = new Pair<>(v,c);
                    UpdateHighlightedVehicle();
                }
            });
            field.getChildren().add(c);
            vehicles.put(v, c);
        }
    }

    @FXML
    public void RemoveVehicle(Vehicle v) {
        field.getChildren().remove(vehicles.get(v));
        vehicles.remove(v);
        if(highlightedVehicle.getKey() == v)
            highlightedVehicle = null;
    }

    @FXML
    public void TickTime(long deltaInMillis) {
        CONFIG.CURRENT_TIME = CONFIG.CURRENT_TIME.plus(deltaInMillis, ChronoUnit.MILLIS);
        timeLabel.setText(CONFIG.CURRENT_TIME.withNano(0).toString());

        UpdateHighlightedVehicle();
    }

    private void UpdateHighlightedVehicle() {
        if(highlightedVehicle != null) {
            busLineId.setText("Line " + highlightedVehicle.getKey().getLine().getId());
            busState.setText(highlightedVehicle.getKey().getState().toString());
            //TODO add list of stops
        } else {
            busLineId.setText("No line");
            busState.setText("INACTIVE");
        }
    }

    @FXML
    private void UpdateSimSpeedLabel() {
        simSpeedLabel.setText("sim speed: "+CONFIG.DELTA+":1");
    }

    @FXML
    void onPauseClicked(ActionEvent actionEvent) {
        if(!initTimeHasBeenSet) {
            try {
                CONFIG.CURRENT_TIME =  LocalTime.parse(timeLabel.getText(), DateTimeFormatter.ofPattern("H:m"));
                timeLabel.setDisable(true);
                initTimeHasBeenSet = true;
            } catch(DateTimeParseException e) {
                timeLabel.setText("H:m");
                return;
            }
        }

        isPaused = !isPaused;
        if(isPaused) {
            pauseButton.setText("▶");
        } else {
            pauseButton.setText("⏸");
        }
    }
}

