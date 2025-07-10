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
    
 // CATAN-authentische Board-Layout-Konstanten
    private static final double HEX_RADIUS = 45; //davor 55
    private static final double BOARD_CENTER_X = 300.0;   //ursprünglich 400 WENN MAN ÄNDERN WILL CatanApplication WINDOWS_HEIGHT etc
    private static final double BOARD_CENTER_Y = 200.0;   //URSPRüNGLICH 350!!!
    
    // Siedlungs- und Straßen-Konstanten
    private static final double SETTLEMENT_SIZE = 8.0;
    private static final double ROAD_LENGTH = 20.0;
    private static final double ROAD_WIDTH = 4.0;

    private static final Set<HexCoordinate> STANDARD_HEX_SET = new HashSet<>(STANDARD_HEX_POSITIONS);

    // Konstanten
    private final double hexSize;
    private final double centerX;
    private final double centerY;

    private final Map<HexCoordinate, TerrainTile> hexTiles;
    private final Map<VertexCoordinate, Building> buildings;
    private final Map<EdgeCoordinate, Road> roads;
    private HexCoordinate robberPosition;

  //die obere sollte für euch nur wichtig sein, ist Ausgabe von validEdges()
    //gibt gibt map aus mit der man normalisieren kann validEdges.get(edge) -> normalisiert
    private final Map<EdgeCoordinate, EdgeCoordinate> normalizedEdgeMap;
    private final Map<RoundedPoint2D, List<EdgeCoordinate>> coordEdgeMap;
    private  final Map<RoundedPoint2D, EdgeCoordinate> normalizedCatanCoordMapEdge;
    
   //die obere sollte für euch nur wichtig sein, ist Ausgabe von validVertices()
    //gibt gibt map aus mit der man normalisieren kann validVertices.get(vertex) -> normalisiert
    private final Map<VertexCoordinate, VertexCoordinate> normalizedVerticeMap;
    private final Map<RoundedPoint2D, List<VertexCoordinate>> coordVerticeMap;
    private  final Map<RoundedPoint2D, VertexCoordinate> normalizedCatanCoordMap;

    // === Neuer Default-Konstruktor ===
    

    // === Dein bisheriger Konstruktor bleibt bestehen ===
    public AuthenticCatanBoard() {
        hexSize = this.HEX_RADIUS;
        centerX = this.BOARD_CENTER_X;
        centerY = this.BOARD_CENTER_Y;

        this.hexTiles = new HashMap<>();
        this.buildings = new HashMap<>();
        this.roads = new HashMap<>();
        //maps
        //54 rounded coords to 114 unnormalized catan coords
        this.coordVerticeMap = calculateAuthenticVertices();
        //114 catan coords to 54 normalized catan coords
        this.normalizedVerticeMap = createNormalizeVertexMap(coordVerticeMap);
        //54 rounded coords to 54 normalized catan coords
        this.normalizedCatanCoordMap = getNormalizedCatanCoordsHelper(coordVerticeMap);
        
        this.coordEdgeMap = calculateAuthenticEdges();
        this.normalizedEdgeMap = createNormalizeEdgeMap(coordEdgeMap);
        this.normalizedCatanCoordMapEdge = getNormalizedCatanCoordsHelperEdge(coordEdgeMap);
        
        initializeHexBoard();

        System.out.println("✓ Authentisches CATAN-Board initialisiert: " + 
                           normalizedVerticeMap.size() + " Siedlungen, " + 
                           normalizedEdgeMap.size() + " Siedlungsoptionen"); //eigentlich unnötige Abfrage
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
    //alle diese Map Sachen ignorieren, sorgt nur für dafür Vertice maps brauchbar zu initialisieren
    private Map<RoundedPoint2D, List<VertexCoordinate>> calculateAuthenticVertices() {
        Map<RoundedPoint2D, List<VertexCoordinate>> verticeMap = new HashMap<>();
        for (HexCoordinate hex : STANDARD_HEX_SET) {
            int q = hex.getQ();
            int r = hex.getR();

            for (int dir = 0; dir < 6; dir++) {
            	
                VertexCoordinate vertex = new VertexCoordinate(q, r, dir);
                RoundedPoint2D rounded = vertex.toPixel(hexSize, centerX, centerY);

                System.out.println("Vertex dir=" + dir + " q: " + q + " r: " + r +
                        " x=" + rounded.getX() + " roundedX=" + rounded.x +
                        " y=" + rounded.getY() + " roundedY=" + rounded.y);
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
    
    
    private Map<RoundedPoint2D, List<EdgeCoordinate>> calculateAuthenticEdges() {
        Map<RoundedPoint2D, List<EdgeCoordinate>> edgeMap = new HashMap<>();
        for (HexCoordinate hex : STANDARD_HEX_SET) {
            int q = hex.getQ();
            int r = hex.getR();

            for (int dir = 0; dir < 6; dir++) {
            	
                EdgeCoordinate edge = new EdgeCoordinate(q, r, dir);
                RoundedPoint2D rounded = edge.toPixel(hexSize, centerX, centerY);

                System.out.println("Vertex dir=" + dir + " q: " + q + " r: " + r +
                        " x=" + rounded.getX() + " roundedX=" + rounded.x +
                        " y=" + rounded.getY() + " roundedY=" + rounded.y);
                edgeMap.computeIfAbsent(rounded, k -> new ArrayList<>()).add(edge);
                                

                
            }
        }
        for (Map.Entry<RoundedPoint2D, List<EdgeCoordinate>> entry : edgeMap.entrySet()) {
            RoundedPoint2D key = entry.getKey();
            List<EdgeCoordinate> value = entry.getValue();
            System.out.println("Key X: " + key.getX() + "Key Y: " + key.getY() + ", Value: " + value);
        }

        return edgeMap;
    }
private Map<VertexCoordinate, VertexCoordinate> createNormalizeVertexMap(Map<RoundedPoint2D, List<VertexCoordinate>>oldMap){
	Map<VertexCoordinate, VertexCoordinate> verticeMap = new HashMap<>();
	for (Map.Entry<RoundedPoint2D, List<VertexCoordinate>> entry : oldMap.entrySet()) {
		for (VertexCoordinate element : entry.getValue()) {
			verticeMap.put(element, entry.getValue().get(0)); //nimmt immer das erste Element und mappt die anderen auf das
		}
	}
	for (Map.Entry<VertexCoordinate, VertexCoordinate> entry : verticeMap.entrySet()) {
	    System.out.println("Key: " + entry.getKey() + " -> Value: " + entry.getValue());
	    System.out.println("Size: " + verticeMap.size());	
}
	return verticeMap;
}
//creates normalizedEdgeMap, same as VerticeMap
private Map<EdgeCoordinate, EdgeCoordinate> createNormalizeEdgeMap(Map<RoundedPoint2D, List<EdgeCoordinate>> oldMap) {
    Map<EdgeCoordinate, EdgeCoordinate> edgeMap = new HashMap<>();
    for (Map.Entry<RoundedPoint2D, List<EdgeCoordinate>> entry : oldMap.entrySet()) {
        for (EdgeCoordinate element : entry.getValue()) {
            edgeMap.put(element, entry.getValue().get(0)); // nimmt immer das erste Element und mappt die anderen darauf
        }
    }
    for (Map.Entry<EdgeCoordinate, EdgeCoordinate> entry : edgeMap.entrySet()) {
        System.out.println("Key: " + entry.getKey() + " -> Value: " + entry.getValue());
        System.out.println("Size: " + edgeMap.size());
    }
    return edgeMap;
}

private Map<RoundedPoint2D, VertexCoordinate> getNormalizedCatanCoordsHelper(Map<RoundedPoint2D, List<VertexCoordinate>> oldMap) {
    Map<RoundedPoint2D, VertexCoordinate> verticeMap = new HashMap<>();
    for (Map.Entry<RoundedPoint2D, List<VertexCoordinate>> entry : oldMap.entrySet()) {
            verticeMap.put(entry.getKey(), entry.getValue().get(0)); // nimmt immer das erste Element und mappt die anderen darauf
            
    }
    return verticeMap;

}

private Map<RoundedPoint2D, EdgeCoordinate> getNormalizedCatanCoordsHelperEdge(Map<RoundedPoint2D, List<EdgeCoordinate>> oldMap) {
    Map<RoundedPoint2D, EdgeCoordinate> edgeMap = new HashMap<>();
    for (Map.Entry<RoundedPoint2D, List<EdgeCoordinate>> entry : oldMap.entrySet()) {
        edgeMap.put(entry.getKey(), entry.getValue().get(0)); // nimmt immer das erste Element und mappt die anderen darauf
    }
    return edgeMap;
}



//ECHTE GETTER FÜR FUNKTIONEN
public VertexCoordinate getNormalizedVertexCoordinate(VertexCoordinate vertex) {
	VertexCoordinate normalizedVertex = this.normalizedVerticeMap.get(vertex);
	return normalizedVertex;
}

public EdgeCoordinate getNormalizedEdgeCoordinate(EdgeCoordinate edge) {
    EdgeCoordinate normalizedEdge = this.normalizedEdgeMap.get(edge);
    return normalizedEdge;
}

public Map<RoundedPoint2D, VertexCoordinate> getNormalizedCatanCoordMap(){
	return normalizedCatanCoordMap;
}
public Map<RoundedPoint2D, EdgeCoordinate> getNormalizedCatanCoordMapEdge(){
	return normalizedCatanCoordMapEdge;
}

 //gibt für x und y wert die korrekten normalized catan coords an
public VertexCoordinate getNormalizedVertexCoordinate(int x, int y) {
	RoundedPoint2D point = new RoundedPoint2D(x, y);
	VertexCoordinate normalizedVertex = normalizedCatanCoordMap.get(point);
	return normalizedVertex;
}
public EdgeCoordinate getNormalizedEdgeCoordinate(int x, int y) {
	RoundedPoint2D point = new RoundedPoint2D(x, y);
	EdgeCoordinate normalizedEdge = normalizedCatanCoordMapEdge.get(point);
	return normalizedEdge;
}

    
    /**
     * Gibt das Gebäude an einem Vertex zurück.
     */
    public Building getBuildingAt(VertexCoordinate vertex) {
        return buildings.get(vertex);
    }

    // === GETTERS ===
    
    public Map<VertexCoordinate, VertexCoordinate> getValidVertices() {
        return new HashMap<>(normalizedVerticeMap);
    }
    
    public Map<EdgeCoordinate, EdgeCoordinate> getValidEdges() {
        return new HashMap<>(normalizedEdgeMap);
    }
    
    public Collection<TerrainTile> getAllTiles() {
        return hexTiles.values();
    }
    
    public TerrainTile getHexTile(HexCoordinate hexCoord) {
        return hexTiles.get(hexCoord);
    }
    
    public Map<VertexCoordinate, Building> getBuildings() {
        return buildings;
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
    public static double getHexRadius() {
        return HEX_RADIUS;
    }

    public static double getBoardCenterX() {
        return BOARD_CENTER_X;
    }

    public static double getBoardCenterY() {
        return BOARD_CENTER_Y;
    }

    public static double getSettlementSize() {
        return SETTLEMENT_SIZE;
    }

    public static double getRoadLength() {
        return ROAD_LENGTH;
    }

    public static double getRoadWidth() {
        return ROAD_WIDTH;
    }
}

