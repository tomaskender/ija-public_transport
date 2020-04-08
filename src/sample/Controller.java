package sample;

import data.enums.StreetState;
import data.implementations.CONFIG;
import data.interfaces.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Pair;
import sun.awt.ConstrainableGraphics;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class Controller {
    public Button pauseButton;
    public TextField timeLabel;
    public Slider simSpeedSlider;
    public Label simSpeedLabel;

    public Label busLineId;
    public Label busState;
    public Pane highlightedBusPath;

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

                    String line = v.getLine().getId();
                    data.interfaces.Line lines = CONFIG.lines.get(line);
                    List<AbstractMap.SimpleImmutableEntry<Stop, Integer>> stops_on_lines = lines.getStops();

                    int old_offset_x = 100;
                    final int y_pos = 70;
                    List<Route> line_route = highlightedVehicle.getKey().getRoutes();
                    List<Coordinate> coords = new ArrayList<>();
                    for(Route list_streets : line_route){
                        for(AbstractMap.SimpleImmutableEntry<Street, Coordinate> list_all_streets : list_streets.getRoute()){
                            coords.add(list_all_streets.getValue());
                        }
                    }
                    int old_x = coords.get(0).getX();
                    int old_y = coords.get(0).getY();
                    for(Coordinate coordinate : coords){
                        Line streetObj = new Line(old_x, old_y, coordinate.getX(), coordinate.getY());
                        streetObj.setStrokeWidth(5);
                        streetObj.setStroke(highlightedVehicle.getKey().getLine().getMapColor());
                        field.getChildren().add(streetObj);
                        old_x = coordinate.getX();
                        old_y = coordinate.getY();
                    }

                    // replace drawn path
                    highlightedBusPath.getChildren().removeAll(highlightedBusPath.getChildren());
                    for(AbstractMap.SimpleImmutableEntry<Stop, Integer> stop : stops_on_lines){
                        Integer x_pos = old_offset_x + stop.getValue()*30;
                        Line route1  = new Line(old_offset_x + 5, y_pos, x_pos, y_pos);
                        Circle circle = new Circle();
                        Text text = new Text(stop.getKey().getId());
                        circle.setCenterX(x_pos);
                        circle.setCenterY(y_pos);
                        circle.setRadius(5);
                        circle.setFill(highlightedVehicle.getKey().getLine().getMapColor());
                        route1.setStroke(highlightedVehicle.getKey().getLine().getMapColor());
                        text.setX(x_pos - 7.5);
                        text.setY(y_pos - 10);
                        highlightedBusPath.getChildren().addAll(route1, circle, text);
                        old_offset_x = x_pos;
                    }
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
        if(highlightedVehicle != null && highlightedVehicle.getKey() == v)
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
            Image pause = new Image(getClass().getResourceAsStream("./media/play.png"));
            pauseButton.setGraphic(new ImageView(pause));
        } else {
            Image pause = new Image(getClass().getResourceAsStream("./media/pause.png"));
            pauseButton.setGraphic(new ImageView(pause));
        }
    }
}

