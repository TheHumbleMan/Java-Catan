package com.catan.demo;

import java.util.ArrayList;
import java.util.List;

import com.catan.model.EdgeCoordinate;
import com.catan.model.HexCoordinate;
import com.catan.model.VertexCoordinate;

/**
 * Simple test to understand edge sharing between hexes
 */
public class SimpleEdgeTest {
    public static void main(String[] args) {
        System.out.println("=== Simple Edge Sharing Test ===");
        
        // Test two adjacent hexes
        HexCoordinate hex1 = new HexCoordinate(0, 0);
        HexCoordinate hex2 = new HexCoordinate(1, 0); // Right neighbor
        
        System.out.println("Hex1: " + hex1);
        System.out.println("Hex2: " + hex2);
        
        // Create edges for both hexes
        List<EdgeCoordinate> edges1 = new ArrayList<>();
        List<EdgeCoordinate> edges2 = new ArrayList<>();
        
        for (int dir = 0; dir < 6; dir++) {
            edges1.add(new EdgeCoordinate(hex1.getQ(), hex1.getR(), dir));
            edges2.add(new EdgeCoordinate(hex2.getQ(), hex2.getR(), dir));
        }
        
        System.out.println("\nHex1 edges:");
        for (EdgeCoordinate edge : edges1) {
            System.out.println("  " + edge);
        }
        
        System.out.println("\nHex2 edges:");
        for (EdgeCoordinate edge : edges2) {
            System.out.println("  " + edge);
        }
        
        // Check which edges should be shared
        // Hex1's direction 1 (right edge) should be same as Hex2's direction 4 (left edge)
        EdgeCoordinate hex1Right = new EdgeCoordinate(0, 0, 1);
        EdgeCoordinate hex2Left = new EdgeCoordinate(1, 0, 4);
        
        System.out.println("\nTesting shared edge:");
        System.out.println("Hex1 right edge (0,0,1): " + hex1Right);
        System.out.println("Hex2 left edge (1,0,4): " + hex2Left);
        
        // Check if they have the same vertices
        VertexCoordinate[] vertices1 = hex1Right.getConnectedVertices();
        VertexCoordinate[] vertices2 = hex2Left.getConnectedVertices();
        
        System.out.println("\nHex1 right edge connects:");
        System.out.println("  " + vertices1[0]);
        System.out.println("  " + vertices1[1]);
        
        System.out.println("Hex2 left edge connects:");
        System.out.println("  " + vertices2[0]);
        System.out.println("  " + vertices2[1]);
        
        // Check pixel positions
        try {
            HexCoordinate.Point2D pos1 = hex1Right.toPixel(50.0, 0, 0);
            HexCoordinate.Point2D pos2 = hex2Left.toPixel(50.0, 0, 0);
            
            System.out.println("\nPixel positions:");
            System.out.println("Hex1 right edge: (" + pos1.x + ", " + pos1.y + ")");
            System.out.println("Hex2 left edge: (" + pos2.x + ", " + pos2.y + ")");
            
            double distance = Math.sqrt(Math.pow(pos1.x - pos2.x, 2) + Math.pow(pos1.y - pos2.y, 2));
            System.out.println("Distance between positions: " + distance);
            
        } catch (Exception e) {
            System.out.println("Error calculating pixel positions: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Check the vertex pixel positions to understand the issue
        System.out.println("\n=== Vertex Analysis ===");
        try {
            for (int i = 0; i < 2; i++) {
                HexCoordinate.Point2D vPos1 = vertices1[i].toPixel(50.0, 0, 0);
                HexCoordinate.Point2D vPos2 = vertices2[i].toPixel(50.0, 0, 0);
                
                System.out.println("Vertex " + i + ":");
                System.out.println("  From hex1 edge: " + vertices1[i] + " -> (" + vPos1.x + ", " + vPos1.y + ")");
                System.out.println("  From hex2 edge: " + vertices2[i] + " -> (" + vPos2.x + ", " + vPos2.y + ")");
            }
        } catch (Exception e) {
            System.out.println("Error calculating vertex positions: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
