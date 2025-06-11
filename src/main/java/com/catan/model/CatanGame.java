package com.catan.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Main game class that manages the CATAN game state and rules.
 * Handles turn management, dice rolling, resource production, and victory conditions.
 */
public class CatanGame {
    public static final int VICTORY_POINTS_TO_WIN = 10;
    public static final int MAX_HAND_SIZE_ON_SEVEN = 7;
    
    private final List<Player> players;
    private final GameBoard board;
    private final Random random;
    private int currentPlayerIndex;
    private GamePhase currentPhase;
    private boolean gameFinished;
    private Player winner;
    private int lastDiceRoll;
    
    public enum GamePhase {
        INITIAL_PLACEMENT_1,
        INITIAL_PLACEMENT_2,
        PLAYING
    }
    
    public CatanGame(List<String> playerNames) {
        this(playerNames, true, true); // Default to enhanced hexagonal board
    }
    
    public CatanGame(List<String> playerNames, boolean useHexagonal) {
        this(playerNames, useHexagonal, true); // Default to enhanced if hexagonal
    }
    
    public CatanGame(List<String> playerNames, boolean useHexagonal, boolean useEnhanced) {
        if (playerNames.size() < 2 || playerNames.size() > 4) {
            throw new IllegalArgumentException("CATAN requires 2-4 players");
        }
        
        this.players = new ArrayList<>();
        PlayerColor[] colors = PlayerColor.values();
        
        for (int i = 0; i < playerNames.size(); i++) {
            players.add(new Player(playerNames.get(i), colors[i]));
        }
        
        // Initialize board based on hexagonal and enhanced preferences
        if (useHexagonal) {
            this.board = new GameBoard(true, useEnhanced); // Use hexagonal board with optional enhancement
        } else {
            this.board = new GameBoard(); // Use legacy square board
        }
        
        this.random = new Random();
        this.currentPlayerIndex = 0;
        this.currentPhase = GamePhase.INITIAL_PLACEMENT_1;
        this.gameFinished = false;
        this.lastDiceRoll = 0;
    }
    
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }
    
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }
    
    public GameBoard getBoard() {
        return board;
    }
    
    public GamePhase getCurrentPhase() {
        return currentPhase;
    }
    
    public boolean isGameFinished() {
        return gameFinished;
    }
    
    public Player getWinner() {
        return winner;
    }
    
    public int getLastDiceRoll() {
        return lastDiceRoll;
    }
    
    public int rollDice() {
        if (currentPhase != GamePhase.PLAYING) {
            return 0;
        }
        
        int die1 = random.nextInt(6) + 1;
        int die2 = random.nextInt(6) + 1;
        lastDiceRoll = die1 + die2;
        
        if (lastDiceRoll == 7) {
            handleSevenRolled();
        } else {
            produceResources(lastDiceRoll);
        }
        
        return lastDiceRoll;
    }
    
    private void handleSevenRolled() {
        // Each player with more than 7 cards must discard half
        for (Player player : players) {
            int totalCards = player.getTotalResourceCount();
            if (totalCards > MAX_HAND_SIZE_ON_SEVEN) {
                int cardsToDiscard = totalCards / 2;
                // In a real implementation, this would trigger UI for player to choose cards
                discardRandomCards(player, cardsToDiscard);
            }
        }
        
        // Current player must move the robber
        // In a real implementation, this would trigger UI for robber placement
    }
    
    private void discardRandomCards(Player player, int count) {
        List<ResourceType> availableResources = new ArrayList<>();
        
        for (ResourceType type : ResourceType.values()) {
            int amount = player.getResourceCount(type);
            for (int i = 0; i < amount; i++) {
                availableResources.add(type);
            }
        }
        
        Collections.shuffle(availableResources);
        
        for (int i = 0; i < Math.min(count, availableResources.size()); i++) {
            player.removeResource(availableResources.get(i), 1);
        }
    }
    
    private void produceResources(int diceRoll) {
        for (int x = 0; x < GameBoard.BOARD_SIZE; x++) {
            for (int y = 0; y < GameBoard.BOARD_SIZE; y++) {
                TerrainTile tile = board.getTile(x, y);
                if (tile != null && tile.producesResource(diceRoll)) {
                    ResourceType resource = tile.getTerrainType().getResourceType();
                    List<Building> adjacentBuildings = board.getBuildingsAdjacentToTile(x, y);
                    
                    for (Building building : adjacentBuildings) {
                        int production = building.getResourceProduction();
                        building.getOwner().addResource(resource, production);
                    }
                }
            }
        }
    }
    
    public boolean buildSettlement(int x, int y) {
        Player currentPlayer = getCurrentPlayer();
        
        if (currentPhase == GamePhase.PLAYING) {
            if (currentPlayer.canBuildSettlement() && board.canPlaceBuilding(x, y, currentPlayer)) {
                if (currentPlayer.buildSettlement() && board.placeBuilding(Building.Type.SETTLEMENT, x, y, currentPlayer)) {
                    checkVictoryCondition();
                    return true;
                }
            }
        } else {
            // Initial placement - free settlement
            if (board.canPlaceBuilding(x, y, currentPlayer)) {
                if (board.placeBuilding(Building.Type.SETTLEMENT, x, y, currentPlayer)) {
                    currentPlayer.addVictoryPoints(1);
                    
                    // In second round, player gets resources from adjacent tiles
                    if (currentPhase == GamePhase.INITIAL_PLACEMENT_2) {
                        giveInitialResources(x, y, currentPlayer);
                    }
                    
                    return true;
                }
            }
        }
        return false;
    }
    
    private void giveInitialResources(int x, int y, Player player) {
        // Give resources from adjacent tiles
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                TerrainTile tile = board.getTile(x + dx, y + dy);
                if (tile != null && tile.getTerrainType().producesResource()) {
                    player.addResource(tile.getTerrainType().getResourceType(), 1);
                }
            }
        }
    }
    
    public boolean buildCity(int x, int y) {
        Player currentPlayer = getCurrentPlayer();
        
        if (currentPhase != GamePhase.PLAYING) {
            return false;
        }
        
        // Check if there's a settlement at this position owned by current player
        Building existing = null;
        for (Building building : board.getBuildings()) {
            if (building.getX() == x && building.getY() == y && 
                building.getOwner() == currentPlayer && 
                building.getType() == Building.Type.SETTLEMENT) {
                existing = building;
                break;
            }
        }
        
        if (existing != null && currentPlayer.canBuildCity()) {
            if (currentPlayer.buildCity() && board.upgradeToCity(x, y, currentPlayer)) {
                checkVictoryCondition();
                return true;
            }
        }
        return false;
    }
    
    public boolean buildRoad(int startX, int startY, int endX, int endY) {
        Player currentPlayer = getCurrentPlayer();
        
        if (currentPhase == GamePhase.PLAYING) {
            if (currentPlayer.canBuildRoad() && board.canPlaceRoad(startX, startY, endX, endY, currentPlayer)) {
                if (currentPlayer.buildRoad() && board.placeRoad(startX, startY, endX, endY, currentPlayer)) {
                    return true;
                }
            }
        } else {
            // Initial placement - free road
            if (board.canPlaceRoad(startX, startY, endX, endY, currentPlayer)) {
                return board.placeRoad(startX, startY, endX, endY, currentPlayer);
            }
        }
        return false;
    }
    
    public boolean offerTrade(Player otherPlayer, Map<ResourceType, Integer> give, Map<ResourceType, Integer> receive) {
        Player currentPlayer = getCurrentPlayer();
        
        if (currentPhase != GamePhase.PLAYING || otherPlayer == currentPlayer) {
            return false;
        }
        
        // Check if both players have the required resources
        for (Map.Entry<ResourceType, Integer> entry : give.entrySet()) {
            if (currentPlayer.getResourceCount(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        
        for (Map.Entry<ResourceType, Integer> entry : receive.entrySet()) {
            if (otherPlayer.getResourceCount(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        
        // Execute trade
        for (Map.Entry<ResourceType, Integer> entry : give.entrySet()) {
            currentPlayer.removeResource(entry.getKey(), entry.getValue());
            otherPlayer.addResource(entry.getKey(), entry.getValue());
        }
        
        for (Map.Entry<ResourceType, Integer> entry : receive.entrySet()) {
            otherPlayer.removeResource(entry.getKey(), entry.getValue());
            currentPlayer.addResource(entry.getKey(), entry.getValue());
        }
        
        return true;
    }
    
    public void endTurn() {
        if (currentPhase == GamePhase.INITIAL_PLACEMENT_1) {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            if (currentPlayerIndex == 0) {
                currentPhase = GamePhase.INITIAL_PLACEMENT_2;
                currentPlayerIndex = players.size() - 1; // Reverse order
            }
        } else if (currentPhase == GamePhase.INITIAL_PLACEMENT_2) {
            currentPlayerIndex = (currentPlayerIndex - 1 + players.size()) % players.size();
            if (currentPlayerIndex == players.size() - 1) {
                currentPhase = GamePhase.PLAYING;
                currentPlayerIndex = 0;
            }
        } else {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        }
    }
    
    private void checkVictoryCondition() {
        for (Player player : players) {
            if (player.getVictoryPoints() >= VICTORY_POINTS_TO_WIN) {
                gameFinished = true;
                winner = player;
                break;
            }
        }
    }
    
    public void moveRobber(int x, int y) {
        if (currentPhase == GamePhase.PLAYING) {
            board.moveRobber(x, y);
        }
    }
}
