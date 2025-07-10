package com.catan.model;

import java.util.List;

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
    private final EdgeCoordinate edge; // New coordinate system
    
    public Road(Player owner, EdgeCoordinate edge) {
        this.owner = owner;
        this.edge = edge;
        // Create legacy coordinates from edge coordinate
        List<VertexCoordinate> vertices = edge.getConnectedVertices();
    }
    
    public Player getOwner() {
        return owner;
    }
    
    
    public EdgeCoordinate getEdgeCoordinate() {
        return edge;
    }
    
    public boolean connectsToVertex(VertexCoordinate vertex) {
        return edge.getConnectedVertices().contains(vertex);
    }
    
    public String toString() {
        List<VertexCoordinate> vertices = edge.getConnectedVertices();
        return String.format("Road{owner=%s, from=%s, to=%s}", 
                             owner.getName(), vertices.get(0), vertices.get(1));
    }
}
