package sample;

import data.implementations.CONFIG;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.text.SimpleDateFormat;
import java.time.LocalTime;

public class Controller {
    public Button pauseButton;
    public TextField timeLabel;


    boolean isPaused = true;

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

