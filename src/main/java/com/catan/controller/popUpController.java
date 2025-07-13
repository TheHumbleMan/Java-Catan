package com.catan.controller;

import java.util.Arrays;
import java.util.List;

import com.catan.model.CatanGame;
import com.catan.model.Player;
import com.catan.model.ResourceType;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;




public class popUpController {

    @FXML private ComboBox<String> playerComboBox;

    @FXML private ComboBox<String> giveResourceComboBox;
    @FXML private Spinner<Integer> giveAmountSpinner;

    @FXML private ComboBox<String> receiveResourceComboBox;
    @FXML private Spinner<Integer> receiveAmountSpinner;

    private CatanGame game;

    private String[] players; // Will be initialized in initialize()
    private List<String> resources = List.of("Holz", "Lehm", "Getreide", "Schaf", "Erz");
     
      public void setGame(CatanGame game) {
        this.game = game;
        initializeDropdowns(); // ruft alles auf, sobald game gesetzt ist
    }
    @FXML
    private void initializeDropdowns() {

        List<String> players = game.getPlayers().stream()
            .map(Player::getName)
            .toList();

        List<String> resources = Arrays.stream(ResourceType.values())
            .map(Enum::name)
            .toList();

        playerComboBox.getItems().addAll(players);
        giveResourceComboBox.getItems().addAll(resources);
        receiveResourceComboBox.getItems().addAll(resources);

        playerComboBox.getSelectionModel().selectFirst();
        giveResourceComboBox.getSelectionModel().selectFirst();
        receiveResourceComboBox.getSelectionModel().selectFirst();

        giveAmountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
        receiveAmountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));

    }

    @FXML
    private void handleTradeConfirm() {

        
        String selectedPlayer = playerComboBox.getValue();
        String giveRes = giveResourceComboBox.getValue();
        int giveAmt = giveAmountSpinner.getValue();
        String receiveRes = receiveResourceComboBox.getValue();
        int receiveAmt = receiveAmountSpinner.getValue();

        String message = "Du bietest " + giveAmt + "x " + giveRes +
                         " und möchtest " + receiveAmt + "x " + receiveRes +
                         " von " + selectedPlayer + ".";

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Handelsangebot");
        alert.setHeaderText("Angebot erstellt");
        alert.setContentText(message);
        alert.showAndWait();
    }
}


