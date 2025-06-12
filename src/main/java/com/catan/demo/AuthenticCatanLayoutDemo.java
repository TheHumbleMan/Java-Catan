package com.catan.demo;

import java.util.Collection;

import com.catan.model.EnhancedHexGameBoard;
import com.catan.model.HexCoordinate;
import com.catan.model.HexGameBoard;
import com.catan.model.TerrainTile;

/**
 * Demo to showcase the authentic CATAN 5-row layout (3-4-5-4-3).
 */
public class AuthenticCatanLayoutDemo {
    public static void main(String[] args) {
        System.out.println("=== AUTHENTIC CATAN 5-ROW LAYOUT DEMO ===\n");
        
        // Create both boards to compare
        HexGameBoard legacyBoard = new HexGameBoard();
        EnhancedHexGameBoard enhancedBoard = new EnhancedHexGameBoard();
        
        System.out.println("1. LEGACY BOARD LAYOUT:");
        visualizeBoardLayout(legacyBoard.getAllTiles());
        
        System.out.println("\n2. ENHANCED BOARD LAYOUT:");
        visualizeBoardLayout(enhancedBoard.getAllTiles());
        
        System.out.println("\n3. ENHANCED BOARD STATISTICS:");
        System.out.println("   - Valid vertices: " + enhancedBoard.getValidVertices().size());
        System.out.println("   - Valid edges: " + enhancedBoard.getValidEdges().size());
        System.out.println("   - Hex tiles: " + enhancedBoard.getAllTiles().size());
        
        // Test vertex/edge calculations
        System.out.println("\n4. SAMPLE VERTEX COORDINATES (first 10):");
        enhancedBoard.getValidVertices().stream()
            .limit(10)
            .forEach(vertex -> {
                System.out.println("   " + vertex + " -> Pixel: " + vertex.toPixel(64.0, 400, 300));
            });
        
        System.out.println("\n5. SAMPLE EDGE COORDINATES (first 10):");
        enhancedBoard.getValidEdges().stream()
            .limit(10)
            .forEach(edge -> {
                System.out.println("   " + edge + " -> Pixel: " + edge.toPixel(64.0, 400, 300) + 
                                 ", Rotation: " + Math.toDegrees(edge.getRotationAngle(64.0, 400, 300)) + "°");
            });
        
        System.out.println("\n=== LAYOUT VERIFICATION ===");
        System.out.println("Row counts (should be 3-4-5-4-3):");
        verifyRowCounts(enhancedBoard.getAllTiles());
    }
    
    private static void visualizeBoardLayout(Collection<TerrainTile> tiles) {
        System.out.println("   Hex Coordinates (q,r) by row:");
        
        // Group by row (r coordinate)
        for (int r = -2; r <= 2; r++) {
            final int currentR = r; // Make effectively final for lambda
            System.out.print("   Row " + (r + 3) + " (r=" + r + "): ");
            tiles.stream()
                .filter(tile -> tile.getHexCoordinate().getR() == currentR)
                .sorted((a, b) -> Integer.compare(a.getHexCoordinate().getQ(), b.getHexCoordinate().getQ()))
                .forEach(tile -> {
                    HexCoordinate pos = tile.getHexCoordinate();
                    System.out.print("(" + pos.getQ() + "," + pos.getR() + ") ");
                });
            System.out.println();
        }
    }
    
    private static void verifyRowCounts(Collection<TerrainTile> tiles) {
        int[] expectedCounts = {3, 4, 5, 4, 3}; // 3-4-5-4-3 pattern
        
        for (int r = -2; r <= 2; r++) {
            final int currentR = r; // Make effectively final for lambda
            int rowIndex = r + 2; // Convert r to array index (0-4)
            long actualCount = tiles.stream()
                .filter(tile -> tile.getHexCoordinate().getR() == currentR)
                .count();
            
            System.out.println("   Row " + (r + 3) + " (r=" + r + "): " + actualCount + 
                             " hexagons (expected: " + expectedCounts[rowIndex] + ")");
        }
    }
}
