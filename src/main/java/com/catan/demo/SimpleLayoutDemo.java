package com.catan.demo;

import java.util.Collection;

import com.catan.model.EnhancedHexGameBoard;
import com.catan.model.HexCoordinate;
import com.catan.model.TerrainTile;

/**
 * Simple demo to showcase the authentic CATAN 5-row layout (3-4-5-4-3).
 */
public class SimpleLayoutDemo {
    public static void main(String[] args) {
        System.out.println("=== AUTHENTIC CATAN 5-ROW LAYOUT DEMO ===\n");
        
        // Create enhanced board
        EnhancedHexGameBoard enhancedBoard = new EnhancedHexGameBoard();
        
        System.out.println("ENHANCED BOARD LAYOUT:");
        visualizeBoardLayout(enhancedBoard.getAllTiles());
        
        System.out.println("\nBOARD STATISTICS:");
        System.out.println("   - Valid vertices: " + enhancedBoard.getValidVertices().size());
        System.out.println("   - Valid edges: " + enhancedBoard.getValidEdges().size());
        System.out.println("   - Hex tiles: " + enhancedBoard.getAllTiles().size());
        
        System.out.println("\n=== LAYOUT VERIFICATION ===");
        System.out.println("Row counts (should be 3-4-5-4-3):");
        verifyRowCounts(enhancedBoard.getAllTiles());
    }
    
    private static void visualizeBoardLayout(Collection<TerrainTile> tiles) {
        System.out.println("   Hex Coordinates (q,r) by row:");
        
        // Group by row (r coordinate) - avoid lambda issues
        for (int r = -2; r <= 2; r++) {
            System.out.print("   Row " + (r + 3) + " (r=" + r + "): ");
            
            // Manual iteration to avoid lambda
            for (TerrainTile tile : tiles) {
                if (tile.getHexCoordinate().getR() == r) {
                    HexCoordinate pos = tile.getHexCoordinate();
                    System.out.print("(" + pos.getQ() + "," + pos.getR() + ") ");
                }
            }
            System.out.println();
        }
    }
    
    private static void verifyRowCounts(Collection<TerrainTile> tiles) {
        int[] expectedCounts = {3, 4, 5, 4, 3}; // 3-4-5-4-3 pattern
        
        for (int r = -2; r <= 2; r++) {
            int rowIndex = r + 2; // Convert r to array index (0-4)
            int actualCount = 0;
            
            // Manual count to avoid lambda
            for (TerrainTile tile : tiles) {
                if (tile.getHexCoordinate().getR() == r) {
                    actualCount++;
                }
            }
            
            System.out.println("   Row " + (r + 3) + " (r=" + r + "): " + actualCount + 
                             " hexagons (expected: " + expectedCounts[rowIndex] + ")");
        }
    }
}
