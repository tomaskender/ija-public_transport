package sample;

import data.implementations.CONFIG;
import data.interfaces.Vehicle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Public Transport Tracker");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();


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
