package com.catan.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents an edge coordinate in the hexagonal CATAN board.
 * Edges are where roads can be placed, connecting two vertices.
 * Each edge lies between two hexagon tiles.
 */
public class EdgeCoordinate {
    private final int x;
    private final int y;
    private final int direction; // 0-5, representing which edge of a hex (0=top-right, 1=right, 2=bottom-right, etc.)
    
    public EdgeCoordinate(int x, int y, int direction) {
        this.x = x;
        this.y = y;
        this.direction = direction % 6; // Ensure direction is 0-5
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getDirection() {
        return direction;
    }
    
    /**
     * Get the two vertices that this edge connects.
     */
    public VertexCoordinate[] getConnectedVertices() {
        VertexCoordinate vertex1 = new VertexCoordinate(x, y, direction);
        VertexCoordinate vertex2 = new VertexCoordinate(x, y, (direction + 1) % 6);
        return new VertexCoordinate[]{vertex1, vertex2};
    }
    
    /**
     * Get the hexagon tiles that share this edge.
     */
    public List<HexCoordinate> getAdjacentHexes() {
        List<HexCoordinate> hexes = new ArrayList<>();
        
        // Add the primary hex that this edge belongs to
        hexes.add(new HexCoordinate(x, y));
        
        // Add the neighboring hex on the other side of this edge
        switch (direction) {
            case 0: // Top-right edge
                hexes.add(new HexCoordinate(x + 1, y - 1));
                break;
            case 1: // Right edge
                hexes.add(new HexCoordinate(x + 1, y));
                break;
            case 2: // Bottom-right edge
                hexes.add(new HexCoordinate(x, y + 1));
                break;
            case 3: // Bottom-left edge
                hexes.add(new HexCoordinate(x - 1, y + 1));
                break;
            case 4: // Left edge
                hexes.add(new HexCoordinate(x - 1, y));
                break;
            case 5: // Top-left edge
                hexes.add(new HexCoordinate(x, y - 1));
                break;
        }
        
        return hexes;
    }
    
    /**
     * Convert edge coordinate to pixel position for rendering.
     */
    public HexCoordinate.Point2D toPixel(double hexSize, double centerX, double centerY) {
        // First get the hex center using CATAN positioning
        HexCoordinate hexCoord = new HexCoordinate(x, y);
        HexCoordinate.Point2D hexCenter = hexCoord.toPixelCatan(hexSize);
        
        // Get the two vertices that this edge connects
        VertexCoordinate[] vertices = getConnectedVertices();
        HexCoordinate.Point2D vertex1Pos = vertices[0].toPixel(hexSize, centerX, centerY);
        HexCoordinate.Point2D vertex2Pos = vertices[1].toPixel(hexSize, centerX, centerY);
        
        // Calculate edge center (midpoint between vertices)
        double edgeCenterX = (vertex1Pos.x + vertex2Pos.x) / 2.0;
        double edgeCenterY = (vertex1Pos.y + vertex2Pos.y) / 2.0;
        
        return new HexCoordinate.Point2D(edgeCenterX, edgeCenterY);
    }
    
    /**
     * Get the rotation angle for rendering the road on this edge.
     */
    public double getRotationAngle(double hexSize, double centerX, double centerY) {
        // Get the two vertices that this edge connects
        VertexCoordinate[] vertices = getConnectedVertices();
        HexCoordinate.Point2D vertex1Pos = vertices[0].toPixel(hexSize, centerX, centerY);
        HexCoordinate.Point2D vertex2Pos = vertices[1].toPixel(hexSize, centerX, centerY);
        
        // Calculate angle between vertices
        double deltaX = vertex2Pos.x - vertex1Pos.x;
        double deltaY = vertex2Pos.y - vertex1Pos.y;
        
        return Math.toDegrees(Math.atan2(deltaY, deltaX));
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        EdgeCoordinate that = (EdgeCoordinate) obj;
        return x == that.x && y == that.y && direction == that.direction;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y, direction);
    }
    
    @Override
    public String toString() {
        return String.format("Edge(%d, %d, %d)", x, y, direction);
    }
}
