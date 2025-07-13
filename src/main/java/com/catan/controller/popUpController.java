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
import javafx.util.StringConverter;

public class popUpController {

    @FXML private ComboBox<String> playerComboBox;
    @FXML private ComboBox<ResourceType> giveResourceComboBox;
    @FXML private Spinner<Integer> giveAmountSpinner;
    @FXML private Spinner<Integer> receiveAmountSpinner;
    @FXML private ComboBox<ResourceType> receiveResourceComboBox;

    private CatanGame game;
    private Consumer<TradeOffer> onOfferCreated;

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
            .filter(name -> !name.equals(game.getCurrentPlayer().getName()))  // aktuellen Spieler ausschließen
            .toList();

        playerComboBox.getItems().addAll(players);

        // ResourceType direkt als Enum in ComboBoxes
        giveResourceComboBox.getItems().addAll(ResourceType.values());
        receiveResourceComboBox.getItems().addAll(ResourceType.values());

        // StringConverter für deutschen Namen in den ResourceType-ComboBoxes
        StringConverter<ResourceType> converter = new StringConverter<>() {
            @Override
            public String toString(ResourceType resource) {
                return resource == null ? "" : resource.getGermanName();
            }

            @Override
            public ResourceType fromString(String string) {
                return Arrays.stream(ResourceType.values())
                    .filter(rt -> rt.getGermanName().equals(string))
                    .findFirst()
                    .orElse(null);
            }
        };
        giveResourceComboBox.setConverter(converter);
        receiveResourceComboBox.setConverter(converter);

        // Erste Einträge auswählen
        playerComboBox.getSelectionModel().selectFirst();
        giveResourceComboBox.getSelectionModel().selectFirst();
        receiveResourceComboBox.getSelectionModel().selectFirst();

        // Spinner initialisieren
        giveAmountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
        receiveAmountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
    }

    @FXML
private void handleTradeConfirm() {
    TradeOffer offer = new TradeOffer(
        game.getCurrentPlayer(),
        game.getPlayerByName(playerComboBox.getValue()),
        giveResourceComboBox.getValue(),
        giveAmountSpinner.getValue(),
        receiveResourceComboBox.getValue(),
        receiveAmountSpinner.getValue()
    );

    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Handelsangebot erstellt");
    alert.setHeaderText(null);
    alert.setContentText("Angebot wurde erstellt und weitergeleitet.");
    alert.showAndWait();  // Warte hier, bis der Nutzer bestätigt

    if (onOfferCreated != null) {
        onOfferCreated.accept(offer);
    }

    ((Stage) playerComboBox.getScene().getWindow()).close();
}


}
