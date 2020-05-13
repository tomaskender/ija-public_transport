package sample;

import data.implementations.CONFIG;
import data.interfaces.Vehicle;
import javafx.application.Application;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.time.LocalTime;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application {
    @FXML
    AnchorPane field;
    private boolean onlyAtStart = true;
    LocalTime wantedTime;

    /**
     * @brief main application stage
     * @param primaryStage application main stage
     * @throws Exception stage could not be loaded
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Public Transport Tracker");
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);

        primaryStage.show();

        Controller controller = loader.getController();
        //open file chooser to choose a map
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./src/sample/maps"));
        fileChooser.getExtensionFilters().add( new FileChooser.ExtensionFilter("XML", "*.xml"));
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(primaryStage);
        XMLParser.main(file);
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
                        Platform.runLater(new Runnable() {
                            public void run() {
                                long deltaInMillis = (long)(CONFIG.DELTA*CONFIG.SIM_DELTA*1000);
                                controller.TickTime(deltaInMillis);
                            }
                        });
                    }
                }.start();
            }
        }, 0, (long)(CONFIG.SIM_DELTA*1000));
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                Platform.exit();
                System.exit(0);
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
