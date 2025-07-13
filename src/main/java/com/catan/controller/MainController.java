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

import javafx.application.HostServices;
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
 * Main controller für das CATAN-Spiel UI.
 * Erkennt automatisch und verwendet den passenden Board-Controller.
 */
public class MainController implements Initializable {
        
    // UI-Komponenten für das Spieler-Setup
    @FXML private VBox playerSetupBox;
    @FXML private TextField player1Field;
    @FXML private TextField player2Field;
    @FXML private TextField player3Field;
    @FXML private TextField player4Field;
    @FXML private Button startGameButton;
    
    // Spiel-UI-Komponenten
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

    // Layout-Komponenten
    @FXML private MenuBar mainMenuBar;
    @FXML private BorderPane rootPane;
    @FXML private VBox rightPanel;
    @FXML private ScrollPane scrollPane;
    @FXML private Label resourceLine;
    
    // Spiel-Logik und Controller
    private CatanGame game;
    private AuthenticBoardController boardController;
    private BankTradeController bankTradeController;
    private PlayerTradeController playerTradeController;
    private HostServices hostServices; // Für das Öffnen von externen Links





    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // UI-Komponenten initialisieren und stylen
        // TODO: UI-Styling-Methoden zur UIComponents-Klasse hinzufügen falls benötigt

        // Farben für verschiedene UI-Bereiche setzen
        rootPane.setStyle("-fx-background-color: #deb887;");
        mainMenuBar.setStyle("-fx-background-color: #D3D3D3;");
        playerSetupBox.setStyle("-fx-background-color: #87CEFA;");
        rightPanel.setStyle("-fx-background-color: #c4883fff;");
        playerInfoArea.setStyle("-fx-background-color: #deb887;");
        resourceLine.setStyle("-fx-font-size: 14px;");

        // Spiel-Controls am Anfang verstecken
        gameControlsBox.setVisible(false);
        boardScrollPane.setVisible(false);
        
        // Spieler-Setup anzeigen damit am Anfang nach Namen gefragt wird
        playerSetupBox.setVisible(true);
    }

    /**
     * Setzt die HostServices für das Öffnen von externen Links
     */
    public void setHostServices(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    /**
     * Startet das Spiel mit den eingegebenen Spielernamen
     */
    @FXML
    private void startGame() {
        // Spielernamen aus den Textfeldern sammeln
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
        
        // Mindestens 2 Spieler benötigt
        if (playerNames.size() < 2) {
            gameStatusLabel.setText("Error: At least 2 players required!");
            return;
        }
        
        try {
            // Neues Spiel mit authentischem CATAN-Board erstellen
            game = new CatanGame(playerNames);
            
            // Controller für Bank- und Spielerhandel initialisieren
            bankTradeController = new BankTradeController(game);
            
            // Board-Controller erstellen und initialisieren
            boardController = new AuthenticBoardController(game, gamePane);
            boardController = new AuthenticBoardController(game, gamePane);
            boardController.setMainController(this); // Controller-Referenz übergeben
            
            // Spielbrett rendern
            boardController.renderBoard();
            
            // Spiel-Interface anzeigen
            playerSetupBox.setVisible(false);
            gameControlsBox.setVisible(true);
            boardScrollPane.setVisible(true);
            
            // UI aktualisieren
            updateGameStatus();
            
        } catch (Exception e) {
            gameStatusLabel.setText("Error starting game: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Spieler-Info und Ressourcen anzeigen
        printPlayerInfo();
        printResources();
        gameLogArea.appendText(game.getCurrentPlayer().getName() + ": Beginnt das Spiel.\n");
    }

    /**
     * Würfelt die Würfel und verteilt Ressourcen entsprechend dem Ergebnis
     */
    @FXML
    private void rollDice() {
        if (game != null && game.getCurrentPhase() == CatanGame.GamePhase.PLAYING) {
            int roll = game.rollDice();
            game.setHasRolledDice(true);
            
            // Buttons nach Würfeln aktivieren/deaktivieren
            tradeButton.setDisable(false);
            tradeWithBankButton.setDisable(false);
            endTurnButton.setDisable(false);
            rollDiceButton.setDisable(true);
            
            // Board neu rendern und Würfelergebnis anzeigen
            boardController.renderBoard();
            diceRollLabel.setText("Dice Roll: " + roll);
            
            // Ressourcen basierend auf Würfelergebnis verteilen
            game.ingameResourceDistribution(roll);
            game.listPlayersResources(); // Übersicht vor grafischer Implementierung
            game.listPlayersVictoryPoints();
            
            updateGameStatus();
            
            // Log-Eintrag für Würfelergebnis
            gameLogArea.appendText(game.getCurrentPlayer()+" hat "+roll+ " gewürfelt.\n");
            printPlayerInfo();
            printResources();
        }
    }
     
    /**
     * Öffnet das Handelsfenster für Spieler-zu-Spieler-Handel
     */
    @FXML
    private void trade() {
    	if (game != null && game.getCurrentPhase() == CatanGame.GamePhase.PLAYING && game.hasRolledDice() && game.hasMovedRobber()) {
            gameLogArea.appendText(game.getCurrentPlayer()+" handelt.\n");
            
            // Popup-Fenster für Handel öffnen
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/popUp.fxml"));
                Parent root = loader.load();

                popUpController controller = loader.getController();
                controller.setGame(game);

                // Callback für erstellte Handelsangebote setzen
                controller.setOnOfferCreated(this::showTradeResponseWindow);


                // Popup-Fenster konfigurieren und anzeigen
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

        }
        
        // UI nach Handel aktualisieren
        printPlayerInfo();
        printResources();
        boardController.renderBoard();
    }
    
    /**
     * Zeigt das Antwortfenster für Handelsangebote
     */
    @FXML
    private void showTradeResponseWindow(TradeOffer offer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tradeResponse.fxml"));
            Parent root = loader.load();

            TradeResponseController ctrl = loader.getController();
            ctrl.setTradeOffer(offer); // Handelsangebot übergeben

            // Antwort-Popup konfigurieren und anzeigen
            Stage popup = new Stage();
            popup.setScene(new Scene(root));
            popup.setTitle("Antwort auf Handel von " + offer.getFromPlayer().getName());
            popup.initModality(Modality.APPLICATION_MODAL);
            popup.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Öffnet das Handelsfenster für Handel mit der Bank
     */
    @FXML
    private void tradeWithBank() {
        if (game != null && game.getCurrentPhase() == CatanGame.GamePhase.PLAYING && game.hasRolledDice() && game.hasMovedRobber()) {
            gameLogArea.appendText(game.getCurrentPlayer()+" handelt mit der Bank.\n");
            
            // Bank-Handel-Dialog anzeigen
            String logMessage = bankTradeController.showTradeDialog();
            if (logMessage != null) {
            	gameLogArea.appendText(logMessage + "\n");
            }
            
            // Board neu rendern
            boardController.renderBoard();
            
            // Ressourcen des aktuellen Spielers anzeigen
            playerLogArea.setText("   \n");
            Map<ResourceType, Integer> resources = game.getCurrentPlayer().getResources();
            for (Map.Entry<ResourceType, Integer> entry : resources.entrySet()) {
                if(entry.getKey() != null){
                    playerLogArea.appendText(entry.getKey() + ": " + entry.getValue() + "\n");
                }
            }
        }
        
        // UI nach Handel aktualisieren
        printPlayerInfo();
        printResources();
    }

    /**
     * Behandelt das Öffnen des Popup-Handelsfensters (Alternative Implementierung)
     */
    @FXML
    private void handleOpenPopupTrade() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PopupView.fxml"));
            Parent popupRoot = loader.load();

            Stage popupStage = new Stage();
            popupStage.setTitle("Popup Fenster");
            popupStage.initModality(Modality.APPLICATION_MODAL); // Blockiert Hauptfenster
            popupStage.setScene(new Scene(popupRoot));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        printPlayerInfo();
        printResources();
    }

    /**
     * Beendet den aktuellen Spielerzug
     */
    @FXML
    private void endTurn() {
    	if (game != null && game.hasCompletedPlacementForCurrentPhase() && game.hasRolledDice() && game.hasMovedRobber()) { 
    		boolean isBeginning = game.isBeginning(); // Wichtig vor endTurn abzufragen
            
            // Log-Eintrag für Rundenende
            gameLogArea.appendText(game.getCurrentPlayer().getName() + ": Runde beendet.\n");
            
            // Zug beenden und zum nächsten Spieler wechseln
            game.endTurn();
            updateGameStatus();
            printPlayerInfo();
            printResources();
            
            // Log-Eintrag für neuen Spieler
            gameLogArea.appendText(game.getCurrentPlayer().getName() + ": Runde beginnt.\n");

            // Gestohlene Ressourcen-Log anzeigen falls vorhanden
            if (game.getStolenResourcesLog() != null) {
            	gameLogArea.appendText(game.getStolenResourcesLog() + "\n");
            }
            
            // Board neu rendern wenn im Anfangsspiel
            if (isBeginning) {
            	boardController.renderBoard();
            }
            
            // Buttons für neue Runde zurücksetzen
            if (game.getCurrentPhase() == CatanGame.GamePhase.PLAYING) {
                diceRollLabel.setText("Dice Roll: -");
                rollDiceButton.setDisable(false);
                tradeWithBankButton.setDisable(true);
                tradeButton.setDisable(true);
                endTurnButton.setDisable(true);
                
                game.setHasRolledDice(false); // Für nächste Phase zurücksetzen
        		boardController.renderBoard();
            }
        }
    }
    
    /**
     * Aktualisiert den Spielstatus und die UI-Elemente
     */
    private void updateGameStatus() {
        if (game == null) return;
        
        // Siegbedingung prüfen
        game.checkVictoryCondition();
        Player currentPlayer = game.getCurrentPlayer();
        
        // Aktueller Spieler und Siegpunkte anzeigen
        currentPlayerLabel.setText("Aktueller Spieler: " + currentPlayer.getName() +
                                   " (VP: " + currentPlayer.getVictoryPoints() + ")");
        
        // Aktuelle Spielphase bestimmen
        String phase = switch (game.getCurrentPhase()) {
            case INITIAL_PLACEMENT_1 -> "Initial Setup - Round 1";
            case INITIAL_PLACEMENT_2 -> "Initial Setup - Round 2";
            case PLAYING -> "Playing";
        };
        
        // Spiel beendet oder weiterspielen
        if (game.isGameFinished()) {
            gameStatusLabel.setText("Game Over! Winner: " + game.getWinner().getName());
            
            // Alle Buttons deaktivieren
            rollDiceButton.setDisable(true);
            tradeButton.setDisable(true);
            tradeWithBankButton.setDisable(true);
            endTurnButton.setDisable(true);
            
            // Siegmeldung anzeigen
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Spiel beendet");
            alert.setHeaderText("🎉 " + game.getWinner().getName() + " hat gewonnen!");
            alert.setContentText("Herzlichen Glückwunsch zum Sieg!\nDas Spiel ist beendet.");
            alert.showAndWait();
        } else {
            // Spiel läuft weiter - Buttons entsprechend aktivieren/deaktivieren
            gameStatusLabel.setText("Phase: " + phase);
            rollDiceButton.setDisable(game.getCurrentPhase() != CatanGame.GamePhase.PLAYING);
            tradeButton.setDisable(game.getCurrentPhase() != CatanGame.GamePhase.PLAYING);
            tradeWithBankButton.setDisable(game.getCurrentPhase() != CatanGame.GamePhase.PLAYING);
            endTurnButton.setDisable(false);
        }
    }

    /**
     * Zeigt die Informationen des aktuellen Spielers an
     */
    @FXML
    public void printPlayerInfo(){
        playerInfoHeader.setText("Spieler-Info: " + game.getCurrentPlayer().getName());
        
        // Verfügbare Bauteile anzeigen
        playerLogArea.setText("Übrige Siedlungen: " + game.getCurrentPlayer().getSettlementCount() + "/5" + "\n");
        playerLogArea.appendText("Übrige Städte: " + game.getCurrentPlayer().getCityCount() + "/4" + "\n");
        playerLogArea.appendText("Übrige Straßen: " + game.getCurrentPlayer().getRoadCount()+ "/15" + "\n");
        playerLogArea.appendText("\n");
        
        // Baukosten anzeigen
        playerLogArea.appendText("Kosten: \n");
        playerLogArea.appendText("-Straße: 1xHolz, 1xLehm\n");
        playerLogArea.appendText("-Siedlung: 1xHolz, 1xLehm, 1xGetreide, 1xWolle\n");
        playerLogArea.appendText("-Stadt: 3xErz, 2xGetreide");
    }

    /**
     * Zeigt die Ressourcen des aktuellen Spielers in der Ressourcen-Zeile an
     */
    @FXML
    public void printResources() {
        Player currentPlayer = game.getCurrentPlayer();
        StringBuilder sb = new StringBuilder();
        
        // Ressourcen-String zusammenbauen
        String lumber = "Holz:" + String.valueOf(currentPlayer.getResourceCount(ResourceType.LUMBER));
        String brick  = "  " + "Lehm:" + String.valueOf(currentPlayer.getResourceCount(ResourceType.BRICK));
        String wool   = "  " + "Wolle:" + String.valueOf(currentPlayer.getResourceCount(ResourceType.WOOL));
        String grain  = "  " + "Getreide:" + String.valueOf(currentPlayer.getResourceCount(ResourceType.GRAIN));
        String ore    = "  " + "Erz:" + String.valueOf(currentPlayer.getResourceCount(ResourceType.ORE));
        
        sb.append(lumber).append(brick).append(wool).append(grain).append(ore);
        resourceLine.setText(sb.toString());
        System.out.println("Ressourcen aktualisiert: " + sb.toString());
    }

    /**
     * Behandelt das Starten eines neuen Spiels über das Menü
     */
    @FXML
	private void handleNewGame() {
	    System.out.println("Neues Spiel gestartet!");
	    
	    // Setup-Panel anzeigen
	    playerSetupBox.setVisible(true);
	    
	    // Spiel-Controls und Board ausblenden
	    gameControlsBox.setVisible(false);
	    gamePane.setVisible(false);
	    
	    // Eingabefelder leeren
	    player1Field.clear();
	    player2Field.clear();
	    player3Field.clear();
	    player4Field.clear();
	    
	    // Status-Label zurücksetzen
	    gameStatusLabel.setText("");
	}
    
    /**
     * Beendet die Anwendung
     */
    @FXML
    private void handleExit() {
        System.out.println("Spiel wird beendet...");
        System.exit(0); // Anwendung schließen
    }

    /**
     * Öffnet die CATAN-Regeln im Browser
     */
    @FXML
    private void handleAboutCatan() {
        if (hostServices != null) {
            // Link zu den offiziellen CATAN-Regeln öffnen
            hostServices.showDocument("https://www.catan.de/sites/default/files/2025-03/4002051684655_CAT_NE_Basis34_Manual_DE_web.pdf");
        } else {
            System.err.println("HostServices nicht gesetzt!");
        }
    }
}
