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
     * Convert hex coordinates to pixel coordinates for display.
     */
    
    /**
     * Convert hex coordinates to pixel coordinates for authentic CATAN 5-row layout.
     * This method positions hexagons in straight rows (3-4-5-4-3) with vertical symmetry.
     */
    public RoundedPoint2D toPixelCatan(double hexSize) {
        double hexWidth = Math.sqrt(3) * hexSize; // für pointy-top
        double hexHeight = 2.0 * hexSize;

        double vertSpacing = 0.75 * hexHeight;

        double x;
        double y = r * vertSpacing;

        // 3-4-5-4-3 Layout
        switch (r) {
            case -2: // 3 Hexes, mittig zentriert ➔ +2 * 0.5 * hexWidth Offset
                x = (q + 0.0) * hexWidth;
                break;
            case -1: // 4 Hexes, +0.5 Offset
                x = (q + 0.5) * hexWidth;
                break;
            case 0: // 5 Hexes, kein Offset
                x = q * hexWidth;
                break;
            case 1: // 4 Hexes, +0.5 Offset
                x = (q + 0.5) * hexWidth;
                break;
            case 2: // 3 Hexes, +1.0 Offset
                x = (q + 0.0) * hexWidth;
                break;
            default:
                x = q * hexWidth; // fallback
        }

        return new RoundedPoint2D(x, y);
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
    
}
