/*
 * Controller for application initialization
 * Authors: Tomas Duris and Tomas Kender
 */
package sample;

import data.implementations.CONFIG;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application {

    /**
     * @brief main application stage
     * @param primaryStage application main stage
     * @throws Exception stage could not be loaded
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Public Transport Tracker");
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);

        primaryStage.show();

        Controller controller = loader.getController();
        //open file chooser to choose a map
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("./data"));
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
                new Thread(() -> Platform.runLater(() -> {
                    long deltaInMillis = (long)(CONFIG.DELTA*CONFIG.SIM_DELTA*1000);
                    controller.TickTime(deltaInMillis);
                })).start();
            }
        }, 0, (long)(CONFIG.SIM_DELTA*1000));
        primaryStage.setOnCloseRequest(windowEvent -> {
            Platform.exit();
            System.exit(0);
        });
    }


    public static void main(String[] args) {
        launch(args);
    }
}
