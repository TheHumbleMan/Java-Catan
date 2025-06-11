package com.catan.model;

/**
 * Represents a road on the CATAN board.
 */
public class Road {
    // Cost of building a road in resources
    public static final java.util.Map<ResourceType, Integer> COST = java.util.Map.of(
        ResourceType.LUMBER, 1,
        ResourceType.BRICK, 1
    );
    
    private final Player owner;
    private final int startX;
    private final int startY;
    private final int endX;
    private final int endY;
    private final EdgeCoordinate edgeCoordinate; // New coordinate system
    
    public Road(Player owner, int startX, int startY, int endX, int endY) {
        this.owner = owner;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.edgeCoordinate = null; // Legacy constructor
    }
    
    public Road(Player owner, EdgeCoordinate edgeCoordinate) {
        this.owner = owner;
        this.edgeCoordinate = edgeCoordinate;
        // Create legacy coordinates from edge coordinate
        VertexCoordinate[] vertices = edgeCoordinate.getConnectedVertices();
        this.startX = vertices[0].getX() * 6 + vertices[0].getDirection();
        this.startY = vertices[0].getY();
        this.endX = vertices[1].getX() * 6 + vertices[1].getDirection();
        this.endY = vertices[1].getY();
    }
    
    public Player getOwner() {
        return owner;
    }
    
    public int getStartX() {
        return startX;
    }
    
    public int getStartY() {
        return startY;
    }
    
    public int getEndX() {
        return endX;
    }
    
    public int getEndY() {
        return endY;
    }
    
    public boolean connectsTo(int x, int y) {
        return (startX == x && startY == y) || (endX == x && endY == y);
    }
    
    public EdgeCoordinate getEdgeCoordinate() {
        return edgeCoordinate;
    }
    
    public boolean connectsToVertex(VertexCoordinate vertex) {
        if (edgeCoordinate == null) {
            // Legacy check
            int vertexLegacyCoord = vertex.getX() * 6 + vertex.getDirection();
            return connectsTo(vertexLegacyCoord, vertex.getY());
        }
        
        VertexCoordinate[] connectedVertices = edgeCoordinate.getConnectedVertices();
        return vertex.equals(connectedVertices[0]) || vertex.equals(connectedVertices[1]);
    }
    
    @Override
    public String toString() {
        return String.format("Road{owner=%s, from=(%d,%d), to=(%d,%d)}", 
                            owner.getName(), startX, startY, endX, endY);
    }
}
