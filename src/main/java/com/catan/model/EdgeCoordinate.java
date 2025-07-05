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
     * Simple Point2D class for pixel coordinates
     */
    public static class Point2D {
        public final double x;
        public final double y;
        
        public Point2D(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
     /**
     * Get the two vertices that this edge connects.
     */
    public VertexCoordinate[] getConnectedVertices() {
        // Each edge connects two adjacent vertices
        int nextDirection = (direction + 1) % 6;
        
        // First vertex: current direction on this hex
        VertexCoordinate vertex1 = new VertexCoordinate(x, y, direction);
        
        // Second vertex: next direction on this hex
        VertexCoordinate vertex2 = new VertexCoordinate(x, y, nextDirection);
        
        return new VertexCoordinate[]{vertex1, vertex2};
    }

    /**
     * Get the hexagon tiles that share this edge.
     */
    public List<HexCoordinate> getAdjacentHexes() {
        List<HexCoordinate> hexes = new ArrayList<>();
        
        // Add the primary hex that this edge belongs to
        hexes.add(new HexCoordinate(x, y));
        
        // Add neighboring hex based on edge direction (if it exists)
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
        // Get the two vertices that this edge connects
        VertexCoordinate[] vertices = getConnectedVertices();
        
        // Calculate pixel positions for both vertices
        HexCoordinate.Point2D vertex1Pos = vertices[0].toPixel(hexSize, centerX, centerY);
        HexCoordinate.Point2D vertex2Pos = vertices[1].toPixel(hexSize, centerX, centerY);
        
        // Return the midpoint between the two vertices
        double midX = (vertex1Pos.x + vertex2Pos.x) / 2.0;
        double midY = (vertex1Pos.y + vertex2Pos.y) / 2.0;
        
        return new HexCoordinate.Point2D(midX, midY);
    }
    /**
     * Gibt die kanonische (normalisierte) Repräsentation dieser EdgeCoordinate zurück.
     * Dadurch wird sichergestellt, dass dieselbe logische Kante, die von benachbarten Hex-Feldern erzeugt wird,
     * immer konsistent dargestellt wird. Dies verhindert Duplikate in Sets oder Maps.
     */
    public EdgeCoordinate normalize() {
        EdgeCoordinate canonical = this;

        // Berechne Nachbarhex, das diese Kante ebenfalls erzeugt
        HexCoordinate neighborHex = this.getNeighborHexCoordinate(direction);
        int neighborDir = (direction + 3) % 6; // Gegenüberliegende Kante im Nachbarhex

        EdgeCoordinate candidate = new EdgeCoordinate(neighborHex.getQ(), neighborHex.getR(), neighborDir);

        // Wähle lexikografisch kleinste Repräsentation
        if (candidate.compareTo(canonical) < 0) {
            canonical = candidate;
        }

        return canonical;
    }
    private HexCoordinate getNeighborHexCoordinate(int dir) {
        int[] DIRECTION_Q = {1, 1, 0, -1, -1, 0};
        int[] DIRECTION_R = {0, -1, -1, 0, 1, 1};

        int q = this.x + DIRECTION_Q[dir];
        int r = this.y + DIRECTION_R[dir];
        return new HexCoordinate(q, r);
    }
    public int compareTo(EdgeCoordinate other) {
        if (this.x != other.x) return Integer.compare(this.x, other.x);
        if (this.y != other.y) return Integer.compare(this.y, other.y);
        return Integer.compare(this.direction, other.direction);
    }

    
    /**
     * Get the rotation angle for rendering the road on this edge.
     */
    public double getRotationAngle(double hexSize, double centerX, double centerY) {
        // Calculate angle based on direction (each direction is 60 degrees apart)
        return (direction * 60.0) - 30; // Convert direction to degrees
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
