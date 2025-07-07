package com.catan.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Präziser mathematischer Generator für exakt 54 Siedlungen und 72 Straßen eines CATAN-Boards.
 */
public class CatanGeometryCalculator {
    
    /**
     * Berechnet die exakten 54 Siedlungsplätze eines CATAN-Boards.
     */
    public static Set<VertexCoordinate> calculateAuthenticVertices() {
        Set<VertexCoordinate> vertices = new HashSet<>();
        
        // CATAN Board Hexagon-Positionen
        List<HexCoordinate> hexPositions = Arrays.asList(
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
        
        // Sammle alle Vertex-Positionen
        Map<String, VertexCoordinate> uniqueVertices = new HashMap<>();
        
        for (HexCoordinate hex : hexPositions) {
            for (int direction = 0; direction < 6; direction++) {
                VertexCoordinate vertex = new VertexCoordinate(hex.getQ(), hex.getR(), direction);
                
                // Berechne eindeutige Position
                RoundedPoint2D pos = vertex.toPixel(100.0, 0, 0);
                String key = Math.round(pos.x * 10) + "," + Math.round(pos.y * 10);
                
                uniqueVertices.put(key, vertex);
            }
        }
        
        return new HashSet<>(uniqueVertices.values());
    }
    
    /**
     * Berechnet die exakten 72 Straßenpositionen eines CATAN-Boards.
     */
    public static Set<EdgeCoordinate> calculateAuthenticEdges() {
        Set<EdgeCoordinate> edges = new HashSet<>();
        
        // CATAN Board Hexagon-Positionen
        List<HexCoordinate> hexPositions = Arrays.asList(
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
        
        // Sammle alle Edge-Positionen
        Map<String, EdgeCoordinate> uniqueEdges = new HashMap<>();
        
        for (HexCoordinate hex : hexPositions) {
            for (int direction = 0; direction < 6; direction++) {
                EdgeCoordinate edge = new EdgeCoordinate(hex.getQ(), hex.getR(), direction);
                
                // Berechne eindeutige Position
                RoundedPoint2D pos = edge.toPixel(100.0, 0, 0);
                String key = Math.round(pos.x * 10) + "," + Math.round(pos.y * 10);
                
                uniqueEdges.put(key, edge);
            }
        }
        
        return new HashSet<>(uniqueEdges.values());
    }
    
    /**
     * Teste die Berechnungen.
     */
    public static void main(String[] args) {
        System.out.println("=== CATAN GEOMETRIE KALKULATOR ===");
        
        Set<VertexCoordinate> vertices = calculateAuthenticVertices();
        Set<EdgeCoordinate> edges = calculateAuthenticEdges();
        
        System.out.println("Berechnete Vertex-Positionen: " + vertices.size());
        System.out.println("Berechnete Edge-Positionen: " + edges.size());
        
        if (vertices.size() == 54) {
            System.out.println("✓ Korrekte Anzahl Siedlungsplätze!");
        } else {
            System.out.println("✗ Falsche Anzahl Siedlungsplätze: " + vertices.size() + " statt 54");
        }
        
        if (edges.size() == 72) {
            System.out.println("✓ Korrekte Anzahl Straßen!");
        } else {
            System.out.println("✗ Falsche Anzahl Straßen: " + edges.size() + " statt 72");
        }
    }
}
