package com.catan.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.catan.model.CatanGame;
import com.catan.model.Player;
import com.catan.model.ResourceType;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;


/**
 * Main controller for the CATAN game UI.
 * Automatically detects and uses the appropriate board controller.
 */
public class MainController implements Initializable {
        
    @FXML private VBox playerSetupBox;
    @FXML private TextField player1Field;
    @FXML private TextField player2Field;
    @FXML private TextField player3Field;
    @FXML private TextField player4Field;
    @FXML private Button startGameButton;
    @FXML private Pane gamePane;
    @FXML private Label gameStatusLabel;
    @FXML private Label currentPlayerLabel;
    @FXML private Label diceRollLabel;
    @FXML private Button rollDiceButton;
    @FXML private Button tradeButton;
    @FXML private Button tradeWithBankButton;
    @FXML private Button buyDevelopmentCardButton;
    @FXML private Button endTurnButton;
    @FXML private VBox gameControlsBox;
    @FXML private TextArea gameLogArea;
    @FXML private TextArea playerLogArea;
    @FXML private Label playerInfoHeader;
    @FXML private ScrollPane boardScrollPane;
    @FXML private VBox playerInfoArea;

    
    private CatanGame  game;
    private AuthenticBoardController boardController;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize UI components
        // TODO: Add UI styling methods to UIComponents class if needed
        
        // Hide game controls initially
        gameControlsBox.setVisible(false);
        boardScrollPane.setVisible(false);
        
        //sodass am Anfang gefragt wird nach Namen
        playerSetupBox.setVisible(true);
    }
    
    @FXML
    private void startGame() {
        // Collect player names
        List<String> playerNames = new ArrayList<>();
        
        if (!player1Field.getText().trim().isEmpty()) {
            playerNames.add(player1Field.getText().trim());
        }
        if (!player2Field.getText().trim().isEmpty()) {
            playerNames.add(player2Field.getText().trim());
        }
        if (!player3Field.getText().trim().isEmpty()) {
            playerNames.add(player3Field.getText().trim());
        }
        if (!player4Field.getText().trim().isEmpty()) {
            playerNames.add(player4Field.getText().trim());
        }
        
        // Validate player count
        if (playerNames.size() < 2) {
            gameStatusLabel.setText("Error: At least 2 players required!");
            return;
        }
        
        try {
            // Create game with authentic CATAN board by default
            game = new CatanGame(playerNames);
            
            // Create and initialize the board controller
            boardController = new AuthenticBoardController(game, gamePane);
            
            // Render the board
            boardController.renderBoard();
            
            // Show game interface
            playerSetupBox.setVisible(false);
            gameControlsBox.setVisible(true);
            boardScrollPane.setVisible(true);
            
            // Update UI
            updateGameStatus();
            
        } catch (Exception e) {
            gameStatusLabel.setText("Error starting game: " + e.getMessage());
            e.printStackTrace();
        }

        /*        // Update player info header and log area
        playerInfoHeader.setText("Spieler-Info: " + game.getCurrentPlayer().getName());
        playerLogArea.setText("Resourcen:\n"); //Resourcen anzeigen
            Map<ResourceType, Integer> resources = game.getCurrentPlayer().getResources();
            for (Map.Entry<ResourceType, Integer> entry : resources.entrySet()) {
                if(entry.getKey() != null){
                    playerLogArea.appendText(entry.getKey() + ": " + entry.getValue() + "\n");
            }
        }*/
        printPlayerInfo();
    }
    
    @FXML
    private void rollDice() {
        if (game != null && game.getCurrentPhase() == CatanGame.GamePhase.PLAYING) {
            int roll = game.rollDice();
            game.setHasRolledDice(true);
            tradeButton.setDisable(false);
            tradeWithBankButton.setDisable(false);
            endTurnButton.setDisable(false);
            boardController.renderBoard();
            diceRollLabel.setText("Dice Roll: " + roll);
            game.ingameResourceDistribution(roll);
            game.listPlayersResources(); //die beiden nur zum Überblick vor grafischer Impl.
            game.listPlayersVictoryPoints();
            updateGameStatus();
            rollDiceButton.setDisable(true);
            tradeButton.setDisable(false);
            tradeWithBankButton.setDisable(false);
            buyDevelopmentCardButton.setDisable(false);
            gameLogArea.appendText(game.getCurrentPlayer()+" hat "+roll+ " gewürfelt.\n");

            /*            // Update player info header and log area
             //playerInfoHeader.setText("Spieler-Info: " + game.getCurrentPlayer().getName());
            playerLogArea.setText("Resourcen:\n"); //Resourcen anzeigen
            Map<ResourceType, Integer> resources = game.getCurrentPlayer().getResources();
            for (Map.Entry<ResourceType, Integer> entry : resources.entrySet()) {
                if(entry.getKey() != null){
                    playerLogArea.appendText(entry.getKey() + ": " + entry.getValue() + "\n");
                }
            }*/
            printPlayerInfo();

        }
    }
     
    @FXML
    private void trade() {
        if (game != null && game.getCurrentPhase() == CatanGame.GamePhase.PLAYING && game.hasRolledDice() && game.hasMovedRobber()) {
            // Implement trade logic here
            // For now, just a placeholder action
            tradeButton.setDisable(true);
            gameLogArea.appendText(game.getCurrentPlayer()+" handelt.\n");
            // Open a popup for trading
            try {
               FXMLLoader loader = new FXMLLoader(getClass().getResource("/popUp.fxml"));
                Parent popupRoot = loader.load();

                  popUpController popUpController = loader.getController();
                  popUpController.setGame(game);  // game an PopUpController übergeben


                Stage popupStage = new Stage();
                popupStage.setTitle("Handelsfenster");
                popupStage.initModality(Modality.APPLICATION_MODAL);
                popupStage.setScene(new Scene(popupRoot));
                popupStage.setWidth(300);
                popupStage.setHeight(200);
                popupStage.centerOnScreen();
                //popupStage.showAndWait();
                popupStage.show(); // statt showAndWait();

            //} catch (IOException e) {
              //  e.printStackTrace();
            } catch (IOException e) {
                System.err.println("Fehler beim Laden des FXML:");
                e.printStackTrace();
            }


            playerLogArea.setText(" \n");
            Map<ResourceType, Integer> resources = game.getCurrentPlayer().getResources();
            for (Map.Entry<ResourceType, Integer> entry : resources.entrySet()) {
                if(entry.getKey() != null){
                    playerLogArea.appendText(entry.getKey() + " " + entry.getValue() + "\n");
                }
            }
            // offerTrade nach Fenster zum Auswählen der Person und ressourcen

        }
    }
     
    @FXML
    private void tradeWithBank() {
        if (game != null && game.getCurrentPhase() == CatanGame.GamePhase.PLAYING && game.hasRolledDice() && game.hasMovedRobber()) {
            tradeWithBankButton.setDisable(true);
            gameLogArea.appendText(game.getCurrentPlayer()+" handelt am Hafen.\n");

            //vorher:tradeLogik
            playerLogArea.setText("   \n");
            Map<ResourceType, Integer> resources = game.getCurrentPlayer().getResources();
            for (Map.Entry<ResourceType, Integer> entry : resources.entrySet()) {
                if(entry.getKey() != null){
                    playerLogArea.appendText(entry.getKey() + ": " + entry.getValue() + "\n");
            }
            }

        }
    }

    @FXML
    private void handleOpenPopupTrade() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PopupView.fxml"));
            Parent popupRoot = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("Popup Fenster");
            popupStage.initModality(Modality.APPLICATION_MODAL); // blockiert Hauptfenster
            popupStage.setScene(new Scene(popupRoot));
            //popupStage.showAndWait(); // oder show() für nicht-blockierend
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    @FXML
    private void buyDevelopmentCard() {
        if (game != null && game.getCurrentPhase() == CatanGame.GamePhase.PLAYING && game.hasRolledDice() && game.hasMovedRobber() ) {
            buyDevelopmentCardButton.setDisable(true);
            gameLogArea.appendText(game.getCurrentPlayer().getName() + " möchte eine Entwicklungskarte kaufen.\n");
            if (game.getCurrentPlayer().canBuyDevelopmentCard()) {                
                gameLogArea.appendText(game.getCurrentPlayer().getName() + " hat eine Entwicklungskarte gekauft.\n");
                playerLogArea.setText("   \n");
                Map<ResourceType, Integer> resources = game.getCurrentPlayer().getResources();
                for (Map.Entry<ResourceType, Integer> entry : resources.entrySet()) {
                    if(entry.getKey() != null){
                        playerLogArea.appendText(entry.getKey() + ": " + entry.getValue() + "\n");
                    }
                }
                
            } else {
                gameLogArea.appendText(game.getCurrentPlayer().getName() + " kann keine Entwicklungskarte kaufen.\n");
            }
        }
    }


    @FXML
    private void endTurn() {    	
    	if (game != null && game.hasCompletedPlacementForCurrentPhase() && game.hasRolledDice() && game.hasMovedRobber()) { 
    		boolean isBeginning = game.isBeginning(); //wichtig vor endturn abzufragen
            game.endTurn();
            updateGameStatus();
            /*            // Update player info header and log area
            playerInfoHeader.setText("Spieler-Info: " + game.getCurrentPlayer().getName());
            playerLogArea.setText("Resourcen:\n"); //Resourcen anzeigen
            Map<ResourceType, Integer> resources = game.getCurrentPlayer().getResources();
            for (Map.Entry<ResourceType, Integer> entry : resources.entrySet()) {
                if(entry.getKey() != null){
                    playerLogArea.appendText(entry.getKey() + ": " + entry.getValue() + "\n");
            }
            }*/
            printPlayerInfo();

            if (game.getStolenResourcesLog() != null) {
            	gameLogArea.appendText(game.getStolenResourcesLog() + "\n");
            }
            if (isBeginning) {
            	boardController.renderBoard();
            }
            // Clear dice roll for new turn
            if (game.getCurrentPhase() == CatanGame.GamePhase.PLAYING) {
                diceRollLabel.setText("Dice Roll: -");
                rollDiceButton.setDisable(false);
                tradeWithBankButton.setDisable(true);
                tradeButton.setDisable(true);
                buyDevelopmentCardButton.setDisable(true);
                endTurnButton.setDisable(true);
                System.out.println("MEIN ENDTURN STATEMENT");
                game.setHasRolledDice(false); //für nächste Phase
        		boardController.renderBoard();
               // boardController.renderBoard();                
            }
            
        }
        
        }
    
    
    private void updateGameStatus() {
        if (game == null) return;
        game.checkVictoryCondition();
        Player currentPlayer = game.getCurrentPlayer();
        currentPlayerLabel.setText("Current Player: " + currentPlayer.getName() + 
                                   " (VP: " + currentPlayer.getVictoryPoints() + ")");
        
        String phase = switch (game.getCurrentPhase()) {
            case INITIAL_PLACEMENT_1 -> "Initial Setup - Round 1";
            case INITIAL_PLACEMENT_2 -> "Initial Setup - Round 2";
            case PLAYING -> "Playing";
        };
        
        if (game.isGameFinished()) {
            gameStatusLabel.setText("Game Over! Winner: " + game.getWinner().getName());
            rollDiceButton.setDisable(true);
            tradeButton.setDisable(true);
            tradeWithBankButton.setDisable(true);
            endTurnButton.setDisable(true);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Spiel beendet");
            alert.setHeaderText("🎉 " + game.getWinner().getName() + " hat gewonnen!");
            alert.setContentText("Herzlichen Glückwunsch zum Sieg!\nDas Spiel ist beendet.");
            alert.showAndWait();
        } else {
            gameStatusLabel.setText("Phase: " + phase);
            rollDiceButton.setDisable(game.getCurrentPhase() != CatanGame.GamePhase.PLAYING);
            tradeButton.setDisable(game.getCurrentPhase() != CatanGame.GamePhase.PLAYING);
            tradeWithBankButton.setDisable(game.getCurrentPhase() != CatanGame.GamePhase.PLAYING);
            buyDevelopmentCardButton.setDisable(game.getCurrentPhase() != CatanGame.GamePhase.PLAYING);
            endTurnButton.setDisable(false);
        }
    }

    @FXML
    private void printPlayerInfo(){
        playerInfoHeader.setText("Spieler-Info: " + game.getCurrentPlayer().getName());
        playerLogArea.setText("Resourcen:\n"); //Resourcen anzeigen
        Map<ResourceType, Integer> resources = game.getCurrentPlayer().getResources();
        for (Map.Entry<ResourceType, Integer> entry : resources.entrySet()) {
            if(entry.getKey() != null){
                playerLogArea.appendText(entry.getKey() + ": " + entry.getValue() + "\n");
            }
        }
    }

    @FXML
	private void handleNewGame() {
	    System.out.println("Neues Spiel gestartet!");
	    
	    // Zeige Setup Panel an
	    playerSetupBox.setVisible(true);
	    
	    // Blende Controls und Board aus
	    gameControlsBox.setVisible(false);
	    gamePane.setVisible(false);
	    
	    // Optional: Felder leeren
	    player1Field.clear();
	    player2Field.clear();
	    player3Field.clear();
	    player4Field.clear();
	    
	    // Status Label leeren
	    gameStatusLabel.setText("");
	}
    @FXML
    private void handleExit() {
        System.out.println("Spiel wird beendet...");
        System.exit(0); // Anwendung schließen
    }
}
