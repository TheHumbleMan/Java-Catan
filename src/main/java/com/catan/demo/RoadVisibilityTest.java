package com.catan.demo;

import java.util.Arrays;
import java.util.List;

import com.catan.model.CatanGame;
import com.catan.model.EdgeCoordinate;
import com.catan.model.EnhancedHexGameBoard;
import com.catan.model.Player;
import com.catan.model.Road;

/**
 * Test to verify road visibility and positioning in the enhanced board system.
 */
public class RoadVisibilityTest {
    
    public static void main(String[] args) {
        System.out.println("=== CATAN Road Visibility Test ===");
        
        // Create a game with enhanced hexagonal board
        List<String> playerNames = Arrays.asList("Player 1", "Player 2", "Player 3", "Player 4");
        CatanGame game = new CatanGame(playerNames, true, true);
        
        EnhancedHexGameBoard board = game.getBoard().getEnhancedHexBoard();
        Player currentPlayer = game.getCurrentPlayer();
        
        System.out.println("Board initialized with:");
        System.out.println("- Valid vertices: " + board.getValidVertices().size());
        System.out.println("- Valid edges: " + board.getValidEdges().size());
        
        // Test road placement
        List<EdgeCoordinate> validEdges = board.getValidEdges().stream().limit(5).toList();
        int roadsPlaced = 0;
        
        for (EdgeCoordinate edge : validEdges) {
            if (board.canPlaceRoad(edge, currentPlayer)) {
                if (board.placeRoad(edge, currentPlayer)) {
                    roadsPlaced++;
                    System.out.println("✅ Road placed at edge: " + edge);
                } else {
                    System.out.println("❌ Failed to place road at edge: " + edge);
                }
            } else {
                System.out.println("⚠️ Cannot place road at edge: " + edge);
            }
        }
        
        System.out.println("\nRoad placement summary:");
        System.out.println("- Roads placed: " + roadsPlaced);
        System.out.println("- Total roads on board: " + board.getRoads().size());
        
        // Check if roads have proper coordinates
        for (Road road : board.getRoads()) {
            System.out.println("Road: " + road + " | EdgeCoord: " + road.getEdgeCoordinate());
        }
        
        System.out.println("\n=== Test Complete ===");
    }
}
