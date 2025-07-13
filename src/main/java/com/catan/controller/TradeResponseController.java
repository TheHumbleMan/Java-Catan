package com.catan.controller;

import com.catan.model.Player;
import com.catan.model.TradeOffer;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class TradeResponseController {

    @FXML
    private Label offerLabel;

    private TradeOffer offer;

    public void setTradeOffer(TradeOffer offer) {
        this.offer = offer;
        System.out.println("TradeOffer gesetzt: " + offer);
        if (offerLabel != null) {
            offerLabel.setText(
                offer.getFromPlayer().getName() + " bietet " +
                offer.getGiveAmount() + "x " + offer.getGiveResource() +
                " und möchte " + offer.getReceiveAmount() + "x " +
                offer.getReceiveResource()
            );
        }
    }

    @FXML
    private void acceptTrade() {
        Player from = offer.getFromPlayer();
        Player to = offer.getToPlayer();

        // Ressourcen prüfen
        boolean fromHasEnough = from.getResources().getOrDefault(offer.getGiveResource(), 0) >= offer.getGiveAmount();
        boolean toHasEnough   = to.getResources().getOrDefault(offer.getReceiveResource(), 0) >= offer.getReceiveAmount();


        if (fromHasEnough && toHasEnough) {
            // Handel durchführen
            from.removeResource(offer.getGiveResource(), offer.getGiveAmount());
            to.addResource(offer.getGiveResource(), offer.getGiveAmount());

            to.removeResource(offer.getReceiveResource(), offer.getReceiveAmount());
            from.addResource(offer.getReceiveResource(), offer.getReceiveAmount());

            showInfo("Handel erfolgreich!");
        } else {
            showError("Mindestens ein Spieler hat nicht genug Ressourcen.");
        }

        // Fenster schließen
        ((Stage) offerLabel.getScene().getWindow()).close();
    }

    @FXML
    private void declineTrade() {
        showInfo("Handel wurde abgelehnt.");
        ((Stage) offerLabel.getScene().getWindow()).close();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Fehler beim Handel");
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
