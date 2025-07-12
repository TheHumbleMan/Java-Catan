package com.catan.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.catan.model.CatanGame;
import com.catan.model.Player;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;


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
    @FXML private Button endTurnButton;
    @FXML private VBox gameControlsBox;
    @FXML private ScrollPane boardScrollPane;
    

    
    private CatanGame game;
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
    }
    
    @FXML
    private void rollDice() {
        if (game != null && game.getCurrentPhase() == CatanGame.GamePhase.PLAYING) {
            int roll = game.rollDice();
            game.setHasRolledDice(true);
            endTurnButton.setDisable(false);
            boardController.renderBoard();
            diceRollLabel.setText("Dice Roll: " + roll);
            game.ingameResourceDistribution(roll);
            game.listPlayersResources(); //die beiden nur zum Überblick vor grafischer Impl.
            game.listPlayersVictoryPoints();
            updateGameStatus();
            rollDiceButton.setDisable(true);

        }
    }
     
    @FXML
    private void trade() {
        if (game != null && game.getCurrentPhase() == CatanGame.GamePhase.PLAYING) {
            // Implement trade logic here
            // For now, just a placeholder action
            System.out.println("Trade action triggered. Implement trade logic here.");
            // You can open a trade dialog or perform other actions as needed

        }
    }
     
    @FXML
    private void tradeWithBank() {
        if (game != null && game.getCurrentPhase() == CatanGame.GamePhase.PLAYING) {
            // Implement trade with bank logic here
            // For now, just a placeholder action
            System.out.println("Trade with bank action triggered. Implement trade logic here.");
            // You can open a trade dialog or perform other actions as needed

        }
    }
    
    @FXML
    private void endTurn() {
        if (game != null && game.hasCompletedPlacementForCurrentPhase() && game.hasRolledDice()) {
            game.endTurn();
            boardController.renderBoard();
            updateGameStatus();
            
            // Clear dice roll for new turn
            if (game.getCurrentPhase() == CatanGame.GamePhase.PLAYING) {
                diceRollLabel.setText("Dice Roll: -");
                rollDiceButton.setDisable(false);
                endTurnButton.setDisable(true);
                System.out.println("MEIN ENDTURN STATEMENT");
                game.setHasRolledDice(false); //für nächste Phase
                boardController.renderBoard();
            }
            
        }
    }
    
    private void updateGameStatus() {
        if (game == null) return;
        
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
            endTurnButton.setDisable(true);
        } else {
            gameStatusLabel.setText("Phase: " + phase);
            rollDiceButton.setDisable(game.getCurrentPhase() != CatanGame.GamePhase.PLAYING);
            endTurnButton.setDisable(false);
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
