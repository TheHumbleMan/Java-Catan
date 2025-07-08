package com.catan.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    public List<VertexCoordinate> getAdjacentVertices(double hexSize, double centerX, double centerY, Map<RoundedPoint2D, VertexCoordinate> coordMap) {
        List<VertexCoordinate> vertices = new ArrayList<>();
        //add current 
        VertexCoordinate vertex0 = new VertexCoordinate(x, y, direction);
        VertexCoordinate vertex1 = new VertexCoordinate(x, y, (direction+1) % 6);
        VertexCoordinate vertex2 = new VertexCoordinate(x, y, (direction-1) % 6);
        VertexCoordinate vertex3 = null;
        vertices.add(vertex0);
        vertices.add(vertex1);
        vertices.add(vertex2);
        if (direction == 1 || direction == 3 || direction == 5) {
        	vertex3 = calculateVerticeParityOdd(hexSize, centerX, centerY, vertex0, vertex1, vertex2, coordMap);
        }
        else if (direction == 0 || direction == 2 || direction == 4) {
        	vertex3 = calculateVerticeParityEven(hexSize, centerX, centerY, vertex0, vertex1, vertex2, coordMap);
        }
        vertices.add(vertex3);
        return vertices;
    }
    
    /**
     * Convert vertex coordinate to pixel position for rendering.
     */
    public RoundedPoint2D toPixel(double hexSize, double centerX, double centerY) { //hexSize is radius
        // First get the hex center using CATAN positioning
        HexCoordinate hexCoord = new HexCoordinate(x, y);
        RoundedPoint2D hexCenter = hexCoord.toPixelCatan(hexSize);
        
        // Calculate vertex offset from hex center
        double vertexRadius = hexSize;
        
        double vertexX = centerX + hexCenter.x + vertexRadius * Math.cos((Math.PI / 2) - (direction * Math.PI / 3.0));
        double vertexY = centerY + hexCenter.y - vertexRadius * Math.sin((Math.PI / 2) - (direction * Math.PI / 3.0));
        return new RoundedPoint2D(vertexX, vertexY);
    }
   public VertexCoordinate calculateVerticeParityOdd (double hexSize, double centerX, double centerY,VertexCoordinate vertex0, VertexCoordinate vertex1, VertexCoordinate vertex2, Map<RoundedPoint2D, VertexCoordinate> coordMap) {
	   
       RoundedPoint2D centerVertice = vertex0.toPixel(hexSize, centerX, centerY);
	   RoundedPoint2D comp1 = vertex1.toPixel(hexSize, centerX, centerY);
	   RoundedPoint2D comp2 = vertex2.toPixel(hexSize, centerX, centerY);
	   VertexCoordinate vertex = null;
	   for (int i = 0; i<3;i++) {
		   double vertexX = centerVertice.getX() + hexSize * Math.cos((Math.PI / 6) + (4 * i * Math.PI / 6.0));
	       double vertexY = centerVertice.getY() - hexSize * Math.sin((Math.PI / 6) + (4 * i * Math.PI / 6.0));
	       RoundedPoint2D point = new RoundedPoint2D(vertexX, vertexY);
	       if (!point.equals(comp1) && !point.equals(comp2)) {
	    	   vertex = coordMap.get(point);
	       }
	       }
	   return vertex;
    } 
   
   //ist eigentlich wie parityodd nur mit anderem startwinkel
   public VertexCoordinate calculateVerticeParityEven (double hexSize, double centerX, double centerY, VertexCoordinate vertex0, VertexCoordinate vertex1, VertexCoordinate vertex2, Map<RoundedPoint2D, VertexCoordinate> coordMap) {
	   RoundedPoint2D centerVertice = vertex0.toPixel(hexSize, centerX, centerY);
	   RoundedPoint2D comp1 = vertex1.toPixel(hexSize, centerX, centerY);
	   RoundedPoint2D comp2 = vertex2.toPixel(hexSize, centerX, centerY);
	   VertexCoordinate vertex = null;
	   for (int i = 0; i<3;i++) {
		   double vertexX = centerVertice.getX() + hexSize * Math.cos((Math.PI / 2) + (4 * i * Math.PI / 6.0));
	       double vertexY = centerVertice.getY() - hexSize * Math.sin((Math.PI / 2) + (4 * i * Math.PI / 6.0));
	       RoundedPoint2D point = new RoundedPoint2D(vertexX, vertexY);
	       if (!point.equals(comp1) && !point.equals(comp2)) {
	    	   vertex = coordMap.get(point);
	       }
	       }
	   return vertex;
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
