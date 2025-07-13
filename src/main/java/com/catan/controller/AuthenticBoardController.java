package com.catan.controller;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.catan.model.AuthenticCatanBoard;
import com.catan.model.Building;
import com.catan.model.CatanGame;
import com.catan.model.EdgeCoordinate;
import com.catan.model.HexCoordinate;
import com.catan.model.Player;
import com.catan.model.RoundedPoint2D;
import com.catan.model.TerrainTile;
import com.catan.model.VertexCoordinate;
import com.catan.view.UIComponents;

import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
/**
 * Controller für das authentische CATAN-Board.
 * Verantwortlich für das Rendern aller Spielfeldelemente wie Hexfelder, Siedlungen, Straßen, Räuber und Spieleranzeige.
 * Unterstützt Interaktionen wie den Bau von Gebäuden, das Platzieren von Straßen und die Bewegung des Räubers.
 * Voraussetzungen: Das übergebene {@link CatanGame} muss ein {@link AuthenticCatanBoard} enthalten.
 */
public class AuthenticBoardController {
    
    // CATAN-authentische Board-Layout-Konstanten
    private static final double HEX_RADIUS = 45; // Radius der Hexagon-Felder (reduziert von 55)
    private static final double BOARD_CENTER_X = 300.0; // X-Zentrum des Boards (ursprünglich 400)
    private static final double BOARD_CENTER_Y = 200.0; // Y-Zentrum des Boards (ursprünglich 350)
    
    // Siedlungs- und Straßen-Konstanten
    private static final double SETTLEMENT_SIZE = 8.0; // Größe der Siedlungsmarkierungen
    private static final double ROAD_LENGTH = 20.0; // Länge der Straßensegmente
    private static final double ROAD_WIDTH = 4.0; // Breite der Straßensegmente
    private static final double ROBBER_SIZE = 70.0; // Größe des Räuber-Symbols
    
    // Spiel-Logik und UI-Komponenten
    private final CatanGame game;
    private final AuthenticCatanBoard board;
    private final Pane boardPane;

    /**
     * Konstruktor für den AuthenticBoardController
     * @param game Das aktuelle CATAN-Spiel
     * @param boardPane Das UI-Panel für das Spielbrett
     */
    public AuthenticBoardController(CatanGame game, Pane boardPane) {
    	this.board = game.getBoard();
        this.game = game;
        this.boardPane = boardPane;
        
        // Validierung: Spiel muss authentisches Board verwenden
        if (board == null) {
            throw new IllegalArgumentException("Game muss authentisches CATAN-Board verwenden");
        }
    }

    // Referenz zum MainController für UI-Updates
    private MainController mainController;
    
    /**
     * Setzt die Referenz zum MainController für UI-Updates
     */
    public void setMainController(MainController controller) {
        this.mainController = controller;
    }
    
    /**
     * Rendert das komplette authentische CATAN-Board neu.
     * Löscht alle vorherigen Elemente und erstellt das Board von Grund auf neu.
     */
    public void renderBoard() {
    	// Prüfung ob Räuber bereits bewegt wurde (wichtig für Spiellogik)
    	if (game.hasMovedRobber() == true) {
            // Board-Panel leeren und Hintergrundfarbe setzen
            boardPane.getChildren().clear();
            boardPane.setStyle("-fx-background-color: #87CEFA;"); // Hellblau (SkyBlue)
            
            System.out.println("robber position: " + board.getRobberPosition());
            
            // Alle Board-Elemente in der korrekten Reihenfolge rendern
            renderHexagonTiles(); // Hexagon-Felder mit Terrain
            renderSettlementSpots(); // Siedlungsplätze
            renderRoadSpots(game.isBeginning()); // Straßenplätze
            renderPlayerDot(); // Spieler-Indikator
            
            // Debug-Ausgabe für Board-Statistiken
            int settlement_count = new HashSet<>(board.getValidVertices().values()).size();
            System.out.println("✓ Authentisches CATAN-Board gerendert: " +
            		settlement_count + " Siedlungsmöglichkeiten, " +
                             board.getValidVertices().size() + " Siedlungsoptionen");
    	}
        
        // UI-Updates über MainController auslösen
        mainController.printPlayerInfo(); // Spieler-Informationen aktualisieren
        mainController.printResources(); // Ressourcen-Anzeige aktualisieren
    }

    /**
     * Rendert einen farbigen Punkt oben links auf dem Board zur Anzeige der aktuellen Spielerfarbe
     */
    private void renderPlayerDot() {
        Player currentPlayer = game.getCurrentPlayer();
        
        // Farbigen Punkt erstellen
        Rectangle playerDot = new Rectangle(20, 20, getPlayerColor(currentPlayer));
        playerDot.setLayoutX(30);
        playerDot.setLayoutY(20);
        playerDot.setStroke(Color.BLACK);
        playerDot.setStrokeWidth(2.0);
        
        // Alte Spieler-Punkte entfernen
        boardPane.getChildren().removeIf(node -> node.getUserData() != null && node.getUserData().equals("playerDot"));
        playerDot.setUserData("playerDot");
        
        // Tooltip für Spieler-Information
        Tooltip playerTooltip = new Tooltip("Farbe von: " + currentPlayer.getName());
        Tooltip.install(playerDot, playerTooltip);
        
        boardPane.getChildren().add(playerDot);
    }
    
    /**
     * Rendert alle 19 Hexagon-Tiles des authentischen CATAN-Boards.
     * Jedes Tile wird mit entsprechendem Terrain-Typ gestylt und erhält Nummern-Token.
     */
    private void renderHexagonTiles() {
        // Über alle Terrain-Tiles iterieren
        for (TerrainTile tile : board.getAllTiles().values()) {
            if (tile.getHexCoordinate() != null) {
                HexCoordinate hexCoord = tile.getHexCoordinate();
                RoundedPoint2D hexCenter = hexCoord.toPixelCatan(AuthenticCatanBoard.getHexRadius());
                
                // Hexagon-Form erstellen
                Polygon hexagon = UIComponents.createHexagon(AuthenticCatanBoard.getHexRadius());
                hexagon.setLayoutX(BOARD_CENTER_X + hexCenter.x);
                hexagon.setLayoutY(BOARD_CENTER_Y + hexCenter.y);
                
                // Terrain-spezifisches Styling anwenden
                styleTerrainTile(hexagon, tile);
                
                // Klick-Handler für Räuber-Bewegung hinzufügen
                final HexCoordinate finalHexCoord = hexCoord;
                hexagon.setOnMouseClicked(e -> handleTileClick(finalHexCoord));
                
                boardPane.getChildren().add(hexagon);
                
                // Räuber-Symbol rendern falls vorhanden
                if (tile.hasRobber()) {
                    ImageView robberImage = new ImageView(new Image(getClass().getResourceAsStream("/images/robber3Small.png")));
                    robberImage.setFitWidth(ROBBER_SIZE);
                    robberImage.setFitHeight(ROBBER_SIZE);
                    robberImage.setLayoutX(BOARD_CENTER_X + hexCenter.x - (ROBBER_SIZE / 2));
                    robberImage.setLayoutY(BOARD_CENTER_Y + hexCenter.y - (ROBBER_SIZE / 2));
                    boardPane.getChildren().add(robberImage);
                }
                
                // Nummern-Token für Nicht-Wüsten-Tiles erstellen
                if (tile.getNumberToken() > 0) {
                    Text numberText = UIComponents.createNumberToken(tile.getNumberToken());
                    numberText.setTextOrigin(VPos.CENTER);
                    numberText.applyCss();
                    
                    // Text-Dimensionen für Zentrierung berechnen
                    double textWidth = numberText.getBoundsInLocal().getWidth();
                    double textHeight = numberText.getBoundsInLocal().getHeight();
                    double centerX = BOARD_CENTER_X + hexCenter.x;
                    double centerY = BOARD_CENTER_Y + hexCenter.y;
                    
                    // Weißen Hintergrundkreis für Nummern-Token erstellen
                    Circle backgroundCircle = new Circle(13,Color.web("#FFFFFF", 0.8));
                    backgroundCircle.setLayoutX(centerX);
                    backgroundCircle.setLayoutY(centerY);
                    backgroundCircle.setStroke(Color.BLACK);
                    backgroundCircle.setStrokeWidth(0.5);
                    
                    // Text präzise zentrieren
                    numberText.setLayoutX(centerX - textWidth / 2);
                    // Vertikale Zentrierung mit Baseline-Offset-Korrektur
                    numberText.setLayoutY(centerY + numberText.getBaselineOffset() - textHeight / 2 - 9);
                    
                    // Hintergrundkreis und Text hinzufügen
                    boardPane.getChildren().add(backgroundCircle);
                    boardPane.getChildren().add(numberText);
                }
            }
        }
    }
    
    /**
     * Rendert alle 54 authentischen Siedlungsplätze des CATAN-Boards.
     * Zeigt existierende Gebäude an und markiert baubare Positionen.
     */
    private void renderSettlementSpots() {
        Player currentPlayer = game.getCurrentPlayer();
        Map<VertexCoordinate, VertexCoordinate> allVertices = board.getValidVertices();
        Set<VertexCoordinate> uniqueVertices = new HashSet<>(allVertices.values()); // Normalisierte Vertices
        
        // Über alle eindeutigen Vertex-Koordinaten iterieren
        for (VertexCoordinate uniqueVertice : uniqueVertices) {
        	VertexCoordinate vertex = uniqueVertice;
            RoundedPoint2D vertexPos = uniqueVertice.toPixel(HEX_RADIUS, BOARD_CENTER_X, BOARD_CENTER_Y);
            
            // Bau-Möglichkeiten prüfen
            boolean canBuildSettlement = game.canPlaceSettlement(vertex, currentPlayer, game.isBeginning());
            boolean canBuildCity = game.canPlaceCity(vertex, currentPlayer, game.isBeginning());
            boolean canBuildAnything = game.canPlaceAnyBuilding(vertex, currentPlayer, game.isBeginning());
            boolean isInitialSettlementPlaced = currentPlayer.isInitialSettlementPlaced() && game.isBeginning();
            boolean isOccupied = board.getBuildings().containsKey(vertex);
            
            // Siedlungsspot-Kreis erstellen
            Circle settlementSpot = new Circle(SETTLEMENT_SIZE);
            settlementSpot.setLayoutX(vertexPos.x);
            settlementSpot.setLayoutY(vertexPos.y);
            
            // Debug-Ausgabe für Bau-Bedingungen
            System.out.println("== Bedingungen für Siedlungsbau ==");
            System.out.println("canBuildSettlement: " + canBuildSettlement);
            System.out.println("!isInitialSettlementPlaced: " + !isInitialSettlementPlaced);
            System.out.println("game.hasRolledDice(): " + game.hasRolledDice());
            System.out.println("isOccupied: " + isOccupied);

            // Styling basierend auf Zustand
            if (isOccupied) {
                // Existierendes Gebäude finden und mit Spielerfarbe anzeigen
                Building building = board.getBuildings().values().stream()
                    .filter(b -> b.getVertexCoordinate() != null && b.getVertexCoordinate().equals(vertex))
                    .findFirst().orElse(null);
                
                if (building != null) {
                    settlementSpot.setFill(getPlayerColor(building.getOwner()));
                    settlementSpot.setStroke(Color.BLACK);
                    settlementSpot.setStrokeWidth(2.0);

                    // Interaktive Effekte für Stadt-Upgrade
                    if (canBuildCity && !isInitialSettlementPlaced && game.hasRolledDice()) {
                    	startPulseEffect(settlementSpot); // Pulsieren für Upgrade-Möglichkeit
                    	settlementSpot.setOnMouseClicked(e -> handleSettlementClick(vertex, settlementSpot));
                    	
                    	// Hover-Effekte für Upgrade
                    	settlementSpot.setOnMouseEntered(e -> {
                            settlementSpot.setScaleX(1.2);
                            settlementSpot.setScaleY(1.2);
                        });
                    	settlementSpot.setOnMouseExited(e -> {
                            settlementSpot.setScaleX(1.0);
                            settlementSpot.setScaleY(1.0);
                        });
                    }
                    
                    // Städte sind größer und dunkler dargestellt
                    if (building.getType() == Building.Type.CITY) {
                        settlementSpot.setRadius(SETTLEMENT_SIZE * 1.5);
                        settlementSpot.setFill(getPlayerColor(building.getOwner()).darker());
                    }
                }
            }
            else if (canBuildSettlement && !isInitialSettlementPlaced && game.hasRolledDice()) {
            	System.out.println("Bebaubare Siedlungsposition wird gerendert");
                // Bebaubare Position - grün mit Interaktivität
                settlementSpot.setFill(Color.LIGHTGREEN);
                settlementSpot.setStroke(Color.DARKGREEN);
                settlementSpot.setStrokeWidth(1.5);
                settlementSpot.setOnMouseClicked(e -> handleVertexClick(vertex));
                
                // Hover-Effekte für baubare Positionen
                settlementSpot.setOnMouseEntered(e -> {
                    settlementSpot.setScaleX(1.2);
                    settlementSpot.setScaleY(1.2);
                });
                settlementSpot.setOnMouseExited(e -> {
                    settlementSpot.setScaleX(1.0);
                    settlementSpot.setScaleY(1.0);
                });
            } 
            else {
                // Nicht bebaubare Position - grau und transparent
                settlementSpot.setFill(Color.LIGHTGRAY);
                settlementSpot.setStroke(Color.GRAY);
                settlementSpot.setStrokeWidth(0.5);
                settlementSpot.setOpacity(0.4);
            }
            
            // Tooltip mit Zustandsinformation
            String tooltipText = isOccupied ? "Gebäude vorhanden" :
                                (canBuildAnything ? "Klicken für Siedlung" : "Nicht bebaubar");
            Tooltip tooltip = new Tooltip(tooltipText);
            Tooltip.install(settlementSpot, tooltip);
            
            // Siedlungsspot zum Board hinzufügen
            boardPane.getChildren().add(settlementSpot);
            settlementSpot.toFront(); // Vor andere Elemente bringen
        }
    }
    
    /**
     * Rendert alle 72 authentischen Straßenpositionen des CATAN-Boards.
     * Zeigt existierende Straßen an und markiert baubare Positionen.
     */
    private void renderRoadSpots(boolean isBeginning) {
        Player currentPlayer = game.getCurrentPlayer();
        Set<EdgeCoordinate> uniqueEdges = board.getValidEdges(); 
        
        // Über alle Edge-Koordinaten iterieren
        for (EdgeCoordinate edge : uniqueEdges) {
            RoundedPoint2D edgePos = edge.toPixel(HEX_RADIUS, BOARD_CENTER_X, BOARD_CENTER_Y);
            double rotation = edge.getRotationAngle(HEX_RADIUS, BOARD_CENTER_X, BOARD_CENTER_Y);
            
            // Straßenbau-Möglichkeiten prüfen
            boolean canBuildRoad = game.canPlaceRoad(edge, currentPlayer);
            boolean isInitialRoadPlaced = currentPlayer.isInitialRoadPlaced() && game.isBeginning();
            boolean isRoadOccupied = board.getRoads().values().stream()
                    .anyMatch(r -> r.getEdgeCoordinate() != null && r.getEdgeCoordinate().equals(edge));
            
            // Straßensegment-Rechteck erstellen
            Rectangle roadSegment = new Rectangle(ROAD_LENGTH, ROAD_WIDTH);
            roadSegment.setX(edgePos.x - ROAD_LENGTH/2);
            roadSegment.setY(edgePos.y - ROAD_WIDTH/2);
            roadSegment.setRotate(rotation); // Rotation für korrekte Ausrichtung
            
            // Styling basierend auf Straßenzustand
            if (isRoadOccupied) {
                // Existierende Straße finden und mit Spielerfarbe anzeigen
                com.catan.model.Road road = board.getRoads().values().stream()
                    .filter(r -> r.getEdgeCoordinate() != null && r.getEdgeCoordinate().equals(edge))
                    .findFirst().orElse(null);
                if (road != null) {
                    roadSegment.setFill(getPlayerColor(road.getOwner()));
                    roadSegment.setStroke(Color.BLACK);
                    roadSegment.setStrokeWidth(2.0);
                }
            } else if (canBuildRoad && !isInitialRoadPlaced && game.hasRolledDice()) {
                // Bebaubare Straße - blau mit Interaktivität
                roadSegment.setFill(Color.LIGHTBLUE);
                roadSegment.setStroke(Color.DARKBLUE);
                roadSegment.setStrokeWidth(1.5);
                roadSegment.setOnMouseClicked(e -> handleEdgeClick(edge));
                
                // Hover-Effekte für baubare Straßen
                roadSegment.setOnMouseEntered(e -> {
                    roadSegment.setScaleX(1.1);
                    roadSegment.setScaleY(1.1);
                });
                roadSegment.setOnMouseExited(e -> {
                    roadSegment.setScaleX(1.0);
                    roadSegment.setScaleY(1.0);
                });
            } else {
                // Nicht bebaubare Straße - grau und transparent
                roadSegment.setFill(Color.LIGHTGRAY);
                roadSegment.setStroke(Color.GRAY);
                roadSegment.setStrokeWidth(0.5);
                roadSegment.setOpacity(0.3);
            }
            
            // Tooltip mit Zustandsinformation
            String tooltipText = isRoadOccupied ? "Straße vorhanden" :
                               (canBuildRoad ? "Klicken für Straße" : "Straße nicht möglich");
            Tooltip roadTooltip = new Tooltip(tooltipText);
            Tooltip.install(roadSegment, roadTooltip);
            
            // Straßensegment zum Board hinzufügen
            boardPane.getChildren().add(roadSegment);
            roadSegment.toFront(); // Vor andere Elemente bringen
        }
    }

    /**
     * Behandelt Klicks auf Hexagon-Tiles für Räuber-Bewegung.
     * Wird aufgerufen wenn ein Spieler auf ein Terrain-Feld klickt.
     */
    private void handleTileClick(HexCoordinate hexCoord) {
        // Räuber-Bewegung nur erlauben wenn noch nicht bewegt
    	if (game.hasMovedRobber() == false) {
            game.moveRobber(hexCoord); // Räuber auf neues Feld bewegen
            renderBoard(); // Board nach Änderung neu rendern
    	}
    }
    
    /**
     * Behandelt Klicks auf Siedlungsplätze für Siedlungsbau.
     * Wird aufgerufen wenn ein Spieler auf einen freien Vertex klickt.
     */
    private void handleVertexClick(VertexCoordinate vertex) {
        Player currentPlayer = game.getCurrentPlayer();
        if (game.canPlaceSettlement(vertex, currentPlayer, game.isBeginning())) {
            // Siedlung platzieren
            game.placeBuilding(Building.Type.SETTLEMENT, vertex, currentPlayer);
            
            // Flags für Anfangsphase setzen
            if (game.isBeginning()) {
            	currentPlayer.setInitialSettlementPlaced(true);
            }
            
            // Spezialfall: Zweite Siedlung des letzten Spielers in Anfangsrunde
            if (game.isBeginning() && board.getBuildings().size() == game.getPlayers().size() + 1 && 
                game.getCurrentPlayerIndex() == game.getPlayers().size() - 1) {
            	currentPlayer.setInitialRoadPlaced(false);
            }
            
            System.out.println("Siedlung platziert für " + currentPlayer.getName() + " bei " + vertex);
            
            // Debug: Angrenzende Vertices anzeigen
            for (VertexCoordinate adjacentVertex : vertex.getAdjacentVertices(HEX_RADIUS, BOARD_CENTER_X, BOARD_CENTER_Y, board.getNormalizedCatanCoordMap(), board.getValidVertices())) {
            	System.out.println("X wert:" + adjacentVertex.getX() + "Y wert:" + adjacentVertex.getY() + "dir wert:" + adjacentVertex.getDirection());
            }
            
            renderBoard(); // Board nach Änderung neu rendern
        }
    }
    
    /**
     * Behandelt Klicks auf existierende Siedlungen für Stadt-Upgrade.
     * Wird aufgerufen wenn ein Spieler auf seine eigene Siedlung klickt.
     */
    private void handleSettlementClick(VertexCoordinate vertex, Circle settlementSpot) {
    	Player currentPlayer = game.getCurrentPlayer();
    	
    	// Debug-Ausgaben für Stadt-Upgrade-Bedingungen
    	System.out.println("In canPlaceCity, boolean ownsBuilding: " + board.getBuildings().entrySet().stream()
    		    .anyMatch(entry ->
		        entry.getKey().equals(vertex) &&
		        entry.getValue().getOwner().equals(game.getCurrentPlayer()) &&
		        entry.getValue().getType() == Building.Type.SETTLEMENT
		        )
		    );
    	System.out.println("In canPlaceCity, boolean !hasSufficientResourcesForCity(): " + !game.hasSufficientResourcesForCity());
    	System.out.println("In canPlaceCity, boolean isBeginning: " + game.isBeginning());
    	System.out.println("in handleSettlementClick vor canPlaceCity");
    	
        if (game.canPlaceCity(vertex, currentPlayer, game.isBeginning())) {
        	System.out.println("in handleSettlementClick nach canPlaceCity");
            // Siedlung zu Stadt upgraden
            game.placeBuilding(Building.Type.CITY, vertex, currentPlayer);
            stopPulseEffect(settlementSpot); // Pulsieren stoppen
            
            // Flags für Anfangsphase setzen
            if (game.isBeginning()) {
            	currentPlayer.setInitialSettlementPlaced(true);
            }
            
            // Spezialfall: Zweite Stadt des letzten Spielers in Anfangsrunde
            if (game.isBeginning() && board.getBuildings().size() == game.getPlayers().size() + 1 && 
                game.getCurrentPlayerIndex() == game.getPlayers().size() - 1) {
            	currentPlayer.setInitialRoadPlaced(false);
            }
            
            System.out.println("Stadt platziert für " + currentPlayer.getName() + " bei " + vertex);
            
            // Debug: Angrenzende Vertices anzeigen
            for (VertexCoordinate adjacentVertex : vertex.getAdjacentVertices(HEX_RADIUS, BOARD_CENTER_X, BOARD_CENTER_Y, board.getNormalizedCatanCoordMap(), board.getValidVertices())) {
            	System.out.println("X wert:" + adjacentVertex.getX() + "Y wert:" + adjacentVertex.getY() + "dir wert:" + adjacentVertex.getDirection());
            }
            
            renderBoard(); // Board nach Änderung neu rendern
        }
    }
    
    /**
     * Stoppt den Pulsiereffekt für einen UI-Knoten.
     * Wird verwendet um Animationen zu beenden.
     */
    private void stopPulseEffect(Node node) {
        if (node.getUserData() instanceof ScaleTransition pulse) {
            pulse.stop(); // Animation stoppen
            node.setScaleX(1.0); // Ursprüngliche Größe wiederherstellen
            node.setScaleY(1.0);
            node.setUserData(null); // UserData zurücksetzen
        }
    }
    
    /**
     * Startet einen Pulsiereffekt für einen Siedlungsspot.
     * Wird verwendet um Upgrade-Möglichkeiten zu signalisieren.
     */
    private void startPulseEffect(Circle settlementSpot) {
    	if (settlementSpot.getUserData() == null) {
            // Neue Pulsier-Animation erstellen
            ScaleTransition pulse = new ScaleTransition(Duration.seconds(0.5), settlementSpot);
            pulse.setFromX(1.0);
            pulse.setFromY(1.0);
            pulse.setToX(1.2);
            pulse.setToY(1.2);
            pulse.setAutoReverse(true); // Hin- und Her-Animation
            pulse.setCycleCount(Animation.INDEFINITE); // Unendlich wiederholen
            pulse.play();

            // Animation-Referenz speichern
            settlementSpot.setUserData(pulse);
        }
    }
    
    /**
     * Zeigt Dialog zur Auswahl des Gebäudetyps an.
     * Aktuell nicht verwendet, aber für zukünftige Erweiterungen vorhanden.
     */
    private Building.Type buildingType(VertexCoordinate vertex) {
    	Building.Type type = null;

        // Auswahl-Dialog erstellen
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Build Menu");
        alert.setHeaderText("Choose what to build here:");

        // Button-Optionen definieren
        ButtonType settlementBtn = new ButtonType("Build Settlement");
        ButtonType cityBtn = new ButtonType("Build City");
        ButtonType cancelBtn = ButtonType.CANCEL;

        alert.getButtonTypes().setAll(settlementBtn, cityBtn, cancelBtn);

        // Dialog anzeigen und Auswahl verarbeiten
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()) {
            if (result.get() == settlementBtn) {
            	type = Building.Type.SETTLEMENT;
                System.out.println("Settlement selected.");
            } else if (result.get() == cityBtn) {
            	type = Building.Type.CITY;
                System.out.println("City selected.");
            } else {
                System.out.println("Cancelled.");
            }
        }
        return type;
    }
    
    /**
     * Behandelt Klicks auf Straßenplätze für Straßenbau.
     * Wird aufgerufen wenn ein Spieler auf eine baubare Edge klickt.
     */
    private void handleEdgeClick(EdgeCoordinate edge) {
        Player currentPlayer = game.getCurrentPlayer();
        
        if (game.canPlaceRoad(edge, currentPlayer)) {
            boolean success = game.placeRoad(edge, currentPlayer);
            if (success) {
                // Flags für Anfangsphase setzen
            	if (game.isBeginning()) {
                	currentPlayer.setInitialRoadPlaced(true);
                }
                
                // Spezialfall: Erste Straße des letzten Spielers in Anfangsrunde
                if (game.isBeginning() && board.getRoads().size() == game.getPlayers().size() && 
                    game.getCurrentPlayerIndex() == game.getPlayers().size() - 1) {
                	currentPlayer.setInitialSettlementPlaced(false);
                }
                
                System.out.println("Straße platziert für " + currentPlayer.getName() + " bei " + edge);
                renderBoard(); // Board nach Änderung neu rendern
            }
        }
    }
    
    /**
     * Stylt ein Terrain-Tile basierend auf seinem Terrain-Typ.
     * Lädt entsprechende Bilder und setzt Hover-Effekte.
     */
    private void styleTerrainTile(Polygon hexagon, TerrainTile tile) {
        // Bild-Pfad basierend auf Terrain-Typ bestimmen
        String imagePath = switch (tile.getTerrainType()) {
            case FOREST -> "/images/forestSmall.jpg";
            case HILLS -> "/images/hillsSmall.jpeg";
            case PASTURE -> "/images/pastureSmall.jpg";
            case FIELDS -> "/images/fieldsSmall.jpg";
            case MOUNTAINS -> "/images/mountainsSmall.jpg";
            case DESERT -> "/images/desertSmall.jpg";
        };
        
        // Bild laden und als Füllung setzen
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        hexagon.setFill(new ImagePattern(image));
        hexagon.setStroke(Color.BLACK);
        hexagon.setStrokeWidth(2.0);
        
        // Hover-Effekte für Terrain-Tiles
        hexagon.setOnMouseEntered(e -> hexagon.setStroke(Color.YELLOW));
        hexagon.setOnMouseExited(e -> hexagon.setStroke(tile.hasRobber() ? Color.RED : Color.BLACK));
    }
    
    /**
     * Konvertiert eine Spieler-Farbe in eine JavaFX-Farbe.
     * Verwendet für die Anzeige von Spielerelementen.
     */
    private Color getPlayerColor(Player player) {
        return switch (player.getColor()) {
            case RED -> Color.RED;
            case BLUE -> Color.BLUE;
            case WHITE -> Color.WHITE;
            case ORANGE -> Color.ORANGE;
        };
    }
}
