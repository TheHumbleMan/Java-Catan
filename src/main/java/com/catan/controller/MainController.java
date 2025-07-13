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
import com.catan.model.TradeOffer;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
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
    @FXML private Button endTurnButton;
    @FXML private VBox gameControlsBox;
    @FXML private TextArea gameLogArea;
    @FXML private TextArea playerLogArea;
    @FXML private Label playerInfoHeader;
    @FXML private ScrollPane boardScrollPane;
    @FXML private VBox playerInfoArea;

    @FXML private MenuBar mainMenuBar;
    @FXML private BorderPane rootPane;
    @FXML private VBox rightPanel;
    @FXML private ScrollPane scrollPane;
    @FXML private Label resourceLine;
    
    private CatanGame game;
    private AuthenticBoardController boardController;
    private BankTradeController bankTradeController;
    private PlayerTradeController playerTradeController;


    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize UI components
        // TODO: Add UI styling methods to UIComponents class if needed

        rootPane.setStyle("-fx-background-color: #deb887;");
        mainMenuBar.setStyle("-fx-background-color: #D3D3D3;");
        playerSetupBox.setStyle("-fx-background-color: #87CEFA;");
        rightPanel.setStyle("-fx-background-color: #c4883fff;");
        playerInfoArea.setStyle("-fx-background-color: #deb887;");
        resourceLine.setStyle("-fx-font-size: 14px;");


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
            
            //für banklogik
            bankTradeController = new BankTradeController(game);
            playerTradeController = new PlayerTradeController(game);
            
            // Create and initialize the board controller
            boardController = new AuthenticBoardController(game, gamePane);
            boardController = new AuthenticBoardController(game, gamePane);
            boardController.setMainController(this); // ← Controllerübergabe
            
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
        printPlayerInfo();
        printResources();
        gameLogArea.appendText(game.getCurrentPlayer().getName() + ": Beginnt das Spiel.\n");
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
            gameLogArea.appendText(game.getCurrentPlayer()+" hat "+roll+ " gewürfelt.\n");
            printPlayerInfo();
            printResources();
            
        }
    }
     
    @FXML
    private void trade() {
    	if (game != null && game.getCurrentPhase() == CatanGame.GamePhase.PLAYING && game.hasRolledDice() && game.hasMovedRobber()) {
            //tradeWithBankButton.setDisable(true); Löschen?
            gameLogArea.appendText(game.getCurrentPlayer()+" handelt.\n");
            // Open a popup for trading
             try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/popUp.fxml"));
        Parent root = loader.load();

        popUpController controller = loader.getController();
        controller.setGame(game);

        controller.setOnOfferCreated(this::showTradeResponseWindow);

        Stage popupStage = new Stage();
        popupStage.setTitle("Handel anbieten");
        popupStage.setScene(new Scene(root));
        popupStage.setWidth(350);
        popupStage.setHeight(200);
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.showAndWait();
    } catch (IOException e) {
        e.printStackTrace();
    }

            /* Löschen?
            String logMessage = playerTradeController.showTradeDialog();
            if (logMessage != null) {
            	gameLogArea.appendText(logMessage + "\n");
            }
            boardController.renderBoard();


            playerLogArea.setText(" \n");
            Map<ResourceType, Integer> resources = game.getCurrentPlayer().getResources();
            for (Map.Entry<ResourceType, Integer> entry : resources.entrySet()) {
                if(entry.getKey() != null){
                    playerLogArea.appendText(entry.getKey() + " " + entry.getValue() + "\n");
                }
            }
            // offerTrade nach Fenster zum Auswählen der Person und ressourcen
*/
        }
        printPlayerInfo();
        printResources();
        boardController.renderBoard();
    }
    @FXML
    private void showTradeResponseWindow(TradeOffer offer) {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/tradeResponse.fxml"));
        Parent root = loader.load();

        TradeResponseController ctrl = loader.getController();
        ctrl.setTradeOffer(offer); // Angebot übergeben

        Stage popup = new Stage();
        popup.setScene(new Scene(root));
        popup.setTitle("Antwort auf Handel von " + offer.getFromPlayer().getName());
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.showAndWait();
    } catch (IOException e) {
        e.printStackTrace();
    }
}

     
    @FXML
    private void tradeWithBank() {
        if (game != null && game.getCurrentPhase() == CatanGame.GamePhase.PLAYING && game.hasRolledDice() && game.hasMovedRobber()) {
            //tradeWithBankButton.setDisable(true);
            gameLogArea.appendText(game.getCurrentPlayer()+" handelt am mit der Bank.\n");
            String logMessage = bankTradeController.showTradeDialog();
            if (logMessage != null) {
            	gameLogArea.appendText(logMessage + "\n");
            }
            boardController.renderBoard();
            
            //vorher:tradeLogik
            playerLogArea.setText("   \n");
            Map<ResourceType, Integer> resources = game.getCurrentPlayer().getResources();
            for (Map.Entry<ResourceType, Integer> entry : resources.entrySet()) {
                if(entry.getKey() != null){
                    playerLogArea.appendText(entry.getKey() + ": " + entry.getValue() + "\n");
            }
            }

        }
        printPlayerInfo();
        printResources();
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
        printPlayerInfo();
        printResources();
    }


    
    @FXML
    private void endTurn() {
    	if (game != null && game.hasCompletedPlacementForCurrentPhase() && game.hasRolledDice() && game.hasMovedRobber()) { 
    		boolean isBeginning = game.isBeginning(); //wichtig vor endturn abzufragen
            gameLogArea.appendText(game.getCurrentPlayer().getName() + ": Runde beendet.\n");
            game.endTurn();
            updateGameStatus();
            printPlayerInfo();
            printResources();
            gameLogArea.appendText(game.getCurrentPlayer().getName() + ": Runde beginnt.\n");

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
        currentPlayerLabel.setText("Aktueller Spieler: " + currentPlayer.getName() +
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
            endTurnButton.setDisable(false);
        }
    }

    @FXML
    public void printPlayerInfo(){
        playerInfoHeader.setText("Spieler-Info: " + game.getCurrentPlayer().getName());
        playerLogArea.setText("Übrige Siedlungen: " + game.getCurrentPlayer().getSettlementCount() + "/5" + "\n");
        playerLogArea.appendText("Übrige Städte: " + game.getCurrentPlayer().getCityCount() + "/4" + "\n");
        playerLogArea.appendText("Übrige Straßen: " + game.getCurrentPlayer().getRoadCount()+ "/15");

    }

    /*
     * Druckt die Resourcen in das Recourcen-Feld
     */
    @FXML
    public void printResources() {
        Player currentPlayer = game.getCurrentPlayer();
        StringBuilder sb = new StringBuilder();
        String lumber = "Holz:" + String.valueOf(currentPlayer.getResourceCount(ResourceType.LUMBER));
        String brick  = "  " + "Lehm:" + String.valueOf(currentPlayer.getResourceCount(ResourceType.BRICK));
        String wool   = "  " + "Wolle:" + String.valueOf(currentPlayer.getResourceCount(ResourceType.WOOL));
        String grain  = "  " + "Getreide:" + String.valueOf(currentPlayer.getResourceCount(ResourceType.GRAIN));
        String ore    = "  " + "Erz:" + String.valueOf(currentPlayer.getResourceCount(ResourceType.ORE));
        sb.append(lumber).append(brick).append(wool).append(grain).append(ore);
        resourceLine.setText(sb.toString());
        System.out.println("Ressourcen aktualisiert: " + sb.toString());
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
