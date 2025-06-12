package com.catan.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.catan.model.Building;
import com.catan.model.CatanGame;
import com.catan.model.EdgeCoordinate;
import com.catan.model.EnhancedHexGameBoard;
import com.catan.model.GameBoard;
import com.catan.model.HexCoordinate;
import com.catan.model.Player;
import com.catan.model.ResourceType;
import com.catan.model.TerrainTile;
import com.catan.model.VertexCoordinate;
import com.catan.view.UIComponents;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * Main controller for the CATAN game JavaFX application.
 * Handles user interactions and updates the UI based on game state.
 */
public class MainController {
    
    @FXML private Pane boardGrid;
    @FXML private VBox playerInfoPanel;
    @FXML private Label currentPlayerLabel;
    @FXML private Label gamePhaseLabel;
    @FXML private Label diceRollLabel;
    @FXML private Button rollDiceButton;
    @FXML private Button endTurnButton;
    @FXML private VBox resourcePanel;
    @FXML private TextArea gameLogArea;
    
    private CatanGame game;
    private boolean gameStarted = false;
    
    @FXML
    private void initialize() {
        setupNewGame();
        updateUI();
    }
    
    private void setupNewGame() {
        // For demo purposes, create a game with 4 players using enhanced hexagonal board
        List<String> playerNames = Arrays.asList("Spieler 1", "Spieler 2", "Spieler 3", "Spieler 4");
        game = new CatanGame(playerNames, true, true); // Use enhanced hexagonal board
        gameStarted = true;
        
        setupBoard();
        logMessage("Neues Spiel gestartet mit " + playerNames.size() + " Spielern.");
        logMessage("Anfangsphase: Jeder Spieler platziert 2 Siedlungen und 2 Straßen.");
    }
    
    private void setupBoard() {
        boardGrid.getChildren().clear();
        
        // Check if we're using hexagonal board
        if (game.getBoard().isHexagonal()) {
            setupHexagonalBoard();
        } else {
            setupSquareBoard();
        }
    }
    
    private void setupHexagonalBoard() {
        // Clear the board completely
        boardGrid.getChildren().clear();
        
        // Check if we should use the enhanced board system
        boolean useEnhanced = true; // Enable the new vertex/edge system
        
        if (useEnhanced) {
            setupEnhancedHexagonalBoard();
        } else {
            setupLegacyHexagonalBoard();
        }
    }
    
    private void setupEnhancedHexagonalBoard() {
        // Clear the board first
        boardGrid.getChildren().clear();
        
        // Use the enhanced board from the game instead of creating a new one
        if (!game.getBoard().isEnhanced()) {
            logMessage("Warnung: Enhanced Board nicht verfügbar, verwende Legacy-System");
            setupLegacyHexagonalBoard();
            return;
        }
        
        EnhancedHexGameBoard enhancedBoard = game.getBoard().getEnhancedHexBoard();
        
        // CATAN-authentic board layout constants - ultra-kompakte Anordnung
        final double HEX_RADIUS = 55.0;
        final double HEX_SPACING = 64.0; // Ultra-kompakt: gleich wie Legacy-System
        final double BOARD_CENTER_X = 400.0;
        final double BOARD_CENTER_Y = 350.0;
        
        // Settlement/road positioning constants
        final double SETTLEMENT_SIZE = 8.0;
        final double ROAD_LENGTH = 35.0; // Increased length for better visibility
        final double ROAD_WIDTH = 8.0; // Increased width for better visibility
        
        // First: Render all hexagon terrain tiles
        for (TerrainTile tile : enhancedBoard.getAllTiles()) {
            if (tile.getHexCoordinate() != null) {
                HexCoordinate hexCoord = tile.getHexCoordinate();
                HexCoordinate.Point2D hexCenter = hexCoord.toPixelCatan(HEX_SPACING); // Use CATAN positioning
                
                // Create authentic-looking hexagon
                Polygon hexagon = UIComponents.createHexagon(HEX_RADIUS);
                hexagon.setLayoutX(BOARD_CENTER_X + hexCenter.x);
                hexagon.setLayoutY(BOARD_CENTER_Y + hexCenter.y);
                
                // Style based on terrain type
                UIComponents.styleTerrainTile(hexagon, tile);
                
                // Add click handler for robber movement
                final HexCoordinate finalHexCoord = hexCoord;
                hexagon.setOnMouseClicked(e -> handleTileClick(finalHexCoord.getQ(), finalHexCoord.getR()));
                
                boardGrid.getChildren().add(hexagon);
                
                // Add number token for non-desert tiles
                if (tile.getNumberToken() > 0) {
                    Text numberText = UIComponents.createNumberToken(tile.getNumberToken());
                    numberText.setLayoutX(BOARD_CENTER_X + hexCenter.x);
                    numberText.setLayoutY(BOARD_CENTER_Y + hexCenter.y);
                    boardGrid.getChildren().add(numberText);
                }
            }
        }
        
        // Second: Show only authentic CATAN settlement positions (54 total)
        Player currentPlayer = game.getCurrentPlayer();
        Set<VertexCoordinate> relevantVertices = getRelevantVerticesForCurrentState(enhancedBoard, currentPlayer);
        
        System.out.println("DEBUG: Rendering " + relevantVertices.size() + " settlement vertices");
        
        for (VertexCoordinate vertex : relevantVertices) {
            // Calculate proper vertex position using corrected math
            HexCoordinate.Point2D vertexPos = calculateFixedVertexPosition(vertex, HEX_RADIUS, HEX_SPACING, BOARD_CENTER_X, BOARD_CENTER_Y);
            
            boolean canBuild = enhancedBoard.canPlaceBuilding(vertex, currentPlayer);
            boolean isOccupied = enhancedBoard.getBuildings().stream()
                    .anyMatch(b -> b.getVertexCoordinate() != null && b.getVertexCoordinate().equals(vertex));
            
            // Create settlement spot
            javafx.scene.shape.Circle settlementSpot = new javafx.scene.shape.Circle(SETTLEMENT_SIZE);
            settlementSpot.setLayoutX(vertexPos.x);
            settlementSpot.setLayoutY(vertexPos.y);
            
            // Style the settlement spot based on state
            if (isOccupied) {
                // Find the building and show it with player color
                Building building = enhancedBoard.getBuildings().stream()
                    .filter(b -> b.getVertexCoordinate() != null && b.getVertexCoordinate().equals(vertex))
                    .findFirst().orElse(null);
                
                if (building != null) {
                    settlementSpot.setFill(getPlayerColor(building.getOwner()));
                    settlementSpot.setStroke(Color.BLACK);
                    settlementSpot.setStrokeWidth(2.0);
                    
                    // Make cities larger
                    if (building.getType() == Building.Type.CITY) {
                        settlementSpot.setRadius(SETTLEMENT_SIZE * 1.5);
                    }
                }
            } else if (canBuild) {
                // Buildable position - green
                settlementSpot.setFill(Color.LIGHTGREEN);
                settlementSpot.setStroke(Color.DARKGREEN);
                settlementSpot.setStrokeWidth(1.5);
                settlementSpot.setOnMouseClicked(e -> handleEnhancedVertexClick(vertex, settlementSpot, enhancedBoard));
                
                // Add hover effects
                settlementSpot.setOnMouseEntered(e -> {
                    settlementSpot.setScaleX(1.2);
                    settlementSpot.setScaleY(1.2);
                });
                settlementSpot.setOnMouseExited(e -> {
                    settlementSpot.setScaleX(1.0);
                    settlementSpot.setScaleY(1.0);
                });
            } else {
                // Non-buildable position - show as light gray for reference
                settlementSpot.setFill(Color.LIGHTGRAY);
                settlementSpot.setStroke(Color.GRAY);
                settlementSpot.setStrokeWidth(0.5);
                settlementSpot.setOpacity(0.4);
            }
            
            // Add tooltip
            String tooltipText = isOccupied ? "Gebäude vorhanden" : 
                                (canBuild ? "Klicken für Siedlung" : "Nicht bebaubar");
            javafx.scene.control.Tooltip tooltip = new javafx.scene.control.Tooltip(tooltipText);
            javafx.scene.control.Tooltip.install(settlementSpot, tooltip);
            
            boardGrid.getChildren().add(settlementSpot);
            // Bring settlements to front
            settlementSpot.toFront();
        }
        
        // Third: Show only authentic CATAN road positions (72 total)
        Set<EdgeCoordinate> relevantEdges = getRelevantEdgesForCurrentState(enhancedBoard, currentPlayer);
        System.out.println("DEBUG: Rendering " + relevantEdges.size() + " road edges");
        
        for (EdgeCoordinate edge : relevantEdges) {
            // Calculate proper edge position using corrected math
            HexCoordinate.Point2D edgePos = calculateFixedEdgePosition(edge, HEX_RADIUS, HEX_SPACING, BOARD_CENTER_X, BOARD_CENTER_Y);
            double rotation = calculateFixedEdgeRotation(edge);
            
            boolean canBuildRoad = enhancedBoard.canPlaceRoad(edge, currentPlayer);
            boolean isRoadOccupied = enhancedBoard.getRoads().stream()
                    .anyMatch(r -> r.getEdgeCoordinate() != null && r.getEdgeCoordinate().equals(edge));
            
            // Create road segment with increased visibility
            Rectangle roadSegment = new Rectangle(ROAD_LENGTH, ROAD_WIDTH);
            roadSegment.setX(edgePos.x - ROAD_LENGTH/2);
            roadSegment.setY(edgePos.y - ROAD_WIDTH/2);
            roadSegment.setRotate(rotation);
            
            // Make all roads more visible by adding an outline effect
            roadSegment.setEffect(new javafx.scene.effect.DropShadow(
                javafx.scene.effect.BlurType.THREE_PASS_BOX, Color.BLACK, 2, 0.0, 1, 1));
            
            // Color and style the road segment
            if (isRoadOccupied) {
                // Find the road and show it with player color
                com.catan.model.Road road = enhancedBoard.getRoads().stream()
                    .filter(r -> r.getEdgeCoordinate() != null && r.getEdgeCoordinate().equals(edge))
                    .findFirst().orElse(null);
                if (road != null) {
                    roadSegment.setFill(getPlayerColor(road.getOwner()));
                    roadSegment.setStroke(Color.BLACK);
                    roadSegment.setStrokeWidth(2.0); // Thicker stroke for existing roads
                }
            } else if (canBuildRoad) {
                // Buildable road - blue
                roadSegment.setFill(Color.LIGHTBLUE);
                roadSegment.setStroke(Color.DARKBLUE);
                roadSegment.setStrokeWidth(2.0); // Thicker stroke for better visibility
                roadSegment.setOnMouseClicked(e -> handleEnhancedEdgeClick(edge, roadSegment, enhancedBoard));
                
                // Add hover effects for buildable roads
                roadSegment.setOnMouseEntered(e -> {
                    roadSegment.setScaleX(1.1);
                    roadSegment.setScaleY(1.1);
                });
                roadSegment.setOnMouseExited(e -> {
                    roadSegment.setScaleX(1.0);
                    roadSegment.setScaleY(1.0);
                });
            } else {
                // Non-buildable roads - show in dim gray for reference
                roadSegment.setFill(Color.LIGHTGRAY);
                roadSegment.setStroke(Color.GRAY);
                roadSegment.setStrokeWidth(1.0);
                roadSegment.setOpacity(0.3);
            }
            
            // Add tooltip
            String tooltipText = isRoadOccupied ? "Straße vorhanden" : 
                               (canBuildRoad ? "Klicken für Straße" : "Straße nicht möglich");
            javafx.scene.control.Tooltip roadTooltip = new javafx.scene.control.Tooltip(tooltipText);
            javafx.scene.control.Tooltip.install(roadSegment, roadTooltip);
            
            boardGrid.getChildren().add(roadSegment);
            // Bring roads to front to ensure they're visible above tiles
            roadSegment.toFront();
        }
    }
    
    private void setupLegacyHexagonalBoard() {
        // Get hexagonal tiles from the board
        List<TerrainTile> hexTiles = new ArrayList<>(game.getBoard().getHexBoard().getAllTiles());
        
        // Ultra-compact CATAN board constants - maximum closeness while maintaining functionality
        final double TILE_RADIUS = 45.0;     // Keep hexagon tile size for visibility
        final double VERTEX_RADIUS = 44.0;   // Very close settlement spots for ultra-compact layout
        final double HEX_SPACING = 64.0;     // Extremely close hex centers for tightest board
        final double BOARD_CENTER_X = 400.0;
        final double BOARD_CENTER_Y = 350.0;
        
        // First pass: Create building spots and road spots to establish the grid
        createOptimizedBuildingSpots(hexTiles, VERTEX_RADIUS, HEX_SPACING, BOARD_CENTER_X, BOARD_CENTER_Y);
        createOptimizedRoadSpots(hexTiles, VERTEX_RADIUS, HEX_SPACING, BOARD_CENTER_X, BOARD_CENTER_Y);
        
        // Second pass: Place hexagon tiles centered within the grid of building spots
        for (TerrainTile tile : hexTiles) {
            if (tile.getHexCoordinate() != null) {
                HexCoordinate hexCoord = tile.getHexCoordinate();
                
                // Calculate tile center position - tiles should be centered within the building spot grid
                HexCoordinate.Point2D pixelPos = hexCoord.toPixelCatan(HEX_SPACING);
                
                // Create enhanced hexagonal tile with smaller radius
                javafx.scene.Group enhancedTile = UIComponents.createEnhancedHexagonalTile(tile, TILE_RADIUS);
                
                // Position the tile at the center of the hex grid area
                enhancedTile.setLayoutX(BOARD_CENTER_X + pixelPos.x);
                enhancedTile.setLayoutY(BOARD_CENTER_Y + pixelPos.y);
                
                boardGrid.getChildren().add(enhancedTile);
                
                // Add click handler for hex tile interactions
                final HexCoordinate tileCoord = hexCoord;
                enhancedTile.setOnMouseClicked(e -> handleHexTileClick(tileCoord));
            }
        }
    }
    
    private void createOptimizedBuildingSpots(List<TerrainTile> hexTiles, double hexRadius, 
                                              double hexSpacing, double centerX, double centerY) {
        Player currentPlayer = game.getCurrentPlayer();
        Set<String> processedVertices = new HashSet<>();
        
        // In CATAN, building spots are at the intersection of hexagons
        // Use improved duplicate detection with higher precision
        for (TerrainTile tile : hexTiles) {
            if (tile.getHexCoordinate() == null) continue;
            
            HexCoordinate hexCoord = tile.getHexCoordinate();
            HexCoordinate.Point2D hexCenter = hexCoord.toPixelCatan(hexSpacing);
            
            // Calculate the 6 vertices of this hexagon
            for (int i = 0; i < 6; i++) {
                // Angle for pointy-top hexagon vertices (CATAN standard)
                double angle = (Math.PI / 3.0 * i) + (Math.PI / 6.0);
                double vertexX = centerX + hexCenter.x + hexRadius * Math.cos(angle);
                double vertexY = centerY + hexCenter.y + hexRadius * Math.sin(angle);
                
                // Use very high precision duplicate detection to eliminate overlapping spots
                // Ultra-compact layout requires higher precision to catch near-identical coordinates
                // Use decimal formatting for even higher precision control
                double precision = 1000.0; // Sub-pixel precision
                long roundedX = Math.round(vertexX * precision);
                long roundedY = Math.round(vertexY * precision);
                String vertexKey = roundedX + "," + roundedY;
                
                if (processedVertices.contains(vertexKey)) {
                    continue; // Skip duplicates
                }
                processedVertices.add(vertexKey);
                
                // Convert to game coordinates for rule checking
                int gameX = (int) Math.round((vertexX - centerX) / 25.0);
                int gameY = (int) Math.round((vertexY - centerY) / 25.0);
                
                boolean canBuild = game.getBoard().canPlaceBuilding(gameX, gameY, currentPlayer);
                boolean isOccupied = game.getBoard().getBuildings().stream()
                        .anyMatch(b -> b.getX() == gameX && b.getY() == gameY);
                
                // Create building spot
                javafx.scene.shape.Circle buildingSpot = UIComponents.createBuildingSpot(canBuild, isOccupied);
                buildingSpot.setRadius(5.0); // Keep original size, just closer spacing
                buildingSpot.setLayoutX(vertexX);
                buildingSpot.setLayoutY(vertexY);
                
                // Add tooltip
                javafx.scene.control.Tooltip tooltip = new javafx.scene.control.Tooltip(
                        UIComponents.createBuildingTooltip(canBuild, isOccupied, Building.Type.SETTLEMENT));
                javafx.scene.control.Tooltip.install(buildingSpot, tooltip);
                
                // Add click handler if building is possible
                if (canBuild && !isOccupied) {
                    final int finalGameX = gameX;
                    final int finalGameY = gameY;
                    
                    buildingSpot.setOnMouseClicked(e -> handleEnhancedBuildingClick(
                            null, 0, finalGameX, finalGameY, buildingSpot));
                }
                
                boardGrid.getChildren().add(buildingSpot);
            }
        }
    }
    
    private void createOptimizedRoadSpots(List<TerrainTile> hexTiles, double hexRadius,
                                         double hexSpacing, double centerX, double centerY) {
        Player currentPlayer = game.getCurrentPlayer();
        Set<String> processedEdges = new HashSet<>();
        
        // In CATAN, road spots are on the edges between hexagon vertices
        for (TerrainTile tile : hexTiles) {
            if (tile.getHexCoordinate() == null) continue;
            
            HexCoordinate hexCoord = tile.getHexCoordinate();
            HexCoordinate.Point2D hexCenter = hexCoord.toPixelCatan(hexSpacing);
            
            // Calculate the 6 edges of this hexagon
            for (int i = 0; i < 6; i++) {
                double angle1 = (Math.PI / 3.0 * i) + (Math.PI / 6.0);
                double angle2 = (Math.PI / 3.0 * ((i + 1) % 6)) + (Math.PI / 6.0);
                
                double vertex1X = centerX + hexCenter.x + hexRadius * Math.cos(angle1);
                double vertex1Y = centerY + hexCenter.y + hexRadius * Math.sin(angle1);
                double vertex2X = centerX + hexCenter.x + hexRadius * Math.cos(angle2);
                double vertex2Y = centerY + hexCenter.y + hexRadius * Math.sin(angle2);
                
                // Calculate edge center positioned exactly between settlement spots
                double baseCenterX = (vertex1X + vertex2X) / 2.0;
                double baseCenterY = (vertex1Y + vertex2Y) / 2.0;
                
                // Calculate edge direction vector
                double edgeVectorX = vertex2X - vertex1X;
                double edgeVectorY = vertex2Y - vertex1Y;
                double edgeLength = Math.sqrt(edgeVectorX * edgeVectorX + edgeVectorY * edgeVectorY);
                
                // Position road spot directly between the two settlement vertices
                // For ultra-compact spacing, apply minimal inward offset for optimal visual alignment
                double offsetFactor = 0.015; // Reduced inward offset (1.5% of distance) for tighter layout
                double tileToEdgeX = baseCenterX - (centerX + hexCenter.x);
                double tileToEdgeY = baseCenterY - (centerY + hexCenter.y);
                double perpLength = Math.sqrt(tileToEdgeX * tileToEdgeX + tileToEdgeY * tileToEdgeY);
                
                double edgeCenterX = baseCenterX - (tileToEdgeX / perpLength) * (perpLength * offsetFactor);
                double edgeCenterY = baseCenterY - (tileToEdgeY / perpLength) * (perpLength * offsetFactor);
                
                // Use very high precision duplicate detection to eliminate overlapping roads
                // Ultra-compact layout requires higher precision to catch near-identical coordinates
                // Use decimal formatting for even higher precision control
                double precision = 1000.0; // Sub-pixel precision
                long roundedX = Math.round(edgeCenterX * precision);
                long roundedY = Math.round(edgeCenterY * precision);
                String edgeKey = roundedX + "," + roundedY;
                
                if (processedEdges.contains(edgeKey)) {
                    continue; // Skip duplicates
                }
                processedEdges.add(edgeKey);
                
                // Convert to game coordinates for rule checking
                int startX = (int) Math.round((vertex1X - centerX) / 25.0);
                int startY = (int) Math.round((vertex1Y - centerY) / 25.0);
                int endX = (int) Math.round((vertex2X - centerX) / 25.0);
                int endY = (int) Math.round((vertex2Y - centerY) / 25.0);
                
                boolean canBuildRoad = game.getBoard().canPlaceRoad(startX, startY, endX, endY, currentPlayer);
                boolean isRoadOccupied = game.getBoard().getRoads().stream()
                        .anyMatch(r -> (r.getStartX() == startX && r.getStartY() == startY && 
                                      r.getEndX() == endX && r.getEndY() == endY) ||
                                     (r.getStartX() == endX && r.getStartY() == endY && 
                                      r.getEndX() == startX && r.getEndY() == startY));
                
                // Calculate rotation for road
                double edgeAngle = Math.atan2(vertex2Y - vertex1Y, vertex2X - vertex1X);
                double roadRotation = Math.toDegrees(edgeAngle);
                
                // Create road spot with updated dimensions
                Rectangle roadSpot = UIComponents.createRoadSpot(canBuildRoad, isRoadOccupied, roadRotation);
                roadSpot.setWidth(UIComponents.ROAD_SPOT_LENGTH);  // Use updated constant
                roadSpot.setHeight(UIComponents.ROAD_SPOT_WIDTH); // Use updated constant
                roadSpot.setLayoutX(edgeCenterX);
                roadSpot.setLayoutY(edgeCenterY);
                
                // Add tooltip
                javafx.scene.control.Tooltip roadTooltip = new javafx.scene.control.Tooltip(
                        isRoadOccupied ? "Straße vorhanden" : 
                        (canBuildRoad ? "Klicken zum Straße bauen" : "Straße hier nicht möglich"));
                javafx.scene.control.Tooltip.install(roadSpot, roadTooltip);
                
                // Add click handler if road building is possible
                if (canBuildRoad && !isRoadOccupied) {
                    final int finalStartX = startX, finalStartY = startY;
                    final int finalEndX = endX, finalEndY = endY;
                    
                    roadSpot.setOnMouseClicked(e -> handleEnhancedRoadClick(
                            finalStartX, finalStartY, finalEndX, finalEndY, roadSpot));
                }
                
                boardGrid.getChildren().add(roadSpot);
            }
        }
    }
    
    private void setupSquareBoard() {
        // Original square board setup logic
        for (int x = 0; x < GameBoard.BOARD_SIZE; x++) {
            for (int y = 0; y < GameBoard.BOARD_SIZE; y++) {
                TerrainTile tile = game.getBoard().getTile(x, y);
                
                if (tile != null) {
                    Rectangle tileRect = createTileRectangle(tile);
                    tileRect.setLayoutX(x * 65); // Position the rectangle
                    tileRect.setLayoutY(y * 65);
                    boardGrid.getChildren().add(tileRect);
                    
                    // Add click handler for tile interactions
                    final int tileX = x;
                    final int tileY = y;
                    tileRect.setOnMouseClicked(e -> handleTileClick(tileX, tileY));
                }
            }
        }
        
        // Add building positions (intersections)
        for (int x = 0; x <= GameBoard.BOARD_SIZE; x++) {
            for (int y = 0; y <= GameBoard.BOARD_SIZE; y++) {
                Button buildingSpot = new Button();
                buildingSpot.setPrefSize(20, 20);
                buildingSpot.setStyle("-fx-background-color: lightgray; -fx-border-color: black;");
                
                final int buildX = x;
                final int buildY = y;
                buildingSpot.setOnAction(e -> handleBuildingClick(buildX, buildY));
                
                buildingSpot.setLayoutX(x * 65);
                buildingSpot.setLayoutY(y * 65);
                boardGrid.getChildren().add(buildingSpot);
            }
        }
    }
    
    private Rectangle createTileRectangle(TerrainTile tile) {
        Rectangle rect = new Rectangle(60, 60);
        
        // Set color based on terrain type
        switch (tile.getTerrainType()) {
            case FOREST:
                rect.setFill(Color.DARKGREEN);
                break;
            case HILLS:
                rect.setFill(Color.BROWN);
                break;
            case PASTURE:
                rect.setFill(Color.LIGHTGREEN);
                break;
            case FIELDS:
                rect.setFill(Color.YELLOW);
                break;
            case MOUNTAINS:
                rect.setFill(Color.GRAY);
                break;
            case DESERT:
                rect.setFill(Color.SANDYBROWN);
                break;
        }
        
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(2);
        
        // Add number token if not desert
        if (tile.getNumberToken() > 0) {
            Text numberText = new Text(String.valueOf(tile.getNumberToken()));
            numberText.setFill(Color.WHITE);
            // In a more sophisticated implementation, we would overlay this text on the rectangle
        }
        
        return rect;
    }
    
    private Polygon createHexagonalTile(TerrainTile tile) {
        return UIComponents.createHexagonalTerrainTile(tile);
    }
    
    private void handleEnhancedRoadClick(int startX, int startY, int endX, int endY, Rectangle roadSpot) {
        Player currentPlayer = game.getCurrentPlayer();
        
        if (game.getCurrentPhase() != CatanGame.GamePhase.PLAYING) {
            // Initial placement phase
            if (game.buildRoad(startX, startY, endX, endY)) {
                logMessage(currentPlayer.getName() + " hat eine Straße von (" + startX + "," + startY + 
                          ") nach (" + endX + "," + endY + ") platziert.");
                // Change road spot appearance to indicate it's built
                roadSpot.setFill(javafx.scene.paint.Color.BROWN);
                roadSpot.setOpacity(1.0);
                updateUI();
            }
        } else {
            // Regular game - check if player can afford a road
            if (currentPlayer.canBuildRoad()) {
                if (game.buildRoad(startX, startY, endX, endY)) {
                    logMessage(currentPlayer.getName() + " hat eine Straße gebaut von (" + startX + "," + startY + 
                              ") nach (" + endX + "," + endY + ").");
                    // Change road spot appearance
                    roadSpot.setFill(javafx.scene.paint.Color.BROWN);
                    roadSpot.setOpacity(1.0);
                    updateUI();
                }
            } else {
                logMessage(currentPlayer.getName() + " hat nicht genug Ressourcen für eine Straße.");
            }
        }
    }
    
    private void handleTileClick(int x, int y) {
        if (game.getLastDiceRoll() == 7 && game.getCurrentPhase() == CatanGame.GamePhase.PLAYING) {
            // Move robber
            game.moveRobber(x, y);
            logMessage(game.getCurrentPlayer().getName() + " hat den Räuber auf Feld (" + x + "," + y + ") bewegt.");
            updateUI();
        }
    }
    
    private void handleBuildingClick(int x, int y) {
        Player currentPlayer = game.getCurrentPlayer();
        
        if (game.getCurrentPhase() != CatanGame.GamePhase.PLAYING) {
            // Initial placement
            if (game.buildSettlement(x, y)) {
                logMessage(currentPlayer.getName() + " hat eine Siedlung bei (" + x + "," + y + ") platziert.");
                updateUI();
            }
        } else {
            // Show building options
            showBuildingOptions(x, y);
        }
    }
    
    private void handleHexTileClick(HexCoordinate hexCoord) {
        if (game.getLastDiceRoll() == 7 && game.getCurrentPhase() == CatanGame.GamePhase.PLAYING) {
            // Move robber to hexagonal coordinate
            // For now, we'll use the conversion to x,y coordinates for compatibility
            HexCoordinate.Point2D pixelPos = hexCoord.toPixelCatan(52.0);
            int x = (int) (pixelPos.x / 60);
            int y = (int) (pixelPos.y / 60);
            
            game.moveRobber(x, y);
            logMessage(game.getCurrentPlayer().getName() + " hat den Räuber auf Hex-Feld " + hexCoord + " bewegt.");
            updateUI();
        }
    }
    
    private void handleHexBuildingClick(HexCoordinate hexCoord, int vertex) {
        Player currentPlayer = game.getCurrentPlayer();
        
        // Convert hex coordinate and vertex to approximate x,y for compatibility
        HexCoordinate.Point2D pixelPos = hexCoord.toPixelCatan(52.0);
        double angle = Math.PI / 3 * vertex;
        double vertexX = pixelPos.x + 52 * Math.cos(angle);
        double vertexY = pixelPos.y + 52 * Math.sin(angle);
        
        int x = (int) (vertexX / 60);
        int y = (int) (vertexY / 60);
        
        if (game.getCurrentPhase() != CatanGame.GamePhase.PLAYING) {
            // Initial placement
            if (game.buildSettlement(x, y)) {
                logMessage(currentPlayer.getName() + " hat eine Siedlung bei Hex " + hexCoord + " Vertex " + vertex + " platziert.");
                updateUI();
            }
        } else {
            // Show building options
            showHexBuildingOptions(hexCoord, vertex, x, y);
        }
    }
    
    private void showBuildingOptions(int x, int y) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Gebäude bauen");
        alert.setHeaderText("Was möchten Sie bei Position (" + x + "," + y + ") bauen?");
        
        ButtonType settlementButton = new ButtonType("Siedlung");
        ButtonType cityButton = new ButtonType("Stadt");
        ButtonType cancelButton = new ButtonType("Abbrechen", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        alert.getButtonTypes().setAll(settlementButton, cityButton, cancelButton);
        
        Optional<ButtonType> result = alert.showAndWait();
        
        if (result.isPresent()) {
            if (result.get() == settlementButton) {
                if (game.buildSettlement(x, y)) {
                    logMessage(game.getCurrentPlayer().getName() + " hat eine Siedlung gebaut.");
                    updateUI();
                } else {
                    showError("Siedlung kann nicht gebaut werden.");
                }
            } else if (result.get() == cityButton) {
                if (game.buildCity(x, y)) {
                    logMessage(game.getCurrentPlayer().getName() + " hat eine Stadt gebaut.");
                    updateUI();
                } else {
                    showError("Stadt kann nicht gebaut werden.");
                }
            }
        }
    }
    
    private void showHexBuildingOptions(HexCoordinate hexCoord, int vertex, int x, int y) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Gebäude bauen");
        alert.setHeaderText("Was möchten Sie bei Hex " + hexCoord + " Vertex " + vertex + " bauen?");
        
        ButtonType settlementButton = new ButtonType("Siedlung");
        ButtonType cityButton = new ButtonType("Stadt");
        ButtonType cancelButton = new ButtonType("Abbrechen", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        alert.getButtonTypes().setAll(settlementButton, cityButton, cancelButton);
        
        Optional<ButtonType> result = alert.showAndWait();
        
        if (result.isPresent()) {
            if (result.get() == settlementButton) {
                if (game.buildSettlement(x, y)) {
                    logMessage(game.getCurrentPlayer().getName() + " hat eine Siedlung gebaut.");
                    updateUI();
                } else {
                    showError("Siedlung kann nicht gebaut werden.");
                }
            } else if (result.get() == cityButton) {
                if (game.buildCity(x, y)) {
                    logMessage(game.getCurrentPlayer().getName() + " hat eine Stadt gebaut.");
                    updateUI();
                } else {
                    showError("Stadt kann nicht gebaut werden.");
                }
            }
        }
    }
    
    @FXML
    private void handleRollDice() {
        if (game.getCurrentPhase() == CatanGame.GamePhase.PLAYING) {
            int roll = game.rollDice();
            logMessage(game.getCurrentPlayer().getName() + " hat " + roll + " gewürfelt.");
            
            if (roll == 7) {
                logMessage("Sieben gewürfelt! Spieler mit mehr als 7 Karten müssen abwerfen. Räuber muss bewegt werden.");
            }
            
            updateUI();
        }
    }
    
    @FXML
    private void handleEndTurn() {
        String previousPlayer = game.getCurrentPlayer().getName();
        game.endTurn();
        
        if (game.isGameFinished()) {
            showGameEnd();
        } else {
            logMessage(previousPlayer + " hat den Zug beendet. " + game.getCurrentPlayer().getName() + " ist dran.");
            updateUI();
        }
    }
    
    private void showGameEnd() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Spiel beendet");
        alert.setHeaderText("Glückwunsch!");
        alert.setContentText(game.getWinner().getName() + " hat das Spiel mit " + 
                           game.getWinner().getVictoryPoints() + " Siegpunkten gewonnen!");
        alert.showAndWait();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fehler");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void updateUI() {
        if (!gameStarted) return;
        
        Player currentPlayer = game.getCurrentPlayer();
        
        // Update current player info
        currentPlayerLabel.setText("Aktueller Spieler: " + currentPlayer.getName() + 
                                 " (" + currentPlayer.getColor().getGermanName() + ")");
        
        // Update game phase
        String phaseText = switch (game.getCurrentPhase()) {
            case INITIAL_PLACEMENT_1 -> "Anfangsphase 1: Siedlungen und Straßen platzieren";
            case INITIAL_PLACEMENT_2 -> "Anfangsphase 2: Siedlungen und Straßen platzieren";
            case PLAYING -> "Spielphase";
        };
        gamePhaseLabel.setText("Phase: " + phaseText);
        
        // Update dice roll
        if (game.getLastDiceRoll() > 0) {
            diceRollLabel.setText("Letzter Wurf: " + game.getLastDiceRoll());
        } else {
            diceRollLabel.setText("Letzter Wurf: -");
        }
        
        // Update resource panel
        updateResourcePanel();
        
        // Update player info panel
        updatePlayerInfoPanel();
        
        // Enable/disable buttons
        rollDiceButton.setDisable(game.getCurrentPhase() != CatanGame.GamePhase.PLAYING);
        endTurnButton.setDisable(game.isGameFinished());
        
        // Refresh board visuals to show updated building/road availability
        refreshBoardVisuals();
    }
    
    private void updateResourcePanel() {
        resourcePanel.getChildren().clear();
        
        Player currentPlayer = game.getCurrentPlayer();
        
        Label resourceTitle = new Label("Ressourcen von " + currentPlayer.getName() + ":");
        resourceTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        resourcePanel.getChildren().add(resourceTitle);
        
        for (ResourceType type : ResourceType.values()) {
            int count = currentPlayer.getResourceCount(type);
            Label resourceLabel = new Label(UIComponents.formatResourceText(type, count));
            resourceLabel.setStyle("-fx-font-size: 12px;");
            
            // Highlight resources the player can afford to build with
            if (count > 0) {
                resourceLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: darkgreen; -fx-font-weight: bold;");
            }
            
            resourcePanel.getChildren().add(resourceLabel);
        }
        
        Label totalLabel = new Label("📦 Gesamt: " + currentPlayer.getTotalResourceCount());
        totalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        resourcePanel.getChildren().add(totalLabel);
        
        // Add building cost information
        Label separator = new Label("─────────────");
        separator.setStyle("-fx-text-fill: gray;");
        resourcePanel.getChildren().add(separator);
        
        Label costTitle = new Label("Baukosten:");
        costTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        resourcePanel.getChildren().add(costTitle);
        
        // Settlement costs
        boolean canAffordSettlement = currentPlayer.canBuildSettlement();
        Label settlementCost = new Label("🏘️ Siedlung: 1🌲 1🧱 1🐑 1🌾");
        settlementCost.setStyle("-fx-font-size: 10px; " + 
                              (canAffordSettlement ? "-fx-text-fill: green;" : "-fx-text-fill: red;"));
        resourcePanel.getChildren().add(settlementCost);
        
        // City costs
        boolean canAffordCity = currentPlayer.canBuildCity();
        Label cityCost = new Label("🏛️ Stadt: 2🌾 3⛰️");
        cityCost.setStyle("-fx-font-size: 10px; " + 
                         (canAffordCity ? "-fx-text-fill: green;" : "-fx-text-fill: red;"));
        resourcePanel.getChildren().add(cityCost);
        
        // Road costs
        boolean canAffordRoad = currentPlayer.canBuildRoad();
        Label roadCost = new Label("🛤️ Straße: 1🌲 1🧱");
        roadCost.setStyle("-fx-font-size: 10px; " + 
                         (canAffordRoad ? "-fx-text-fill: green;" : "-fx-text-fill: red;"));
        resourcePanel.getChildren().add(roadCost);
    }
    
    private void updatePlayerInfoPanel() {
        playerInfoPanel.getChildren().clear();
        
        Label title = new Label("Spieler-Übersicht:");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        playerInfoPanel.getChildren().add(title);
        
        for (Player player : game.getPlayers()) {
            VBox playerBox = new VBox(2);
            playerBox.setStyle("-fx-border-color: " + player.getColor().getHexColor() + "; " +
                             "-fx-border-width: 2; -fx-padding: 5;");
            
            Label nameLabel = new Label(player.getName() + " (" + player.getColor().getGermanName() + ")");
            nameLabel.setStyle("-fx-font-weight: bold;");
            
            Label pointsLabel = new Label("Siegpunkte: " + player.getVictoryPoints());
            Label resourcesLabel = new Label("Ressourcen: " + player.getTotalResourceCount());
            Label buildingsLabel = new Label("Siedlungen: " + (5 - player.getSettlementsLeft()) + 
                                           ", Städte: " + (4 - player.getCitiesLeft()) + 
                                           ", Straßen: " + (15 - player.getRoadsLeft()));
            
            playerBox.getChildren().addAll(nameLabel, pointsLabel, resourcesLabel, buildingsLabel);
            playerInfoPanel.getChildren().add(playerBox);
        }
    }
    
    private void logMessage(String message) {
        if (gameLogArea != null) {
            gameLogArea.appendText(message + "\n");
        }
    }
    
    @FXML
    private void handleNewGame() {
        setupNewGame();
        updateUI();
    }

    private void handleEnhancedBuildingClick(HexCoordinate hexCoord, int vertex, int x, int y, 
                                               javafx.scene.shape.Circle buildingSpot) {
        Player currentPlayer = game.getCurrentPlayer();
        
        if (game.getCurrentPhase() != CatanGame.GamePhase.PLAYING) {
            // Initial placement - just place settlement
            if (game.buildSettlement(x, y)) {
                logMessage(currentPlayer.getName() + " hat eine Siedlung bei Hex " + hexCoord + 
                          " Vertex " + vertex + " platziert.");
                
                // Update visual appearance
                buildingSpot.setFill(UIComponents.getPlayerColor(currentPlayer.getColor()));
                buildingSpot.setStroke(Color.BLACK);
                buildingSpot.setOnMouseClicked(null); // Remove click handler
                
                // Update tooltip
                javafx.scene.control.Tooltip newTooltip = new javafx.scene.control.Tooltip(
                        "Siedlung von " + currentPlayer.getName());
                javafx.scene.control.Tooltip.install(buildingSpot, newTooltip);
                
                updateUI();
            }
        } else {
            // Show enhanced building options
            showEnhancedBuildingOptions(hexCoord, vertex, x, y, buildingSpot);
        }
    }
    
    private void handleRoadClick(int startX, int startY, int endX, int endY, Rectangle roadSpot) {
        Player currentPlayer = game.getCurrentPlayer();
        
        if (game.buildRoad(startX, startY, endX, endY)) {
            logMessage(currentPlayer.getName() + " hat eine Straße gebaut.");
            
            // Update visual appearance
            roadSpot.setFill(UIComponents.getPlayerColor(currentPlayer.getColor()));
            roadSpot.setStroke(Color.BLACK);
            roadSpot.setOnMouseClicked(null); // Remove click handler
            
            // Update tooltip
            javafx.scene.control.Tooltip newTooltip = new javafx.scene.control.Tooltip(
                    "Straße von " + currentPlayer.getName());
            javafx.scene.control.Tooltip.install(roadSpot, newTooltip);
            
            updateUI();
        } else {
            showError("Straße kann hier nicht gebaut werden.");
        }
    }
    
    private void showEnhancedBuildingOptions(HexCoordinate hexCoord, int vertex, int x, int y, 
                                           javafx.scene.shape.Circle buildingSpot) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Gebäude bauen");
        alert.setHeaderText("Was möchten Sie bei Hex " + hexCoord + " Vertex " + vertex + " bauen?");
        
        // Check what can be built
        boolean canBuildSettlement = game.getBoard().canPlaceBuilding(x, y, game.getCurrentPlayer());
        Building existingBuilding = game.getBoard().getBuildings().stream()
                .filter(b -> b.getX() == x && b.getY() == y)
                .findFirst().orElse(null);
        
        ButtonType settlementButton = new ButtonType("Siedlung (🏘️)");
        ButtonType cityButton = new ButtonType("Stadt (🏛️)");
        ButtonType cancelButton = new ButtonType("Abbrechen", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        if (existingBuilding != null && existingBuilding.getType() == Building.Type.SETTLEMENT) {
            // Can upgrade to city
            alert.getButtonTypes().setAll(cityButton, cancelButton);
        } else if (canBuildSettlement) {
            // Can build settlement
            alert.getButtonTypes().setAll(settlementButton, cancelButton);
        } else {
            // Cannot build anything
            alert.setAlertType(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Bauen nicht möglich");
            alert.setContentText("An dieser Position kann nichts gebaut werden.");
            alert.getButtonTypes().setAll(cancelButton);
        }
        
        Optional<ButtonType> result = alert.showAndWait();
        
        if (result.isPresent()) {
            if (result.get() == settlementButton) {
                if (game.buildSettlement(x, y)) {
                    logMessage(game.getCurrentPlayer().getName() + " hat eine Siedlung gebaut.");
                    updateBuildingSpotAfterBuild(buildingSpot, Building.Type.SETTLEMENT);
                    updateUI();
                } else {
                    showError("Siedlung kann nicht gebaut werden.");
                }
            } else if (result.get() == cityButton) {
                if (game.buildCity(x, y)) {
                    logMessage(game.getCurrentPlayer().getName() + " hat eine Stadt gebaut.");
                    updateBuildingSpotAfterBuild(buildingSpot, Building.Type.CITY);
                    updateUI();
                } else {
                    showError("Stadt kann nicht gebaut werden.");
                }
            }
        }
    }
    
    private void updateBuildingSpotAfterBuild(javafx.scene.shape.Circle buildingSpot, Building.Type buildingType) {
        Player currentPlayer = game.getCurrentPlayer();
        
        // Update appearance
        buildingSpot.setFill(UIComponents.getPlayerColor(currentPlayer.getColor()));
        buildingSpot.setStroke(Color.BLACK);
        buildingSpot.setStrokeWidth(2.0);
        
        // Make it slightly larger for cities
        if (buildingType == Building.Type.CITY) {
            buildingSpot.setRadius(UIComponents.BUILDING_SPOT_SIZE / 1.5);
        }
        
        // Remove click handler
        buildingSpot.setOnMouseClicked(null);
        buildingSpot.setOnMouseEntered(null);
        buildingSpot.setOnMouseExited(null);
        
        // Update tooltip
        String buildingName = buildingType == Building.Type.SETTLEMENT ? "Siedlung" : "Stadt";
        javafx.scene.control.Tooltip newTooltip = new javafx.scene.control.Tooltip(
                buildingName + " von " + currentPlayer.getName());
        javafx.scene.control.Tooltip.install(buildingSpot, newTooltip);
    }
    
    /**
     * Refresh all building and road spots to reflect current game state.
     */
    private void refreshBoardVisuals() {
        // Clear and rebuild board for visual updates
        boardGrid.getChildren().clear();
        
        if (game.getBoard().isHexagonal()) {
            setupHexagonalBoard();
        } else {
            setupSquareBoard();
        }
    }
    
    // Enhanced coordinate system click handlers
    private void handleEnhancedVertexClick(VertexCoordinate vertex, javafx.scene.shape.Circle buildingSpot, 
                                          EnhancedHexGameBoard enhancedBoard) {
        Player currentPlayer = game.getCurrentPlayer();
        
        if (game.getCurrentPhase() != CatanGame.GamePhase.PLAYING) {
            // Initial placement phase
            if (enhancedBoard.placeBuilding(Building.Type.SETTLEMENT, vertex, currentPlayer)) {
                logMessage(currentPlayer.getName() + " hat eine Siedlung bei " + vertex + " platziert.");
                
                // Update visual appearance
                buildingSpot.setFill(getPlayerColor(currentPlayer));
                buildingSpot.setStroke(Color.BLACK);
                buildingSpot.setStrokeWidth(2.0);
                buildingSpot.setOnMouseClicked(null); // Remove click handler
                
                updateUI();
            } else {
                showError("Siedlung kann hier nicht platziert werden.");
            }
        } else {
            // Show building options during normal play
            showEnhancedBuildingOptions(vertex, enhancedBoard, buildingSpot);
        }
    }
    
    private void handleEnhancedEdgeClick(EdgeCoordinate edge, Rectangle roadSpot, 
                                        EnhancedHexGameBoard enhancedBoard) {
        Player currentPlayer = game.getCurrentPlayer();
        
        if (enhancedBoard.placeRoad(edge, currentPlayer)) {
            logMessage(currentPlayer.getName() + " hat eine Straße bei " + edge + " gebaut.");
            
            // Update visual appearance
            roadSpot.setFill(getPlayerColor(currentPlayer));
            roadSpot.setStroke(Color.BLACK);
            roadSpot.setStrokeWidth(1.0);
            roadSpot.setOnMouseClicked(null); // Remove click handler
            
            updateUI();
        } else {
            showError("Straße kann hier nicht gebaut werden.");
        }
    }
    
    private void showEnhancedBuildingOptions(VertexCoordinate vertex, EnhancedHexGameBoard enhancedBoard, 
                                           javafx.scene.shape.Circle buildingSpot) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Gebäude bauen");
        alert.setHeaderText("Was möchten Sie bei " + vertex + " bauen?");
        
        ButtonType settlementButton = new ButtonType("Siedlung");
        ButtonType cityButton = new ButtonType("Stadt");
        ButtonType cancelButton = new ButtonType("Abbrechen", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        alert.getButtonTypes().setAll(settlementButton, cityButton, cancelButton);
        
        Optional<ButtonType> result = alert.showAndWait();
        
        if (result.isPresent()) {
            Player currentPlayer = game.getCurrentPlayer();
            if (result.get() == settlementButton) {
                if (enhancedBoard.placeBuilding(Building.Type.SETTLEMENT, vertex, currentPlayer)) {
                    logMessage(currentPlayer.getName() + " hat eine Siedlung gebaut.");
                    
                    // Update visual appearance
                    buildingSpot.setFill(getPlayerColor(currentPlayer));
                    buildingSpot.setStroke(Color.BLACK);
                    buildingSpot.setStrokeWidth(2.0);
                    
                    updateUI();
                } else {
                    showError("Siedlung kann nicht gebaut werden.");
                }
            } else if (result.get() == cityButton) {
                if (enhancedBoard.upgradeToCity(vertex, currentPlayer)) {
                    logMessage(currentPlayer.getName() + " hat eine Stadt gebaut.");
                    
                    // Update visual appearance for city (larger)
                    buildingSpot.setRadius(buildingSpot.getRadius() * 1.5);
                    buildingSpot.setFill(getPlayerColor(currentPlayer));
                    buildingSpot.setStroke(Color.BLACK);
                    buildingSpot.setStrokeWidth(3.0);
                    
                    updateUI();
                } else {
                    showError("Stadt kann nicht gebaut werden.");
                }
            }
        }
    }
    
    /**
     * Calculate the precise position of a vertex around a hexagon, matching original CATAN layout.
     */
    private HexCoordinate.Point2D calculateVertexPosition(VertexCoordinate vertex, double hexRadius, double hexSpacing, double centerX, double centerY) {
        // Get the hex center position
        HexCoordinate hexCoord = new HexCoordinate(vertex.getX(), vertex.getY());
        HexCoordinate.Point2D hexCenter = hexCoord.toPixelCatan(hexSpacing);
        
        // Calculate vertex position around the hexagon
        double angle = Math.toRadians(vertex.getDirection() * 60); // 60 degrees per vertex direction
        double vertexX = hexCenter.x + hexRadius * Math.cos(angle);
        double vertexY = hexCenter.y + hexRadius * Math.sin(angle);
        
        return new HexCoordinate.Point2D(centerX + vertexX, centerY + vertexY);
    }
    
    /**
     * Calculate the precise position of an edge between two vertices, matching original CATAN layout.
     */
    private HexCoordinate.Point2D calculateEdgePosition(EdgeCoordinate edge, double hexRadius, double hexSpacing, double centerX, double centerY) {
        // Get the two vertices that this edge connects
        VertexCoordinate[] vertices = edge.getConnectedVertices();
        
        // Calculate positions of both vertices
        HexCoordinate.Point2D vertex1Pos = calculateVertexPosition(vertices[0], hexRadius, hexSpacing, centerX, centerY);
        HexCoordinate.Point2D vertex2Pos = calculateVertexPosition(vertices[1], hexRadius, hexSpacing, centerX, centerY);
        
        // Edge position is the midpoint between the two vertices
        double edgeX = (vertex1Pos.x + vertex2Pos.x) / 2.0;
        double edgeY = (vertex1Pos.y + vertex2Pos.y) / 2.0;
        
        return new HexCoordinate.Point2D(edgeX, edgeY);
    }
    
    /**
     * Calculate the rotation angle for a road segment along an edge.
     */
    private double calculateEdgeRotation(EdgeCoordinate edge) {
        // Calculate the angle based on the edge direction
        double angle = Math.toRadians(edge.getDirection() * 60); // 60 degrees per edge direction
        return Math.toDegrees(angle);
    }
    
    /**
     * Get the display color for a player.
     */
    private Color getPlayerColor(Player player) {
        return switch (player.getColor()) {
            case RED -> Color.RED;
            case BLUE -> Color.BLUE;
            case WHITE -> Color.WHITE;
            case ORANGE -> Color.ORANGE;
            default -> Color.GRAY;
        };
    }
    
    /**
     * Get only the authentic CATAN settlement positions (54 total).
     * Shows only the actual game positions, not all mathematical possibilities.
     */
    private Set<VertexCoordinate> getRelevantVerticesForCurrentState(EnhancedHexGameBoard enhancedBoard, Player currentPlayer) {
        Set<VertexCoordinate> relevantVertices = new HashSet<>();
        
        // Add all occupied vertices (always show existing buildings)
        for (Building building : enhancedBoard.getBuildings()) {
            if (building.getVertexCoordinate() != null) {
                relevantVertices.add(building.getVertexCoordinate());
            }
        }
        
        // Only add the valid vertices from the enhanced board (these are the authentic CATAN positions)
        relevantVertices.addAll(enhancedBoard.getValidVertices());
        
        // Do NOT generate additional vertices - use only the authentic game positions
        System.out.println("DEBUG: Showing " + relevantVertices.size() + " authentic settlement positions");
        return relevantVertices;
    }
    
    /**
     * Get only the authentic CATAN road positions (72 total).
     * Shows only the actual game positions, not all mathematical possibilities.
     */
    private Set<EdgeCoordinate> getRelevantEdgesForCurrentState(EnhancedHexGameBoard enhancedBoard, Player currentPlayer) {
        Set<EdgeCoordinate> relevantEdges = new HashSet<>();
        
        // Add all occupied edges (always show existing roads)
        for (com.catan.model.Road road : enhancedBoard.getRoads()) {
            if (road.getEdgeCoordinate() != null) {
                relevantEdges.add(road.getEdgeCoordinate());
            }
        }
        
        // Only add the valid edges from the enhanced board (these are the authentic CATAN positions)
        relevantEdges.addAll(enhancedBoard.getValidEdges());
        
        // Do NOT generate additional edges - use only the authentic game positions
        System.out.println("DEBUG: Rendering " + relevantEdges.size() + " authentic road positions");
        return relevantEdges;
    }
    
    /**
     * Calculate the corrected vertex position using proper hexagon math and CATAN positioning.
     */
    private HexCoordinate.Point2D calculateFixedVertexPosition(VertexCoordinate vertex, double hexRadius, double hexSpacing, double centerX, double centerY) {
        // Get the hex center position using CATAN positioning
        HexCoordinate hexCoord = new HexCoordinate(vertex.getX(), vertex.getY());
        HexCoordinate.Point2D hexCenter = hexCoord.toPixelCatan(hexSpacing);
        
        // CATAN uses pointy-top hexagons. Vertices are numbered 0-5 starting from top and going clockwise.
        // Direction 0 = top (90°), Direction 1 = top-right (30°), etc.
        double angleOffset = Math.PI / 2; // Start from top (90 degrees)
        double angle = angleOffset - (vertex.getDirection() * Math.PI / 3); // Subtract to go clockwise
        
        double vertexX = hexCenter.x + hexRadius * Math.cos(angle);
        double vertexY = hexCenter.y + hexRadius * Math.sin(angle);
        
        return new HexCoordinate.Point2D(centerX + vertexX, centerY + vertexY);
    }
    
    /**
     * Calculate the corrected edge position using proper hexagon math.
     */
    private HexCoordinate.Point2D calculateFixedEdgePosition(EdgeCoordinate edge, double hexRadius, double hexSpacing, double centerX, double centerY) {
        // Get the two vertices that this edge connects
        VertexCoordinate[] vertices = edge.getConnectedVertices();
        
        // Calculate positions of both vertices using the fixed calculation
        HexCoordinate.Point2D vertex1Pos = calculateFixedVertexPosition(vertices[0], hexRadius, hexSpacing, centerX, centerY);
        HexCoordinate.Point2D vertex2Pos = calculateFixedVertexPosition(vertices[1], hexRadius, hexSpacing, centerX, centerY);
        
        // Edge position is the midpoint between the two vertices
        double edgeX = (vertex1Pos.x + vertex2Pos.x) / 2.0;
        double edgeY = (vertex1Pos.y + vertex2Pos.y) / 2.0;
        
        return new HexCoordinate.Point2D(edgeX, edgeY);
    }
    
    /**
     * Calculate the corrected rotation angle for a road segment.
     */
    private double calculateFixedEdgeRotation(EdgeCoordinate edge) {
        // Get the two vertices that this edge connects
        VertexCoordinate[] vertices = edge.getConnectedVertices();
        
        // Use a dummy position to calculate the direction vector
        HexCoordinate.Point2D vertex1Pos = calculateFixedVertexPosition(vertices[0], 1.0, 1.0, 0.0, 0.0);
        HexCoordinate.Point2D vertex2Pos = calculateFixedVertexPosition(vertices[1], 1.0, 1.0, 0.0, 0.0);
        
        // Calculate the angle between the two vertices
        double dx = vertex2Pos.x - vertex1Pos.x;
        double dy = vertex2Pos.y - vertex1Pos.y;
        double angle = Math.atan2(dy, dx);
        
        return Math.toDegrees(angle);
    }
}
