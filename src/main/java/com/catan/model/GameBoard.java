package com.catan.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the CATAN game board with terrain tiles, buildings, and roads.
 * Uses a simplified square grid instead of hexagons for easier implementation.
 * For hexagonal board, use HexGameBoard class.
 */
public class GameBoard {
    public static final int BOARD_SIZE = 5; // 5x5 grid of terrain tiles
    
    private final TerrainTile[][] tiles;
    private final Map<String, Building> buildings; // Key: "x,y"
    private final List<Road> roads;
    private int robberX;
    private int robberY;
    
    // Optional hexagonal board support
    private final HexGameBoard hexBoard;
    private final EnhancedHexGameBoard enhancedHexBoard;
    private final boolean useHexBoard;
    private final boolean useEnhanced;
    
    public GameBoard() {
        this(false, false); // Default to square grid for backward compatibility
    }
    
    public GameBoard(boolean useHexagonal) {
        this(useHexagonal, false); // Use legacy hex board by default
    }
    
    public GameBoard(boolean useHexagonal, boolean useEnhanced) {
        this.useHexBoard = useHexagonal;
        this.useEnhanced = useEnhanced && useHexagonal; // Enhanced only works with hexagonal
        
        if (useHexagonal) {
            if (this.useEnhanced) {
                // Use enhanced hexagonal board with vertex/edge coordinates
                this.enhancedHexBoard = new EnhancedHexGameBoard();
                this.hexBoard = null;
            } else {
                // Use legacy hexagonal board
                this.hexBoard = new HexGameBoard();
                this.enhancedHexBoard = null;
            }
            this.tiles = null;
            this.buildings = null;
            this.roads = null;
        } else {
            // Use traditional square board
            this.hexBoard = null;
            this.enhancedHexBoard = null;
            this.tiles = new TerrainTile[BOARD_SIZE][BOARD_SIZE];
            this.buildings = new HashMap<>();
            this.roads = new ArrayList<>();
            initializeBoard();
        }
    }
    
    private void initializeBoard() {
        // Define the standard CATAN terrain distribution
        List<TerrainType> terrainTypes = Arrays.asList(
            TerrainType.FOREST, TerrainType.FOREST, TerrainType.FOREST, TerrainType.FOREST,
            TerrainType.HILLS, TerrainType.HILLS, TerrainType.HILLS,
            TerrainType.PASTURE, TerrainType.PASTURE, TerrainType.PASTURE, TerrainType.PASTURE,
            TerrainType.FIELDS, TerrainType.FIELDS, TerrainType.FIELDS, TerrainType.FIELDS,
            TerrainType.MOUNTAINS, TerrainType.MOUNTAINS, TerrainType.MOUNTAINS,
            TerrainType.DESERT
        );
        
        // Number tokens (excluding 7 and matching CATAN distribution)
        List<Integer> numbers = Arrays.asList(
            2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12
        );
        
        // Shuffle for random board generation
        Collections.shuffle(terrainTypes);
        Collections.shuffle(numbers);
        
        int terrainIndex = 0;
        int numberIndex = 0;
        
        // Fill the 5x5 board (25 tiles total, but we only use 19 for standard CATAN)
        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                // Skip corner tiles to create a more CATAN-like shape
                if (isValidTilePosition(x, y) && terrainIndex < terrainTypes.size()) {
                    TerrainType terrain = terrainTypes.get(terrainIndex++);
                    int numberToken = 0;
                    
                    if (terrain != TerrainType.DESERT && numberIndex < numbers.size()) {
                        numberToken = numbers.get(numberIndex++);
                    }
                    
                    tiles[x][y] = new TerrainTile(terrain, numberToken, x, y);
                    
                    // Set initial robber position on desert
                    if (terrain == TerrainType.DESERT) {
                        robberX = x;
                        robberY = y;
                    }
                }
            }
        }
    }
    
    private boolean isValidTilePosition(int x, int y) {
        // Create a diamond-like shape similar to CATAN board
        int center = BOARD_SIZE / 2;
        int distance = Math.abs(x - center) + Math.abs(y - center);
        return distance <= 2; // This creates a diamond shape with 13 tiles
    }
    
    public TerrainTile getTile(int x, int y) {
        if (useHexBoard) {
            if (useEnhanced) {
                return enhancedHexBoard.getTile(x, y);
            } else {
                return hexBoard.getTile(x, y);
            }
        }
        
        if (x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE) {
            return tiles[x][y];
        }
        return null;
    }
    
    public boolean canPlaceBuilding(int x, int y, Player player) {
        if (useHexBoard) {
            if (useEnhanced) {
                return enhancedHexBoard.canPlaceBuilding(x, y, player);
            } else {
                return hexBoard.canPlaceBuilding(x, y, player);
            }
        }
        
        // Check if position is valid and not occupied
        if (!isValidBuildingPosition(x, y) || buildings.containsKey(x + "," + y)) {
            return false;
        }
        
        // Check if player has an adjacent road (after initial placement)
        return hasAdjacentRoad(x, y, player) || getTotalBuildings() < 8; // First 2 rounds
    }
    
    public boolean canPlaceRoad(int startX, int startY, int endX, int endY, Player player) {
        if (useHexBoard) {
            if (useEnhanced) {
                return enhancedHexBoard.canPlaceRoad(startX, startY, endX, endY, player);
            } else {
                return hexBoard.canPlaceRoad(startX, startY, endX, endY, player);
            }
        }
        
        // Check if positions are valid and adjacent
        if (!isValidBuildingPosition(startX, startY) || !isValidBuildingPosition(endX, endY)) {
            return false;
        }
        
        if (!areAdjacent(startX, startY, endX, endY)) {
            return false;
        }
        
        // Check if road already exists
        for (Road road : roads) {
            if ((road.getStartX() == startX && road.getStartY() == startY && 
                 road.getEndX() == endX && road.getEndY() == endY) ||
                (road.getStartX() == endX && road.getStartY() == endY && 
                 road.getEndX() == startX && road.getEndY() == startY)) {
                return false;
            }
        }
        
        // Check if player has adjacent building or road
        return hasAdjacentBuilding(startX, startY, player) || hasAdjacentBuilding(endX, endY, player) ||
               hasAdjacentRoad(startX, startY, player) || hasAdjacentRoad(endX, endY, player) ||
               roads.size() < 8; // First 2 rounds
    }
    
    public boolean placeBuilding(Building.Type type, int x, int y, Player player) {
        if (useHexBoard) {
            if (useEnhanced) {
                return enhancedHexBoard.placeBuilding(type, x, y, player);
            } else {
                return hexBoard.placeBuilding(type, x, y, player);
            }
        }
        
        if (canPlaceBuilding(x, y, player)) {
            buildings.put(x + "," + y, new Building(type, player, x, y));
            return true;
        }
        return false;
    }
    
    public boolean placeRoad(int startX, int startY, int endX, int endY, Player player) {
        if (useHexBoard) {
            if (useEnhanced) {
                return enhancedHexBoard.placeRoad(startX, startY, endX, endY, player);
            } else {
                return hexBoard.placeRoad(startX, startY, endX, endY, player);
            }
        }
        
        if (canPlaceRoad(startX, startY, endX, endY, player)) {
            roads.add(new Road(player, startX, startY, endX, endY));
            return true;
        }
        return false;
    }
    
    public void moveRobber(int x, int y) {
        if (useHexBoard) {
            if (useEnhanced) {
                enhancedHexBoard.moveRobber(x, y);
            } else {
                hexBoard.moveRobber(x, y);
            }
            return;
        }
        
        // Remove robber from current position
        TerrainTile currentTile = getTile(robberX, robberY);
        if (currentTile != null) {
            currentTile.setRobber(false);
        }
        
        // Place robber on new position
        TerrainTile newTile = getTile(x, y);
        if (newTile != null) {
            newTile.setRobber(true);
            robberX = x;
            robberY = y;
        }
    }
    
    public List<Building> getBuildingsAdjacentToTile(int tileX, int tileY) {
        if (useHexBoard) {
            if (useEnhanced) {
                return enhancedHexBoard.getBuildingsAdjacentToTile(tileX, tileY);
            } else {
                return hexBoard.getBuildingsAdjacentToTile(tileX, tileY);
            }
        }
        
        List<Building> adjacentBuildings = new ArrayList<>();
        
        // Check all possible building positions around the tile
        for (int dx = 0; dx <= 1; dx++) {
            for (int dy = 0; dy <= 1; dy++) {
                Building building = buildings.get((tileX + dx) + "," + (tileY + dy));
                if (building != null) {
                    adjacentBuildings.add(building);
                }
            }
        }
        
        return adjacentBuildings;
    }
    
    public boolean upgradeToCity(int x, int y, Player player) {
        if (useHexBoard) {
            if (useEnhanced) {
                return enhancedHexBoard.upgradeToCity(x, y, player);
            } else {
                return hexBoard.upgradeToCity(x, y, player);
            }
        }
        
        String key = x + "," + y;
        Building existing = buildings.get(key);
        
        if (existing != null && existing.getOwner() == player && 
            existing.getType() == Building.Type.SETTLEMENT) {
            buildings.put(key, new Building(Building.Type.CITY, player, x, y));
            return true;
        }
        return false;
    }
    
    public int getTotalBuildings() {
        if (useHexBoard) {
            if (useEnhanced) {
                return enhancedHexBoard.getTotalBuildings();
            } else {
                return hexBoard.getTotalBuildings();
            }
        }
        return buildings.size();
    }
    
    public Collection<Building> getBuildings() {
        if (useHexBoard) {
            if (useEnhanced) {
                return enhancedHexBoard.getBuildings();
            } else {
                return hexBoard.getBuildings();
            }
        }
        return buildings.values();
    }
    
    public List<Road> getRoads() {
        if (useHexBoard) {
            if (useEnhanced) {
                return new ArrayList<>(enhancedHexBoard.getRoads());
            } else {
                return hexBoard.getRoads();
            }
        }
        return roads;
    }
    
    public int getRobberX() {
        if (useHexBoard) {
            if (useEnhanced) {
                return enhancedHexBoard.getRobberX();
            } else {
                return hexBoard.getRobberX();
            }
        }
        return robberX;
    }
    
    public int getRobberY() {
        if (useHexBoard) {
            if (useEnhanced) {
                return enhancedHexBoard.getRobberY();
            } else {
                return hexBoard.getRobberY();
            }
        }
        return robberY;
    }
    
    // Hexagonal board specific methods
    public boolean isHexagonal() {
        return useHexBoard;
    }
    
    public boolean isEnhanced() {
        return useEnhanced;
    }
    
    public HexGameBoard getHexBoard() {
        return hexBoard;
    }
    
    public EnhancedHexGameBoard getEnhancedHexBoard() {
        return enhancedHexBoard;
    }
    
    // Helper methods for square board (when not using hexagonal)
    private boolean isValidBuildingPosition(int x, int y) {
        return x >= 0 && x <= BOARD_SIZE && y >= 0 && y <= BOARD_SIZE;
    }
    
    private boolean areAdjacent(int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x1 - x2);
        int dy = Math.abs(y1 - y2);
        return (dx == 1 && dy == 0) || (dx == 0 && dy == 1);
    }
    
    private boolean hasAdjacentRoad(int x, int y, Player player) {
        if (useHexBoard) return false; // Handled by hexBoard
        return roads.stream().anyMatch(road -> 
            road.getOwner() == player && road.connectsTo(x, y));
    }
    
    private boolean hasAdjacentBuilding(int x, int y, Player player) {
        if (useHexBoard) return false; // Handled by hexBoard
        Building building = buildings.get(x + "," + y);
        return building != null && building.getOwner() == player;
    }
    
    public TerrainTile[][] getTiles() {
        return tiles;
    }
}
