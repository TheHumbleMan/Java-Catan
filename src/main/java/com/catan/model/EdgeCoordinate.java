package com.catan.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Represents an edge coordinate in the hexagonal CATAN board.
 * Edges are where roads can be placed, connecting two vertices.
 * Each edge lies between two hexagon tiles.
 */
public class EdgeCoordinate {
	private final VertexCoordinate vertexA;
    private final VertexCoordinate vertexB;
    
    public EdgeCoordinate(VertexCoordinate vertexA, VertexCoordinate vertexB) {
        this.vertexA = vertexA;
        this.vertexB = vertexB;
    }
    
    public VertexCoordinate getVertexA() {
        return vertexA;
    }
    
    public VertexCoordinate getVertexB() {
        return vertexB;
    }
    
    

    
    /**
     * Convert edge coordinate to pixel position for rendering.
     */
    public RoundedPoint2D toPixel(double hexSize, double centerX, double centerY) {
        // Get the two vertices that this edge connects
        VertexCoordinate[] vertices = {vertexA, vertexB};
        
        // Calculate pixel positions for both vertices
        RoundedPoint2D vertexAPos = vertices[0].toPixel(hexSize, centerX, centerY);
        RoundedPoint2D vertexBPos = vertices[1].toPixel(hexSize, centerX, centerY);
        
        // Return the midpoint between the two vertices
        double midX = (vertexAPos.x + vertexBPos.x) / 2.0;
        double midY = (vertexAPos.y + vertexBPos.y) / 2.0;
        
        return new RoundedPoint2D(midX, midY);
    }
   
  
    /**
     * Get the rotation angle for rendering the road on this edge.
     */
    public double getRotationAngle(double hexSize, double centerX, double centerY) {
        RoundedPoint2D posA = vertexA.toPixel(hexSize, centerX, centerY);
        RoundedPoint2D posB = vertexB.toPixel(hexSize, centerX, centerY);

        double deltaX = posB.getX() - posA.getX();
        double deltaY = posB.getY() - posA.getY();

        double angleRad = Math.atan2(deltaY, deltaX);
        double angleDeg = Math.toDegrees(angleRad);

        if (angleDeg < 0) {
            angleDeg += 360;
        }

        return angleDeg;
    }
    public List<VertexCoordinate> getConnectedVertices(){
    	return Arrays.asList(getVertexA(), getVertexB());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EdgeCoordinate that = (EdgeCoordinate) obj;

        // Prüfe ungerichtete Gleichheit: (A,B) == (B,A)
        return (vertexA.equals(that.vertexA) && vertexB.equals(that.vertexB)) ||
               (vertexA.equals(that.vertexB) && vertexB.equals(that.vertexA));
    }
    
    @Override
    public int hashCode() {
        // Addition ist kommutativ: a+b = b+a
        return vertexA.hashCode() + vertexB.hashCode();
    }

    
    @Override
    public String toString() {
        return String.format("Edge[%s <-> %s]", vertexA, vertexB);
    }
}
