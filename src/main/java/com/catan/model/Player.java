package com.catan.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    private int road_count;
    private final Set<EdgeCoordinate> roads_set;
    private final Set<Building> buildings;
    private boolean initialSettlementPlaced = false;
    private boolean initialRoadPlaced = false;
    

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
    	this.buildings = new HashSet<>();
    	this.roads_set = new HashSet<>();
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
        this.road_count = 15; // Maximum roads per player
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
        return resources.get(type);
    }
    
    public void addResources(Map<ResourceType, Integer> resourcesToAdd) {
        for (Map.Entry<ResourceType, Integer> entry : resourcesToAdd.entrySet()) {
            ResourceType type = entry.getKey();
            int amount = entry.getValue();
            resources.put(type, resources.get(type) + amount);
        }
    } 
    public void addResource(ResourceType type, int amount) {
        resources.put(type, resources.getOrDefault(type, 0) + amount);
    }
    
    public void removeResource(Map<ResourceType, Integer> resourcesToSubtract) {
    	for (Map.Entry<ResourceType, Integer> entry : resourcesToSubtract.entrySet()) {
            ResourceType type = entry.getKey();
            int amount = entry.getValue();
        resources.put(type, resources.get(type) - amount);                          
    	}
    }
    //evtl obsolet
    public boolean canRemoveResource(ResourceType type, int amount) {
        return resources.get(type) >= amount;
    }
    
    public int getTotalResourceCount() {
        return resources.values().stream().mapToInt(Integer::intValue).sum();
    }
    
    public int getSettlementCount() {
        return settlements;
    }
    
    public int getCityCount() {
        return cities;
    }
    
    public int getRoadCount() {
        return road_count;
    }
    public void setSettlementCount(int settlements) {
        this.settlements = settlements;
    }

    public void setCityCount(int cities) {
        this.cities = cities;
    }

    public void setRoadCount(int road_count) {
        this.road_count = road_count;
    }

    
    public boolean hasSufficientResources(Map<ResourceType, Integer> cost) {
        for (Map.Entry<ResourceType, Integer> entry : cost.entrySet()) {
            if (getResourceCount(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }
    
    public boolean spendResources(Map<ResourceType, Integer> cost) {
        if (!hasSufficientResources(cost)) {
            return false;
        }
        
        for (Map.Entry<ResourceType, Integer> entry : cost.entrySet()) {
            removeResource(entry.getKey(), entry.getValue());
        }
        return true;
    }
    public void removeResource(ResourceType type, int amount) {
        int current = resources.getOrDefault(type, 0);
        resources.put(type, current - amount);
    }
    public Map<ResourceType, Integer> stealRandomResource() {
        Map<ResourceType, Integer> resMap = getResources();
        List<ResourceType> weightedList = new ArrayList<>();
        for (Map.Entry<ResourceType, Integer> entry : resMap.entrySet()) {
            ResourceType type = entry.getKey();
            int count = entry.getValue();
            for (int i = 0; i < count; i++) {
                weightedList.add(type);
            }
        }

        if (weightedList.isEmpty()) {
            return Collections.emptyMap(); // Spieler hat keine Ressourcen
        }

        Collections.shuffle(weightedList);
        ResourceType stolen = weightedList.get(0);

        Map<ResourceType, Integer> stolenMap = new HashMap<>();
        stolenMap.put(stolen, 1);

        spendResources(stolenMap); // Einheitlich mit deinem System

        return stolenMap;
    }
    

    
    public int getSettlementsLeft() {
        return settlements;
    }
    
    public int getCitiesLeft() {
        return cities;
    }
    
    public int getRoadsLeft() {
        return road_count;
    }
    public Set<Building> getBuildings() {
        return buildings;
    }
    public boolean isInitialSettlementPlaced() {
        return initialSettlementPlaced;
    }

    public boolean isInitialRoadPlaced() {
        return initialRoadPlaced;
    }
    public void setInitialSettlementPlaced(boolean initialSettlementPlaced) {
        this.initialSettlementPlaced = initialSettlementPlaced;
    }
    public void setInitialRoadPlaced(boolean initialRoadPlaced) {
        this.initialRoadPlaced = initialRoadPlaced;
    }
    public void setResources(ResourceType type, int multiplicity) {
    	resources.merge(type, multiplicity, Integer::sum);
    }
    @Override
    public String toString() {
        return name;
    }

}
