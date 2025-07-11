package com.catan.model;

/**
 * Represents a terrain tile (hex) on the CATAN board.
 * Each tile has a terrain type, a number token, and hexagonal coordinates.
 */
public class TerrainTile {
    private final TerrainType terrainType;
    private final int numberToken;
    private final HexCoordinate hexCoordinate;
    private final int x; // Legacy coordinate, kept for backward compatibility
    private final int y; // Legacy coordinate, kept for backward compatibility
    private boolean hasRobber;
    
    public TerrainTile(TerrainType terrainType, int numberToken, int x, int y) {
        this(terrainType, numberToken, new HexCoordinate(x, y), x, y);
    }
    
    public TerrainTile(TerrainType terrainType, int numberToken, HexCoordinate hexCoordinate) {
        this(terrainType, numberToken, hexCoordinate, hexCoordinate.getQ(), hexCoordinate.getR());
    }
    
    private TerrainTile(TerrainType terrainType, int numberToken, HexCoordinate hexCoordinate, int x, int y) {
        this.terrainType = terrainType;
        this.numberToken = numberToken;
        this.hexCoordinate = hexCoordinate;
        this.x = x;
        this.y = y;
        this.hasRobber = terrainType == TerrainType.DESERT;
    }
    
    public TerrainType getTerrainType() {
        return terrainType;
    }
    
    public int getNumberToken() {
        return numberToken;
    }
    
    public TerrainType compareTokens(int compare_number) {
    	if (compare_number == this.numberToken) {
    		return this.terrainType;
    	}
    	return null;
    }
    
    public HexCoordinate getHexCoordinate() {
        return hexCoordinate;
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public boolean hasRobber() {
        return hasRobber;
    }
    
    public void setRobber(boolean hasRobber) {
        this.hasRobber = hasRobber;
    }
    
    public boolean producesResource(int diceRoll) {
        return !hasRobber && terrainType.producesResource() && numberToken == diceRoll;
    }
    
    @Override
    public String toString() {
        return String.format("TerrainTile{terrain=%s, number=%d, pos=(%d,%d), robber=%s}", 
                            terrainType, numberToken, x, y, hasRobber);
    }
}
