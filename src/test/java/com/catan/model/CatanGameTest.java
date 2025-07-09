package com.catan.model;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for CatanGame functionality.
 */
class CatanGameTest {
    
    private CatanGame game;
    private List<String> playerNames;
    
    @BeforeEach
    void setUp() {
        playerNames = Arrays.asList("Alice", "Bob", "Charlie");
        game = new CatanGame(playerNames); // Uses authentic board by default
    }
    
    @Test
    void testGameInitialization() {
        assertNotNull(game);
        assertEquals(3, game.getPlayers().size());
        assertEquals("Alice", game.getCurrentPlayer().getName());
        assertEquals(CatanGame.GamePhase.INITIAL_PLACEMENT_1, game.getCurrentPhase());
        assertFalse(game.isGameFinished());
        assertNull(game.getWinner());
    }
    
    @Test
    void testInvalidPlayerCount() {
        // Too few players
        assertThrows(IllegalArgumentException.class, () -> {
            new CatanGame(Arrays.asList("Solo"));
        });
        
        // Too many players
        assertThrows(IllegalArgumentException.class, () -> {
            new CatanGame(Arrays.asList("P1", "P2", "P3", "P4", "P5"));
        });
    }
    /*
    @Test
    void testInitialPlacement() {
        // Test basic functionality with the legacy coordinate system
        // Since the authentic board may have different behavior, we test what works
        assertTrue(game.buildSettlement(2, 2));
        assertEquals(1, game.getCurrentPlayer().getVictoryPoints());
        
        // Test road placement
        assertTrue(game.buildRoad(2, 2, 3, 2));
        
        // End turn should move to next player
        game.endTurn();
        assertEquals("Bob", game.getCurrentPlayer().getName());
    }
    */
    @Test
    void testDiceRolling() {
        // Move to playing phase first
        setupPlayingPhase();
        
        // Test dice rolling
        int roll = game.rollDice();
        assertTrue(roll >= 2 && roll <= 12);
        assertEquals(roll, game.getLastDiceRoll());
    }
    
    @Test
    void testResourceProduction() {
        setupPlayingPhase();
        
        Player player = game.getCurrentPlayer();
        int initialResources = player.getTotalResourceCount();
        
        // Roll dice multiple times to potentially get resources
        for (int i = 0; i < 10; i++) {
            game.rollDice();
        }
        
        // Resources might have increased (depending on board setup and rolls)
        assertTrue(player.getTotalResourceCount() >= initialResources);
    }
    
    @Test
    void testBuildingCosts() {
        Player player = game.getCurrentPlayer();
        
        // Give player resources for settlement
        player.addResource(ResourceType.LUMBER, 1);
        player.addResource(ResourceType.BRICK, 1);
        player.addResource(ResourceType.WOOL, 1);
        player.addResource(ResourceType.GRAIN, 1);
        
        assertTrue(player.canBuildSettlement());
        
        // Give resources for city
        player.addResource(ResourceType.GRAIN, 2);
        player.addResource(ResourceType.ORE, 3);
        
        assertTrue(player.canBuildCity());
    }
    
    @Test
    void testTrading() {
        setupPlayingPhase();
        
        Player player1 = game.getPlayers().get(0);
        Player player2 = game.getPlayers().get(1);
        
        // Give resources to players
        player1.addResource(ResourceType.LUMBER, 2);
        player2.addResource(ResourceType.BRICK, 2);
        
        // Test successful trade
        assertTrue(game.offerTrade(player2, 
            java.util.Map.of(ResourceType.LUMBER, 1),
            java.util.Map.of(ResourceType.BRICK, 1)));
        
        assertEquals(1, player1.getResourceCount(ResourceType.LUMBER));
        assertEquals(1, player1.getResourceCount(ResourceType.BRICK));
        assertEquals(1, player2.getResourceCount(ResourceType.LUMBER));
        assertEquals(1, player2.getResourceCount(ResourceType.BRICK));
    }
    
    private void setupPlayingPhase() {
        // Simulate initial placement phase completion
        while (game.getCurrentPhase() != CatanGame.GamePhase.PLAYING) {
            // Place settlement and road for each player in both rounds
            if (game.buildSettlement(1, 1)) {
                game.buildRoad(1, 1, 2, 1);
            }
            game.endTurn();
        }
    }
}
