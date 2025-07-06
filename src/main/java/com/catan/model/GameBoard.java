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
 * Supports both legacy square grid and authentic CATAN hexagonal layout.
 */
public class GameBoard {
    public static final int BOARD_SIZE = 5; // 5x5 grid of terrain tiles for legacy board
    
    private final TerrainTile[][] tiles;
    private final Map<String, Building> buildings; // Key: "x,y"
    private final List<Road> roads;
    private int robberX;
    private int robberY;
    
    // Authentic CATAN board support
    public final AuthenticCatanBoard authenticBoard;
    public final boolean useAuthentic;
    
    public GameBoard() {
        this(true); // Default to authentic CATAN board
    }
    
    public GameBoard(boolean useAuthentic) {
        this.useAuthentic = useAuthentic;
        
        if (this.useAuthentic) {
            // Use new authentic CATAN board
            this.authenticBoard = new AuthenticCatanBoard();
            this.tiles = null;
            this.buildings = null;
            this.roads = null;
        } else {
            // Use legacy square board
            this.authenticBoard = null;
            this.tiles = new TerrainTile[BOARD_SIZE][BOARD_SIZE];
            this.buildings = new HashMap<>();
            this.roads = new ArrayList<>();
            this.robberX = 2;
            this.robberY = 2;
            
            // Initialize the legacy board
            initializeLegacyBoard();
        }
    }
    
    private void initializeLegacyBoard() {
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
        
        // Fill the 5x5 board
        for (int x = 0; x < BOARD_SIZE; x++) {
            for (int y = 0; y < BOARD_SIZE; y++) {
                if (terrainIndex < terrainTypes.size()) {
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
    
    public TerrainTile getTile(int x, int y) {
        if (useAuthentic) {
            return authenticBoard.getHexTile(new HexCoordinate(x, y));
        }
        
        if (x >= 0 && x < BOARD_SIZE && y >= 0 && y < BOARD_SIZE) {
            return tiles[x][y];
        }
        return null;
    }
    
    public boolean canPlaceBuilding(int x, int y, Player player) {
        if (useAuthentic) {
            // For authentic board, use vertex-based methods instead
            return false;
        }
        
        // Check if position is valid and not occupied
        if (!isValidBuildingPosition(x, y) || buildings.containsKey(x + "," + y)) {
            return false;
        }
        
        // Check if player has an adjacent road (after initial placement)
        return hasAdjacentRoad(x, y, player) || getTotalBuildings() < 8; // First 2 rounds
    }
    
    public boolean canPlaceRoad(int startX, int startY, int endX, int endY, Player player) {
        if (useAuthentic) {
            // For authentic board, use edge-based methods instead
            return false;
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
        if (useAuthentic) {
            return false; // Use vertex-based methods
        }
        
        if (canPlaceBuilding(x, y, player)) {
            buildings.put(x + "," + y, new Building(type, player, x, y));
            return true;
        }
        return false;
    }
    
    public boolean placeRoad(int startX, int startY, int endX, int endY, Player player) {
        if (useAuthentic) {
            return false; // Use edge-based methods
        }
        
        if (canPlaceRoad(startX, startY, endX, endY, player)) {
            roads.add(new Road(player, startX, startY, endX, endY));
            return true;
        }
        return false;
    }
    
    public void moveRobber(int x, int y) {
        if (useAuthentic) {
            authenticBoard.moveRobber(new HexCoordinate(x, y));
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
        if (useAuthentic) {
            return authenticBoard.getBuildingsAdjacentToTile(new HexCoordinate(tileX, tileY));
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
        if (useAuthentic) {
            return false; // Use vertex-based methods
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
        if (useAuthentic) {
            return authenticBoard.getTotalBuildings();
        }
        return buildings.size();
    }
    
    public Collection<Building> getBuildings() {
        if (useAuthentic) {
            return authenticBoard.getBuildings();
        }
        return buildings.values();
    }
    
    public List<Road> getRoads() {
        if (useAuthentic) {
            return new ArrayList<>(authenticBoard.getRoads());
        }
        return roads;
    }
    
    // === AUTHENTIC CATAN BOARD METHODS ===
    
    /**
     * Check if a building can be placed at a vertex coordinate (authentic board).
     */
    public boolean canPlaceBuilding(VertexCoordinate vertex, Player player) {
        if (useAuthentic) {
            return authenticBoard.canPlaceBuilding(vertex, player);
        }
        return false;
    }
    
    /**
     * Place a building at a vertex coordinate (authentic board).
     */
    public boolean placeBuilding(Building.Type type, VertexCoordinate vertex, Player player) {
        if (useAuthentic) {
            return authenticBoard.placeBuilding(type, vertex, player);
        }
        return false;
    }
    
    /**
     * Check if a road can be placed at an edge coordinate (authentic board).
     */
    public boolean canPlaceRoad(EdgeCoordinate edge, Player player) {
        if (useAuthentic) {
            return authenticBoard.canPlaceRoad(edge, player);
        }
        return false;
    }
    
    /**
     * Place a road at an edge coordinate (authentic board).
     */
    public boolean placeRoad(EdgeCoordinate edge, Player player) {
        if (useAuthentic) {
            return authenticBoard.placeRoad(edge, player);
        }
        return false;
    }
    
    /**
     * Get building at a vertex coordinate (authentic board).
     */
    public Building getBuildingAt(VertexCoordinate vertex) {
        if (useAuthentic) {
            return authenticBoard.getBuildingAt(vertex);
        }
        return null;
    }
    
    /**
     * Upgrade a settlement to a city at a vertex coordinate (authentic board).
     */
    public boolean upgradeToCity(VertexCoordinate vertex, Player player) {
        if (useAuthentic) {
            return authenticBoard.upgradeToCity(vertex, player);
        }
        return false;
    }
    
    /**
     * Get valid settlement positions for current game state.
     */
    /*
    public List<VertexCoordinate> getValidSettlementPositions() {
        if (useAuthentic) {
            return new ArrayList<>(authenticBoard.getValidVertices());
        }
        return new ArrayList<>();
    } */
    
    /**
     * Get valid road positions for current game state.
     */
    public List<EdgeCoordinate> getValidRoadPositions() {
        if (useAuthentic) {
            return new ArrayList<>(authenticBoard.getValidEdges());
        }
        return new ArrayList<>();
    }
    
    /**
     * Get the authentic CATAN board instance.
     */
    public AuthenticCatanBoard getAuthenticBoard() {
        return authenticBoard;
    }
    
    // Helper methods for legacy square board
    private boolean isValidBuildingPosition(int x, int y) {
        return x >= 0 && x <= BOARD_SIZE && y >= 0 && y <= BOARD_SIZE;
    }
    
    private boolean areAdjacent(int x1, int y1, int x2, int y2) {
        int dx = Math.abs(x1 - x2);
        int dy = Math.abs(y1 - y2);
        return (dx == 1 && dy == 0) || (dx == 0 && dy == 1);
    }
    
    private boolean hasAdjacentRoad(int x, int y, Player player) {
        return roads.stream().anyMatch(road -> 
            road.getOwner() == player && road.connectsTo(x, y));
    }
    
    private boolean hasAdjacentBuilding(int x, int y, Player player) {
        Building building = buildings.get(x + "," + y);
        return building != null && building.getOwner() == player;
    }
    
    public TerrainTile[][] getTiles() {
        return tiles;
    }
}
