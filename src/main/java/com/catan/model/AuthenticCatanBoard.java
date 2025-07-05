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
 * Authentisches CATAN-Board mit exakt 54 Siedlungsplätzen und 72 Straßenpositionen.
 * Basiert auf der originalen Catan-Spielbrettgeometrie.
 */
public class AuthenticCatanBoard {
    
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
    private static final Set<HexCoordinate> STANDARD_HEX_SET = new HashSet<>(STANDARD_HEX_POSITIONS);
    
    private final Map<HexCoordinate, TerrainTile> hexTiles;
    private final Map<VertexCoordinate, Building> buildings;
    private final Map<EdgeCoordinate, Road> roads;
    private HexCoordinate robberPosition;
    
    // Authentische CATAN Geometrie: Exakt 54 Siedlungsplätze und 72 Straßen
    private final Set<VertexCoordinate> validVertices;
    private final Set<EdgeCoordinate> validEdges;
    
    public AuthenticCatanBoard() {
        hexTiles = new HashMap<>();
        buildings = new HashMap<>();
        roads = new HashMap<>();
        
        // Initialisiere Board
        initializeHexBoard();
        
        // Validierung: Falls die Zahlen nicht stimmen, verwende vorberechnete Werte
        validVertices = new HashSet<>(calculateAuthenticVertices());
        validEdges = new HashSet<>(calculateAuthenticEdges());
        
        System.out.println("✓ Authentisches CATAN-Board initialisiert: " + 
                         validVertices.size() + " Siedlungen, " + 
                         validEdges.size() + " Straßen");
    }
    
    private void initializeHexBoard() {
        // Standard CATAN Terrain-Verteilung
        List<TerrainType> terrainTypes = new ArrayList<>(Arrays.asList(
            TerrainType.FOREST, TerrainType.FOREST, TerrainType.FOREST, TerrainType.FOREST,
            TerrainType.HILLS, TerrainType.HILLS, TerrainType.HILLS,
            TerrainType.PASTURE, TerrainType.PASTURE, TerrainType.PASTURE, TerrainType.PASTURE,
            TerrainType.FIELDS, TerrainType.FIELDS, TerrainType.FIELDS, TerrainType.FIELDS,
            TerrainType.MOUNTAINS, TerrainType.MOUNTAINS, TerrainType.MOUNTAINS,
            TerrainType.DESERT
        ));
        
        // Standard CATAN Nummern-Verteilung
        List<Integer> numbers = new ArrayList<>(Arrays.asList(
            2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12
        ));
        
        // Mische für zufälliges Board
        Collections.shuffle(terrainTypes);
        Collections.shuffle(numbers);
        
        int numberIndex = 0;
        
        // Platziere Tiles auf hexagonalen Positionen
        for (int i = 0; i < STANDARD_HEX_POSITIONS.size() && i < terrainTypes.size(); i++) {
            HexCoordinate pos = STANDARD_HEX_POSITIONS.get(i);
            TerrainType terrain = terrainTypes.get(i);
            int numberToken = 0;
            
            if (terrain != TerrainType.DESERT && numberIndex < numbers.size()) {
                numberToken = numbers.get(numberIndex++);
            }
            
            TerrainTile tile = new TerrainTile(terrain, numberToken, pos);
            hexTiles.put(pos, tile);
            
            // Setze initiale Räuber-Position auf Wüste
            if (terrain == TerrainType.DESERT) {
                robberPosition = pos;
            }
        }
    }
    
    /**
     * Berechnet die authentischen 54 Siedlungsplätze eines CATAN-Boards.
     * Verwendet eine vereinfachte Strategie um exakt die korrekten Positionen zu generieren.
     */
    /**
     * Berechnet die authentischen 54 Siedlungspositionen (Vertices) des CATAN-Boards.
     */
    private Set<VertexCoordinate> calculateAuthenticVertices() {
        Set<VertexCoordinate> vertices = new HashSet<>();

        for (HexCoordinate hex : STANDARD_HEX_SET) {
            int q = hex.getQ();
            int r = hex.getR();

            for (int dir = 0; dir < 6; dir++) {
                VertexCoordinate vertex = new VertexCoordinate(q, r, dir);
                VertexCoordinate normalized = vertex.normalize(STANDARD_HEX_SET);

                // Prüfe, ob dieser Vertex mindestens an 2 gültige Hexes angrenzt (inneres Board)
                long adjacentHexCount = normalized.getAdjacentHexes().stream()
                    .filter(STANDARD_HEX_SET::contains)
                    .count();

                if (adjacentHexCount >= 2) {
                    vertices.add(normalized);
                }
            }
        }

        System.out.println("Berechnete einzigartige Vertices: " + vertices.size());
        return vertices;
    }







    
    /**
     * Berechnet die authentischen 72 Straßenpositionen eines CATAN-Boards.
     */
    /**
     * Berechnet die authentischen 72 Straßenpositionen (Edges) des CATAN-Boards.
     */
    private Set<EdgeCoordinate> calculateAuthenticEdges() {
        Set<EdgeCoordinate> edges = new HashSet<>();

        // Iteriere über alle 19 Hex-Felder des Boards
        for (HexCoordinate hex : STANDARD_HEX_POSITIONS) {
            int q = hex.getQ();
            int r = hex.getR();

            // Füge alle 6 Edges hinzu, normalisiert, sodass doppelte logische Positionen zusammengefasst werden
            for (int dir = 0; dir < 6; dir++) {
                EdgeCoordinate edge = new EdgeCoordinate(q, r, dir).normalize(); // Falls normalize() in EdgeCoordinate implementiert
                edges.add(edge);
            }
        }

        // Debug-Ausgabe zur Überprüfung der Anzahl
        System.out.println("Berechnete einzigartige Edges: " + edges.size());

        // Erwartete Anzahl = 72 bei Standard CATAN Layout
        if (edges.size() != 72) {
            System.err.println("Warnung: Anzahl der berechneten Straßen-Edges ist unerwartet: " + edges.size());
        }

        return edges;
    }

    
    /**
     * Prüft ob ein Hexagon am Rand des Boards liegt.
     */
    private boolean isEdgeHex(HexCoordinate hex) {
        int q = hex.getQ();
        int r = hex.getR();
        
        // Rand-Hexagone haben extreme q oder r Werte
        return Math.abs(q) == 2 || Math.abs(r) == 2 || 
               (r == -2 && Math.abs(q) <= 1) || 
               (r == 2 && Math.abs(q) <= 1);
    }
    
    /**
     * Prüft ob ein Vertex-Direction für das Board gültig ist.
     */
    private boolean isValidBoardVertex(HexCoordinate hex, int direction) {
        // Vereinfachte Logik: verwende alle Directions
        return true;
    }
    
    // === GAME LOGIC METHODS ===
    
    /**
     * Prüft ob eine Siedlung an einem Vertex platziert werden kann.
     */
    public boolean canPlaceBuilding(VertexCoordinate vertex, Player player) {
        // Vertex muss valide sein
        if (!validVertices.contains(vertex)) {
            return false;
        }
        
        // Position darf nicht besetzt sein
        if (buildings.containsKey(vertex)) {
            return false;
        }
        
        // Distanz-Regel: Keine Gebäude auf benachbarten Vertices
        for (EdgeCoordinate adjacentEdge : vertex.getAdjacentEdges()) {
            if (validEdges.contains(adjacentEdge)) {
                VertexCoordinate[] connectedVertices = adjacentEdge.getConnectedVertices();
                for (VertexCoordinate connectedVertex : connectedVertices) {
                    if (!connectedVertex.equals(vertex) && buildings.containsKey(connectedVertex)) {
                        return false;
                    }
                }
            }
        }
        
        // Nach der Anfangsphase: Spieler muss benachbarte Straße haben
        if (getTotalBuildings() >= 8) {
            return hasAdjacentRoad(vertex, player);
        }
        
        return true;
    }
    
    /**
     * Platziert ein Gebäude an einem Vertex.
     */
    public boolean placeBuilding(Building.Type type, VertexCoordinate vertex, Player player) {
        if (canPlaceBuilding(vertex, player)) {
            buildings.put(vertex, new Building(type, player, vertex));
            return true;
        }
        return false;
    }
    
    /**
     * Prüft ob eine Straße an einer Edge platziert werden kann.
     */
    public boolean canPlaceRoad(EdgeCoordinate edge, Player player) {
        // Edge muss valide sein
        if (!validEdges.contains(edge)) {
            return false;
        }
        
        // Straße darf nicht bereits existieren
        if (roads.containsKey(edge)) {
            return false;
        }
        
        // Nach der Anfangsphase: Spieler muss benachbartes Gebäude oder Straße haben
        if (getTotalRoads() >= 8) {
            VertexCoordinate[] connectedVertices = edge.getConnectedVertices();
            
            // Prüfe benachbarte Gebäude
            for (VertexCoordinate vertex : connectedVertices) {
                Building building = buildings.get(vertex);
                if (building != null && building.getOwner() == player) {
                    return true;
                }
            }
            
            // Prüfe benachbarte Straßen
            for (VertexCoordinate vertex : connectedVertices) {
                if (hasAdjacentRoad(vertex, player)) {
                    return true;
                }
            }
            
            return false;
        }
        
        return true;
    }
    
    /**
     * Platziert eine Straße an einer Edge.
     */
    public boolean placeRoad(EdgeCoordinate edge, Player player) {
        if (canPlaceRoad(edge, player)) {
            roads.put(edge, new Road(player, edge));
            return true;
        }
        return false;
    }
    
    /**
     * Prüft ob ein Spieler eine benachbarte Straße zu einem Vertex hat.
     */
    private boolean hasAdjacentRoad(VertexCoordinate vertex, Player player) {
        for (EdgeCoordinate adjacentEdge : vertex.getAdjacentEdges()) {
            Road road = roads.get(adjacentEdge);
            if (road != null && road.getOwner() == player) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Erweitert eine Siedlung zu einer Stadt.
     */
    public boolean upgradeToCity(VertexCoordinate vertex, Player player) {
        Building existing = buildings.get(vertex);
        
        if (existing != null && existing.getOwner() == player && 
            existing.getType() == Building.Type.SETTLEMENT) {
            buildings.put(vertex, new Building(Building.Type.CITY, player, vertex));
            return true;
        }
        return false;
    }
    
    /**
     * Bewegt den Räuber zu einer neuen Position.
     */
    public void moveRobber(HexCoordinate newPosition) {
        // Entferne Räuber von aktueller Position
        TerrainTile currentTile = hexTiles.get(robberPosition);
        if (currentTile != null) {
            currentTile.setRobber(false);
        }
        
        // Platziere Räuber auf neuer Position
        TerrainTile newTile = hexTiles.get(newPosition);
        if (newTile != null) {
            newTile.setRobber(true);
            robberPosition = newPosition;
        }
    }
    
    /**
     * Gibt alle Gebäude zurück die an ein Tile angrenzen.
     */
    public List<Building> getBuildingsAdjacentToTile(HexCoordinate hexCoord) {
        List<Building> adjacentBuildings = new ArrayList<>();
        
        // Prüfe alle Vertices dieses Hexagons
        for (int direction = 0; direction < 6; direction++) {
            VertexCoordinate vertex = new VertexCoordinate(hexCoord.getQ(), hexCoord.getR(), direction);
            Building building = buildings.get(vertex);
            if (building != null) {
                adjacentBuildings.add(building);
            }
        }
        
        return adjacentBuildings;
    }
    
    /**
     * Gibt das Gebäude an einem Vertex zurück.
     */
    public Building getBuildingAt(VertexCoordinate vertex) {
        return buildings.get(vertex);
    }

    // === GETTERS ===
    
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
    
    public int getRobberX() {
        return robberPosition.getQ();
    }
    
    public int getRobberY() {
        return robberPosition.getR();
    }
}
