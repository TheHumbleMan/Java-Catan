package com.catan.demo;

import java.util.List;

import com.catan.model.EnhancedHexGameBoard;
import com.catan.model.HexCoordinate;
import com.catan.model.VertexCoordinate;

public class VertexDebugger {
    public static void main(String[] args) {
        System.out.println("=== Vertex Debugging ===");
        
        EnhancedHexGameBoard board = new EnhancedHexGameBoard();
        
        // Look at the first few vertices and their relationships
        VertexCoordinate[] vertices = board.getValidVertices().toArray(new VertexCoordinate[0]);
        
        System.out.println("Total vertices: " + vertices.length);
        System.out.println();
        
        // Examine the first 10 vertices in detail
        for (int i = 0; i < Math.min(10, vertices.length); i++) {
            VertexCoordinate vertex = vertices[i];
            List<HexCoordinate> adjacentHexes = vertex.getAdjacentHexes();
            HexCoordinate.Point2D position = vertex.toPixel(50.0, 0, 0);
            
            System.out.println("Vertex " + i + ": " + vertex);
            System.out.println("  Position: (" + position.x + ", " + position.y + ")");
            System.out.println("  Adjacent hexes: " + adjacentHexes);
            
            // Check how many other vertices share adjacent hexes
            int sharedCount = 0;
            for (int j = i + 1; j < Math.min(20, vertices.length); j++) {
                VertexCoordinate other = vertices[j];
                List<HexCoordinate> otherHexes = other.getAdjacentHexes();
                
                boolean shareHex = false;
                for (HexCoordinate hex1 : adjacentHexes) {
                    for (HexCoordinate hex2 : otherHexes) {
                        if (hex1.equals(hex2)) {
                            shareHex = true;
                            break;
                        }
                    }
                    if (shareHex) break;
                }
                
                if (shareHex) {
                    HexCoordinate.Point2D otherPos = other.toPixel(50.0, 0, 0);
                    double distance = Math.sqrt(Math.pow(position.x - otherPos.x, 2) + Math.pow(position.y - otherPos.y, 2));
                    
                    System.out.println("    Shares hex with " + other + " at distance " + distance);
                    sharedCount++;
                    
                    if (distance < 1.0) {
                        System.out.println("      *** POTENTIAL DUPLICATE ***");
                    }
                }
            }
            
            if (sharedCount == 0) {
                System.out.println("  No shared hexes found with other vertices");
            }
            
            System.out.println();
        }
    }
}
