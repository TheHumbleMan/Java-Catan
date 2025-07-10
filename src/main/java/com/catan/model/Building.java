package com.catan.model;

/**
 * Represents a building (settlement or city) on the CATAN board.
 */
public class Building {
    
    public enum Type {
        SETTLEMENT(1, "settlement"),
        CITY(2, "city");
        
        private final int victoryPoints;
        private final String name;
        Type(int victoryPoints, String name) {
            this.victoryPoints = victoryPoints;
            this.name = name;
        }
        
        public int getVictoryPoints() {
            return victoryPoints;
        }
        
        public String getEnglishName() {
            return name;
        }
    }
    
    private final Type type;
    private final Player owner;
    private final VertexCoordinate vertexCoordinate; // New coordinate system
    
    
    public Building(Type type, Player owner, VertexCoordinate vertexCoordinate) {
        this.type = type;
        this.owner = owner;
        this.vertexCoordinate = vertexCoordinate;
    }
    
    public Type getType() {
        return type;
    }
    
    public Player getOwner() {
        return owner;
    }
    
    public VertexCoordinate getVertexCoordinate() {
        return vertexCoordinate;
    }
    
    public int getResourceProduction() {
        return type.getVictoryPoints(); // Cities produce 2 resources, settlements produce 1
    }
    
    @Override
    public String toString() {
        return String.format("Building{type=%s, owner=%s, pos=%s}", 
                             type.getEnglishName(), owner.getName(), vertexCoordinate);
    }
}

