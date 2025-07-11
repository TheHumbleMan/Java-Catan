package com.catan.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
    private boolean hasRolledDice;
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
        this.hasRolledDice = true;
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
    public int getCurrentPlayerIndex() {
    	return this.currentPlayerIndex;
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
    public boolean hasRolledDice() {
    	return hasRolledDice;
    }
    public void setHasRolledDice(boolean hasRolledDice) {
        this.hasRolledDice = hasRolledDice;
    }
    
    public int rollDice() {
        if (currentPhase != GamePhase.PLAYING) {
            return 0;
        }
        
        int die1 = random.nextInt(6) + 1;
        int die2 = random.nextInt(6) + 1;
        setHasRolledDice(true);
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

    private void initialResourceDistribution() {
    	for (Player player : getPlayers()) {
    	Map<VertexCoordinate, Building> playerBuildings = board.getBuildings().entrySet().stream()
    		    .filter(entry -> entry.getValue().getOwner().equals(player))
    		    .collect(Collectors.toMap(
    		        Map.Entry::getKey,
    		        Map.Entry::getValue
    		    ));
    	Map<HexCoordinate, TerrainTile> tiles = board.getAllTiles();
    	for (Building building : playerBuildings.values()) {
    		List<HexCoordinate> neighbourHexes = board.getHexNeighbours(building.getVertexCoordinate());
    		for (HexCoordinate neighbourHex : neighbourHexes) {
    			TerrainTile tile = tiles.get(neighbourHex);
    			player.setResources(tile.getTerrainType().getResourceType(), 1);
    		}
    	}
    }
 }
    
    public void ingameResourceDistribution(int roll){
    	for (Player player : getPlayers()) {
        	Map<VertexCoordinate, Building> playerBuildings = board.getBuildings().entrySet().stream()
        		    .filter(entry -> entry.getValue().getOwner().equals(player))
        		    .collect(Collectors.toMap(
        		        Map.Entry::getKey,
        		        Map.Entry::getValue
        		    ));
        	Map<HexCoordinate, TerrainTile> tiles = board.getAllTiles();
        	for (Building building : playerBuildings.values()) {
        		List<HexCoordinate> neighbourHexes = board.getHexNeighbours(building.getVertexCoordinate());
        		for (HexCoordinate neighbourHex : neighbourHexes) {
        			TerrainTile tile = tiles.get(neighbourHex);
        			//System.out.println("Checking tile at " + neighbourHex + " with numberToken " + tile.getNumberToken() +
        	                 //  " for roll " + roll);

        			TerrainType type = tile.compareTokens(roll);
        			if (type != null) {
        			player.setResources(type.getResourceType(), 1);
        			Map<ResourceType, Integer> playerResources = player.getResources();
        			System.out.println("Resources for player " + player.getName() + ":");
        			playerResources.forEach((resource, amount) -> 
        			    System.out.println(" - " + resource + ": " + amount));
        			
        			

        			}
    }
        	}
    	}
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
    	boolean skipInkrement = false;
        if (currentPhase == GamePhase.INITIAL_PLACEMENT_1) {
        	this.getCurrentPlayer().setInitialSettlementPlaced(false);
        	this.getCurrentPlayer().setInitialRoadPlaced(false);
            currentPlayerIndex = (currentPlayerIndex + 1);
            System.out.println("index: " + getCurrentPlayerIndex());
            if (currentPlayerIndex == players.size()) {
                currentPhase = GamePhase.INITIAL_PLACEMENT_2;
                currentPlayerIndex = players.size() - 2; // Reverse order
                System.out.println("Geht jetzt in Phase 2");
            }
        } else if (currentPhase == GamePhase.INITIAL_PLACEMENT_2) {
            currentPlayerIndex = (currentPlayerIndex - 1);
            if (currentPlayerIndex == -1) {
                currentPhase = GamePhase.PLAYING;
                currentPlayerIndex = 0;
                skipInkrement = true;
                initialResourceDistribution();
                for (Player player : getPlayers()) {
                System.out.println("=== Ressourcen des Spielers ===" + player.getName());
                player.getResources().forEach((type, amount) -> {
                    System.out.println(type + ": " + amount);
                });
                }                   
                System.out.println("geht jetzt in normale 'Spielphase'");
                setHasRolledDice(false);
            }
        } else {
        	if (!skipInkrement) {
        	    currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        	    setHasRolledDice(false); //für nächste Phase
            }
        	}
        	skipInkrement = false;
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
    public boolean hasSufficientResourcesForRoad() {
    	Player currentPlayer = getCurrentPlayer();
    	return currentPlayer.hasSufficientResources(Player.ROAD_COST);
    	
    }
    public boolean hasSufficientResourcesForSettlement() {
    	Player currentPlayer = getCurrentPlayer();
    	return currentPlayer.hasSufficientResources(Player.SETTLEMENT_COST);
    	
    }
    public boolean hasSufficientResourcesForCity() {
    	Player currentPlayer = getCurrentPlayer();
    	return currentPlayer.hasSufficientResources(Player.CITY_COST);
    	
    }
    
    public boolean isBeginning() {
        return currentPhase == GamePhase.INITIAL_PLACEMENT_1 || currentPhase == GamePhase.INITIAL_PLACEMENT_2;
    }
    public boolean canPlaceAnyBuilding(VertexCoordinate vertex, Player player, boolean isBeginning) {
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
        
        return true;
    }
    public boolean canPlaceSpecificBuilding(VertexCoordinate vertex, Player player, boolean isBeginning, Building.Type type) {
    	boolean canBuildCity = hasSufficientResourcesForCity();
    	boolean canBuildSettlement = hasSufficientResourcesForCity() || isBeginning;
    	
    	if (isBeginning && type == Building.Type.CITY) {
    		return false;
    	}
        // Vertex muss valide sein
        if (!board.getValidVertices().containsKey(vertex) || board.getBuildings().containsKey(vertex)) {
            return false;
        }  
        //Distanzregel
        for (VertexCoordinate vert : vertex.getAdjacentVertices(AuthenticCatanBoard.getHexRadius(), AuthenticCatanBoard.getBoardCenterX(), AuthenticCatanBoard.getBoardCenterY(), board.getNormalizedCatanCoordMap(), board.getValidVertices())) {
        	if (board.getBuildings().containsKey(vert)) {
        		return false;
        	}
        }
        if (type == Building.Type.CITY && !canBuildCity) {
        	return false;
        }
        else if (type == Building.Type.SETTLEMENT && !canBuildSettlement) {
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
        //also falls es sich um letzten turn handelt
        if (isBuildingClose(edge, player) && isBeginning()) {
        	return !isAdjacentRoad(edge, player);
        }
        //prüft ob Anbindung zu Siedlung besteht
        if (isBuildingClose(edge, player)) {
        	return true;
        }
        
        //prüft ob Anbindung zu Straße besteht
        List<Road> ownedRoads = board.getRoads().values().stream()
        	    .filter(b -> b.getOwner().equals(player))
        	    .collect(Collectors.toList());
        for (Road road : ownedRoads) {
            List<VertexCoordinate> roadVertices = road.getEdgeCoordinate().getConnectedVertices();

            for (VertexCoordinate roadVertex : roadVertices) {
                if ((roadVertex.equals(edge.getVertexA()) || roadVertex.equals(edge.getVertexB())) && !isBeginning()) {
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
    
    boolean isBuildingClose(EdgeCoordinate edge, Player player) {
    	List<Building> ownedBuildings = board.getBuildings().values().stream()
        	    .filter(b -> b.getOwner().equals(player))
        	    .collect(Collectors.toList());
        for (Building building : ownedBuildings) {
			if (building.getVertexCoordinate().equals(edge.getVertexA()) || building.getVertexCoordinate().equals(edge.getVertexB())) 
			{	
				return true;
			}
        }
        return false;
    	
    }
    
    boolean isAdjacentRoad(EdgeCoordinate edge, Player player) {
    	List<Road> ownedRoads = board.getRoads().values().stream()
        	    .filter(b -> b.getOwner().equals(player))
        	    .collect(Collectors.toList());
    	Optional<Building> building = board.getBuildings().values().stream()
    		    .filter(b -> b.getVertexCoordinate().equals(edge.getVertexA()) || b.getVertexCoordinate().equals(edge.getVertexB()))
    		    .findFirst();
    	if (building.isPresent()){
    		Building b = building.get();
    		for (Road road : ownedRoads) {
    			if (b.getVertexCoordinate() == road.getEdgeCoordinate().getVertexA() || b.getVertexCoordinate() == road.getEdgeCoordinate().getVertexB()) {
    				return true;
    			}
    		}
    	}
    	return false;
    }
    
    public boolean hasCompletedPlacementForCurrentPhase() {
    	Player player = getCurrentPlayer();
        long settlementCount = board.getBuildings().values().stream()
            .filter(b -> b.getOwner().equals(player))
            .count();

        long roadCount = board.getRoads().values().stream()
            .filter(r -> r.getOwner().equals(player))
            .count();
        
        if (currentPhase == GamePhase.INITIAL_PLACEMENT_1 && getCurrentPlayerIndex() == getPlayers().size() - 1) {
            return settlementCount >= 2 && roadCount >= 2;
        } else if (currentPhase == GamePhase.INITIAL_PLACEMENT_1) {
            return settlementCount >= 1 && roadCount >= 1; 
        } else if (currentPhase == GamePhase.INITIAL_PLACEMENT_2) {
            return settlementCount >= 2 && roadCount >= 2;
        } 
        

        // Falls keine Initialphase, Standard = true (z.B. PlayPhase)
        return true;
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
            existing.setType(Building.Type.CITY);
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
    
    
}
