package sample;

import data.enums.GUIState;
import data.enums.StreetState;
import data.enums.VehicleState;
import data.implementations.*;
import data.interfaces.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.w3c.dom.ranges.Range;
import sun.security.ssl.Debug;
import sun.security.util.ArrayUtil;
import utils.Math2D;

import java.awt.*;
import java.awt.font.NumericShaper;
import java.rmi.UnexpectedException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Controller {
    public Button pauseButton;
    public TextField timeLabel;
    public Slider simSpeedSlider;
    public Label simSpeedLabel;

    public ListView<ChangePath> altRouteSelector;

    public Label busLineId;
    public Label busState;
    public Pane highlightedBusPath;

    public ChoiceBox streetBusinessSelector;
    public Button closeStreetButton;
    @FXML
    AnchorPane field;
    @FXML
    AnchorPane zoom_map;

    GUIState state = GUIState.NORMAL;

    private Map<Vehicle,Circle> vehicles = new HashMap<>();
    public boolean isPaused = true;
    boolean initTimeHasBeenSet = false;
    Street currHoveredStreet = null;
    Circle mousePlaceStopMarker;
    List<Pair<Shape, GUIMapElement>> highlightedObjects = new ArrayList<>();
    List<Street> selectedRouteStreets = new ArrayList<>();
    Route selectedRoute = new MyRoute();

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

        mousePlaceStopMarker = new Circle();
        mousePlaceStopMarker.setVisible(false);
        Image stop = new Image(getClass().getResourceAsStream("./media/close.png"));
        mousePlaceStopMarker.setFill(new ImagePattern(stop));
        mousePlaceStopMarker.setRadius(10);
        mousePlaceStopMarker.setMouseTransparent(true);
        field.getChildren().add(mousePlaceStopMarker);

        field.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(state == GUIState.CLOSING_STREETS && currHoveredStreet != null) {
                    Coordinate p = getClosurePoint(currHoveredStreet, (int)mouseEvent.getX(), (int)mouseEvent.getY());
                    mousePlaceStopMarker.setCenterX(p.getX());
                    mousePlaceStopMarker.setCenterY(p.getY());
                }
            }
        });

        altRouteSelector.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ChangePath>() {

            @Override
            public void changed(ObservableValue<? extends ChangePath> observable, ChangePath oldValue, ChangePath newValue) {
                ClearHighlights();
                if(newValue != null) {
                    selectedRouteStreets.clear();
                    selectedRouteStreets.add(newValue.getBeginning().getStreet());
                    RedrawSelectedRoute(selectedRoute);
                    state = GUIState.ALT_ROUTE_SELECTION;
                    closeStreetButton.setDisable(true);
                }
            }
        });
    }

    @FXML
    public void Zoom(ScrollEvent zoom){
        zoom.consume();
        double zoom_scale = zoom.getDeltaY() > 0 ? 1.1 : 0.9;
        zoom_map.setScaleX(zoom_scale * zoom_map.getScaleX());
        zoom_map.setScaleY(zoom_scale * zoom_map.getScaleY());
        zoom_map.layout();
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
                    switch (state) {
                        case NORMAL:
                            HighlightObject(streetObj, street, true);
                            streetBusinessSelector.setValue(street.getStreetState().toString());
                            break;
                        case CLOSING_STREETS:
                            ClearHighlights();
                            onStreetClosed();
                            break;
                        case ALT_ROUTE_SELECTION:
                            if(!highlightedObjects.isEmpty() && Math2D.isLocatedBetweenPoints(Coordinate.CreateCoordinate(mouseEvent.getX(),mouseEvent.getY()),
                                                                                            Coordinate.CreateCoordinate(
                                                                                                    ((Line)highlightedObjects.get(highlightedObjects.size()-1).getKey()).getStartX(),
                                                                                                    ((Line)highlightedObjects.get(highlightedObjects.size()-1).getKey()).getStartY()),
                                                                                            Coordinate.CreateCoordinate(
                                                                                                    ((Line)highlightedObjects.get(highlightedObjects.size()-1).getKey()).getEndX(),
                                                                                                    ((Line)highlightedObjects.get(highlightedObjects.size()-1).getKey()).getEndY()))) {
                                // remove if it's already highlighted
                                selectedRouteStreets.remove(street);
                            } else {
                                // otherwise highlight it
                                selectedRouteStreets.add(street);
                            }
                            RedrawSelectedRoute(selectedRoute);

                    }
                }
            });

            streetObj.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if(state == GUIState.CLOSING_STREETS) {
                        currHoveredStreet = street;
                        mousePlaceStopMarker.setVisible(true);
                    }
                }
            });

            streetObj.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if(currHoveredStreet == street) {
                        currHoveredStreet = null;
                        mousePlaceStopMarker.setVisible(false);
                    }
                }
            });
            field.getChildren().add(streetObj);
        }
    }

    private void RedrawSelectedRoute(Route r) {
        for(PointInPath endEntry:altRouteSelector.getSelectionModel().getSelectedItem().getEnds()) {
            if (r.ConstructRoute(selectedRouteStreets,
                    altRouteSelector.getSelectionModel().getSelectedItem().getBeginning(),
                    endEntry,
                    altRouteSelector.getSelectionModel().getSelectedItem().getDeltaInMins())) {
                //succeeded, remove vehicle from alt route list
                System.out.println("Alternative route was set.");
                altRouteSelector.getSelectionModel().getSelectedItem().SetFoundAlternativeRoute(r);
                break;
            }
        }
        ClearHighlights();

        ChangePath path = altRouteSelector.getSelectionModel().getSelectedItem();
        Circle beg = new Circle(path.getBeginning().getCoordinate().getX(), path.getBeginning().getCoordinate().getY(), 10, Color.GREEN);
        field.getChildren().add(beg);
        HighlightObject(beg, new GUIVehiclePath(Color.GREEN), false);

        for (PointInPath e : path.getEnds()) {
            Circle end = new Circle(e.getCoordinate().getX(), e.getCoordinate().getY(), 10, Color.RED);
            field.getChildren().add(end);
            HighlightObject(end, new GUIVehiclePath(Color.RED), false);
        }

        List<PointInPath> route = r.getRoute();
        for(int i=0;i<route.size()-1; i++) {
            Coordinate c1 = route.get(i).getCoordinate();
            Coordinate c2 = route.get(i+1).getCoordinate();
            Line altRoute = new Line(c1.getX(), c1.getY(), c2.getX(), c2.getY());
            altRoute.setStrokeWidth(2);
            field.getChildren().add(altRoute);
            HighlightObject(altRoute, new GUIVehiclePath(Color.GREEN), false);
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
                        for(PointInPath list_all_streets : list_streets.getRoute()){
                            coords.add(list_all_streets.getCoordinate());
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

    }

    private Coordinate getClosurePoint(Street currHoveredStreet, int mouseX, int mouseY) {
        Coordinate currMousePosition = Coordinate.CreateCoordinate(mouseX, mouseY);

        Coordinate closestPoint = null;
        if(currHoveredStreet != null) {
            for (int i = 0; i < currHoveredStreet.getStreetPoints().size() - 1; i++) {
                Coordinate p = Math2D.getClosestPointOnSegment(currHoveredStreet.getStreetPoints().get(i),
                        currHoveredStreet.getStreetPoints().get(i + 1),
                        currMousePosition);
                if (closestPoint == null || Math2D.getDistanceBetweenPoints(currMousePosition, p) < Math2D.getDistanceBetweenPoints(currMousePosition, closestPoint))
                    closestPoint = p;
            }
        }
        return closestPoint;
    }

    private void onStreetClosed() {
        // create closure
        Circle c = new Circle();
        Coordinate p = Coordinate.CreateCoordinate(mousePlaceStopMarker.getCenterX(), mousePlaceStopMarker.getCenterY());
        c.setCenterX(p.getX());
        c.setCenterY(p.getY());
        Image stop = new Image(getClass().getResourceAsStream("./media/close.png"));
        c.setFill(new ImagePattern(stop));
        c.setRadius(10);
        c.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(state == GUIState.CLOSING_STREETS) {
                    currHoveredStreet.RemoveClosurePoint(p);
                    field.getChildren().remove(c);
                }
            }
        });
        currHoveredStreet.AddClosurePoint(p);
        field.getChildren().add(c);

        EvaluateStreetsAffectedByClosedPoint();
    }

    @FXML
    private void EvaluateStreetsAffectedByClosedPoint() {
        // paths that are already fixed and will be removed from selector list
        List<ChangePath> toRemove = new ArrayList<>();

        for(ChangePath path:altRouteSelector.getItems()) {
            Route altRoute = path.getFoundAlternativeRoute();
            // check if selector list items have been fixed and if so, add altRoute to vehicles route
            if(altRoute != null) {
                if(altRouteSelector.getSelectionModel().getSelectedItem() == path)
                    ClearHighlights();

                PointInPath firstPoint = altRoute.getRoute().get(0);
                PointInPath lastPoint = altRoute.getRoute().get(altRoute.getRoute().size()-1);

                for(Vehicle vehicle:path.getSubscribedVehicles()) {
                    Route routeWithFirstAltRoutePoint = vehicle.getRoutes().stream().filter(r->r.getRoute().contains(firstPoint)).findFirst().get();
                    // remove everything up to (including) this index
                    Route route = vehicle.getRoutes()
                                .stream()
                                .filter(r -> r.getRoute().get(r.getRoute().size() - 1).equals(lastPoint)).findFirst().get();
                    int lastIndexToRemove = vehicle.getRoutes().indexOf(route);


                    //TODO FUJ HACK
                    List<Street> streetList = new ArrayList<>();
                    streetList.add(altRoute.getRoute().get(0).getStreet());

                    for(int i=0; i<routeWithFirstAltRoutePoint.getRoute().size()-1; i++) {
                        if(streetList.get(streetList.size()-1) != routeWithFirstAltRoutePoint.getRoute().get(i).getStreet()) {
                            streetList.add(routeWithFirstAltRoutePoint.getRoute().get(i).getStreet());
                        }
                        PointInPath p1 = routeWithFirstAltRoutePoint.getRoute().get(i);
                        PointInPath p2 = routeWithFirstAltRoutePoint.getRoute().get(i + 1);
                        if(Math2D.isLocatedBetweenPoints(altRoute.getRoute().get(0).getCoordinate(), p1.getCoordinate(), p2.getCoordinate())) {
                            break;
                        }
                    }

                    for(int i=0; i<altRoute.getRoute().size();i++) {
                        if(streetList.get(streetList.size()-1) != altRoute.getRoute().get(i).getStreet()) {
                            streetList.add(altRoute.getRoute().get(i).getStreet());
                        }
                    }

                    int modifiedIndex = vehicle.getRoutes().indexOf(routeWithFirstAltRoutePoint);
                    Route newRoute = new MyRoute();
                    newRoute.ConstructRoute(streetList,
                            routeWithFirstAltRoutePoint.getRoute().get(0),
                            altRoute.getRoute().get(altRoute.getRoute().size()-1),
                            altRoute.getExpectedDeltaTime()/60);
                    vehicle.EditRouteAndNormalizeProgress(modifiedIndex, newRoute);

                    for(int i=lastIndexToRemove; i>modifiedIndex; i--)
                        vehicle.RemoveRoute(i);

                }
                toRemove.add(path);
            }
        }
        toRemove.forEach(p->altRouteSelector.getItems().remove(p));

        for(Street street: CONFIG.streets.values()) {
            if(!street.getClosurePoints().isEmpty()) {
                for(Coordinate closurePoint:street.getClosurePoints()) {
                    for (Vehicle v : CONFIG.vehicles.values()) {
                        PointInPath pip = v.getLastRoutePointBeforeCoordinate(street, closurePoint);
                        if (pip != null) {
                            ChangePath path = new ChangePath(v.getLine(),
                                    pip,
                                    v.getRoutes().subList(v.getRoutes().indexOf(pip.getRoute()), v.getRoutes().size())
                                            .stream()
                                            .map(r -> r.getRoute().get(r.getRoute().size() - 1))
                                            .collect(Collectors.toList()),
                                    8);
                            path.AddVehicle(v);

                            int index = altRouteSelector.getItems().indexOf(path);
                            if (index != -1)
                                altRouteSelector.getItems().get(index).AddVehicle(v);
                            else
                                altRouteSelector.getItems().add(path);
                        }
                    }
                }
            }
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

        if(isPaused && state != GUIState.NORMAL) {
            EvaluateStreetsAffectedByClosedPoint();
            if(!altRouteSelector.getItems().isEmpty()) return;
            closeStreetButton.setDisable(false);
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

        if(state == GUIState.NORMAL) {
            state = GUIState.CLOSING_STREETS;
            if(isPaused==false) {
                onPauseClicked(null);
            }

            closeStreetButton.setText("Cancel");
        } else {
            state = GUIState.NORMAL;
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
        for (Pair<Shape, GUIMapElement> pair : highlightedObjects) {
            ClearHighlight(pair);
        }
        streetBusinessSelector.setDisable(true);
        highlightedObjects.removeAll(highlightedObjects);
    }

    private void ClearHighlight(Pair<Shape,GUIMapElement> pair) {
        if (pair.getValue() instanceof GUIVehiclePath) {
            field.getChildren().remove(pair.getKey());
        } else {
            pair.getKey().setFill(pair.getValue().getNormalColor());
            pair.getKey().setStroke(pair.getValue().getNormalColor());
            if (pair.getKey() instanceof Circle) {
                pair.getKey().setStrokeWidth(1);
                highlightedBusPath.getChildren().removeAll(highlightedBusPath.getChildren());
            }
        }
    }
}

