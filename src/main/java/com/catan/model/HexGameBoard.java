package com.catan.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Hexagonal implementation of the CATAN game board.
 * Uses proper hexagonal coordinates and CATAN standard layout.
 */
public class HexGameBoard {
    // Standard CATAN hex board layout (19 tiles) - Authentic 5-row pattern: 3-4-5-4-3
    private static final List<HexCoordinate> STANDARD_HEX_POSITIONS = Arrays.asList(
        // Row 1 (top, 3 hexagons): r = -2
        new HexCoordinate(-1, -2), new HexCoordinate(0, -2), new HexCoordinate(1, -2),
        
        // Row 2 (4 hexagons): r = -1  
        new HexCoordinate(-2, -1), new HexCoordinate(-1, -1), new HexCoordinate(0, -1), new HexCoordinate(1, -1),
        
        // Row 3 (center, 5 hexagons): r = 0
        new HexCoordinate(-2, 0), new HexCoordinate(-1, 0), new HexCoordinate(0, 0), new HexCoordinate(1, 0), new HexCoordinate(2, 0),
        
        // Row 4 (4 hexagons): r = 1
        new HexCoordinate(-2, 1), new HexCoordinate(-1, 1), new HexCoordinate(0, 1), new HexCoordinate(1, 1),
        
        // Row 5 (bottom, 3 hexagons): r = 2
        new HexCoordinate(-1, 2), new HexCoordinate(0, 2), new HexCoordinate(1, 2)
    );
    
    private final Map<HexCoordinate, TerrainTile> hexTiles;
    private final Map<String, Building> buildings; // Key: "q,r"
    private final List<Road> roads;
    private HexCoordinate robberPosition;
    
    public HexGameBoard() {
        hexTiles = new HashMap<>();
        buildings = new HashMap<>();
        roads = new ArrayList<>();
        initializeHexBoard();
    }
    
    private void initializeHexBoard() {
        // Define the standard CATAN terrain distribution
        List<TerrainType> terrainTypes = new ArrayList<>(Arrays.asList(
            TerrainType.FOREST, TerrainType.FOREST, TerrainType.FOREST, TerrainType.FOREST,
            TerrainType.HILLS, TerrainType.HILLS, TerrainType.HILLS,
            TerrainType.PASTURE, TerrainType.PASTURE, TerrainType.PASTURE, TerrainType.PASTURE,
            TerrainType.FIELDS, TerrainType.FIELDS, TerrainType.FIELDS, TerrainType.FIELDS,
            TerrainType.MOUNTAINS, TerrainType.MOUNTAINS, TerrainType.MOUNTAINS,
            TerrainType.DESERT
        ));
        
        // Number tokens (excluding 7 and matching CATAN distribution)
        List<Integer> numbers = new ArrayList<>(Arrays.asList(
            2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12
        ));
        
        // Shuffle for random board generation
        Collections.shuffle(terrainTypes);
        Collections.shuffle(numbers);
        
        int numberIndex = 0;
        
        // Place tiles on hexagonal positions
        for (int i = 0; i < STANDARD_HEX_POSITIONS.size() && i < terrainTypes.size(); i++) {
            HexCoordinate pos = STANDARD_HEX_POSITIONS.get(i);
            TerrainType terrain = terrainTypes.get(i);
            int numberToken = 0;
            
            if (terrain != TerrainType.DESERT && numberIndex < numbers.size()) {
                numberToken = numbers.get(numberIndex++);
            }
            
            TerrainTile tile = new TerrainTile(terrain, numberToken, pos);
            hexTiles.put(pos, tile);
            
            // Set initial robber position on desert
            if (terrain == TerrainType.DESERT) {
                robberPosition = pos;
            }
        }
    }
    
    public TerrainTile getHexTile(HexCoordinate hexCoord) {
        return hexTiles.get(hexCoord);
    }
    
    public TerrainTile getTile(int x, int y) {
        // Legacy support - convert x,y to hex coordinate
        return getHexTile(new HexCoordinate(x, y));
    }
    
    public Collection<TerrainTile> getAllTiles() {
        return hexTiles.values();
    }
    
    public boolean canPlaceBuilding(HexCoordinate hexCoord, Player player) {
        String key = hexCoord.getQ() + "," + hexCoord.getR();
        
        // Check if position is not occupied
        if (buildings.containsKey(key)) {
            return false;
        }
        
        // Check if player has an adjacent road (after initial placement)
        return hasAdjacentRoad(hexCoord, player) || getTotalBuildings() < 8; // First 2 rounds
    }
    
    public boolean canPlaceBuilding(int x, int y, Player player) {
        return canPlaceBuilding(new HexCoordinate(x, y), player);
    }
    
    public boolean canPlaceRoad(HexCoordinate start, HexCoordinate end, Player player) {
        // Check if positions are adjacent
        if (start.distanceTo(end) != 1) {
            return false;
        }
        
        // Check if road already exists
        for (Road road : roads) {
            HexCoordinate roadStart = new HexCoordinate(road.getStartX(), road.getStartY());
            HexCoordinate roadEnd = new HexCoordinate(road.getEndX(), road.getEndY());
            
            if ((roadStart.equals(start) && roadEnd.equals(end)) ||
                (roadStart.equals(end) && roadEnd.equals(start))) {
                return false;
            }
        }
        
        // Check if player has adjacent building or road
        return hasAdjacentBuilding(start, player) || hasAdjacentBuilding(end, player) ||
               hasAdjacentRoad(start, player) || hasAdjacentRoad(end, player) ||
               roads.size() < 8; // First 2 rounds
    }
    
    public boolean canPlaceRoad(int startX, int startY, int endX, int endY, Player player) {
        return canPlaceRoad(new HexCoordinate(startX, startY), new HexCoordinate(endX, endY), player);
    }
    
    private boolean hasAdjacentRoad(HexCoordinate hexCoord, Player player) {
        return roads.stream().anyMatch(road -> 
            road.getOwner() == player && 
            (road.connectsTo(hexCoord.getQ(), hexCoord.getR())));
    }
    
    private boolean hasAdjacentBuilding(HexCoordinate hexCoord, Player player) {
        String key = hexCoord.getQ() + "," + hexCoord.getR();
        Building building = buildings.get(key);
        return building != null && building.getOwner() == player;
    }
    
    public boolean placeBuilding(Building.Type type, HexCoordinate hexCoord, Player player) {
        if (canPlaceBuilding(hexCoord, player)) {
            String key = hexCoord.getQ() + "," + hexCoord.getR();
            buildings.put(key, new Building(type, player, hexCoord.getQ(), hexCoord.getR()));
            return true;
        }
        return false;
    }
    
    public boolean placeBuilding(Building.Type type, int x, int y, Player player) {
        return placeBuilding(type, new HexCoordinate(x, y), player);
    }
    
    public boolean placeRoad(HexCoordinate start, HexCoordinate end, Player player) {
        if (canPlaceRoad(start, end, player)) {
            roads.add(new Road(player, start.getQ(), start.getR(), end.getQ(), end.getR()));
            return true;
        }
        return false;
    }
    
    public boolean placeRoad(int startX, int startY, int endX, int endY, Player player) {
        return placeRoad(new HexCoordinate(startX, startY), new HexCoordinate(endX, endY), player);
    }
    
    public void moveRobber(HexCoordinate newPosition) {
        // Remove robber from current position
        TerrainTile currentTile = hexTiles.get(robberPosition);
        if (currentTile != null) {
            currentTile.setRobber(false);
        }
        
        // Place robber on new position
        TerrainTile newTile = hexTiles.get(newPosition);
        if (newTile != null) {
            newTile.setRobber(true);
            robberPosition = newPosition;
        }
    }
    
    public void moveRobber(int x, int y) {
        moveRobber(new HexCoordinate(x, y));
    }
    
    public List<Building> getBuildingsAdjacentToTile(HexCoordinate hexCoord) {
        List<Building> adjacentBuildings = new ArrayList<>();
        
        // Check all neighboring hex positions for buildings
        HexCoordinate[] neighbors = hexCoord.getNeighbors();
        for (HexCoordinate neighbor : neighbors) {
            String key = neighbor.getQ() + "," + neighbor.getR();
            Building building = buildings.get(key);
            if (building != null) {
                adjacentBuildings.add(building);
            }
        }
        
        return adjacentBuildings;
    }
    
    public List<Building> getBuildingsAdjacentToTile(int tileX, int tileY) {
        return getBuildingsAdjacentToTile(new HexCoordinate(tileX, tileY));
    }
    
    public boolean upgradeToCity(HexCoordinate hexCoord, Player player) {
        String key = hexCoord.getQ() + "," + hexCoord.getR();
        Building existing = buildings.get(key);
        
        if (existing != null && existing.getOwner() == player && 
            existing.getType() == Building.Type.SETTLEMENT) {
            buildings.put(key, new Building(Building.Type.CITY, player, hexCoord.getQ(), hexCoord.getR()));
            return true;
        }
        return false;
    }
    
    public boolean upgradeToCity(int x, int y, Player player) {
        return upgradeToCity(new HexCoordinate(x, y), player);
    }
    
    public int getTotalBuildings() {
        return buildings.size();
    }
    
    public Collection<Building> getBuildings() {
        return buildings.values();
    }
    
    public List<Road> getRoads() {
        return roads;
    }
    
    public HexCoordinate getRobberPosition() {
        return robberPosition;
    }
    
    public int getRobberX() {
        return robberPosition.getQ();
    }
    
    public int getRobberY() {
        return robberPosition.getR();
    }
    
    /**
     * Get all hex positions used on the board.
     */
    public Set<HexCoordinate> getHexPositions() {
        return hexTiles.keySet();
    }
    
    /**
     * Get the center hex coordinate for board display.
     */
    public HexCoordinate getCenterHex() {
        return new HexCoordinate(0, 0);
    }
}
