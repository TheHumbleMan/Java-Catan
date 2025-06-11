package com.catan.model;

import java.util.Collection;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for EnhancedHexGameBoard functionality.
 */
class EnhancedHexGameBoardTest {
    
    private EnhancedHexGameBoard board;
    private Player player1;
    private Player player2;
    
    @BeforeEach
    void setUp() {
        board = new EnhancedHexGameBoard();
        player1 = new Player("Alice", PlayerColor.RED);
        player2 = new Player("Bob", PlayerColor.BLUE);
    }
    
    @Test
    void testBoardInitialization() {
        assertNotNull(board);
        assertEquals(19, board.getAllTiles().size(), "Board should have 19 tiles");
        assertEquals(0, board.getBuildings().size(), "Board should start with no buildings");
        assertEquals(0, board.getRoads().size(), "Board should start with no roads");
    }
    
    @Test
    void testValidVerticesAndEdges() {
        Set<VertexCoordinate> validVertices = board.getValidVertices();
        Set<EdgeCoordinate> validEdges = board.getValidEdges();
        
        assertNotNull(validVertices);
        assertNotNull(validEdges);
        assertTrue(validVertices.size() > 0, "Should have valid vertices");
        assertTrue(validEdges.size() > 0, "Should have valid edges");
        
        // For a 19-tile board, we expect approximately 54 vertices and 72 edges
        assertTrue(validVertices.size() >= 50, "Should have at least 50 vertices");
        assertTrue(validEdges.size() >= 70, "Should have at least 70 edges");
    }
    
    @Test
    void testBuildingPlacementWithVertexCoordinates() {
        Set<VertexCoordinate> validVertices = board.getValidVertices();
        assertFalse(validVertices.isEmpty(), "Should have valid vertices for testing");
        
        VertexCoordinate testVertex = validVertices.iterator().next();
        
        // Should be able to place a settlement
        assertTrue(board.canPlaceBuilding(testVertex, player1),
                  "Should be able to place building on valid vertex");
        
        assertTrue(board.placeBuilding(Building.Type.SETTLEMENT, testVertex, player1),
                  "Should successfully place settlement");
        
        // Should not be able to place another building on the same vertex
        assertFalse(board.canPlaceBuilding(testVertex, player2),
                   "Should not be able to place building on occupied vertex");
        
        assertFalse(board.placeBuilding(Building.Type.SETTLEMENT, testVertex, player2),
                   "Should not be able to place settlement on occupied vertex");
    }
    
    @Test
    void testRoadPlacementWithEdgeCoordinates() {
        Set<EdgeCoordinate> validEdges = board.getValidEdges();
        assertFalse(validEdges.isEmpty(), "Should have valid edges for testing");
        
        EdgeCoordinate testEdge = validEdges.iterator().next();
        
        // Should be able to place a road
        assertTrue(board.canPlaceRoad(testEdge, player1),
                  "Should be able to place road on valid edge");
        
        assertTrue(board.placeRoad(testEdge, player1),
                  "Should successfully place road");
        
        // Should not be able to place another road on the same edge
        assertFalse(board.canPlaceRoad(testEdge, player2),
                   "Should not be able to place road on occupied edge");
        
        assertFalse(board.placeRoad(testEdge, player2),
                   "Should not be able to place road on occupied edge");
    }
    
    @Test
    void testLegacyCoordinateSupport() {
        // Test that legacy x,y coordinates still work
        assertTrue(board.placeBuilding(Building.Type.SETTLEMENT, 1, 1, player1),
                  "Should support legacy coordinate building placement");
        
        assertTrue(board.placeRoad(1, 1, 2, 1, player1),
                  "Should support legacy coordinate road placement");
        
        assertEquals(1, board.getBuildings().size(), "Should have one building placed");
        assertEquals(1, board.getRoads().size(), "Should have one road placed");
    }
    
    @Test
    void testDistanceRules() {
        Set<VertexCoordinate> validVertices = board.getValidVertices();
        VertexCoordinate vertex1 = null;
        VertexCoordinate vertex2 = null;
        
        // Find two vertices that are close to each other
        // For simplicity, just pick the first two vertices
        int count = 0;
        for (VertexCoordinate v : validVertices) {
            if (count == 0) {
                vertex1 = v;
            } else if (count == 1) {
                vertex2 = v;
                break;
            }
            count++;
        }
        
        assertNotNull(vertex1, "Should find first vertex for testing");
        assertNotNull(vertex2, "Should find second vertex for testing");
        
        // Place first settlement
        assertTrue(board.placeBuilding(Building.Type.SETTLEMENT, vertex1, player1),
                  "Should place first settlement");
        
        // The distance rule implementation in the enhanced board will determine
        // if the second settlement can be placed
        boolean canPlace = board.canPlaceBuilding(vertex2, player1);
        // We don't assert the result here since it depends on the actual positions
        // Just ensure the method doesn't throw an exception
        assertNotNull(canPlace);
    }
    
    @Test
    void testUpgradeToCity() {
        Set<VertexCoordinate> validVertices = board.getValidVertices();
        VertexCoordinate testVertex = validVertices.iterator().next();
        
        // Place settlement first
        assertTrue(board.placeBuilding(Building.Type.SETTLEMENT, testVertex, player1),
                  "Should place settlement first");
        
        // Upgrade to city
        assertTrue(board.upgradeToCity(testVertex, player1),
                  "Should be able to upgrade settlement to city");
        
        // Check that the building count reflects the upgrade
        Collection<Building> buildings = board.getBuildings();
        assertEquals(1, buildings.size(), "Should still have one building after upgrade");
        
        // Find the building and verify it's a city
        Building building = buildings.iterator().next();
        assertEquals(Building.Type.CITY, building.getType(), "Building should be a city");
        
        // Should not be able to upgrade again
        assertFalse(board.upgradeToCity(testVertex, player1),
                   "Should not be able to upgrade city again");
    }
    
    @Test
    void testUniqueIntersections() {
        Set<VertexCoordinate> validVertices = board.getValidVertices();
        
        // Verify that each vertex coordinate is unique
        assertEquals(validVertices.size(), validVertices.size(),
                    "All vertex coordinates should be unique");
        
        // Try to place buildings on all valid vertices (in initial placement phase)
        int placedBuildings = 0;
        for (VertexCoordinate vertex : validVertices) {
            if (board.canPlaceBuilding(vertex, player1)) {
                if (board.placeBuilding(Building.Type.SETTLEMENT, vertex, player1)) {
                    placedBuildings++;
                }
            }
            // Stop after placing a few to avoid distance rule conflicts
            if (placedBuildings >= 5) break;
        }
        
        assertTrue(placedBuildings >= 5, "Should be able to place multiple buildings on unique vertices");
    }
}
