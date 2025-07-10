package com.catan.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Main game class that manages the CATAN game state and rules.
 * Handles turn management, dice rolling, resource production, and victory conditions.
 */
public class CatanGame {
    public static final int VICTORY_POINTS_TO_WIN = 10;
    public static final int MAX_HAND_SIZE_ON_SEVEN = 7;
    
    
    private final List<Player> players;
    private final AuthenticCatanBoard board;
    private final Random random;
    private int currentPlayerIndex;
    private GamePhase currentPhase;
    private boolean gameFinished;
    private Player winner;
    private int lastDiceRoll;
    private HexCoordinate robberPosition;
    
    public enum GamePhase {
        INITIAL_PLACEMENT_1,
        INITIAL_PLACEMENT_2,
        PLAYING
    }
    
   
    
    public CatanGame(List<String> playerNames) {
        if (playerNames.size() < 2 || playerNames.size() > 4) {
            throw new IllegalArgumentException("CATAN requires 2-4 players");
        }
        
        this.players = new ArrayList<>();
        PlayerColor[] colors = PlayerColor.values();
        
        for (int i = 0; i < playerNames.size(); i++) {
            players.add(new Player(playerNames.get(i), colors[i]));
        }
        
        // Initialize board
        this.robberPosition = new HexCoordinate(0, 0); //MUSS MAN NOCH ÄNDERN, STARTET JETZT IMMER IN DER MITTE
        this.board = new AuthenticCatanBoard();
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
    
    public AuthenticCatanBoard getBoard() {
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
           // produceResources(lastDiceRoll);
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
    

    
    /*private void giveInitialResources(int x, int y, Player player) {
        // Give resources from adjacent tiles
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                TerrainTile tile = board.getTile(x + dx, y + dy);
                if (tile != null && tile.getTerrainType().producesResource()) {
                    player.addResource(tile.getTerrainType().getResourceType(), 1);
                }
            }
        }
    } */


    
    public boolean buildRoad(EdgeCoordinate edge) {
        Player currentPlayer = getCurrentPlayer();
        
        if (currentPhase == GamePhase.PLAYING) {
            if (currentPlayer.canBuildRoad() && canPlaceRoad(edge, currentPlayer)) {
            	placeRoad(edge, currentPlayer);
                currentPlayer.buildRoad(edge);
             
            }
                    return true;
                
            
        } else {
            // Initial placement - free road
            if (canPlaceRoad(edge, currentPlayer)) {
                if (placeRoad(edge, currentPlayer)) {
                    return true;
                }
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
            currentPlayerIndex = (currentPlayerIndex + 1);
            if (currentPlayerIndex == players.size() - 1) {
                currentPhase = GamePhase.INITIAL_PLACEMENT_2;
                currentPlayerIndex = players.size() - 1; // Reverse order
                System.out.println("round1");
            }
        } else if (currentPhase == GamePhase.INITIAL_PLACEMENT_2) {
            currentPlayerIndex = (currentPlayerIndex - 1);
            if (currentPlayerIndex == 0) {
                currentPhase = GamePhase.PLAYING;
                currentPlayerIndex = 0;
                System.out.println("round2");
            }
        } else {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
            System.out.println("normal round");
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
    
    public boolean isBeginning() {
        return currentPhase == GamePhase.INITIAL_PLACEMENT_1 || currentPhase == GamePhase.INITIAL_PLACEMENT_2;
    }
    public boolean canPlaceBuilding(VertexCoordinate vertex, Player player, boolean isBeginning) {
    	//ressourcenlimitierung fehlt noch!!
    	
        // Vertex muss valide sein
        if (!board.getValidVertices().containsKey(vertex)) {
            return false;
        }
        
        // Position darf nicht besetzt sein
        if (board.getBuildings().containsKey(vertex)) {
            return false;
        }
        
        //Distanzregel
        for (VertexCoordinate vert : vertex.getAdjacentVertices(AuthenticCatanBoard.getHexRadius(), AuthenticCatanBoard.getBoardCenterX(), AuthenticCatanBoard.getBoardCenterY(), board.getNormalizedCatanCoordMap(), board.getValidVertices())) {
        	if (board.getBuildings().containsKey(vert)) {
        		return false;
        	}
        }
        if (!isBeginning) { //hier dann noch ressourcenlimitierung rein
        	return false;
        }
        return true;
    }
    
    /**
     * Platziert ein Gebäude an einem Vertex.
     */
    public void placeBuilding(Building.Type type, VertexCoordinate vertex, Player player) {
            board.getBuildings().put(vertex, new Building(type, player, vertex));
    }
    
    /**
     * Prüft ob eine Straße an einer Edge platziert werden kann.
     */
    
    public boolean canPlaceRoad(EdgeCoordinate edge, Player player) {
    	
        // Edge muss valide sein
        if (!board.getValidEdges().contains(edge)) {
            return false;
            
        }    
        // Straße darf nicht bereits existieren
        if (board.getRoads().containsKey(edge)){
        	return false;
        }
        //prüft ob Anbindung besteht
        List<Building> ownedBuildings = board.getBuildings().values().stream()
        	    .filter(b -> b.getOwner().equals(player))
        	    .collect(Collectors.toList());
        for (Building building : ownedBuildings) {
			if (building.getVertexCoordinate().equals(edge.getVertexA()) || building.getVertexCoordinate().equals(edge.getVertexB()))
			{
				return true;
			}
		
        }
        List<Road> ownedRoads = board.getRoads().values().stream()
        	    .filter(b -> b.getOwner().equals(player))
        	    .collect(Collectors.toList());
        for (Road road : ownedRoads) {
            List<VertexCoordinate> roadVertices = road.getEdgeCoordinate().getConnectedVertices();

            for (VertexCoordinate roadVertex : roadVertices) {
                if (roadVertex.equals(edge.getVertexA()) || roadVertex.equals(edge.getVertexB())) {
                    return true;
                }
            }
        }
        if (!isBeginning()) {
        	//hier dann noch ressourcentest sobald implementiert
        }
        
        
        return false;
    }
    
    /**
     * Platziert eine Straße an einer Edge.
     */
    public boolean placeRoad(EdgeCoordinate edge, Player player) {
        if (canPlaceRoad(edge, player)) {
            board.getRoads().put(edge, new Road(player, edge));
            return true;
        }
        return false;
    }
  
    
    /**
     * Prüft ob ein Spieler eine benachbarte Straße zu einem Vertex hat.
     */
   /*
    private boolean hasAdjacentRoad(VertexCoordinate vertex, Player player) {
        for (VertexCoordinate adjacentEdge : vertex.getAdjacentEdges()) {
            Road road = roads.get(adjacentEdge);
            if (road != null && road.getOwner() == player) {
                return true;
            }
        }
        return false;
    } */
    
    /**
     * Erweitert eine Siedlung zu einer Stadt.
     */
    public boolean upgradeToCity(VertexCoordinate vertex, Player player) {
        Building existing = board.getBuildings().get(vertex);
        
        if (existing != null && existing.getOwner() == player && 
            existing.getType() == Building.Type.SETTLEMENT) {
            board.getBuildings().put(vertex, new Building(Building.Type.CITY, player, vertex));
            return true;
        }
        return false;
    }
    
    /**
     * Bewegt den Räuber zu einer neuen Position.
     */
    public void moveRobber(HexCoordinate newPosition) {
        // Entferne Räuber von aktueller Position
        TerrainTile currentTile = board.getHexTile(robberPosition);
        if (currentTile != null) {
            currentTile.setRobber(false);
        }
        
        // Platziere Räuber auf neuer Position
        TerrainTile newTile = board.getHexTile(newPosition);
        if (newTile != null) {
            newTile.setRobber(true);
            robberPosition = newPosition;
        }
    } 
    
    /**
     * Gibt alle Gebäude zurück die an ein Tile angrenzen.
     */
    public List<Building> getBuildingsAdjacentToTile(HexCoordinate hexCoord) {
        List<Building> adjacentBuildings = new ArrayList<>();
        
        // Prüfe alle Vertices dieses Hexagons
        for (int direction = 0; direction < 6; direction++) {
            VertexCoordinate vertex = new VertexCoordinate(hexCoord.getQ(), hexCoord.getR(), direction);
            Building building = board.getBuildings().get(vertex);
            if (building != null) {
                adjacentBuildings.add(building);
            }
        }
        
        return adjacentBuildings;
    }
}
