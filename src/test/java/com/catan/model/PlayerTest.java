package com.catan.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Player functionality.
 */
class PlayerTest {
    
    private Player player;
    
    @BeforeEach
    void setUp() {
        player = new Player("TestPlayer", PlayerColor.RED);
    }
    
    @Test
    void testPlayerInitialization() {
        assertEquals("TestPlayer", player.getName());
        assertEquals(PlayerColor.RED, player.getColor());
        assertEquals(0, player.getVictoryPoints());
        assertEquals(0, player.getTotalResourceCount());
        assertEquals(5, player.getSettlementsLeft());
        assertEquals(4, player.getCitiesLeft());
        assertEquals(15, player.getRoadsLeft());
    }
    
    @Test
    void testResourceManagement() {
        // Test adding resources
        player.addResource(ResourceType.LUMBER, 3);
        player.addResource(ResourceType.BRICK, 2);
        
        assertEquals(3, player.getResourceCount(ResourceType.LUMBER));
        assertEquals(2, player.getResourceCount(ResourceType.BRICK));
        assertEquals(5, player.getTotalResourceCount());
        
        // Test removing resources
        assertTrue(player.removeResource(ResourceType.LUMBER, 2));
        assertEquals(1, player.getResourceCount(ResourceType.LUMBER));
        
        // Test removing more than available
        assertFalse(player.removeResource(ResourceType.LUMBER, 5));
        assertEquals(1, player.getResourceCount(ResourceType.LUMBER));
    }
    
    @Test
    void testBuildingSettlement() {
        // Player doesn't have resources initially
        assertFalse(player.canBuildSettlement());
        
        // Give player settlement resources
        player.addResource(ResourceType.LUMBER, 1);
        player.addResource(ResourceType.BRICK, 1);
        player.addResource(ResourceType.WOOL, 1);
        player.addResource(ResourceType.GRAIN, 1);
        
        assertTrue(player.canBuildSettlement());
        assertTrue(player.buildSettlement());
        
        // Check resources were spent and victory points awarded
        assertEquals(0, player.getTotalResourceCount());
        assertEquals(1, player.getVictoryPoints());
        assertEquals(4, player.getSettlementsLeft());
    }
    
    @Test
    void testBuildingCity() {
        // First build a settlement
        player.addResource(ResourceType.LUMBER, 1);
        player.addResource(ResourceType.BRICK, 1);
        player.addResource(ResourceType.WOOL, 1);
        player.addResource(ResourceType.GRAIN, 1);
        
        assertTrue(player.canBuildSettlement());
        assertTrue(player.buildSettlement());
        assertEquals(1, player.getVictoryPoints()); // Settlement gives 1 point
        
        // Player doesn't have resources for city initially
        assertFalse(player.canBuildCity());
        
        // Give player city resources
        player.addResource(ResourceType.GRAIN, 2);
        player.addResource(ResourceType.ORE, 3);
        
        assertTrue(player.canBuildCity());
        assertTrue(player.buildCity());
        
        // Check resources were spent and victory points awarded
        assertEquals(0, player.getTotalResourceCount());
        assertEquals(2, player.getVictoryPoints()); // Settlement (1) + City upgrade (1) = 2 total
        assertEquals(3, player.getCitiesLeft());
        assertEquals(5, player.getSettlementsLeft()); // Settlement returned when building city
    }
    
    @Test
    void testBuildingRoad() {
        // Player doesn't have resources initially
        assertFalse(player.canBuildRoad());
        
        // Give player road resources
        player.addResource(ResourceType.LUMBER, 1);
        player.addResource(ResourceType.BRICK, 1);
        
        assertTrue(player.canBuildRoad());
        assertTrue(player.buildRoad());
        
        // Check resources were spent
        assertEquals(0, player.getTotalResourceCount());
        assertEquals(14, player.getRoadsLeft());
    }
    
    @Test
    void testSpendResources() {
        // Add some resources
        player.addResource(ResourceType.LUMBER, 2);
        player.addResource(ResourceType.BRICK, 1);
        
        // Test spending resources
        java.util.Map<ResourceType, Integer> cost = java.util.Map.of(
            ResourceType.LUMBER, 1,
            ResourceType.BRICK, 1
        );
        
        assertTrue(player.spendResources(cost));
        assertEquals(1, player.getResourceCount(ResourceType.LUMBER));
        assertEquals(0, player.getResourceCount(ResourceType.BRICK));
        
        // Test spending more than available
        java.util.Map<ResourceType, Integer> expensiveCost = java.util.Map.of(
            ResourceType.LUMBER, 3
        );
        
        assertFalse(player.spendResources(expensiveCost));
        assertEquals(1, player.getResourceCount(ResourceType.LUMBER)); // Should be unchanged
    }
    
    @Test
    void testVictoryPoints() {
        assertEquals(0, player.getVictoryPoints());
        
        player.addVictoryPoints(3);
        assertEquals(3, player.getVictoryPoints());
        
        player.addVictoryPoints(2);
        assertEquals(5, player.getVictoryPoints());
    }
}
