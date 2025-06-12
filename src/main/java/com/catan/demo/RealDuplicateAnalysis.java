package com.catan.demo;

import com.catan.model.EnhancedHexGameBoard;
import com.catan.model.VertexCoordinate;
import com.catan.model.HexCoordinate;
import java.util.List;

public class RealDuplicateAnalysis {
    public static void main(String[] args) {
        System.out.println("=== Real Duplicate Analysis ===");
        
        EnhancedHexGameBoard board = new EnhancedHexGameBoard();
        
        // Let's manually check specific cases that should be duplicates
        // Example: top vertex of hex (0,0) should equal bottom vertex of hex (0,-1)
        
        VertexCoordinate vertex1 = new VertexCoordinate(0, 0, 0);  // Top of (0,0)
        VertexCoordinate vertex2 = new VertexCoordinate(0, -1, 3); // Bottom of (0,-1)
        
        System.out.println("Testing theoretical duplicates:");
        System.out.println("Vertex1: " + vertex1);
        System.out.println("Vertex2: " + vertex2);
        
        HexCoordinate.Point2D pos1 = vertex1.toPixel(50.0, 0, 0);
        HexCoordinate.Point2D pos2 = vertex2.toPixel(50.0, 0, 0);
        
        System.out.println("Position1: (" + pos1.x + ", " + pos1.y + ")");
        System.out.println("Position2: (" + pos2.x + ", " + pos2.y + ")");
        
        double distance = Math.sqrt(Math.pow(pos1.x - pos2.x, 2) + Math.pow(pos1.y - pos2.y, 2));
        System.out.println("Distance: " + distance);
        
        if (distance < 1.0) {
            System.out.println("*** THESE ARE THE SAME VERTEX! ***");
        } else {
            System.out.println("These are different vertices (as expected if toPixel() works correctly)");
        }
        
        System.out.println();
        
        // Check a few more theoretical cases
        checkDuplicateCase(new VertexCoordinate(0, 0, 1), new VertexCoordinate(1, -1, 4), "Top-right of (0,0) vs Bottom-left of (1,-1)");
        checkDuplicateCase(new VertexCoordinate(0, 0, 2), new VertexCoordinate(1, 0, 5), "Bottom-right of (0,0) vs Top-left of (1,0)");
        checkDuplicateCase(new VertexCoordinate(0, 0, 3), new VertexCoordinate(0, 1, 0), "Bottom of (0,0) vs Top of (0,1)");
        
        System.out.println();
        System.out.println("=== Analysis Complete ===");
        System.out.println("If distances are all near 0, then toPixel() correctly identifies same vertices.");
        System.out.println("If distances are large, then the deduplication algorithm needs improvement.");
    }
    
    private static void checkDuplicateCase(VertexCoordinate v1, VertexCoordinate v2, String description) {
        HexCoordinate.Point2D pos1 = v1.toPixel(50.0, 0, 0);
        HexCoordinate.Point2D pos2 = v2.toPixel(50.0, 0, 0);
        double distance = Math.sqrt(Math.pow(pos1.x - pos2.x, 2) + Math.pow(pos1.y - pos2.y, 2));
        
        System.out.println(description + ": distance = " + distance);
        if (distance < 1.0) {
            System.out.println("  *** DUPLICATE FOUND ***");
        }
    }
}
