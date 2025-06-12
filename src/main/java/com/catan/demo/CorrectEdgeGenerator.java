package com.catan.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.catan.model.HexCoordinate;

/**
 * Generate the correct set of edges for CATAN board using mathematical approach
 */
public class CorrectEdgeGenerator {
    public static void main(String[] args) {
        System.out.println("=== Correct CATAN Edge Generation ===");
        
        // Standard CATAN hex positions
        List<HexCoordinate> hexPositions = Arrays.asList(
            // Row 1: 3 hexes
            new HexCoordinate(-1, -2), new HexCoordinate(0, -2), new HexCoordinate(1, -2),
            // Row 2: 4 hexes  
            new HexCoordinate(-2, -1), new HexCoordinate(-1, -1), new HexCoordinate(0, -1), new HexCoordinate(1, -1),
            // Row 3: 5 hexes
            new HexCoordinate(-2, 0), new HexCoordinate(-1, 0), new HexCoordinate(0, 0), new HexCoordinate(1, 0), new HexCoordinate(2, 0),
            // Row 4: 4 hexes
            new HexCoordinate(-2, 1), new HexCoordinate(-1, 1), new HexCoordinate(0, 1), new HexCoordinate(1, 1),
            // Row 5: 3 hexes
            new HexCoordinate(-1, 2), new HexCoordinate(0, 2), new HexCoordinate(1, 2)
        );
        
        System.out.println("Hex positions: " + hexPositions.size());
        
        // Generate all possible edges
        Set<String> uniqueEdges = new HashSet<>();
        
        for (HexCoordinate hex : hexPositions) {
            for (int direction = 0; direction < 6; direction++) {
                // Create edge key that represents the physical edge regardless of which hex defines it
                String edgeKey = getCanonicalEdgeKey(hex, direction, hexPositions);
                uniqueEdges.add(edgeKey);
            }
        }
        
        System.out.println("Unique edges found: " + uniqueEdges.size());
        
        // Also count vertices for comparison
        Set<String> uniqueVertices = new HashSet<>();
        
        for (HexCoordinate hex : hexPositions) {
            for (int direction = 0; direction < 6; direction++) {
                String vertexKey = getCanonicalVertexKey(hex, direction, hexPositions);
                uniqueVertices.add(vertexKey);
            }
        }
        
        System.out.println("Unique vertices found: " + uniqueVertices.size());
        
        // For verification, print some edges
        System.out.println("\nFirst 20 edges:");
        int count = 0;
        for (String edge : uniqueEdges) {
            if (count++ < 20) {
                System.out.println("  " + edge);
            }
        }
        
        if (uniqueEdges.size() == 72) {
            System.out.println("\nSUCCESS: Found exactly 72 edges!");
        } else {
            System.out.println("\nStill not 72 edges. Need to refine the algorithm.");
        }
    }
    
    /**
     * Generate a canonical key for an edge that's independent of which hex defines it
     */
    private static String getCanonicalEdgeKey(HexCoordinate hex, int direction, List<HexCoordinate> allHexes) {
        // Each edge connects two adjacent hexes. We need to find both hexes that share this edge
        // and create a consistent key regardless of which hex we started from.
        
        HexCoordinate neighbor = getNeighborHex(hex, direction);
        
        // Check if both hexes exist on the board
        boolean hexExists = allHexes.contains(hex);
        boolean neighborExists = allHexes.contains(neighbor);
        
        if (!hexExists) {
            return null; // Invalid edge
        }
        
        // Create canonical representation using the "smaller" hex coordinate
        HexCoordinate hex1, hex2;
        if (compareHexCoordinates(hex, neighbor) <= 0) {
            hex1 = hex;
            hex2 = neighbor;
        } else {
            hex1 = neighbor;
            hex2 = hex;
        }
        
        // Only include edges where at least one hex exists
        // (boundary edges have only one hex)
        if (neighborExists) {
            return hex1.getQ() + "," + hex1.getR() + "_" + hex2.getQ() + "," + hex2.getR();
        } else if (hexExists) {
            // Boundary edge - use hex + direction
            return hex.getQ() + "," + hex.getR() + "_boundary_" + direction;
        } else {
            return null; // Neither hex exists
        }
    }
    
    /**
     * Generate a canonical key for a vertex
     */
    private static String getCanonicalVertexKey(HexCoordinate hex, int direction, List<HexCoordinate> allHexes) {
        // A vertex is shared by up to 3 hexes. Find all hexes that share this vertex
        // and create a canonical key.
        
        List<HexCoordinate> adjacentHexes = getVertexAdjacentHexes(hex, direction);
        List<HexCoordinate> existingHexes = new ArrayList<>();
        
        for (HexCoordinate adjHex : adjacentHexes) {
            if (allHexes.contains(adjHex)) {
                existingHexes.add(adjHex);
            }
        }
        
        if (existingHexes.isEmpty()) {
            return null; // No valid hexes
        }
        
        // Sort to create canonical representation
        existingHexes.sort((a, b) -> compareHexCoordinates(a, b));
        
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < existingHexes.size(); i++) {
            if (i > 0) key.append("_");
            HexCoordinate h = existingHexes.get(i);
            key.append(h.getQ()).append(",").append(h.getR());
        }
        
        return key.toString();
    }
    
    /**
     * Get the neighboring hex in the given direction
     */
    private static HexCoordinate getNeighborHex(HexCoordinate hex, int direction) {
        int q = hex.getQ();
        int r = hex.getR();
        
        switch (direction) {
            case 0: return new HexCoordinate(q + 1, r - 1); // Top-right
            case 1: return new HexCoordinate(q + 1, r);     // Right  
            case 2: return new HexCoordinate(q, r + 1);     // Bottom-right
            case 3: return new HexCoordinate(q - 1, r + 1); // Bottom-left
            case 4: return new HexCoordinate(q - 1, r);     // Left
            case 5: return new HexCoordinate(q, r - 1);     // Top-left
            default: return hex;
        }
    }
    
    /**
     * Get all hexes that share a vertex at the given direction
     */
    private static List<HexCoordinate> getVertexAdjacentHexes(HexCoordinate hex, int direction) {
        List<HexCoordinate> hexes = new ArrayList<>();
        hexes.add(hex); // The hex itself
        
        int q = hex.getQ();
        int r = hex.getR();
        
        // Add the two neighboring hexes that share this vertex
        switch (direction) {
            case 0: // Top vertex
                hexes.add(new HexCoordinate(q, r - 1));
                hexes.add(new HexCoordinate(q - 1, r));
                break;
            case 1: // Top-right vertex
                hexes.add(new HexCoordinate(q + 1, r - 1));
                hexes.add(new HexCoordinate(q + 1, r));
                break;
            case 2: // Bottom-right vertex
                hexes.add(new HexCoordinate(q + 1, r));
                hexes.add(new HexCoordinate(q, r + 1));
                break;
            case 3: // Bottom vertex
                hexes.add(new HexCoordinate(q, r + 1));
                hexes.add(new HexCoordinate(q - 1, r + 1));
                break;
            case 4: // Bottom-left vertex
                hexes.add(new HexCoordinate(q - 1, r + 1));
                hexes.add(new HexCoordinate(q - 1, r));
                break;
            case 5: // Top-left vertex
                hexes.add(new HexCoordinate(q - 1, r));
                hexes.add(new HexCoordinate(q, r - 1));
                break;
        }
        
        return hexes;
    }
    
    /**
     * Compare hex coordinates for sorting
     */
    private static int compareHexCoordinates(HexCoordinate a, HexCoordinate b) {
        if (a.getQ() != b.getQ()) {
            return Integer.compare(a.getQ(), b.getQ());
        }
        return Integer.compare(a.getR(), b.getR());
    }
}
