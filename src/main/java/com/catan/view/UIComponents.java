package com.catan.view;

import com.catan.model.Building;
import com.catan.model.PlayerColor;
import com.catan.model.ResourceType;
import com.catan.model.TerrainTile;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Utility class for creating and styling JavaFX UI components for the CATAN game.
 */
public class UIComponents {
    
    // Color constants
    public static final Color FOREST_COLOR = Color.DARKGREEN;
    public static final Color HILLS_COLOR = Color.SADDLEBROWN;
    public static final Color PASTURE_COLOR = Color.LIGHTGREEN;
    public static final Color FIELDS_COLOR = Color.GOLD;
    public static final Color MOUNTAINS_COLOR = Color.GRAY;
    public static final Color DESERT_COLOR = Color.SANDYBROWN;
    public static final Color OCEAN_COLOR = Color.LIGHTBLUE;
    
    // Size constants
    public static final double TILE_SIZE = 80.0;
    public static final double HEX_RADIUS = 45.0; // Updated to match larger tile size
    public static final double BUILDING_SIZE = 12.0;
    public static final double ROAD_WIDTH = 6.0;
    
    // Building spot styling constants - optimized for ultra-compact layout
    public static final double BUILDING_SPOT_SIZE = 10.0; // Keep building spot size for clickability
    public static final double ROAD_SPOT_LENGTH = 16.0;   // Shorter length for ultra-compact spacing
    public static final double ROAD_SPOT_WIDTH = 2.5;     // Thinner width for tighter layout
    
    // Colors for building spots
    public static final Color AVAILABLE_SPOT_COLOR = Color.LIGHTBLUE;
    public static final Color UNAVAILABLE_SPOT_COLOR = Color.LIGHTGRAY;
    public static final Color HOVER_SPOT_COLOR = Color.YELLOW;
    public static final Color OCCUPIED_SPOT_COLOR = Color.DARKGRAY;
    
    /**
     * Create a hexagonal terrain tile with appropriate styling.
     */
    public static Polygon createHexagonalTerrainTile(TerrainTile tile) {
        return createHexagonalTerrainTile(tile, HEX_RADIUS);
    }
    
    /**
     * Create a hexagonal terrain tile with appropriate styling and custom radius.
     */
    public static Polygon createHexagonalTerrainTile(TerrainTile tile, double radius) {
        Polygon hexagon = createHexagon(radius);
        hexagon.setStroke(Color.BLACK);
        hexagon.setStrokeWidth(2.0);
        
        // Add hover effect
        hexagon.setOnMouseEntered(e -> hexagon.setStroke(Color.YELLOW));
        hexagon.setOnMouseExited(e -> hexagon.setStroke(Color.BLACK));
        
        return hexagon;
    }
    
    /**
     * Create a hexagon polygon with given radius.
     * Uses pointy-top orientation for authentic CATAN appearance.
     */
    public static Polygon createHexagon(double radius) {
        Polygon hexagon = new Polygon();
        
        // Calculate hexagon points (6 vertices) with pointy-top orientation
        for (int i = 0; i < 6; i++) {
            // Add 30° offset for pointy-top orientation (like CATAN)
            double angle = (2 * Math.PI * i / 6) + Math.PI / 6;
            double x = radius * Math.cos(angle);
            double y = radius * Math.sin(angle);
            hexagon.getPoints().addAll(x, y);
        }
        
        return hexagon;
    }
    
    /**
     * Create a terrain tile rectangle with appropriate styling.
     * @deprecated Use createHexagonalTerrainTile instead for authentic CATAN experience
     */
    @Deprecated
    public static Rectangle createTerrainTile(TerrainTile tile) {
        Rectangle rect = new Rectangle(TILE_SIZE, TILE_SIZE);
        
        // Set color based on terrain type
        Color fillColor = switch (tile.getTerrainType()) {
            case FOREST -> FOREST_COLOR;
            case HILLS -> HILLS_COLOR;
            case PASTURE -> PASTURE_COLOR;
            case FIELDS -> FIELDS_COLOR;
            case MOUNTAINS -> MOUNTAINS_COLOR;
            case DESERT -> DESERT_COLOR;
        };
        
        rect.setFill(fillColor);
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(2.0);
        
        // Add hover effect
        rect.setOnMouseEntered(e -> rect.setStroke(Color.YELLOW));
        rect.setOnMouseExited(e -> rect.setStroke(Color.BLACK));
        
        return rect;
    }
    
    /**
     * Create a number token text for terrain tiles.
     */
    public static Text createNumberToken(int number) {
        Text text = new Text(String.valueOf(number));
        text.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        text.setFill(Color.BLACK);
        
        // Highlight high-probability numbers
        if (number == 6 || number == 8) {
            text.setFill(Color.RED);
            text.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        }
        
        // Center the text using TextOrigin
        text.setTextOrigin(javafx.geometry.VPos.CENTER);
        text.setX(0);
        text.setY(0);
        
        return text;
    }
    
    /**
     * Create a building shape based on type and player color.
     */
    public static Polygon createBuilding(Building.Type type, PlayerColor playerColor) {
        Polygon building = new Polygon();
        
        if (type == Building.Type.SETTLEMENT) {
            // Create a house shape (pentagon)
            building.getPoints().addAll(new Double[]{
                0.0, BUILDING_SIZE,      // bottom left
                BUILDING_SIZE, BUILDING_SIZE,  // bottom right
                BUILDING_SIZE, BUILDING_SIZE/2, // middle right
                BUILDING_SIZE/2, 0.0,    // top
                0.0, BUILDING_SIZE/2     // middle left
            });
        } else { // CITY
            // Create a larger, more complex shape
            building.getPoints().addAll(new Double[]{
                0.0, BUILDING_SIZE * 1.5,      // bottom left
                BUILDING_SIZE * 1.2, BUILDING_SIZE * 1.5,  // bottom right
                BUILDING_SIZE * 1.2, BUILDING_SIZE * 0.3,  // top right
                BUILDING_SIZE * 0.8, 0.0,      // peak right
                BUILDING_SIZE * 0.4, BUILDING_SIZE * 0.3,  // valley
                0.0, BUILDING_SIZE * 0.6       // top left
            });
        }
        
        building.setFill(Color.web(playerColor.getHexColor()));
        building.setStroke(Color.BLACK);
        building.setStrokeWidth(1.5);
        
        return building;
    }
    
    /**
     * Create a road rectangle with player color.
     */
    public static Rectangle createRoad(PlayerColor playerColor, double length) {
        Rectangle road = new Rectangle(length, ROAD_WIDTH);
        road.setFill(Color.web(playerColor.getHexColor()));
        road.setStroke(Color.BLACK);
        road.setStrokeWidth(1.0);
        
        return road;
    }
    
    /**
     * Create a robber shape.
     */
    public static Polygon createRobber() {
        Polygon robber = new Polygon();
        
        // Create a simple robber figure
        robber.getPoints().addAll(new Double[]{
            BUILDING_SIZE/2, 0.0,        // head top
            BUILDING_SIZE * 0.8, BUILDING_SIZE * 0.3,  // head right
            BUILDING_SIZE, BUILDING_SIZE,              // body bottom right
            0.0, BUILDING_SIZE,                        // body bottom left
            BUILDING_SIZE * 0.2, BUILDING_SIZE * 0.3   // head left
        });
        
        robber.setFill(Color.BLACK);
        robber.setStroke(Color.DARKRED);
        robber.setStrokeWidth(2.0);
        
        return robber;
    }
    
    /**
     * Get a formatted string for resource display.
     */
    public static String formatResourceText(ResourceType type, int count) {
        String icon = switch (type) {
            case LUMBER -> "🌲";
            case BRICK -> "🧱";
            case WOOL -> "🐑";
            case GRAIN -> "🌾";
            case ORE -> "⛰️";
        };
        
        return String.format("%s %s: %d", icon, type.getGermanName(), count);
    }
    
    /**
     * Get player color as JavaFX Color object.
     */
    public static Color getPlayerColor(PlayerColor playerColor) {
        return Color.web(playerColor.getHexColor());
    }
    
    /**
     * Create an enhanced building spot with proper styling and hover effects.
     */
    public static javafx.scene.shape.Circle createBuildingSpot(boolean canBuild, boolean isOccupied) {
        javafx.scene.shape.Circle spot = new javafx.scene.shape.Circle(BUILDING_SPOT_SIZE / 2);
        
        // Set initial appearance based on state
        if (isOccupied) {
            spot.setFill(OCCUPIED_SPOT_COLOR);
            spot.setStroke(Color.BLACK);
        } else if (canBuild) {
            spot.setFill(AVAILABLE_SPOT_COLOR);
            spot.setStroke(Color.DARKBLUE);
        } else {
            spot.setFill(UNAVAILABLE_SPOT_COLOR);
            spot.setStroke(Color.GRAY);
        }
        
        spot.setStrokeWidth(2.0);
        
        // Add hover effects only for available spots
        if (canBuild && !isOccupied) {
            spot.setOnMouseEntered(e -> {
                spot.setFill(HOVER_SPOT_COLOR);
                spot.setStroke(Color.ORANGE);
                spot.setStrokeWidth(3.0);
            });
            spot.setOnMouseExited(e -> {
                spot.setFill(AVAILABLE_SPOT_COLOR);
                spot.setStroke(Color.DARKBLUE);
                spot.setStrokeWidth(2.0);
            });
        }
        
        return spot;
    }
    
    /**
     * Create an enhanced road spot with proper styling.
     */
    public static Rectangle createRoadSpot(boolean canBuild, boolean isOccupied, double rotation) {
        Rectangle roadSpot = new Rectangle(ROAD_SPOT_LENGTH, ROAD_SPOT_WIDTH);
        
        // Set initial appearance based on state
        if (isOccupied) {
            roadSpot.setFill(OCCUPIED_SPOT_COLOR);
            roadSpot.setStroke(Color.BLACK);
        } else if (canBuild) {
            roadSpot.setFill(AVAILABLE_SPOT_COLOR.brighter());
            roadSpot.setStroke(Color.DARKBLUE);
        } else {
            roadSpot.setFill(UNAVAILABLE_SPOT_COLOR);
            roadSpot.setStroke(Color.GRAY);
        }
        
        roadSpot.setStrokeWidth(1.5);
        roadSpot.setRotate(rotation);
        
        // Add hover effects only for available spots
        if (canBuild && !isOccupied) {
            roadSpot.setOnMouseEntered(e -> {
                roadSpot.setFill(HOVER_SPOT_COLOR.brighter());
                roadSpot.setStroke(Color.ORANGE);
                roadSpot.setStrokeWidth(2.5);
            });
            roadSpot.setOnMouseExited(e -> {
                roadSpot.setFill(AVAILABLE_SPOT_COLOR.brighter());
                roadSpot.setStroke(Color.DARKBLUE);
                roadSpot.setStrokeWidth(1.5);
            });
        }
        
        return roadSpot;
    }
    
    /**
     * Create an enhanced hexagonal tile with number token overlay.
     */
    public static javafx.scene.Group createEnhancedHexagonalTile(TerrainTile tile) {
        return createEnhancedHexagonalTile(tile, HEX_RADIUS);
    }
    
    /**
     * Create an enhanced hexagonal tile with number token overlay and custom radius.
     */
    public static javafx.scene.Group createEnhancedHexagonalTile(TerrainTile tile, double radius) {
        javafx.scene.Group tileGroup = new javafx.scene.Group();
        
        // Create the hexagon - centered at (0,0) with custom radius
        Polygon hexagon = createHexagonalTerrainTile(tile, radius);
        tileGroup.getChildren().add(hexagon);
        
        // Add number token if tile has one
        if (tile.getNumberToken() > 0) {
            // Create token background circle - optimized for ultra-compact layout
            double tokenRadius = Math.max(13, radius * 0.30); // Smaller scaling for ultra-compact spacing
            javafx.scene.shape.Circle tokenBackground = new javafx.scene.shape.Circle(tokenRadius);
            tokenBackground.setFill(Color.WHITE);
            tokenBackground.setStroke(Color.BLACK);
            tokenBackground.setStrokeWidth(2.0);
            
            // Create number text - centered at (0,0)
            Text numberText = createNumberToken(tile.getNumberToken());
            
            tileGroup.getChildren().addAll(tokenBackground, numberText);
        }
        
        // Add robber if present
        if (tile.hasRobber()) {
            Polygon robber = createRobber();
            robber.setTranslateX(-BUILDING_SIZE/2);
            robber.setTranslateY(-BUILDING_SIZE/2);
            tileGroup.getChildren().add(robber);
        }
        
        return tileGroup;
    }
    
    /**
     * Create a tooltip text for building spots.
     */
    public static String createBuildingTooltip(boolean canBuild, boolean isOccupied, Building.Type buildingType) {
        if (isOccupied) {
            return buildingType == Building.Type.SETTLEMENT ? "Siedlung vorhanden" : "Stadt vorhanden";
        } else if (canBuild) {
            return "Klicken zum Bauen";
        } else {
            return "Bauen hier nicht möglich";
        }
    }
    
    /**
     * Update building spot appearance based on game state.
     */
    public static void updateBuildingSpotAppearance(javafx.scene.shape.Circle spot, boolean canBuild, boolean isOccupied) {
        if (isOccupied) {
            spot.setFill(OCCUPIED_SPOT_COLOR);
            spot.setStroke(Color.BLACK);
        } else if (canBuild) {
            spot.setFill(AVAILABLE_SPOT_COLOR);
            spot.setStroke(Color.DARKBLUE);
        } else {
            spot.setFill(UNAVAILABLE_SPOT_COLOR);
            spot.setStroke(Color.GRAY);
        }
    }
    
    /**
     * Style an existing hexagon polygon based on terrain tile.
     */
    public static void styleTerrainTile(Polygon hexagon, TerrainTile tile) {
        hexagon.setStroke(Color.BLACK);
        hexagon.setStrokeWidth(2.0);
        
        // Add hover effect
        hexagon.setOnMouseEntered(e -> hexagon.setStroke(Color.YELLOW));
        hexagon.setOnMouseExited(e -> hexagon.setStroke(Color.BLACK));
    }
}
