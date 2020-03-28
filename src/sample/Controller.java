package sample;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

public class Controller {
    static boolean isPaused = false;
    public Pane mapPane;
    public Button pauseButton;


    public void handlePauseButtonAction(ActionEvent actionEvent) {
        isPaused = !isPaused;
        if(isPaused)
            pauseButton.setText("Unpause");
        else
            pauseButton.setText("Pause");
    }
}

