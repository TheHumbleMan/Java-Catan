package com.catan.model;

/**
 * Represents a building (settlement or city) on the CATAN board.
 */
public class Building {
    
    public enum Type {
        SETTLEMENT(1, "Siedlung", "settlement"),
        CITY(2, "Stadt", "city");
        
        private final int victoryPoints;
        private final String germanName;
        private final String englishName;
        
        Type(int victoryPoints, String germanName, String englishName) {
            this.victoryPoints = victoryPoints;
            this.germanName = germanName;
            this.englishName = englishName;
        }
        
        public int getVictoryPoints() {
            return victoryPoints;
        }
        
        public String getGermanName() {
            return germanName;
        }
        
        public String getEnglishName() {
            return englishName;
        }
    }
    
    private final Type type;
    private final Player owner;
    private final int x;
    private final int y;
    private final VertexCoordinate vertexCoordinate; // New coordinate system
    
    public Building(Type type, Player owner, int x, int y) {
        this.type = type;
        this.owner = owner;
        this.x = x;
        this.y = y;
        this.vertexCoordinate = null; // Legacy constructor
    }
    
    public Building(Type type, Player owner, VertexCoordinate vertexCoordinate) {
        this.type = type;
        this.owner = owner;
        this.vertexCoordinate = vertexCoordinate;
        // Use vertex coordinate as legacy x,y for backward compatibility
        this.x = vertexCoordinate.getX() * 6 + vertexCoordinate.getDirection();
        this.y = vertexCoordinate.getY();
    }
    
    public Type getType() {
        return type;
    }
    
    public Player getOwner() {
        return owner;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public VertexCoordinate getVertexCoordinate() {
        return vertexCoordinate;
    }
    
    public int getResourceProduction() {
        return type.getVictoryPoints(); // Cities produce 2 resources, settlements produce 1
    }
    
    @Override
    public String toString() {
        return String.format("Building{type=%s, owner=%s, pos=(%d,%d)}", 
                            type, owner.getName(), x, y);
    }
}
