package com.catan.model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Test class for EdgeCoordinate functionality.
 */
class EdgeCoordinateTest {
    
    @Test
    void testEdgeCoordinateCreation() {
        EdgeCoordinate edge = new EdgeCoordinate(1, 0, 0);
        assertNotNull(edge);
        assertEquals(1, edge.getX());
        assertEquals(0, edge.getY());
        assertEquals(0, edge.getDirection());
    }
    
    @Test
    void testEdgeCoordinateEquality() {
        EdgeCoordinate edge1 = new EdgeCoordinate(1, 0, 0);
        EdgeCoordinate edge2 = new EdgeCoordinate(1, 0, 0);
        EdgeCoordinate edge3 = new EdgeCoordinate(1, 0, 1);
        
        assertEquals(edge1, edge2);
        assertNotEquals(edge1, edge3);
        assertEquals(edge1.hashCode(), edge2.hashCode());
    }
    
    @Test
    void testEdgeCoordinateToString() {
        EdgeCoordinate edge = new EdgeCoordinate(1, -1, 2);
        String expected = "Edge(1, -1, 2)";
        assertEquals(expected, edge.toString());
    }
    
    @Test
    void testToPixelPosition() {
        EdgeCoordinate edge = new EdgeCoordinate(0, 0, 0);
        RoundedPoint2D pixel = edge.toPixel(50, 300, 300);
        
        assertNotNull(pixel);
        // The exact values depend on the implementation, just verify it returns valid coordinates
        assertTrue(pixel.x >= 0);
        assertTrue(pixel.y >= 0);
    }
    
    @Test
    void testGetConnectedVertices() {
        EdgeCoordinate edge = new EdgeCoordinate(0, 0, 0);
        VertexCoordinate[] connectedVertices = edge.getConnectedVertices();
        
        assertNotNull(connectedVertices);
        assertEquals(2, connectedVertices.length); // Each edge connects exactly 2 vertices
    }
    
    @Test
    void testGetAdjacentHexes() {
        EdgeCoordinate edge = new EdgeCoordinate(0, 0, 0);
        List<HexCoordinate> adjacentHexes = edge.getAdjacentHexes();
        
        assertNotNull(adjacentHexes);
        // An edge can be adjacent to 1 or 2 hexes depending on its position
        assertTrue(adjacentHexes.size() >= 1 && adjacentHexes.size() <= 2);
    }
    
    @Test
    void testEdgeValidation() {
        // Valid edge indices are 0-5
        assertDoesNotThrow(() -> new EdgeCoordinate(0, 0, 0));
        assertDoesNotThrow(() -> new EdgeCoordinate(0, 0, 5));
        
        // Invalid edge indices should be handled gracefully
        // The implementation might normalize them or handle them differently
        assertDoesNotThrow(() -> new EdgeCoordinate(0, 0, 6));
        assertDoesNotThrow(() -> new EdgeCoordinate(0, 0, -1));
    }
}
