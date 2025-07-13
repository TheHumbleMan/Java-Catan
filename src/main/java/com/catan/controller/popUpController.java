package com.catan.controller;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import com.catan.model.CatanGame;
import com.catan.model.Player;
import com.catan.model.ResourceType;
import com.catan.model.TradeOffer;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;

public class popUpController {

    @FXML private ComboBox<String> playerComboBox;
    @FXML private ComboBox<String> giveResourceComboBox;
    @FXML private Spinner<Integer> giveAmountSpinner;
    @FXML private ComboBox<String> receiveResourceComboBox;
    @FXML private Spinner<Integer> receiveAmountSpinner;

    private CatanGame game;
    private Consumer<TradeOffer> onOfferCreated; // <<<< NEU

    public void setGame(CatanGame game) {
        this.game = game;
        initializeDropdowns();
    }

    public void setOnOfferCreated(Consumer<TradeOffer> callback) {
        this.onOfferCreated = callback;
    }

    private void initializeDropdowns() {
        List<String> players = game.getPlayers().stream()
        .map(Player::getName)
        .filter(name -> !name.equals(game.getCurrentPlayer().getName()))  // den aktuellen Spieler rausfiltern
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
        // Einträge lesen
        TradeOffer offer = new TradeOffer(
            game.getCurrentPlayer(),
            game.getPlayerByName(playerComboBox.getValue()),
            ResourceType.valueOf(giveResourceComboBox.getValue()),
            giveAmountSpinner.getValue(),
            ResourceType.valueOf(receiveResourceComboBox.getValue()),
            receiveAmountSpinner.getValue()
        );

        // Optionaler Info-Dialog
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Handelsangebot erstellt");
        alert.setHeaderText(null);
        alert.setContentText("Angebot wurde erstellt und weitergeleitet.");
        alert.showAndWait();

        // Übergabe an den MainController
        if (onOfferCreated != null) {
            onOfferCreated.accept(offer);
        }

        // Fenster schließen
        ((Stage) playerComboBox.getScene().getWindow()).close();
    }
}
