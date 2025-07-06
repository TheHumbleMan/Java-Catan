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

    private static final List<HexCoordinate> STANDARD_HEX_POSITIONS = Arrays.asList(
        new HexCoordinate(-1, -2), new HexCoordinate(0, -2), new HexCoordinate(1, -2),
        new HexCoordinate(-2, -1), new HexCoordinate(-1, -1), new HexCoordinate(0, -1), new HexCoordinate(1, -1),
        new HexCoordinate(-2, 0), new HexCoordinate(-1, 0), new HexCoordinate(0, 0), new HexCoordinate(1, 0), new HexCoordinate(2, 0),
        new HexCoordinate(-2, 1), new HexCoordinate(-1, 1), new HexCoordinate(0, 1), new HexCoordinate(1, 1),
        new HexCoordinate(-1, 2), new HexCoordinate(0, 2), new HexCoordinate(1, 2)
    );

    private static final Set<HexCoordinate> STANDARD_HEX_SET = new HashSet<>(STANDARD_HEX_POSITIONS);

    // Konstanten
    private final double hexSize;
    private final double centerX;
    private final double centerY;

    private final Map<HexCoordinate, TerrainTile> hexTiles;
    private final Map<VertexCoordinate, Building> buildings;
    private final Map<EdgeCoordinate, Road> roads;
    private HexCoordinate robberPosition;

    private final Map<RoundedPoint2D, List<VertexCoordinate>> validVertices;
    private final Set<EdgeCoordinate> validEdges;

    // === Neuer Default-Konstruktor ===
    public AuthenticCatanBoard() {
        this(80.0, 580.0, 400.0); // Standardwerte, falls nichts übergeben wird
    }

    // === Dein bisheriger Konstruktor bleibt bestehen ===
    public AuthenticCatanBoard(double hexSize, double centerX, double centerY) {
        this.hexSize = hexSize;
        this.centerX = centerX;
        this.centerY = centerY;

        this.hexTiles = new HashMap<>();
        this.buildings = new HashMap<>();
        this.roads = new HashMap<>();

        initializeHexBoard();

        this.validVertices = new HashMap<>(calculateAuthenticVertices());
        this.validEdges = new HashSet<>(calculateAuthenticEdges());

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
    private Map<RoundedPoint2D, List<VertexCoordinate>> calculateAuthenticVertices() {
        Map<RoundedPoint2D, List<VertexCoordinate>> verticeMap = new HashMap<>();
        for (HexCoordinate hex : STANDARD_HEX_SET) {
            int q = hex.getQ();
            int r = hex.getR();

            for (int dir = 0; dir < 6; dir++) {
                VertexCoordinate vertex = new VertexCoordinate(q, r, dir);
                HexCoordinate.Point2D pos = vertex.toPixel(hexSize, centerX, centerY);
                RoundedPoint2D rounded = new RoundedPoint2D(pos.x, pos.y);

                System.out.println("Vertex dir=" + dir + " q: " + q + " r: " + r +
                        " x=" + pos.x + " roundedX=" + rounded.x +
                        " y=" + pos.y + " roundedY=" + rounded.y);
                verticeMap.computeIfAbsent(rounded, k -> new ArrayList<>()).add(vertex);
                                

                
            }
        }
        for (Map.Entry<RoundedPoint2D, List<VertexCoordinate>> entry : verticeMap.entrySet()) {
            RoundedPoint2D key = entry.getKey();
            List<VertexCoordinate> value = entry.getValue();
            System.out.println("Key X: " + key.getX() + "Key Y: " + key.getY() + ", Value: " + value);
        }

        return verticeMap;
    }








    
    /**
     * Berechnet die authentischen 72 Straßenpositionen eines CATAN-Boards.
     */
    /**
     * Berechnet die authentischen 72 Straßenpositionen (Edges) des CATAN-Boards.
     */
    private Set<EdgeCoordinate> calculateAuthenticEdges() {
        Set<RoundedPoint2D> worldKeys = new HashSet<>();
        Set<EdgeCoordinate> uniqueEdges = new HashSet<>();

        for (HexCoordinate hex : STANDARD_HEX_SET) {
            int q = hex.getQ();
            int r = hex.getR();

            for (int dir = 0; dir < 6; dir++) {
                EdgeCoordinate edge = new EdgeCoordinate(q, r, dir);
                HexCoordinate.Point2D pos = edge.toPixel(hexSize, centerX, centerY);
                RoundedPoint2D rounded = new RoundedPoint2D(pos.x, pos.y);

                if (!worldKeys.contains(rounded)) {
                    worldKeys.add(rounded);
                    uniqueEdges.add(edge);
                }
            }
        }

        System.out.println("Berechnete einzigartige Edges: " + uniqueEdges.size());

        // Erwartete Anzahl = 72 bei Standard CATAN Layout
        if (uniqueEdges.size() != 72) {
            System.err.println("Warnung: Anzahl der berechneten Straßen-Edges ist unerwartet: " + uniqueEdges.size());
        }

        return uniqueEdges;
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
       /* if (!validVertices.contains(vertex)) {
            return false;
        }*/
        
        // Position darf nicht besetzt sein
        if (buildings.containsKey(vertex)) {
            return false;
        }
        
        // Distanz-Regel: Keine Gebäude auf benachbarten Vertices
        for (EdgeCoordinate adjacentEdge : vertex.getAdjacentEdges()) {
        	//System.out.println("X wert:" + adjacentEdge.getX() + "Y wert:" + adjacentEdge.getY() + "dir wert:" + adjacentEdge.getDirection());
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
    
    public Map<RoundedPoint2D, List<VertexCoordinate>> getValidVertices() {
        return new HashMap<>(validVertices);
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
