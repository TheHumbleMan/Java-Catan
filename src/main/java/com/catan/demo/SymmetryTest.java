package com.catan.demo;

import com.catan.model.EnhancedHexGameBoard;
import com.catan.model.HexCoordinate;
import com.catan.model.TerrainTile;

/**
 * Test der vertikalen Achsensymmetrie des CATAN Layouts.
 */
public class SymmetryTest {
    public static void main(String[] args) {
        System.out.println("=== CATAN ACHSENSYMMETRIE TEST ===\n");
        
        EnhancedHexGameBoard board = new EnhancedHexGameBoard();
        
        final double HEX_SPACING = 64.0;
        final double BOARD_CENTER_X = 400.0;
        final double BOARD_CENTER_Y = 350.0;
        
        System.out.println("ERWARTETES SYMMETRISCHES LAYOUT:");
        System.out.println("        🔷 🔷 🔷");
        System.out.println("      🔷 🔷 🔷 🔷");
        System.out.println("    🔷 🔷 🔷 🔷 🔷");
        System.out.println("      🔷 🔷 🔷 🔷");
        System.out.println("        🔷 🔷 🔷");
        System.out.println();
        
        System.out.println("AKTUELLE POSITIONEN:");
        for (int r = -2; r <= 2; r++) {
            System.out.printf("Reihe %d (r=%d):\n", r + 3, r);
            
            for (TerrainTile tile : board.getAllTiles()) {
                if (tile.getHexCoordinate().getR() == r) {
                    HexCoordinate hex = tile.getHexCoordinate();
                    HexCoordinate.Point2D pixelPos = hex.toPixelCatan(HEX_SPACING);
                    
                    double finalX = BOARD_CENTER_X + pixelPos.x;
                    double finalY = BOARD_CENTER_Y + pixelPos.y;
                    
                    System.out.printf("  Hex(%2d,%2d) -> X: %6.1f, Y: %6.1f\n", 
                        hex.getQ(), hex.getR(), finalX, finalY);
                }
            }
            System.out.println();
        }
        
        // Symmetrie-Prüfung
        System.out.println("SYMMETRIE-ANALYSE:");
        System.out.println("Zentrale X-Koordinate sollte bei " + BOARD_CENTER_X + " liegen\n");
        
        for (int r = -2; r <= 2; r++) {
            System.out.printf("Reihe %d: ", r + 3);
            double sumX = 0;
            int count = 0;
            
            for (TerrainTile tile : board.getAllTiles()) {
                if (tile.getHexCoordinate().getR() == r) {
                    HexCoordinate hex = tile.getHexCoordinate();
                    HexCoordinate.Point2D pixelPos = hex.toPixelCatan(HEX_SPACING);
                    double finalX = BOARD_CENTER_X + pixelPos.x;
                    sumX += finalX;
                    count++;
                }
            }
            
            double centerX = sumX / count;
            double offset = centerX - BOARD_CENTER_X;
            
            System.out.printf("Schwerpunkt bei X=%.1f (Offset: %+.1f)\n", centerX, offset);
        }
    }
}
