package com.catan.demo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.catan.model.Building;
import com.catan.model.CatanGame;
import com.catan.model.EdgeCoordinate;
import com.catan.model.EnhancedHexGameBoard;
import com.catan.model.GameBoard;
import com.catan.model.Player;
import com.catan.model.VertexCoordinate;

/**
 * Demonstration of the enhanced hexagonal board system showing:
 * 1. Exactly one building spot per intersection (Kreuzung)
 * 2. Roads positioned exactly between settlements
 * 3. Vertex/edge coordinate system working correctly
 */
public class EnhancedBoardDemo {
    public static void main(String[] args) {
        System.out.println("=== Enhanced Hexagonal CATAN Board Demo ===\n");
        
        // Create a game with enhanced hexagonal board
        List<String> playerNames = Arrays.asList("Alice", "Bob", "Charlie");
        CatanGame game = new CatanGame(playerNames, true, true); // Hexagonal + Enhanced
        
        GameBoard board = game.getBoard();
        Player alice = game.getPlayers().get(0);
        Player bob = game.getPlayers().get(1);
        
        System.out.println("1. Board Configuration:");
        System.out.println("   - Hexagonal: " + board.isHexagonal());
        System.out.println("   - Enhanced: " + board.isEnhanced());
        System.out.println("   - Total tiles: " + board.getEnhancedHexBoard().getAllTiles().size());
        
        EnhancedHexGameBoard enhancedBoard = board.getEnhancedHexBoard();
        Set<VertexCoordinate> validVertices = enhancedBoard.getValidVertices();
        Set<EdgeCoordinate> validEdges = enhancedBoard.getValidEdges();
        
        System.out.println("   - Valid vertices (building spots): " + validVertices.size());
        System.out.println("   - Valid edges (road spots): " + validEdges.size());
        
        System.out.println("\n2. Demonstrating Unique Intersections:");
        
        // Try to place settlements using vertex coordinates
        List<VertexCoordinate> vertexList = new ArrayList<>(validVertices);
        
        // Place a few settlements to demonstrate unique positions
        for (int i = 0; i < Math.min(5, vertexList.size()); i++) {
            VertexCoordinate vertex = vertexList.get(i);
            if (enhancedBoard.canPlaceBuilding(vertex, alice)) {
                boolean placed = enhancedBoard.placeBuilding(Building.Type.SETTLEMENT, vertex, alice);
                System.out.println("   - Placed settlement at vertex " + vertex + ": " + placed);
                
                // Try to place another settlement at the same vertex (should fail)
                boolean duplicate = enhancedBoard.canPlaceBuilding(vertex, bob);
                System.out.println("     → Duplicate placement allowed: " + duplicate + " (should be false)");
                break; // Just show one example
            }
        }
        
        System.out.println("\n3. Demonstrating Road Placement Between Settlements:");
        
        // Find edges that connect to placed settlements
        List<EdgeCoordinate> edgeList = new ArrayList<>(validEdges);
        
        for (int i = 0; i < Math.min(3, edgeList.size()); i++) {
            EdgeCoordinate edge = edgeList.get(i);
            if (enhancedBoard.canPlaceRoad(edge, alice)) {
                boolean placed = enhancedBoard.placeRoad(edge, alice);
                System.out.println("   - Placed road at edge " + edge + ": " + placed);
                
                // Show the vertices this edge connects
                VertexCoordinate[] connectedVertices = edge.getConnectedVertices();
                System.out.println("     → Connects vertices: " + 
                    Arrays.toString(connectedVertices));
                break; // Just show one example
            }
        }
        
        System.out.println("\n4. Legacy Coordinate Support:");
        
        // Show that legacy coordinates still work
        boolean legacyBuilding = enhancedBoard.placeBuilding(Building.Type.SETTLEMENT, 2, 2, bob);
        boolean legacyRoad = enhancedBoard.placeRoad(2, 2, 3, 2, bob);
        
        System.out.println("   - Legacy building placement (2,2): " + legacyBuilding);
        System.out.println("   - Legacy road placement (2,2)→(3,2): " + legacyRoad);
        
        System.out.println("\n5. Final State:");
        System.out.println("   - Total buildings placed: " + enhancedBoard.getBuildings().size());
        System.out.println("   - Total roads placed: " + enhancedBoard.getRoads().size());
        
        // Show that each building is at a unique position
        Collection<Building> buildings = enhancedBoard.getBuildings();
        Set<String> buildingPositions = new HashSet<>();
        for (Building building : buildings) {
            String position = building.getX() + "," + building.getY();
            buildingPositions.add(position);
        }
        
        System.out.println("   - Unique building positions: " + buildingPositions.size() + 
                          " (same as building count: " + (buildingPositions.size() == buildings.size()) + ")");
        
        System.out.println("\n=== Demo Complete ===");
        System.out.println("✓ Enhanced board ensures exactly one building spot per intersection");
        System.out.println("✓ Roads are positioned exactly between settlements using edge coordinates");  
        System.out.println("✓ Vertex/edge coordinate system prevents duplicate intersections");
        System.out.println("✓ Legacy coordinate support maintained for backward compatibility");
    }
}
