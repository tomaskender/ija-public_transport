package sample;

import data.implementations.CONFIG;
import data.interfaces.Vehicle;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

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
        CONFIG.controller = controller;
        controller.LoadStreets();
        controller.LoadStops();
        // moving vehicles on map
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                new Thread() {
                    public void run() {
                        if(!controller.isPaused) {
                            Platform.runLater(new Runnable() {
                                public void run() {
                                    long deltaInMillis = (long)(CONFIG.DELTA*CONFIG.SIM_DELTA*1000);
                                    controller.TickTime(deltaInMillis);
                                    for (Map.Entry<String, Vehicle> v : CONFIG.vehicles.entrySet()) {
                                        v.getValue().Tick(deltaInMillis);
                                    }
                                }
                            });
                        }
                    }
                }.start();
            }
        }, 0, (long)(CONFIG.SIM_DELTA*1000));
    }


    public static void main(String[] args) {
        XMLParser.main(args);
        launch(args);
    }
}
