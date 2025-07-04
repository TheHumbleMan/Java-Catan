package com.catan.controller;

import java.util.Set;

import com.catan.model.AuthenticCatanBoard;
import com.catan.model.Building;
import com.catan.model.CatanGame;
import com.catan.model.EdgeCoordinate;
import com.catan.model.HexCoordinate;
import com.catan.model.Player;
import com.catan.model.TerrainTile;
import com.catan.model.VertexCoordinate;
import com.catan.view.UIComponents;

import javafx.scene.control.Tooltip;
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
    private static final double HEX_RADIUS = 55.0;
    private static final double HEX_SPACING = 95.0; // Optimaler Abstand für authentisches Layout
    private static final double BOARD_CENTER_X = 580.0;   //ursprünglich 400 WENN MAN ÄNDERN WILL CatanApplication WINDOWS_HEIGHT etc
    private static final double BOARD_CENTER_Y = 400.0;   //URSPRüNGLICH 350!!!
    
    // Siedlungs- und Straßen-Konstanten
    private static final double SETTLEMENT_SIZE = 8.0;
    private static final double ROAD_LENGTH = 40.0;
    private static final double ROAD_WIDTH = 6.0;
    
    private final CatanGame game;
    private final AuthenticCatanBoard board;
    private final Pane boardPane;
    
    public AuthenticBoardController(CatanGame game, Pane boardPane) {
        this.game = game;
        this.board = game.getBoard().authenticBoard;
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
        
        renderHexagonTiles();
        renderSettlementSpots();
        renderRoadSpots();
        
        System.out.println("✓ Authentisches CATAN-Board gerendert: " + 
                         board.getValidVertices().size() + " Siedlungen, " + 
                         board.getValidEdges().size() + " Straßen");
    }
    
    /**
     * Rendert die 19 Hexagon-Tiles.
     */
    private void renderHexagonTiles() {
        for (TerrainTile tile : board.getAllTiles()) {
            if (tile.getHexCoordinate() != null) {
                HexCoordinate hexCoord = tile.getHexCoordinate();
                HexCoordinate.Point2D hexCenter = hexCoord.toPixelCatan(HEX_SPACING);
                
                // Erstelle authentisches Hexagon
                Polygon hexagon = UIComponents.createHexagon(HEX_RADIUS);
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
    private void renderSettlementSpots() {
        Player currentPlayer = game.getCurrentPlayer();
        Set<VertexCoordinate> allVertices = board.getValidVertices();
        
        for (VertexCoordinate vertex : allVertices) {
            HexCoordinate.Point2D vertexPos = vertex.toPixel(HEX_RADIUS, BOARD_CENTER_X, BOARD_CENTER_Y);
            
            boolean canBuild = board.canPlaceBuilding(vertex, currentPlayer);
            boolean isOccupied = board.getBuildings().stream()
                    .anyMatch(b -> b.getVertexCoordinate() != null && b.getVertexCoordinate().equals(vertex));
            
            // Erstelle Siedlungsspot
            Circle settlementSpot = new Circle(SETTLEMENT_SIZE);
            settlementSpot.setLayoutX(vertexPos.x);
            settlementSpot.setLayoutY(vertexPos.y);
            
            // Style den Siedlungsspot basierend auf Zustand
            if (isOccupied) {
                // Finde das Gebäude und zeige es mit Spielerfarbe
                Building building = board.getBuildings().stream()
                    .filter(b -> b.getVertexCoordinate() != null && b.getVertexCoordinate().equals(vertex))
                    .findFirst().orElse(null);
                
                if (building != null) {
                    settlementSpot.setFill(getPlayerColor(building.getOwner()));
                    settlementSpot.setStroke(Color.BLACK);
                    settlementSpot.setStrokeWidth(2.0);
                    
                    // Städte sind größer
                    if (building.getType() == Building.Type.CITY) {
                        settlementSpot.setRadius(SETTLEMENT_SIZE * 1.5);
                    }
                }
            } else if (canBuild) {
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
            } else {
                // Nicht bebaubare Position - grau für Referenz
                settlementSpot.setFill(Color.LIGHTGRAY);
                settlementSpot.setStroke(Color.GRAY);
                settlementSpot.setStrokeWidth(0.5);
                settlementSpot.setOpacity(0.4);
            }
            
            // Tooltip
            String tooltipText = isOccupied ? "Gebäude vorhanden" : 
                                (canBuild ? "Klicken für Siedlung" : "Nicht bebaubar");
            Tooltip tooltip = new Tooltip(tooltipText);
            Tooltip.install(settlementSpot, tooltip);
            
            boardPane.getChildren().add(settlementSpot);
            settlementSpot.toFront();
        }
    }
    
    /**
     * Rendert die 72 authentischen Straßenpositionen.
     */
    private void renderRoadSpots() {
        Player currentPlayer = game.getCurrentPlayer();
        Set<EdgeCoordinate> allEdges = board.getValidEdges();
        
        for (EdgeCoordinate edge : allEdges) {
            HexCoordinate.Point2D edgePos = edge.toPixel(HEX_RADIUS, BOARD_CENTER_X, BOARD_CENTER_Y);
            double rotation = edge.getRotationAngle(HEX_RADIUS, BOARD_CENTER_X, BOARD_CENTER_Y);
            
            boolean canBuildRoad = board.canPlaceRoad(edge, currentPlayer);
            boolean isRoadOccupied = board.getRoads().stream()
                    .anyMatch(r -> r.getEdgeCoordinate() != null && r.getEdgeCoordinate().equals(edge));
            
            // Erstelle Straßensegment
            Rectangle roadSegment = new Rectangle(ROAD_LENGTH, ROAD_WIDTH);
            roadSegment.setX(edgePos.x - ROAD_LENGTH/2);
            roadSegment.setY(edgePos.y - ROAD_WIDTH/2);
            roadSegment.setRotate(rotation);
            
            // Style das Straßensegment
            if (isRoadOccupied) {
                // Finde die Straße und zeige sie mit Spielerfarbe
                com.catan.model.Road road = board.getRoads().stream()
                    .filter(r -> r.getEdgeCoordinate() != null && r.getEdgeCoordinate().equals(edge))
                    .findFirst().orElse(null);
                if (road != null) {
                    roadSegment.setFill(getPlayerColor(road.getOwner()));
                    roadSegment.setStroke(Color.BLACK);
                    roadSegment.setStrokeWidth(2.0);
                }
            } else if (canBuildRoad) {
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
        board.moveRobber(hexCoord);
        renderBoard(); // Re-render nach Änderung
    }
    
    /**
     * Behandelt Klicks auf Siedlungsplätze.
     */
    private void handleVertexClick(VertexCoordinate vertex) {
        Player currentPlayer = game.getCurrentPlayer();
        
        if (board.canPlaceBuilding(vertex, currentPlayer)) {
            boolean success = board.placeBuilding(Building.Type.SETTLEMENT, vertex, currentPlayer);
            if (success) {
                System.out.println("Siedlung platziert für " + currentPlayer.getName() + " bei " + vertex);
                renderBoard(); // Re-render nach Änderung
            }
        }
    }
    
    /**
     * Behandelt Klicks auf Straßenplätze.
     */
    private void handleEdgeClick(EdgeCoordinate edge) {
        Player currentPlayer = game.getCurrentPlayer();
        
        if (board.canPlaceRoad(edge, currentPlayer)) {
            boolean success = board.placeRoad(edge, currentPlayer);
            if (success) {
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
