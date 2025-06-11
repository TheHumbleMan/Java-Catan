package com.catan.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a player in the CATAN game.
 * Manages the player's resources, buildings, and victory points.
 */
public class Player {
    private final String name;
    private final PlayerColor color;
    private final Map<ResourceType, Integer> resources;
    private int victoryPoints;
    private int settlements;
    private int cities;
    private int roads;
    
    // Building costs
    public static final Map<ResourceType, Integer> SETTLEMENT_COST = Map.of(
        ResourceType.LUMBER, 1,
        ResourceType.BRICK, 1,
        ResourceType.WOOL, 1,
        ResourceType.GRAIN, 1
    );
    
    public static final Map<ResourceType, Integer> CITY_COST = Map.of(
        ResourceType.GRAIN, 2,
        ResourceType.ORE, 3
    );
    
    public static final Map<ResourceType, Integer> ROAD_COST = Map.of(
        ResourceType.LUMBER, 1,
        ResourceType.BRICK, 1
    );
    
    public Player(String name, PlayerColor color) {
        this.name = name;
        this.color = color;
        this.resources = new HashMap<>();
        
        // Initialize resources to 0
        for (ResourceType type : ResourceType.values()) {
            resources.put(type, 0);
        }
        
        this.victoryPoints = 0;
        this.settlements = 5; // Maximum settlements per player
        this.cities = 4; // Maximum cities per player
        this.roads = 15; // Maximum roads per player
    }
    
    public String getName() {
        return name;
    }
    
    public PlayerColor getColor() {
        return color;
    }
    
    public int getVictoryPoints() {
        return victoryPoints;
    }
    
    public void addVictoryPoints(int points) {
        this.victoryPoints += points;
    }
    
    public Map<ResourceType, Integer> getResources() {
        return new HashMap<>(resources);
    }
    
    public int getResourceCount(ResourceType type) {
        return resources.getOrDefault(type, 0);
    }
    
    public void addResource(ResourceType type, int amount) {
        resources.put(type, resources.getOrDefault(type, 0) + amount);
    }
    
    public boolean removeResource(ResourceType type, int amount) {
        int current = resources.getOrDefault(type, 0);
        if (current >= amount) {
            resources.put(type, current - amount);
            return true;
        }
        return false;
    }
    
    public int getTotalResourceCount() {
        return resources.values().stream().mapToInt(Integer::intValue).sum();
    }
    
    public boolean canBuildSettlement() {
        return settlements > 0 && hasResources(SETTLEMENT_COST);
    }
    
    public boolean canBuildCity() {
        return cities > 0 && hasResources(CITY_COST);
    }
    
    public boolean canBuildRoad() {
        return roads > 0 && hasResources(ROAD_COST);
    }
    
    private boolean hasResources(Map<ResourceType, Integer> cost) {
        for (Map.Entry<ResourceType, Integer> entry : cost.entrySet()) {
            if (getResourceCount(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }
    
    public boolean spendResources(Map<ResourceType, Integer> cost) {
        if (!hasResources(cost)) {
            return false;
        }
        
        for (Map.Entry<ResourceType, Integer> entry : cost.entrySet()) {
            removeResource(entry.getKey(), entry.getValue());
        }
        return true;
    }
    
    public boolean buildSettlement() {
        if (canBuildSettlement() && spendResources(SETTLEMENT_COST)) {
            settlements--;
            addVictoryPoints(1);
            return true;
        }
        return false;
    }
    
    public boolean buildCity() {
        if (canBuildCity() && spendResources(CITY_COST)) {
            cities--;
            settlements++; // Settlement is converted to city
            addVictoryPoints(1); // City gives 2 points total (1 from settlement + 1 from upgrade)
            return true;
        }
        return false;
    }
    
    public boolean buildRoad() {
        if (canBuildRoad() && spendResources(ROAD_COST)) {
            roads--;
            return true;
        }
        return false;
    }
    
    public int getSettlementsLeft() {
        return settlements;
    }
    
    public int getCitiesLeft() {
        return cities;
    }
    
    public int getRoadsLeft() {
        return roads;
    }
}
