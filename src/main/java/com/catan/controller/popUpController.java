package com.catan.controller; // <-- passe das an dein tatsächliches Package an

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;


public class popUpController {

    @FXML
    private Button closeButton;

    @FXML
    private void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}

