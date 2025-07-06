package com.catan.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
        this.direction = (direction) % 6; // Ensure direction is 0-5
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
        int[] DIRECTION_Q_m2 = {0, 1, 0, -1, -1, 0}; //change Q_coordinate to unreasonable value, R can be ignored since field is off_limits
        int[] DIRECTION_R_m2 = {0, -1, -1, 0, 1, 1};
        int[] DIRECTION_Q_m1 = {1, 1, 0, -1, -1, 0};
        int[] DIRECTION_R_m1 = {0, -1, -1, 0, 1, 1};
        int[] DIRECTION_Q_0 = {1, 1, 0, -1, -1, 0};
        int[] DIRECTION_R_0 = {0, -1, -1, 0, 1, 1};
        int[] DIRECTION_Q_1 = {1, 1, 0, -1, -1, 0};
        int[] DIRECTION_R_1 = {0, -1, -1, 0, 1, 1};
        int[] DIRECTION_Q_2 = {1, 1, 0, -1, -1, 0};
        int[] DIRECTION_R_2 = {0, -1, -1, 0, 1, 1};
        // Edge to previous vertex of same hex
        edges.add(new EdgeCoordinate(x, y, prevDirection));
        edges.add(new EdgeCoordinate(x, y, nextDirection));
        edges.add(new EdgeCoordinate(2, 2, 0));
        // Edge to next vertex of same hex
        
        return edges;
    }
    
    /**
     * Convert vertex coordinate to pixel position for rendering.
     */
    public HexCoordinate.Point2D toPixel(double hexSize, double centerX, double centerY) { //hexSize is radius
        // First get the hex center using CATAN positioning
        HexCoordinate hexCoord = new HexCoordinate(x, y);
        HexCoordinate.Point2D hexCenter = hexCoord.toPixelCatan(hexSize);
        
        // Calculate vertex offset from hex center
        double angle = (Math.PI / 3.0 * direction) + (Math.PI / 6.0); // Pointy-top orientation
        double vertexRadius = hexSize;
        
        double vertexX = centerX + hexCenter.x + vertexRadius * Math.cos((Math.PI / 2) - (direction * Math.PI / 3.0));
        double vertexY = centerY + hexCenter.y + vertexRadius * Math.sin((Math.PI / 2) - (direction * Math.PI / 3.0));
        return new HexCoordinate.Point2D(vertexX, vertexY);
    }
   



   
    public int compareTo(VertexCoordinate other) {
        if (this.x != other.x) return Integer.compare(this.x, other.x);
        if (this.y != other.y) return Integer.compare(this.y, other.y);
        return Integer.compare(this.direction, other.direction);
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
