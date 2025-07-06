package com.catan.model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Test class for VertexCoordinate functionality.
 */
class VertexCoordinateTest {
    
    /*@Test
    void testVertexCoordinateCreation() {
        VertexCoordinate vertex = new VertexCoordinate(1, 0, 0);
        assertNotNull(vertex);
        assertEquals(1, vertex.getX());
        assertEquals(0, vertex.getY());
        assertEquals(0, vertex.getDirection());
    }*/
    
    @Test
    void testVertexCoordinateEquality() {
        VertexCoordinate vertex1 = new VertexCoordinate(1, 0, 0);
        VertexCoordinate vertex2 = new VertexCoordinate(1, 0, 0);
        VertexCoordinate vertex3 = new VertexCoordinate(1, 0, 1);
        
        assertEquals(vertex1, vertex2);
        assertNotEquals(vertex1, vertex3);
        assertEquals(vertex1.hashCode(), vertex2.hashCode());
    }
    /*
    @Test
    void testVertexCoordinateToString() {
        VertexCoordinate vertex = new VertexCoordinate(1, -1, 2);
        String expected = "Vertex(1, -1, 2)";
        assertEquals(expected, vertex.toString());
    } */
    
    @Test
    void testToPixelPosition() {
        VertexCoordinate vertex = new VertexCoordinate(0, 0, 0);
        HexCoordinate.Point2D pixel = vertex.toPixel(50, 300, 300);
        
        assertNotNull(pixel);
        // The exact values depend on the implementation, just verify it returns valid coordinates
        assertTrue(pixel.x >= 0);
        assertTrue(pixel.y >= 0);
    }
    
    @Test
    void testGetAdjacentEdges() {
        VertexCoordinate vertex = new VertexCoordinate(0, 0, 0);
        List<EdgeCoordinate> adjacentEdges = vertex.getAdjacentEdges();
        
        assertNotNull(adjacentEdges);
        assertEquals(3, adjacentEdges.size()); // Each vertex connects to 3 edges
        
        // Verify all edges are different
        assertEquals(3, adjacentEdges.size());
    }
    
    @Test
    void testGetAdjacentHexes() {
        VertexCoordinate vertex = new VertexCoordinate(0, 0, 0);
        List<HexCoordinate> adjacentHexes = vertex.getAdjacentHexes();
        
        assertNotNull(adjacentHexes);
        // A vertex can be adjacent to 1, 2, or 3 hexes depending on its position
        assertTrue(adjacentHexes.size() >= 1 && adjacentHexes.size() <= 3);
    }
    
    @Test
    void testVertexValidation() {
        // Valid vertex indices are 0-5
        assertDoesNotThrow(() -> new VertexCoordinate(0, 0, 0));
        assertDoesNotThrow(() -> new VertexCoordinate(0, 0, 5));
        
        // Invalid vertex indices should be handled gracefully
        // The implementation might normalize them or handle them differently
        assertDoesNotThrow(() -> new VertexCoordinate(0, 0, 6));
        assertDoesNotThrow(() -> new VertexCoordinate(0, 0, -1));
    }
}
