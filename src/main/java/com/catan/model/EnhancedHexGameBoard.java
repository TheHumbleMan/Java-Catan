package com.catan.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Enhanced hexagonal implementation of the CATAN game board using vertex/edge coordinates.
 * This ensures exactly one building spot per intersection and roads exactly between settlements.
 */
public class EnhancedHexGameBoard {
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
    private final Map<VertexCoordinate, Building> buildings;
    private final Map<EdgeCoordinate, Road> roads;
    private HexCoordinate robberPosition;
    
    // Cache of all valid vertices and edges on the board
    private Set<VertexCoordinate> validVertices;
    private Set<EdgeCoordinate> validEdges;
    
    public EnhancedHexGameBoard() {
        hexTiles = new HashMap<>();
        buildings = new HashMap<>();
        roads = new HashMap<>();
        initializeHexBoard();
        calculateValidVerticesAndEdges();
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
    
    /**
     * Calculate all valid vertices and edges on the board.
     * A vertex/edge is valid if it's adjacent to at least one hex tile on the board.
     */
    private void calculateValidVerticesAndEdges() {
        validVertices = new HashSet<>();
        validEdges = new HashSet<>();
        
        for (HexCoordinate hex : hexTiles.keySet()) {
            // Add all 6 vertices of this hex
            for (int direction = 0; direction < 6; direction++) {
                VertexCoordinate vertex = new VertexCoordinate(hex.getQ(), hex.getR(), direction);
                validVertices.add(vertex);
                
                // Add the edge from this vertex to the next vertex
                EdgeCoordinate edge = new EdgeCoordinate(hex.getQ(), hex.getR(), direction);
                validEdges.add(edge);
            }
        }
        
        // Remove duplicate vertices that represent the same intersection
        Set<VertexCoordinate> uniqueVertices = new HashSet<>();
        List<VertexCoordinate> vertexList = new ArrayList<>(validVertices);
        
        System.out.println("DEBUG: Starting with " + validVertices.size() + " vertices before deduplication");
        
        for (VertexCoordinate vertex : vertexList) {
            boolean isDuplicate = false;
            
            // Check if this vertex represents the same physical location as any existing vertex
            for (VertexCoordinate existing : uniqueVertices) {
                if (areVerticesAtSameLocation(vertex, existing)) {
                    isDuplicate = true;
                    break;
                }
            }
            
            if (!isDuplicate) {
                uniqueVertices.add(vertex);
            }
        }
        
        System.out.println("DEBUG: After deduplication: " + uniqueVertices.size() + " vertices");
        validVertices = uniqueVertices;
        
        // Similarly, remove duplicate edges
        Set<EdgeCoordinate> uniqueEdges = new HashSet<>();
        List<EdgeCoordinate> edgeList = new ArrayList<>(validEdges);
        
        for (EdgeCoordinate edge : edgeList) {
            boolean isDuplicate = false;
            
            // Check if this edge represents the same physical location as any existing edge
            for (EdgeCoordinate existing : uniqueEdges) {
                if (areEdgesAtSameLocation(edge, existing)) {
                    isDuplicate = true;
                    break;
                }
            }
            
            if (!isDuplicate) {
                uniqueEdges.add(edge);
            }
        }
        
        validEdges = uniqueEdges;
    }
    
    // Building placement methods
    public boolean canPlaceBuilding(VertexCoordinate vertex, Player player) {
        // Check if vertex is valid
        if (!validVertices.contains(vertex)) {
            return false;
        }
        
        // Check if position is not occupied
        if (buildings.containsKey(vertex)) {
            return false;
        }
        
        // Check distance rule: no buildings on adjacent vertices
        for (EdgeCoordinate adjacentEdge : vertex.getAdjacentEdges()) {
            if (validEdges.contains(adjacentEdge)) {
                VertexCoordinate[] connectedVertices = adjacentEdge.getConnectedVertices();
                for (VertexCoordinate connectedVertex : connectedVertices) {
                    if (!connectedVertex.equals(vertex) && buildings.containsKey(connectedVertex)) {
                        return false; // Adjacent vertex is occupied
                    }
                }
            }
        }
        
        // Check if player has an adjacent road (after initial placement)
        if (getTotalBuildings() >= 8) { // After initial setup phase
            return hasAdjacentRoad(vertex, player);
        }
        
        return true;
    }
    
    public boolean placeBuilding(Building.Type type, VertexCoordinate vertex, Player player) {
        if (canPlaceBuilding(vertex, player)) {
            buildings.put(vertex, new Building(type, player, vertex));
            return true;
        }
        return false;
    }
    
    // Road placement methods
    public boolean canPlaceRoad(EdgeCoordinate edge, Player player) {
        // Check if edge is valid
        if (!validEdges.contains(edge)) {
            return false;
        }
        
        // Check if road already exists
        if (roads.containsKey(edge)) {
            return false;
        }
        
        // Check if player has adjacent building or road
        if (getTotalRoads() >= 8) { // After initial setup phase
            VertexCoordinate[] connectedVertices = edge.getConnectedVertices();
            
            // Check for adjacent buildings
            for (VertexCoordinate vertex : connectedVertices) {
                Building building = buildings.get(vertex);
                if (building != null && building.getOwner() == player) {
                    return true;
                }
            }
            
            // Check for adjacent roads
            for (VertexCoordinate vertex : connectedVertices) {
                if (hasAdjacentRoad(vertex, player)) {
                    return true;
                }
            }
            
            return false;
        }
        
        return true; // During initial placement
    }
    
    public boolean placeRoad(EdgeCoordinate edge, Player player) {
        if (canPlaceRoad(edge, player)) {
            roads.put(edge, new Road(player, edge));
            return true;
        }
        return false;
    }
    
    // Utility methods
    private boolean hasAdjacentRoad(VertexCoordinate vertex, Player player) {
        for (EdgeCoordinate adjacentEdge : vertex.getAdjacentEdges()) {
            Road road = roads.get(adjacentEdge);
            if (road != null && road.getOwner() == player) {
                return true;
            }
        }
        return false;
    }
    
    public boolean upgradeToCity(VertexCoordinate vertex, Player player) {
        Building existing = buildings.get(vertex);
        
        if (existing != null && existing.getOwner() == player && 
            existing.getType() == Building.Type.SETTLEMENT) {
            buildings.put(vertex, new Building(Building.Type.CITY, player, vertex));
            return true;
        }
        return false;
    }
    
    // Robber methods
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
    
    // Resource production
    public List<Building> getBuildingsAdjacentToTile(HexCoordinate hexCoord) {
        List<Building> adjacentBuildings = new ArrayList<>();
        
        // Check all vertices of this hex
        for (int direction = 0; direction < 6; direction++) {
            VertexCoordinate vertex = new VertexCoordinate(hexCoord.getQ(), hexCoord.getR(), direction);
            Building building = buildings.get(vertex);
            if (building != null) {
                adjacentBuildings.add(building);
            }
        }
        
        return adjacentBuildings;
    }
    
    // Getters
    public Set<VertexCoordinate> getValidVertices() {
        return new HashSet<>(validVertices);
    }
    
    public Set<EdgeCoordinate> getValidEdges() {
        return new HashSet<>(validEdges);
    }
    
    public Collection<TerrainTile> getAllTiles() {
        return hexTiles.values();
    }
    
    public TerrainTile getHexTile(HexCoordinate hexCoord) {
        return hexTiles.get(hexCoord);
    }
    
    public Collection<Building> getBuildings() {
        return buildings.values();
    }
    
    public Collection<Road> getRoads() {
        return roads.values();
    }
    
    public int getTotalBuildings() {
        return buildings.size();
    }
    
    public int getTotalRoads() {
        return roads.size();
    }
    
    public HexCoordinate getRobberPosition() {
        return robberPosition;
    }
    
    public Set<HexCoordinate> getHexPositions() {
        return hexTiles.keySet();
    }
    
    // Legacy compatibility methods
    public boolean canPlaceBuilding(int x, int y, Player player) {
        // Try to find a vertex coordinate that matches the legacy coordinate
        for (VertexCoordinate vertex : validVertices) {
            if (vertex.getX() * 6 + vertex.getDirection() == x && vertex.getY() == y) {
                return canPlaceBuilding(vertex, player);
            }
        }
        return false;
    }
    
    public boolean placeBuilding(Building.Type type, int x, int y, Player player) {
        for (VertexCoordinate vertex : validVertices) {
            if (vertex.getX() * 6 + vertex.getDirection() == x && vertex.getY() == y) {
                return placeBuilding(type, vertex, player);
            }
        }
        return false;
    }
    
    public boolean canPlaceRoad(int startX, int startY, int endX, int endY, Player player) {
        // Try to find an edge coordinate that connects these legacy coordinates
        for (EdgeCoordinate edge : validEdges) {
            VertexCoordinate[] vertices = edge.getConnectedVertices();
            int start1 = vertices[0].getX() * 6 + vertices[0].getDirection();
            int end1 = vertices[1].getX() * 6 + vertices[1].getDirection();
            
            if ((start1 == startX && vertices[0].getY() == startY && 
                 end1 == endX && vertices[1].getY() == endY) ||
                (start1 == endX && vertices[0].getY() == endY &&
                 end1 == startX && vertices[1].getY() == startY)) {
                return canPlaceRoad(edge, player);
            }
        }
        return false;
    }
    
    public boolean placeRoad(int startX, int startY, int endX, int endY, Player player) {
        for (EdgeCoordinate edge : validEdges) {
            VertexCoordinate[] vertices = edge.getConnectedVertices();
            int start1 = vertices[0].getX() * 6 + vertices[0].getDirection();
            int end1 = vertices[1].getX() * 6 + vertices[1].getDirection();
            
            if ((start1 == startX && vertices[0].getY() == startY && 
                 end1 == endX && vertices[1].getY() == endY) ||
                (start1 == endX && vertices[0].getY() == endY &&
                 end1 == startX && vertices[1].getY() == startY)) {
                return placeRoad(edge, player);
            }
        }
        return false;
    }
    
    public List<Building> getBuildingsAdjacentToTile(int tileX, int tileY) {
        return getBuildingsAdjacentToTile(new HexCoordinate(tileX, tileY));
    }
    
    public TerrainTile getTile(int x, int y) {
        return getHexTile(new HexCoordinate(x, y));
    }
    
    public boolean upgradeToCity(int x, int y, Player player) {
        for (VertexCoordinate vertex : validVertices) {
            if (vertex.getX() * 6 + vertex.getDirection() == x && vertex.getY() == y) {
                return upgradeToCity(vertex, player);
            }
        }
        return false;
    }
    
    public void moveRobber(int x, int y) {
        moveRobber(new HexCoordinate(x, y));
    }
    
    public int getRobberX() {
        return robberPosition.getQ();
    }
    
    public int getRobberY() {
        return robberPosition.getR();
    }
    
    /**
     * Check if two vertices represent the same physical location.
     * This uses the mathematical properties of hexagonal grids to determine
     * if vertices from different hexes are actually the same intersection.
     */
    private boolean areVerticesAtSameLocation(VertexCoordinate v1, VertexCoordinate v2) {
        // If vertices are identical, they're the same
        if (v1.equals(v2)) {
            return true;
        }
        
        // Check if vertices share at least one hex and are at the same physical location
        List<HexCoordinate> hexes1 = v1.getAdjacentHexes();
        List<HexCoordinate> hexes2 = v2.getAdjacentHexes();
        
        // If they share a hex, they might be the same vertex
        boolean shareHex = false;
        for (HexCoordinate hex1 : hexes1) {
            for (HexCoordinate hex2 : hexes2) {
                if (hex1.equals(hex2)) {
                    shareHex = true;
                    break;
                }
            }
            if (shareHex) break;
        }
        
        if (shareHex) {
            // Use pixel distance as final check with larger tolerance
            HexCoordinate.Point2D pos1 = v1.toPixel(50.0, 0, 0);
            HexCoordinate.Point2D pos2 = v2.toPixel(50.0, 0, 0);
            double distance = Math.sqrt(Math.pow(pos1.x - pos2.x, 2) + Math.pow(pos1.y - pos2.y, 2));
            return distance < 5.0; // Larger tolerance for shared vertices
        }
        
        return false;
    }
    
    /**
     * Check if two edges represent the same physical location.
     */
    private boolean areEdgesAtSameLocation(EdgeCoordinate e1, EdgeCoordinate e2) {
        // If edges are identical, they're the same
        if (e1.equals(e2)) {
            return true;
        }
        
        // Use pixel distance check with tolerance
        HexCoordinate.Point2D pos1 = e1.toPixel(50.0, 0, 0);
        HexCoordinate.Point2D pos2 = e2.toPixel(50.0, 0, 0);
        double distance = Math.sqrt(Math.pow(pos1.x - pos2.x, 2) + Math.pow(pos1.y - pos2.y, 2));
        return distance < 5.0; // Tolerance for edge comparison
    }
}
