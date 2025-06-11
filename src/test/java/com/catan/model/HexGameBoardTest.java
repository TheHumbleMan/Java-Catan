package com.catan.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for HexGameBoard functionality.
 */
public class HexGameBoardTest {
    
    private HexGameBoard hexBoard;
    private List<Player> players;
    
    @BeforeEach
    public void setUp() {
        hexBoard = new HexGameBoard();
        players = Arrays.asList(
            new Player("Alice", PlayerColor.RED),
            new Player("Bob", PlayerColor.BLUE)
        );
    }
    
    @Test
    public void testHexBoardInitialization() {
        // Test that the hex board is properly initialized
        Collection<TerrainTile> tiles = hexBoard.getAllTiles();
        
        // Standard CATAN board has 19 tiles
        assertEquals(19, tiles.size(), "Hex board should have 19 tiles");
        
        // Check that all tiles have hex coordinates
        for (TerrainTile tile : tiles) {
            assertNotNull(tile.getHexCoordinate(), "All tiles should have hex coordinates");
        }
    }
    
    @Test
    public void testHexCoordinateSystem() {
        // Test hex coordinate functionality
        HexCoordinate center = new HexCoordinate(0, 0);
        HexCoordinate neighbor = new HexCoordinate(1, 0);
        
        // Test distance calculation
        assertEquals(1, center.distanceTo(neighbor), "Adjacent hex distance should be 1");
        
        // Test neighbors
        HexCoordinate[] neighbors = center.getNeighbors();
        assertEquals(6, neighbors.length, "Hex should have 6 neighbors");
        
        // Test pixel conversion
        HexCoordinate.Point2D pixelPos = center.toPixel(52.0);
        assertNotNull(pixelPos, "Pixel conversion should work");
        assertEquals(0.0, pixelPos.x, 0.1, "Center hex should be at pixel (0,0)");
        assertEquals(0.0, pixelPos.y, 0.1, "Center hex should be at pixel (0,0)");
    }
    
    @Test
    public void testBuildingPlacement() {
        Player player = players.get(0);
        HexCoordinate testCoord = new HexCoordinate(0, 0);
        
        // Test initial building placement (should be allowed during setup)
        assertTrue(hexBoard.canPlaceBuilding(testCoord, player), 
                   "Should be able to place building during initial setup");
        
        // Place a settlement
        assertTrue(hexBoard.placeBuilding(Building.Type.SETTLEMENT, testCoord, player),
                   "Should be able to place settlement");
        
        // Test that the same position can't be used again
        assertFalse(hexBoard.canPlaceBuilding(testCoord, player),
                    "Should not be able to place building on occupied position");
    }
    
    @Test
    public void testRoadPlacement() {
        Player player = players.get(0);
        HexCoordinate start = new HexCoordinate(0, 0);
        HexCoordinate end = new HexCoordinate(1, 0);
        
        // Test road placement during initial setup
        assertTrue(hexBoard.canPlaceRoad(start, end, player),
                   "Should be able to place road during initial setup");
        
        // Place a road
        assertTrue(hexBoard.placeRoad(start, end, player),
                   "Should be able to place road");
        
        // Test that the same road can't be placed again
        assertFalse(hexBoard.canPlaceRoad(start, end, player),
                    "Should not be able to place road on existing position");
    }
    
    @Test
    public void testRobberMovement() {
        HexCoordinate newPosition = new HexCoordinate(1, 0);
        
        // Move robber to new position
        hexBoard.moveRobber(newPosition);
        
        // Check robber position
        assertEquals(newPosition, hexBoard.getRobberPosition(),
                     "Robber should be moved to new position");
    }
    
    @Test
    public void testCityUpgrade() {
        Player player = players.get(0);
        HexCoordinate testCoord = new HexCoordinate(0, 0);
        
        // First place a settlement
        hexBoard.placeBuilding(Building.Type.SETTLEMENT, testCoord, player);
        
        // Upgrade to city
        assertTrue(hexBoard.upgradeToCity(testCoord, player),
                   "Should be able to upgrade settlement to city");
        
        // Try to upgrade again (should fail)
        assertFalse(hexBoard.upgradeToCity(testCoord, player),
                    "Should not be able to upgrade city again");
    }
    
    @Test
    public void testHexGameIntegration() {
        // Test creating a CATAN game with legacy hexagonal board
        List<String> playerNames = Arrays.asList("Alice", "Bob", "Charlie");
        CatanGame game = new CatanGame(playerNames, true, false); // Use legacy hexagonal board
        
        // Check that the game board is hexagonal
        assertTrue(game.getBoard().isHexagonal(), 
                   "Game should use hexagonal board when specified");
        
        // Check that hex board is available (in legacy mode)
        assertNotNull(game.getBoard().getHexBoard(),
                      "Hex board should be available in legacy mode");
        
        // Check that hex board has correct number of tiles
        assertEquals(19, game.getBoard().getHexBoard().getAllTiles().size(),
                     "Hex board should have 19 tiles");
        
        // Test enhanced hexagonal board mode as well
        CatanGame enhancedGame = new CatanGame(playerNames, true, true); // Use enhanced hexagonal board
        
        // Check that the enhanced game board is hexagonal and enhanced
        assertTrue(enhancedGame.getBoard().isHexagonal(), 
                   "Enhanced game should use hexagonal board");
        assertTrue(enhancedGame.getBoard().isEnhanced(), 
                   "Enhanced game should use enhanced mode");
        
        // In enhanced mode, hexBoard should be null and enhancedHexBoard should be available
        assertNull(enhancedGame.getBoard().getHexBoard(),
                   "Legacy hex board should be null in enhanced mode");
        assertNotNull(enhancedGame.getBoard().getEnhancedHexBoard(),
                      "Enhanced hex board should be available in enhanced mode");
    }
}
