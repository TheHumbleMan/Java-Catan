package com.catan.model;

/**
 * Represents hexagonal coordinates for the CATAN board.
 * Uses axial coordinate system (q, r) for hexagonal grids.
 */
public class HexCoordinate {
    private final int q; // column (diagonal coordinate)
    private final int r; // row (diagonal coordinate)
    
    public HexCoordinate(int q, int r) {
        this.q = q;
        this.r = r;
    }
    
    public int getQ() {
        return q;
    }
    
    public int getR() {
        return r;
    }
    
    public int getS() {
        return -q - r; // derived coordinate for cube system
    }
    
    /**
     * Calculate distance between two hex coordinates.
     */
    public int distanceTo(HexCoordinate other) {
        return (Math.abs(q - other.q) + Math.abs(q + r - other.q - other.r) + Math.abs(r - other.r)) / 2;
    }
    
    /**
     * Get neighboring hex coordinates.
     */
    public HexCoordinate[] getNeighbors() {
        return new HexCoordinate[] {
            new HexCoordinate(q + 1, r),     // East
            new HexCoordinate(q + 1, r - 1), // Northeast  
            new HexCoordinate(q, r - 1),     // Northwest
            new HexCoordinate(q - 1, r),     // West
            new HexCoordinate(q - 1, r + 1), // Southwest
            new HexCoordinate(q, r + 1)      // Southeast
        };
    }
    
    /**
     * Convert hex coordinates to pixel coordinates for display.
     */
    public Point2D toPixel(double hexSize) {
        double x = hexSize * (3.0/2 * q);
        double y = hexSize * (Math.sqrt(3)/2 * q + Math.sqrt(3) * r);
        return new Point2D(x, y);
    }
    
    /**
     * Convert hex coordinates to pixel coordinates for authentic CATAN 5-row layout.
     * This method positions hexagons in straight rows (3-4-5-4-3) with vertical symmetry.
     */
    public Point2D toPixelCatan(double hexSize) {
        // CATAN uses straight rows with vertical axis symmetry
        double x, y;
        
        // Vertical spacing between rows
        double rowHeight = hexSize * Math.sqrt(3) * 0.75; // Proper row spacing for pointy-top hexes
        
        // Calculate Y based on row (r coordinate)
        y = r * rowHeight;
        
        // Horizontal spacing between hexes in same row
        double hexWidth = hexSize * 1.5;
        
        // For vertical symmetry, center each row around x=0
        // Row structure: 3-4-5-4-3, centered around the middle hex
        switch (r) {
            case -2: // Top row (3 hexes): q = {-1, 0, 1} -> center at q=0
                x = q * hexWidth;
                break;
            case -1: // Second row (4 hexes): q = {-2, -1, 0, 1} -> center between q=-0.5
                x = (q + 0.5) * hexWidth;
                break;
            case 0:  // Center row (5 hexes): q = {-2, -1, 0, 1, 2} -> center at q=0
                x = q * hexWidth;
                break;
            case 1:  // Fourth row (4 hexes): q = {-2, -1, 0, 1} -> center between q=-0.5
                x = (q + 0.5) * hexWidth;
                break;
            case 2:  // Bottom row (3 hexes): q = {-1, 0, 1} -> center at q=0
                x = q * hexWidth;
                break;
            default:
                x = q * hexWidth;
        }
        
        return new Point2D(x, y);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        HexCoordinate that = (HexCoordinate) obj;
        return q == that.q && r == that.r;
    }
    
    @Override
    public int hashCode() {
        return 31 * q + r;
    }
    
    @Override
    public String toString() {
        return String.format("Hex(%d, %d)", q, r);
    }
    
    /**
     * Helper class for 2D points.
     */
    public static class Point2D {
        public final double x;
        public final double y;
        
        public Point2D(double x, double y) {
            this.x = x;
            this.y = y;
        }
    }
}
