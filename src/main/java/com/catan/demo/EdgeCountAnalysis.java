package com.catan.demo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.catan.model.EdgeCoordinate;
import com.catan.model.EnhancedHexGameBoard;
import com.catan.model.HexCoordinate;
import com.catan.model.VertexCoordinate;

/**
 * Test to analyze edge generation and identify why we have 58 instead of 72 edges.
 * A standard CATAN board should have 72 road positions (edges).
 */
public class EdgeCountAnalysis {
    public static void main(String[] args) {
        System.out.println("=== CATAN Edge Count Analysis ===");
        
        // Create the board
        EnhancedHexGameBoard board = new EnhancedHexGameBoard();
        Set<EdgeCoordinate> validEdges = board.getValidEdges();
        Set<VertexCoordinate> validVertices = board.getValidVertices();
        
        System.out.println("Current valid edges: " + validEdges.size());
        System.out.println("Current valid vertices: " + validVertices.size());
        System.out.println("Expected edges: 72");
        System.out.println("Expected vertices: 54");
        System.out.println();
        
        // Calculate theoretical edge count
        // Each hex has 6 edges, but edges are shared between adjacent hexes
        // For 19 hexes in standard CATAN layout:
        // - Interior edges are shared by 2 hexes
        // - Border edges belong to only 1 hex
        
        System.out.println("=== Detailed Edge Analysis ===");
        
        // Group edges by position to check for duplicates
        Map<String, Integer> positionCounts = new HashMap<>();
        
        for (EdgeCoordinate edge : validEdges) {
            HexCoordinate.Point2D position = edge.toPixel(50.0, 0, 0);
            String posKey = String.format("%.1f,%.1f", position.x, position.y);
            positionCounts.put(posKey, positionCounts.getOrDefault(posKey, 0) + 1);
        }
        
        System.out.println("Unique pixel positions: " + positionCounts.size());
        System.out.println("Positions with multiple edges:");
        positionCounts.entrySet().stream()
            .filter(entry -> entry.getValue() > 1)
            .forEach(entry -> System.out.println("  " + entry.getKey() + ": " + entry.getValue() + " edges"));
        
        // Manual calculation of expected edges
        System.out.println("\n=== Manual Edge Count Calculation ===");
        System.out.println("19 hexes × 6 edges per hex = 114 total edges");
        System.out.println("But edges are shared between adjacent hexes...");
        
        // Count edges by hex coordinate
        Map<String, Set<EdgeCoordinate>> edgesByHex = new HashMap<>();
        for (EdgeCoordinate edge : validEdges) {
            String hexKey = edge.getX() + "," + edge.getY();
            edgesByHex.computeIfAbsent(hexKey, k -> new HashSet<>()).add(edge);
        }
        
        System.out.println("Edges per hex coordinate:");
        edgesByHex.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> {
                System.out.println("  Hex " + entry.getKey() + ": " + entry.getValue().size() + " edges");
                for (EdgeCoordinate edge : entry.getValue()) {
                    System.out.println("    " + edge);
                }
            });
        
        // Let's also check if we're missing any edge directions
        System.out.println("\n=== Edge Direction Analysis ===");
        int[] directionCounts = new int[6];
        for (EdgeCoordinate edge : validEdges) {
            directionCounts[edge.getDirection()]++;
        }
        
        for (int i = 0; i < 6; i++) {
            System.out.println("Direction " + i + ": " + directionCounts[i] + " edges");
        }
        
        // Test the edge creation algorithm manually
        System.out.println("\n=== Testing Edge Generation Algorithm ===");
        testEdgeGeneration();
    }
    
    private static void testEdgeGeneration() {
        // Create a simple test with 2 adjacent hexes to see how edges are shared
        System.out.println("Testing with 2 adjacent hexes:");
        
        HexCoordinate hex1 = new HexCoordinate(0, 0);
        HexCoordinate hex2 = new HexCoordinate(1, 0); // Adjacent hex to the right
        
        Set<EdgeCoordinate> edges1 = new HashSet<>();
        Set<EdgeCoordinate> edges2 = new HashSet<>();
        
        // Generate edges for hex1
        for (int dir = 0; dir < 6; dir++) {
            EdgeCoordinate edge = new EdgeCoordinate(hex1.getQ(), hex1.getR(), dir);
            edges1.add(edge);
        }
        
        // Generate edges for hex2
        for (int dir = 0; dir < 6; dir++) {
            EdgeCoordinate edge = new EdgeCoordinate(hex2.getQ(), hex2.getR(), dir);
            edges2.add(edge);
        }
        
        System.out.println("Hex1 edges: " + edges1.size());
        for (EdgeCoordinate edge : edges1) {
            HexCoordinate.Point2D pos = edge.toPixel(50.0, 0, 0);
            System.out.println("  " + edge + " -> (" + pos.x + ", " + pos.y + ")");
        }
        
        System.out.println("Hex2 edges: " + edges2.size());
        for (EdgeCoordinate edge : edges2) {
            HexCoordinate.Point2D pos = edge.toPixel(50.0, 0, 0);
            System.out.println("  " + edge + " -> (" + pos.x + ", " + pos.y + ")");
        }
        
        // Check for shared edges by pixel position
        Set<String> positions1 = new HashSet<>();
        Set<String> positions2 = new HashSet<>();
        
        for (EdgeCoordinate edge : edges1) {
            HexCoordinate.Point2D pos = edge.toPixel(50.0, 0, 0);
            positions1.add(String.format("%.1f,%.1f", pos.x, pos.y));
        }
        
        for (EdgeCoordinate edge : edges2) {
            HexCoordinate.Point2D pos = edge.toPixel(50.0, 0, 0);
            positions2.add(String.format("%.1f,%.1f", pos.x, pos.y));
        }
        
        Set<String> intersection = new HashSet<>(positions1);
        intersection.retainAll(positions2);
        
        System.out.println("Shared edge positions: " + intersection.size());
        for (String pos : intersection) {
            System.out.println("  " + pos);
        }
    }
}
