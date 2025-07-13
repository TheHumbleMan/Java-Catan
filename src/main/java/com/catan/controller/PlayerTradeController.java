package com.catan.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.catan.model.CatanGame;
import com.catan.model.Player;
import com.catan.model.ResourceType;

import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

public class PlayerTradeController {
	private final CatanGame game;
	
		
	public PlayerTradeController(CatanGame game) {
        this.game = game;
    }
	
	public String showTradeDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Handel ausführen");
        dialog.setHeaderText("Wähle, was und mit wem du handeln willst");

        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.UTILITY);

        //Player
        List<Player> players = game.getPlayers().stream()
        	    .filter(p -> !p.equals(game.getCurrentPlayer()))
        	    .toList();
        
        ChoiceBox<Player> playerChoiceBox = new ChoiceBox<>();
        playerChoiceBox.getItems().addAll(players);
        playerChoiceBox.setValue(players.get(0)); // Standardauswahl
        
     // StringConverter für Player → zeigt Namen im ChoiceBox-Dropdown
        StringConverter<Player> playerConverter = new StringConverter<>() {
            @Override
            public String toString(Player player) {
                return player.getName();
            }

            @Override
            public Player fromString(String string) {
                return players.stream()
                        .filter(p -> p.getName().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        };

        playerChoiceBox.setConverter(playerConverter);
        
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
        ChoiceBox<Integer> amountGiveBox = new ChoiceBox<>();
        for (int i = 1; i <= 10; i++) {
            amountGiveBox.getItems().add(i);
        }
        amountGiveBox.setValue(1);
        
        ChoiceBox<Integer> amountGetBox = new ChoiceBox<>();
        for (int i = 1; i <= 10; i++) {
            amountGetBox.getItems().add(i);
        }
        amountGetBox.setValue(1);

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Handelspartner:"), 0, 0);
        grid.add(playerChoiceBox, 1, 0);
        grid.add(new Label("Geben, Typ:"), 0, 1);
        grid.add(giveBox, 1, 1);
        grid.add(new Label("Erhalten, Typ:"), 0, 2);
        grid.add(getBox, 1, 2);
        grid.add(new Label("Geben, Anzahl:"), 0, 3);
        grid.add(amountGiveBox, 1, 3);
        grid.add(new Label("Erhalten, Anzahl:"), 0, 4);
        grid.add(amountGetBox, 1, 4);
        dialog.getDialogPane().setContent(grid);

        // Buttons
        ButtonType confirmButton = new ButtonType("Bestätigen", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButton, ButtonType.CANCEL);

        // Ergebnis auslesen
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButton) {
                ResourceType give = giveBox.getValue();
                ResourceType get = getBox.getValue();
                Player otherPlayer = playerChoiceBox.getValue();
                int amountGive = amountGiveBox.getValue();
                int amountGet = amountGetBox.getValue();

                System.out.println("Gebe: " + give.getGermanName() +
                                   ", Erhalte: " + get.getGermanName() +
                                   ", Anzahl: " + amountGive);
                                
                Map<ResourceType, Integer> cost = Map.of(give, amountGive);
                Map<ResourceType, Integer> add = Map.of(get, amountGet);
                return game.handlePlayerTrade(otherPlayer, cost, add);
                
            }
            return null;
        });

        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }
}

