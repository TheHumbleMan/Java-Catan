package com.catan.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.catan.model.EdgeCoordinate;
import com.catan.model.EnhancedHexGameBoard;
import com.catan.model.HexCoordinate;

/**
 * Test to understand how many edges should actually exist on the CATAN board.
 * This will help us determine if 72 is actually the correct number.
 */
public class EdgeMathTest {
    public static void main(String[] args) {
        System.out.println("=== CATAN Edge Math Analysis ===");
        
        // First, let's understand the theoretical edge count
        // For a hexagonal grid, we need to count:
        // 1. Interior edges (shared by 2 hexes)
        // 2. Boundary edges (only belong to 1 hex)
        
        // CATAN standard layout: 3-4-5-4-3 hexes per row
        System.out.println("Standard CATAN layout: 3-4-5-4-3 hexes = 19 total hexes");
        
        // Let's calculate edges manually for the standard layout
        calculateEdgesManually();
        
        // Now test our current implementation
        EnhancedHexGameBoard board = new EnhancedHexGameBoard();
        Set<EdgeCoordinate> validEdges = board.getValidEdges();
        
        System.out.println("\nCurrent implementation: " + validEdges.size() + " edges");
        
        // Test edge sharing by examining adjacent hexes
        testEdgeSharing();
        
        // Calculate what we should actually expect
        calculateExpectedEdges();
    }
    
    private static void calculateEdgesManually() {
        System.out.println("\n=== Manual Edge Calculation ===");
        
        // For the standard CATAN board layout, let's count edges by position
        // Row structure: 3-4-5-4-3
        
        // Horizontal edges (connecting hexes within rows):
        int horizontalEdges = 0;
        horizontalEdges += 2; // Row 1: 3 hexes = 2 internal edges
        horizontalEdges += 3; // Row 2: 4 hexes = 3 internal edges  
        horizontalEdges += 4; // Row 3: 5 hexes = 4 internal edges
        horizontalEdges += 3; // Row 4: 4 hexes = 3 internal edges
        horizontalEdges += 2; // Row 5: 3 hexes = 2 internal edges
        
        System.out.println("Horizontal edges (within rows): " + horizontalEdges);
        
        // Vertical edges (connecting hexes between rows):
        int verticalEdges = 0;
        verticalEdges += 3 + 4; // Between rows 1-2: 3 + 4 = 7 connections
        verticalEdges += 4 + 5; // Between rows 2-3: 4 + 5 = 9 connections  
        verticalEdges += 5 + 4; // Between rows 3-4: 5 + 4 = 9 connections
        verticalEdges += 4 + 3; // Between rows 4-5: 4 + 3 = 7 connections
        
        System.out.println("Vertical edges (between rows): " + verticalEdges);
        
        // Boundary edges (perimeter of the board):
        int boundaryEdges = 0;
        // Top and bottom rows
        boundaryEdges += 3 * 2; // Row 1: 3 hexes × 2 top edges each
        boundaryEdges += 3 * 2; // Row 5: 3 hexes × 2 bottom edges each
        // Left and right sides are more complex...
        
        System.out.println("Boundary edges (perimeter): at least " + boundaryEdges);
        System.out.println("Total (partial): " + (horizontalEdges + verticalEdges + boundaryEdges));
    }
    
    private static void testEdgeSharing() {
        System.out.println("\n=== Edge Sharing Test ===");
        
        // Test with a simple 2-hex configuration
        HexCoordinate hex1 = new HexCoordinate(0, 0);
        HexCoordinate hex2 = new HexCoordinate(1, 0); // Adjacent to the right
        
        Set<EdgeCoordinate> edges1 = new HashSet<>();
        Set<EdgeCoordinate> edges2 = new HashSet<>();
        
        // Generate all edges for each hex
        for (int dir = 0; dir < 6; dir++) {
            edges1.add(new EdgeCoordinate(hex1.getQ(), hex1.getR(), dir));
            edges2.add(new EdgeCoordinate(hex2.getQ(), hex2.getR(), dir));
        }
        
        System.out.println("Hex1 (0,0) has " + edges1.size() + " edges");
        System.out.println("Hex2 (1,0) has " + edges2.size() + " edges");
        
        // Check for shared edge positions
        Map<String, List<EdgeCoordinate>> positionMap = new HashMap<>();
        
        for (EdgeCoordinate edge : edges1) {
            HexCoordinate.Point2D pos = edge.toPixel(100.0, 0, 0);
            String key = String.format("%.1f,%.1f", pos.x, pos.y);
            positionMap.computeIfAbsent(key, k -> new ArrayList<>()).add(edge);
        }
        
        for (EdgeCoordinate edge : edges2) {
            HexCoordinate.Point2D pos = edge.toPixel(100.0, 0, 0);
            String key = String.format("%.1f,%.1f", pos.x, pos.y);
            positionMap.computeIfAbsent(key, k -> new ArrayList<>()).add(edge);
        }
        
        int sharedPositions = 0;
        for (Map.Entry<String, List<EdgeCoordinate>> entry : positionMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                sharedPositions++;
                System.out.println("Shared position " + entry.getKey() + ": " + entry.getValue());
            }
        }
        
        System.out.println("Total shared edge positions: " + sharedPositions);
        System.out.println("Unique edge positions: " + positionMap.size());
        System.out.println("Expected unique for 2 adjacent hexes: " + (6 + 6 - sharedPositions));
    }
    
    private static void calculateExpectedEdges() {
        System.out.println("\n=== Expected Edge Count Calculation ===");
        
        // For a hex grid with 19 hexes arranged in the CATAN pattern,
        // we can use Euler's formula: V - E + F = 2
        // Where V = vertices, E = edges, F = faces (hexes + outer face)
        
        // But for edges specifically in a hex grid:
        // Each interior hex contributes 6 edges, but edges are shared
        // The exact count depends on the topology
        
        // Based on CATAN rules, there should be 72 possible road positions
        // This is a known value from the game design
        
        // Let's verify if our board topology matches this
        System.out.println("CATAN game rules specify 72 possible road positions");
        System.out.println("This suggests our target should indeed be 72 edges");
        
        // The discrepancy suggests our edge generation or deduplication has issues
        System.out.println("Current count of 56 suggests we're missing 16 edges");
        System.out.println("This could be due to:");
        System.out.println("1. Incorrect edge generation algorithm");
        System.out.println("2. Over-aggressive deduplication");
        System.out.println("3. Missing boundary edges");
        System.out.println("4. Incorrect hexagonal coordinate math");
    }
}
