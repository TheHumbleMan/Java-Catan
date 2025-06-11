package com.catan.demo;

import java.text.DecimalFormat;

import com.catan.model.EnhancedHexGameBoard;
import com.catan.model.HexCoordinate;

/**
 * Debug tool to show how hexagons should be positioned in the authentic CATAN 5-row layout.
 */
public class HexPositionDebug {
    public static void main(String[] args) {
        System.out.println("=== HEX POSITION DEBUG ===\n");
        
        EnhancedHexGameBoard board = new EnhancedHexGameBoard();
        
        // Constants from MainController
        final double HEX_SPACING = 64.0;
        final double BOARD_CENTER_X = 400.0;
        final double BOARD_CENTER_Y = 350.0;
        
        DecimalFormat df = new DecimalFormat("###.##");
        
        System.out.println("EXPECTED VISUAL LAYOUT (authentic CATAN 5-row pattern):");
        System.out.println("        🔷 🔷 🔷");
        System.out.println("      🔷 🔷 🔷 🔷");
        System.out.println("    🔷 🔷 🔷 🔷 🔷");
        System.out.println("      🔷 🔷 🔷 🔷");
        System.out.println("        🔷 🔷 🔷");
        System.out.println();
        
        System.out.println("HEX POSITIONS (q,r) -> PIXEL POSITIONS:");
        System.out.println("Row | Hex Coord | Pixel X | Pixel Y | Visual Position");
        System.out.println("----+-----------+---------+---------+----------------");
        
        // Analyze each row
        for (int r = -2; r <= 2; r++) {
            final int currentR = r; // Make effectively final for lambda
            int rowNum = r + 3;
            System.out.println();
            System.out.printf("ROW %d (r=%d):\n", rowNum, r);
            
            board.getAllTiles().stream()
                .filter(tile -> tile.getHexCoordinate().getR() == currentR)
                .sorted((a, b) -> Integer.compare(a.getHexCoordinate().getQ(), b.getHexCoordinate().getQ()))
                .forEach(tile -> {
                    HexCoordinate hex = tile.getHexCoordinate();
                    HexCoordinate.Point2D pixelPos = hex.toPixelCatan(HEX_SPACING); // Use CATAN positioning
                    
                    double finalX = BOARD_CENTER_X + pixelPos.x;
                    double finalY = BOARD_CENTER_Y + pixelPos.y;
                    
                    System.out.printf("  | (%2d,%2d)   | %7s | %7s | ", 
                        hex.getQ(), hex.getR(), df.format(finalX), df.format(finalY));
                    
                    // Show visual position relative to center
                    if (pixelPos.x < -50) System.out.print("Links  ");
                    else if (pixelPos.x > 50) System.out.print("Rechts ");
                    else System.out.print("Mitte  ");
                    
                    if (pixelPos.y < -50) System.out.print("Oben");
                    else if (pixelPos.y > 50) System.out.print("Unten");
                    else System.out.print("Zentrum");
                    
                    System.out.println();
                });
        }
        
        System.out.println("\n=== ANALYSIS ===");
        System.out.println("HEX_SPACING: " + HEX_SPACING + "px");
        System.out.println("BOARD_CENTER: (" + BOARD_CENTER_X + ", " + BOARD_CENTER_Y + ")");
        
        // Check if hexagons are properly spaced for 5-row layout
        System.out.println("\nROW SPACING CHECK:");
        for (int r = -2; r <= 1; r++) {
            HexCoordinate hex1 = new HexCoordinate(0, r);
            HexCoordinate hex2 = new HexCoordinate(0, r + 1);
            
            HexCoordinate.Point2D pos1 = hex1.toPixelCatan(HEX_SPACING);
            HexCoordinate.Point2D pos2 = hex2.toPixelCatan(HEX_SPACING);
            
            double verticalSpacing = pos2.y - pos1.y;
            System.out.printf("Row %d -> Row %d: %s px vertical spacing\n", 
                r + 3, r + 4, df.format(verticalSpacing));
        }
        
        System.out.println("\nCOLUMN SPACING CHECK (Row 3, center row):");
        for (int q = -2; q <= 1; q++) {
            HexCoordinate hex1 = new HexCoordinate(q, 0);
            HexCoordinate hex2 = new HexCoordinate(q + 1, 0);
            
            HexCoordinate.Point2D pos1 = hex1.toPixelCatan(HEX_SPACING);
            HexCoordinate.Point2D pos2 = hex2.toPixelCatan(HEX_SPACING);
            
            double horizontalSpacing = pos2.x - pos1.x;
            System.out.printf("Hex (%d,0) -> Hex (%d,0): %s px horizontal spacing\n", 
                q, q + 1, df.format(horizontalSpacing));
        }
    }
}