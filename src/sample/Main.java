package sample;

import data.implementations.CONFIG;
import data.interfaces.Vehicle;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

import java.awt.*;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.List;
import java.util.ArrayList;

public class Main extends Application {
    @FXML
    AnchorPane field;

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Public Transport Tracker");
        primaryStage.setScene(new Scene(root, 1000, 700));
        primaryStage.show();

        Controller controller = loader.getController();
        controller.LoadStreets();
        controller.LoadStops();
        // moving vehicles on map
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                CONFIG.CURRENT_TIME = CONFIG.CURRENT_TIME.plus(CONFIG.DELTA, ChronoUnit.SECONDS);
                for(Map.Entry<String, Vehicle> v: CONFIG.vehicles.entrySet()) {
                    v.getValue().Tick(CONFIG.DELTA);
                }
            }
        }, 0, 1000);
    }


    public static void main(String[] args) {
        XMLParser.main(args);
        launch(args);
    }
}
