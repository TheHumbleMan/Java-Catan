package com.catan.demo;

import java.util.ArrayList;
import java.util.List;

import com.catan.model.HexCoordinate;
import com.catan.model.VertexCoordinate;

/**
 * Test to check if vertices that should be the same produce the same pixel coordinates
 */
public class VertexCoordinateTest {
    public static void main(String[] args) {
        System.out.println("=== Vertex Coordinate Test ===");
        
        // Test if the same physical vertex has the same pixel coordinates 
        // when referenced from different hexes
        
        // The top-right vertex of hex (0,0) should be the same as:
        // - the top-left vertex of hex (1,0) 
        // - the bottom vertex of hex (0,-1)
        
        VertexCoordinate v1 = new VertexCoordinate(0, 0, 1);  // top-right of (0,0)
        VertexCoordinate v2 = new VertexCoordinate(1, 0, 5);  // top-left of (1,0) 
        VertexCoordinate v3 = new VertexCoordinate(0, -1, 3); // bottom of (0,-1)
        
        System.out.println("Testing vertices that should be the same:");
        System.out.println("v1: " + v1 + " (top-right of hex (0,0))");
        System.out.println("v2: " + v2 + " (top-left of hex (1,0))");
        System.out.println("v3: " + v3 + " (bottom of hex (0,-1))");
        
        try {
            HexCoordinate.Point2D pos1 = v1.toPixel(50.0, 0, 0);
            HexCoordinate.Point2D pos2 = v2.toPixel(50.0, 0, 0);
            HexCoordinate.Point2D pos3 = v3.toPixel(50.0, 0, 0);
            
            System.out.println("\nPixel positions:");
            System.out.println("v1: (" + pos1.x + ", " + pos1.y + ")");
            System.out.println("v2: (" + pos2.x + ", " + pos2.y + ")");
            System.out.println("v3: (" + pos3.x + ", " + pos3.y + ")");
            
            // Check if positions are the same (within tolerance)
            double tolerance = 0.01;
            boolean v1v2Same = Math.abs(pos1.x - pos2.x) < tolerance && Math.abs(pos1.y - pos2.y) < tolerance;
            boolean v1v3Same = Math.abs(pos1.x - pos3.x) < tolerance && Math.abs(pos1.y - pos3.y) < tolerance;
            boolean v2v3Same = Math.abs(pos2.x - pos3.x) < tolerance && Math.abs(pos2.y - pos3.y) < tolerance;
            
            System.out.println("\nAre they the same? (tolerance = " + tolerance + ")");
            System.out.println("v1 == v2: " + v1v2Same);
            System.out.println("v1 == v3: " + v1v3Same);
            System.out.println("v2 == v3: " + v2v3Same);
            
            if (v1v2Same && v1v3Same && v2v3Same) {
                System.out.println("SUCCESS: All vertices map to the same position!");
            } else {
                System.out.println("PROBLEM: Vertices that should be the same have different positions!");
            }
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Test a few more sets of vertices
        System.out.println("\n=== Testing more vertex equivalences ===");
        testVertexEquivalence();
    }
    
    private static void testVertexEquivalence() {
        // Test cases for vertices that should be equivalent
        VertexCoordinate[][] equivalentSets = {
            // Set 1: top-right of (0,0)
            { 
                new VertexCoordinate(0, 0, 1),   // top-right of (0,0)
                new VertexCoordinate(1, 0, 5),   // top-left of (1,0)
                new VertexCoordinate(0, -1, 3)   // bottom of (0,-1)
            },
            // Set 2: right vertex of (0,0)  
            {
                new VertexCoordinate(0, 0, 2),   // bottom-right of (0,0)
                new VertexCoordinate(1, 0, 4),   // bottom-left of (1,0)
                new VertexCoordinate(0, 1, 0)    // top of (0,1)
            }
        };
        
        for (int setNum = 0; setNum < equivalentSets.length; setNum++) {
            System.out.println("\nTesting equivalent set " + (setNum + 1) + ":");
            VertexCoordinate[] set = equivalentSets[setNum];
            
            List<HexCoordinate.Point2D> positions = new ArrayList<>();
            
            for (int i = 0; i < set.length; i++) {
                try {
                    HexCoordinate.Point2D pos = set[i].toPixel(50.0, 0, 0);
                    positions.add(pos);
                    System.out.println("  " + set[i] + " -> (" + pos.x + ", " + pos.y + ")");
                } catch (Exception e) {
                    System.out.println("  " + set[i] + " -> ERROR: " + e.getMessage());
                }
            }
            
            // Check if all positions in this set are the same
            boolean allSame = true;
            double tolerance = 0.01;
            
            if (positions.size() >= 2) {
                HexCoordinate.Point2D first = positions.get(0);
                for (int i = 1; i < positions.size(); i++) {
                    HexCoordinate.Point2D current = positions.get(i);
                    if (Math.abs(first.x - current.x) > tolerance || Math.abs(first.y - current.y) > tolerance) {
                        allSame = false;
                        break;
                    }
                }
            }
            
            System.out.println("  Result: " + (allSame ? "SAME" : "DIFFERENT"));
        }
    }
}
