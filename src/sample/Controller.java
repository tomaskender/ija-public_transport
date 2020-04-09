package sample;

import data.enums.StreetState;
import data.enums.VehicleState;
import data.implementations.CONFIG;
import data.implementations.GUIVehiclePath;
import data.interfaces.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.util.Pair;
import utils.Math2D;

import java.awt.*;
import java.rmi.UnexpectedException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;

public class Controller {
    public Button pauseButton;
    public TextField timeLabel;
    public Slider simSpeedSlider;
    public Label simSpeedLabel;

    public Label busLineId;
    public Label busState;
    public Pane highlightedBusPath;

    public ChoiceBox streetBusinessSelector;
    public Button closeStreetButton;
    @FXML
    AnchorPane field;


    private Map<Vehicle,Circle> vehicles = new HashMap<>();
    public boolean isPaused = true;
    boolean initTimeHasBeenSet = false;
    boolean isInClosureMode = false;
    Street currHoveredStreet = null;
    List<Pair<Shape, GUIMapElement>> highlightedObjects = new ArrayList<>();

    @FXML
    public void initialize() {
        timeLabel.setText(CONFIG.CURRENT_TIME.toString());
        UpdateSimSpeedLabel();
        UpdateHighlightedVehicle();
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
                if(!highlightedObjects.isEmpty()) {
                    String newState = streetBusinessSelector.getItems().get((Integer) number2).toString();
                    ((Street)(highlightedObjects.get(highlightedObjects.size()-1).getValue())).SetStreetState(StreetState.valueOf(newState));
                }
            }
        });
    }

    @FXML
    public void LoadStreets(){
        for (Street street : CONFIG.streets.values()){
            Line streetObj = new Line(street.getBegin().getX(),
                                    street.getBegin().getY(),
                                    street.getEnd().getX(),
                                    street.getEnd().getY());
            streetObj.setStrokeWidth(5);

            streetObj.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if(isInClosureMode) {
                        ClearHighlights();
                        street.SetClosed(!street.isClosed());
                        streetObj.setStroke(street.getNormalColor());
                    } else {
                        if(!street.isClosed()) {
                            HighlightObject(streetObj, street, true);
                            streetBusinessSelector.setValue(street.getStreetState().toString());
                        }
                    }
                }
            });
            streetObj.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    currHoveredStreet = street;
                }
            });
            streetObj.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if(currHoveredStreet == street) {
                        currHoveredStreet = null;
                    }
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
                    HighlightObject(c, v, true);

                    String line = v.getLine().getId();
                    data.interfaces.Line lines = CONFIG.lines.get(line);
                    List<AbstractMap.SimpleImmutableEntry<Stop, Integer>> stops_on_lines = lines.getStops();

                    int old_offset_x = 100;
                    final int y_pos = 40;
                    List<Route> line_route = v.getRoutes();
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
                        streetObj.setStroke(v.getLine().getMapColor());
                        streetObj.setMouseTransparent(true);
                        field.getChildren().add(streetObj);
                        old_x = coordinate.getX();
                        old_y = coordinate.getY();
                        HighlightObject(streetObj, new GUIVehiclePath(v.getLine().getMapColor()), false);
                    }

                    // replace drawn path
                    highlightedBusPath.getChildren().removeAll(highlightedBusPath.getChildren());
                    for(AbstractMap.SimpleImmutableEntry<Stop, Integer> stop : stops_on_lines){
                        int x_pos = old_offset_x + stop.getValue()*30;
                        Line route1  = new Line(old_offset_x + 5, y_pos, x_pos, y_pos);
                        Circle circle = new Circle();
                        Text text = new Text(stop.getKey().getId());
                        if(stop.getValue() != 0){
                            Text time = new Text(String.valueOf(stop.getValue()));
                            time.setY(y_pos + 15);
                            time.setX(((old_offset_x+x_pos)/2.0)-5);
                            highlightedBusPath.getChildren().add(time);
                        }
                        circle.setCenterX(x_pos);
                        circle.setCenterY(y_pos);
                        circle.setRadius(5);
                        circle.setFill(v.getLine().getMapColor());
                        route1.setStroke(v.getLine().getMapColor());
                        text.setX(x_pos - 7.5);
                        text.setY(y_pos - 10);
                        highlightedBusPath.getChildren().addAll(route1, circle, text);
                        old_offset_x = x_pos;
                    }

                    c.toFront();
                }
            });
            field.getChildren().add(c);
            vehicles.put(v, c);
        }
    }

    @FXML
    public void RemoveVehicle(Vehicle v) {
        if(!highlightedObjects.isEmpty() && highlightedObjects.get(0).getValue() == v)
            ClearHighlights();

        field.getChildren().remove(vehicles.get(v));
        vehicles.remove(v);
    }

    @FXML
    public void TickTime(long deltaInMillis) {
        CONFIG.CURRENT_TIME = CONFIG.CURRENT_TIME.plus(deltaInMillis, ChronoUnit.MILLIS);
        timeLabel.setText(CONFIG.CURRENT_TIME.withNano(0).toString());

        UpdateHighlightedVehicle();

        if(isInClosureMode && currHoveredStreet != null) {
            try {
                HandleClosure();
            } catch(Exception e) {

            }
        }
    }

    private void HandleClosure() throws UnexpectedException {
        Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
        Coordinate currMousePosition = Coordinate.CreateCoordinate(mouseLocation.x, mouseLocation.y);

        Coordinate closestPoint = null;
        for(int i=0; i<currHoveredStreet.getStreetPoints().size()-1; i++) {
            Coordinate p = Math2D.getClosestPointOnSegment(currHoveredStreet.getStreetPoints().get(i),
                    currHoveredStreet.getStreetPoints().get(i+1),
                    currMousePosition);
            if(closestPoint == null || Math2D.getDistanceBetweenPoints(currMousePosition, p) < Math2D.getDistanceBetweenPoints(currMousePosition, closestPoint))
                closestPoint = p;
        }

        if(closestPoint == null) {
            throw new UnexpectedException("Closure point was not found on selected street");
        } else {
            //TODO move X
        }
    }

    private void UpdateHighlightedVehicle() {
        if(!highlightedObjects.isEmpty() && highlightedObjects.get(0).getValue() instanceof Vehicle) {
            busLineId.setText("Line " + ((Vehicle)(highlightedObjects.get(0).getValue())).getLine().getId());
            busState.setText(((Vehicle)(highlightedObjects.get(0).getValue())).getState().toString());
        } else {
            busLineId.setText("No line");
            busState.setText(VehicleState.INACTIVE.toString());
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

        if(isPaused && isInClosureMode) {
            onCloseStreetClicked(null);
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

    @FXML
    void onCloseStreetClicked(ActionEvent actionEvent) {
        isInClosureMode = !isInClosureMode;

        if(isInClosureMode) {
            if(isPaused==false) {
                onPauseClicked(null);
            }

            closeStreetButton.setText("Cancel");
        } else {
            closeStreetButton.setText("Close Street");
        }
    }

    private void HighlightObject(Shape shape, GUIMapElement element, boolean exclusiveHighlightion) {
        if(exclusiveHighlightion /*|| (!highlightedObjects.isEmpty() && element.getClass() != highlightedObjects.get(highlightedObjects.size()-1).getValue().getClass())*/) {
            ClearHighlights();
        }

        highlightedObjects.add(new Pair<Shape, GUIMapElement>(shape, element));

        if(shape instanceof Line) {
            if(element instanceof Street)
                streetBusinessSelector.setDisable(false);
            
            shape.setStroke(element.getHighlightedColor());
        } else if(shape instanceof Circle) {
            shape.setFill(element.getHighlightedColor());
            shape.setStroke(Color.BLACK);
            shape.setStrokeWidth(5);
        }

        UpdateHighlightedVehicle();
    }

    private void ClearHighlights() {
        for (Pair<Shape, GUIMapElement> s : highlightedObjects) {
            if (s.getValue() instanceof GUIVehiclePath) {
                field.getChildren().remove(s.getKey());
            } else {
                s.getKey().setFill(s.getValue().getNormalColor());
                s.getKey().setStroke(s.getValue().getNormalColor());
                if (s.getKey() instanceof Circle)
                    s.getKey().setStrokeWidth(1);
            }
        }
        streetBusinessSelector.setDisable(true);
        highlightedObjects.removeAll(highlightedObjects);
    }
}

