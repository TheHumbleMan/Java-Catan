package com.catan.controller;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.StringConverter;

import java.util.*;

import com.catan.model.CatanGame;
import com.catan.model.Player;
import com.catan.model.ResourceType;

public class BankTradeController {
	
	private final CatanGame game;
	
	public BankTradeController(CatanGame game) {
        this.game = game;
    }
	
	public String showTradeDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Handel ausführen");
        dialog.setHeaderText("Wähle, was du geben und erhalten willst (4 zu 1):");

        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UTILITY);

        // ResourceTypes (Enums)
        List<ResourceType> resourceTypes = List.of(ResourceType.values());

        // ChoiceBoxes für ResourceType
        ChoiceBox<ResourceType> giveBox = new ChoiceBox<>();
        ChoiceBox<ResourceType> getBox = new ChoiceBox<>();
        giveBox.getItems().addAll(resourceTypes);
        getBox.getItems().addAll(resourceTypes);

        giveBox.setValue(resourceTypes.get(0));
        getBox.setValue(resourceTypes.get(1));

        // StringConverter für deutsche Namen
        StringConverter<ResourceType> converter = new StringConverter<>() {
            @Override
            public String toString(ResourceType type) {
                return type.getGermanName();
            }

            @Override
            public ResourceType fromString(String string) {
                return resourceTypes.stream()
                    .filter(r -> r.getGermanName().equals(string))
                    .findFirst()
                    .orElse(null);
            }
        };
        giveBox.setConverter(converter);
        getBox.setConverter(converter);

        // Mengen-Auswahl
        ChoiceBox<Integer> amountBox = new ChoiceBox<>();
        for (int i = 1; i <= 10; i++) {
            amountBox.getItems().add(i);
        }
        amountBox.setValue(1);

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Geben:"), 0, 0);
        grid.add(giveBox, 1, 0);
        grid.add(new Label("Erhalten:"), 0, 1);
        grid.add(getBox, 1, 1);
        grid.add(new Label("Anzahl:"), 0, 2);
        grid.add(amountBox, 1, 2);
        dialog.getDialogPane().setContent(grid);

        // Buttons
        ButtonType confirmButton = new ButtonType("Bestätigen", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButton, ButtonType.CANCEL);

        // Ergebnis auslesen
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButton) {
                ResourceType give = giveBox.getValue();
                ResourceType get = getBox.getValue();
                int amount = amountBox.getValue();

                System.out.println("Gebe: " + give.getGermanName() +
                                   ", Erhalte: " + get.getGermanName() +
                                   ", Anzahl: " + amount);
                Player currentPlayer = game.getCurrentPlayer();
                                
                Map<ResourceType, Integer> cost = Map.of(give, amount * 4);
                Map<ResourceType, Integer> add = Map.of(get, amount);
                return game.subtractPlayerRessources(currentPlayer, cost, add);
                
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }
}


	   

