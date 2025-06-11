package com.catan.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a vertex coordinate in the hexagonal CATAN board.
 * Vertices are the intersections where buildings (settlements/cities) can be placed.
 * In a hexagonal grid, each vertex is shared by 2-3 hexagon tiles.
 */
public class VertexCoordinate {
    private final int x;
    private final int y;
    private final int direction; // 0-5, representing which vertex of a hex (0=top, 1=top-right, etc.)
    
    public VertexCoordinate(int x, int y, int direction) {
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
     * Get the hexagon tiles that share this vertex.
     */
    public List<HexCoordinate> getAdjacentHexes() {
        List<HexCoordinate> hexes = new ArrayList<>();
        
        // Add the primary hex that this vertex belongs to
        hexes.add(new HexCoordinate(x, y));
        
        // Add neighboring hexes based on vertex direction
        switch (direction) {
            case 0: // Top vertex
                hexes.add(new HexCoordinate(x, y - 1));
                hexes.add(new HexCoordinate(x - 1, y));
                break;
            case 1: // Top-right vertex
                hexes.add(new HexCoordinate(x + 1, y - 1));
                hexes.add(new HexCoordinate(x + 1, y));
                break;
            case 2: // Bottom-right vertex
                hexes.add(new HexCoordinate(x + 1, y));
                hexes.add(new HexCoordinate(x, y + 1));
                break;
            case 3: // Bottom vertex
                hexes.add(new HexCoordinate(x, y + 1));
                hexes.add(new HexCoordinate(x - 1, y + 1));
                break;
            case 4: // Bottom-left vertex
                hexes.add(new HexCoordinate(x - 1, y + 1));
                hexes.add(new HexCoordinate(x - 1, y));
                break;
            case 5: // Top-left vertex
                hexes.add(new HexCoordinate(x - 1, y));
                hexes.add(new HexCoordinate(x, y - 1));
                break;
        }
        
        return hexes;
    }
    
    /**
     * Get the edges (for roads) that connect to this vertex.
     */
    public List<EdgeCoordinate> getAdjacentEdges() {
        List<EdgeCoordinate> edges = new ArrayList<>();
        
        // Each vertex connects to 3 edges
        int prevDirection = (direction + 5) % 6; // Previous direction
        int nextDirection = (direction + 1) % 6; // Next direction
        
        // Edge to previous vertex of same hex
        edges.add(new EdgeCoordinate(x, y, prevDirection));
        // Edge to next vertex of same hex
        edges.add(new EdgeCoordinate(x, y, direction));
        
        // Edge to vertex on adjacent hex (depends on direction)
        switch (direction) {
            case 0: // Top vertex
                edges.add(new EdgeCoordinate(x, y - 1, 3));
                break;
            case 1: // Top-right vertex
                edges.add(new EdgeCoordinate(x + 1, y - 1, 4));
                break;
            case 2: // Bottom-right vertex
                edges.add(new EdgeCoordinate(x + 1, y, 5));
                break;
            case 3: // Bottom vertex
                edges.add(new EdgeCoordinate(x, y + 1, 0));
                break;
            case 4: // Bottom-left vertex
                edges.add(new EdgeCoordinate(x - 1, y + 1, 1));
                break;
            case 5: // Top-left vertex
                edges.add(new EdgeCoordinate(x - 1, y, 2));
                break;
        }
        
        return edges;
    }
    
    /**
     * Convert vertex coordinate to pixel position for rendering.
     */
    public HexCoordinate.Point2D toPixel(double hexSize, double centerX, double centerY) {
        // First get the hex center using CATAN positioning
        HexCoordinate hexCoord = new HexCoordinate(x, y);
        HexCoordinate.Point2D hexCenter = hexCoord.toPixelCatan(hexSize);
        
        // Calculate vertex offset from hex center
        double angle = (Math.PI / 3.0 * direction) + (Math.PI / 6.0); // Pointy-top orientation
        double vertexRadius = hexSize;
        
        double vertexX = centerX + hexCenter.x + vertexRadius * Math.cos(angle);
        double vertexY = centerY + hexCenter.y + vertexRadius * Math.sin(angle);
        
        return new HexCoordinate.Point2D(vertexX, vertexY);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        VertexCoordinate that = (VertexCoordinate) obj;
        return x == that.x && y == that.y && direction == that.direction;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y, direction);
    }
    
    @Override
    public String toString() {
        return String.format("Vertex(%d, %d, %d)", x, y, direction);
    }
}
