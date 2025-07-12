package com.catan.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * Controller für das authentische CATAN-Board mit exakt 54 Siedlungen und 72 Straßen.
 */
public class AuthenticBoardController {
    
    // CATAN-authentische Board-Layout-Konstanten
    private static final double HEX_RADIUS = 45; //davor 55
    private static final double BOARD_CENTER_X = 300.0;   //ursprünglich 400 WENN MAN ÄNDERN WILL CatanApplication WINDOWS_HEIGHT etc
    private static final double BOARD_CENTER_Y = 200.0;   //URSPRüNGLICH 350!!!
    
    // Siedlungs- und Straßen-Konstanten
    private static final double SETTLEMENT_SIZE = 8.0;
    private static final double ROAD_LENGTH = 20.0;
    private static final double ROAD_WIDTH = 4.0;
    
    private final CatanGame game;
    private final AuthenticCatanBoard board;
    private final Pane boardPane;
    
    public AuthenticBoardController(CatanGame game, Pane boardPane) {
    	this.board = game.getBoard();
        this.game = game;
        //this.board = game.getBoard().authenticBoard;
        this.boardPane = boardPane;
        
        if (board == null) {
            throw new IllegalArgumentException("Game muss authentisches CATAN-Board verwenden");
        }
    }
    
    /**
     * Rendert das komplette authentische CATAN-Board.
     */
    public void renderBoard() {
        boardPane.getChildren().clear();
        renderHexagonTiles(game.isBeginning());
        renderSettlementSpots(game.isBeginning());
        renderRoadSpots(game.isBeginning());
        int settlement_count = new HashSet<>(board.getValidVertices().values()).size();
        //board.getValidVertices().size()
        System.out.println("✓ Authentisches CATAN-Board gerendert: " + 
        		settlement_count + " Siedlungsmöglichkeiten, " + 
                         board.getValidVertices().size() + " Siedlungsoptionen");
        				
    }
    
    /**
     * Rendert die 19 Hexagon-Tiles.
     */
    private void renderHexagonTiles(boolean isBeginning) {
        for (TerrainTile tile : board.getAllTiles().values()) {
            if (tile.getHexCoordinate() != null) {
                HexCoordinate hexCoord = tile.getHexCoordinate();
                RoundedPoint2D hexCenter = hexCoord.toPixelCatan(AuthenticCatanBoard.getHexRadius());
                
                // Erstelle authentisches Hexagon
                Polygon hexagon = UIComponents.createHexagon(AuthenticCatanBoard.getHexRadius());
                hexagon.setLayoutX(BOARD_CENTER_X + hexCenter.x);
                hexagon.setLayoutY(BOARD_CENTER_Y + hexCenter.y);
                
                // Style basierend auf Terrain-Typ
                styleTerrainTile(hexagon, tile);
                
                // Klick-Handler für Räuber-Bewegung
                final HexCoordinate finalHexCoord = hexCoord;
                hexagon.setOnMouseClicked(e -> handleTileClick(finalHexCoord));
                
                boardPane.getChildren().add(hexagon);
                
                // Nummern-Token für Nicht-Wüsten-Tiles
                if (tile.getNumberToken() > 0) {
                    Text numberText = UIComponents.createNumberToken(tile.getNumberToken());
                    numberText.setLayoutX(BOARD_CENTER_X + hexCenter.x);
                    numberText.setLayoutY(BOARD_CENTER_Y + hexCenter.y);
                    boardPane.getChildren().add(numberText);
                }
            }
        }
    }
    
    /**
     * Rendert die 54 authentischen Siedlungsplätze.
     */
    private void renderSettlementSpots(boolean isBeginning) {
        Player currentPlayer = game.getCurrentPlayer();
        Map<VertexCoordinate, VertexCoordinate> allVertices = board.getValidVertices();
        Set<VertexCoordinate> uniqueVertices = new HashSet<>(allVertices.values()); //DIE SIND JETZT NORMALIZED
        for (VertexCoordinate uniqueVertice : uniqueVertices) {
            //System.out.println(uniqueVertice); nur überprüfung
        	VertexCoordinate vertex = uniqueVertice;
            RoundedPoint2D vertexPos = uniqueVertice.toPixel(HEX_RADIUS, BOARD_CENTER_X, BOARD_CENTER_Y);
            boolean canBuildSettlement = game.canPlaceSettlement(vertex, currentPlayer, isBeginning);
            boolean canBuildCity = game.canPlaceCity(vertex, currentPlayer, isBeginning);
            boolean canBuildAnything = game.canPlaceAnyBuilding(vertex, currentPlayer, isBeginning); //ob überhaupt eins gesetzt werden kann
            boolean isInitialSettlementPlaced = currentPlayer.isInitialSettlementPlaced() && game.isBeginning();
            boolean isOccupied = board.getBuildings().containsKey(vertex);
            
            // Erstelle Siedlungsspot
            Circle settlementSpot = new Circle(SETTLEMENT_SIZE);
            settlementSpot.setLayoutX(vertexPos.x);
            settlementSpot.setLayoutY(vertexPos.y);
            
            System.out.println("canBuildSettlement: " + canBuildSettlement);
            System.out.println("isInitialSettlementPlaced: " + isInitialSettlementPlaced);
            System.out.println("game.hasRolledDice(): " + game.hasRolledDice());
            
            // Style den Siedlungsspot basierend auf Zustand
            if (isOccupied) {
                // Finde das Gebäude und zeige es mit Spielerfarbe
                Building building = board.getBuildings().values().stream()
                    .filter(b -> b.getVertexCoordinate() != null && b.getVertexCoordinate().equals(vertex))
                    .findFirst().orElse(null);
                
                if (building != null) {
                    settlementSpot.setFill(getPlayerColor(building.getOwner()));
                    settlementSpot.setStroke(Color.BLACK);
                    settlementSpot.setStrokeWidth(2.0);
                    settlementSpot.setOnMouseClicked(e -> handleSettlementClick(vertex));

                    // Städte sind größer
                    if (building.getType() == Building.Type.CITY) {
                        settlementSpot.setRadius(SETTLEMENT_SIZE * 1.5);
                    }
                }
            }
            else if (canBuildSettlement && !isInitialSettlementPlaced && game.hasRolledDice()) {
                // Bebaubare Position - grün
                settlementSpot.setFill(Color.LIGHTGREEN);
                settlementSpot.setStroke(Color.DARKGREEN);
                settlementSpot.setStrokeWidth(1.5);
                settlementSpot.setOnMouseClicked(e -> handleVertexClick(vertex));
                
                // Hover-Effekte
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
                // Nicht bebaubare Position - grau für Referenz
                settlementSpot.setFill(Color.LIGHTGRAY);
                settlementSpot.setStroke(Color.GRAY);
                settlementSpot.setStrokeWidth(0.5);
                settlementSpot.setOpacity(0.4);
            }
            
            // Tooltip
            String tooltipText = isOccupied ? "Gebäude vorhanden" : 
                                (canBuildAnything ? "Klicken für Siedlung" : "Nicht bebaubar");
            Tooltip tooltip = new Tooltip(tooltipText);
            Tooltip.install(settlementSpot, tooltip);
            
            boardPane.getChildren().add(settlementSpot);
            settlementSpot.toFront();
        }
    }
    
    /**
     * Rendert die 72 authentischen Straßenpositionen.
     */
    private void renderRoadSpots(boolean isBeginning) {
        Player currentPlayer = game.getCurrentPlayer();
        Set<EdgeCoordinate> uniqueEdges = board.getValidEdges(); 
        for (EdgeCoordinate edge : uniqueEdges) {
            RoundedPoint2D edgePos = edge.toPixel(HEX_RADIUS, BOARD_CENTER_X, BOARD_CENTER_Y);
            double rotation = edge.getRotationAngle(HEX_RADIUS, BOARD_CENTER_X, BOARD_CENTER_Y);
            
            boolean canBuildRoad = game.canPlaceRoad(edge, currentPlayer);
            boolean isInitialRoadPlaced = currentPlayer.isInitialRoadPlaced() && game.isBeginning();
            boolean isRoadOccupied = board.getRoads().values().stream()
                    .anyMatch(r -> r.getEdgeCoordinate() != null && r.getEdgeCoordinate().equals(edge));
            
            // Erstelle Straßensegment
            Rectangle roadSegment = new Rectangle(ROAD_LENGTH, ROAD_WIDTH);
            roadSegment.setX(edgePos.x - ROAD_LENGTH/2);
            roadSegment.setY(edgePos.y - ROAD_WIDTH/2);
            roadSegment.setRotate(rotation);
            
            // Style das Straßensegment
            if (isRoadOccupied) {
                // Finde die Straße und zeige sie mit Spielerfarbe
                com.catan.model.Road road = board.getRoads().values().stream()
                    .filter(r -> r.getEdgeCoordinate() != null && r.getEdgeCoordinate().equals(edge))
                    .findFirst().orElse(null);
                
                System.out.println("Build Road Conditions:");
                System.out.println("game.hasRolledDice(): " + game.hasRolledDice());
                System.out.println("game.hasSufficientResourcesForRoad(): " + game.hasSufficientResourcesForRoad());

                boolean result = canBuildRoad && !isInitialRoadPlaced && game.hasRolledDice() && (game.hasSufficientResourcesForRoad() || isBeginning);
                System.out.println("Overall result: " + result);
                
                if (road != null) {
                    roadSegment.setFill(getPlayerColor(road.getOwner()));
                    roadSegment.setStroke(Color.BLACK);
                    roadSegment.setStrokeWidth(2.0);
                }
            } else if (canBuildRoad && !isInitialRoadPlaced && game.hasRolledDice()) {
                // Bebaubare Straße - blau
                roadSegment.setFill(Color.LIGHTBLUE);
                roadSegment.setStroke(Color.DARKBLUE);
                roadSegment.setStrokeWidth(1.5);
                roadSegment.setOnMouseClicked(e -> handleEdgeClick(edge));
                
                // Hover-Effekte
                roadSegment.setOnMouseEntered(e -> {
                    roadSegment.setScaleX(1.1);
                    roadSegment.setScaleY(1.1);
                });
                roadSegment.setOnMouseExited(e -> {
                    roadSegment.setScaleX(1.0);
                    roadSegment.setScaleY(1.0);
                });
            } else {
                // Nicht bebaubare Straße - grau für Referenz
                roadSegment.setFill(Color.LIGHTGRAY);
                roadSegment.setStroke(Color.GRAY);
                roadSegment.setStrokeWidth(0.5);
                roadSegment.setOpacity(0.3);
            }
            
            // Tooltip
            String tooltipText = isRoadOccupied ? "Straße vorhanden" : 
                               (canBuildRoad ? "Klicken für Straße" : "Straße nicht möglich");
            Tooltip roadTooltip = new Tooltip(tooltipText);
            Tooltip.install(roadSegment, roadTooltip);
            
            boardPane.getChildren().add(roadSegment);
            roadSegment.toFront();
        }
    }
    
    /**
     * Behandelt Klicks auf Hexagon-Tiles (Räuber-Bewegung).
     */
    private void handleTileClick(HexCoordinate hexCoord) {
        // Implementiere Räuber-Bewegung
        game.moveRobber(hexCoord);
        renderBoard(); // Re-render nach Änderung
    }
    
    /**
     * Behandelt Klicks auf Siedlungsplätze.
     */
    private void handleVertexClick(VertexCoordinate vertex) {
        Player currentPlayer = game.getCurrentPlayer();
        if (game.canPlaceSettlement(vertex, currentPlayer, game.isBeginning())) {
            game.placeBuilding(Building.Type.SETTLEMENT, vertex, currentPlayer);
            //DIE ZWEI IFS SIND PUR FÜR DIE STARTPHASE!!!
            if (game.isBeginning()) {
            	currentPlayer.setInitialSettlementPlaced(true);
            }
            //prüft ob es sich um zweites Plazieren beim letzten Spieler der Anfangsrunde handelt
            if (game.isBeginning() && board.getBuildings().size() == game.getPlayers().size() + 1 && game.getCurrentPlayerIndex() == game.getPlayers().size() - 1) {
            	currentPlayer.setInitialRoadPlaced(false);
            }
            System.out.println("Siedlung platziert für " + currentPlayer.getName() + " bei " + vertex);
            for (VertexCoordinate adjacentVertex : vertex.getAdjacentVertices(HEX_RADIUS, BOARD_CENTER_X, BOARD_CENTER_Y, board.getNormalizedCatanCoordMap(), board.getValidVertices())) {
            	System.out.println("X wert:" + adjacentVertex.getX() + "Y wert:" + adjacentVertex.getY() + "dir wert:" + adjacentVertex.getDirection());
            }
            renderBoard(); // Re-render nach Änderung
            
        }
    }
    private void handleSettlementClick(VertexCoordinate vertex) {
    	
    }
    
    //wird aktuell nicht benutzt, aber mal drin gelassen falls ich es brauche
    private Building.Type buildingType(VertexCoordinate vertex) {
    	Building.Type type = null;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Build Menu");
        alert.setHeaderText("Choose what to build here:");

        ButtonType settlementBtn = new ButtonType("Build Settlement");
        ButtonType cityBtn = new ButtonType("Build City");
        ButtonType cancelBtn = ButtonType.CANCEL;

        alert.getButtonTypes().setAll(settlementBtn, cityBtn, cancelBtn);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent()) {
            if (result.get() == settlementBtn) {
            	type = Building.Type.SETTLEMENT;
                System.out.println("Settlement selected.");
                // game.placeSettlement(vertex, currentPlayer);
            } else if (result.get() == cityBtn) {
            	type = Building.Type.CITY;
                System.out.println("City selected.");
                // game.placeCity(vertex, currentPlayer);
            } else {
                System.out.println("Cancelled.");
            }
            
        }
        return type;
    }
    
    /**
     * Behandelt Klicks auf Straßenplätze.
     */
    private void handleEdgeClick(EdgeCoordinate edge) {
        Player currentPlayer = game.getCurrentPlayer();
        
        if (game.canPlaceRoad(edge, currentPlayer)) {
            boolean success = game.placeRoad(edge, currentPlayer);
            if (success) {
            	if (game.isBeginning()) {
                	currentPlayer.setInitialRoadPlaced(true);
                }
                if (game.isBeginning() && board.getRoads().size() == game.getPlayers().size() && game.getCurrentPlayerIndex() == game.getPlayers().size() - 1) {
                	currentPlayer.setInitialSettlementPlaced(false);
                }
                System.out.println("Straße platziert für " + currentPlayer.getName() + " bei " + edge);
                renderBoard(); // Re-render nach Änderung
            }
        }
    }
    
    /**
     * Stylt ein Terrain-Tile basierend auf seinem Typ.
     */
    private void styleTerrainTile(Polygon hexagon, TerrainTile tile) {
        Color fillColor = switch (tile.getTerrainType()) {
            case FOREST -> UIComponents.FOREST_COLOR;
            case HILLS -> UIComponents.HILLS_COLOR;
            case PASTURE -> UIComponents.PASTURE_COLOR;
            case FIELDS -> UIComponents.FIELDS_COLOR;
            case MOUNTAINS -> UIComponents.MOUNTAINS_COLOR;
            case DESERT -> UIComponents.DESERT_COLOR;
        };
        
        hexagon.setFill(fillColor);
        hexagon.setStroke(Color.BLACK);
        hexagon.setStrokeWidth(2.0);
        
        // Räuber-Anzeige
        if (tile.hasRobber()) {
            hexagon.setStroke(Color.RED);
            hexagon.setStrokeWidth(4.0);
        }
        
        // Hover-Effekte
        hexagon.setOnMouseEntered(e -> hexagon.setStroke(Color.YELLOW));
        hexagon.setOnMouseExited(e -> hexagon.setStroke(tile.hasRobber() ? Color.RED : Color.BLACK));
    }
    
    /**
     * Gibt die JavaFX-Farbe für einen Spieler zurück.
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
