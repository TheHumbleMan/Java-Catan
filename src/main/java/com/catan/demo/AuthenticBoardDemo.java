package com.catan.demo;

import com.catan.model.AuthenticCatanBoard;
import com.catan.model.Building;
import com.catan.model.EdgeCoordinate;
import com.catan.model.Player;
import com.catan.model.PlayerColor;
import com.catan.model.VertexCoordinate;

/**
 * Demo für das authentische CATAN-Board mit exakt 54 Siedlungen und 72 Straßen.
 */
/*
public class AuthenticBoardDemo {
    
    public static void main(String[] args) {
        System.out.println("=== AUTHENTISCHES CATAN-BOARD DEMO ===");
        
        try {
            // Erstelle authentisches Board
            AuthenticCatanBoard board = new AuthenticCatanBoard();
            
            System.out.println("✓ Board erfolgreich erstellt!");
            System.out.println("  - Siedlungsplätze: " + board.getValidVertices().size());
            System.out.println("  - Straßenpositionen: " + board.getValidEdges().size());
            System.out.println("  - Hexagon-Tiles: " + board.getAllTiles().size());
            
            // Teste Spieler-Erstellung
            Player alice = new Player("Alice", PlayerColor.RED);
            Player bob = new Player("Bob", PlayerColor.BLUE);
            
            System.out.println("\n=== GAME LOGIC TEST ===");
            
            // Teste Siedlungsplatzierung
            VertexCoordinate firstVertex = board.getValidVertices().iterator().next();
            boolean canPlace = board.canPlaceBuilding(firstVertex, alice);
            System.out.println("Kann Siedlung platzieren: " + canPlace);
            
            if (canPlace) {
                boolean placed = board.placeBuilding(Building.Type.SETTLEMENT, firstVertex, alice);
                System.out.println("Siedlung platziert: " + placed);
                System.out.println("Gebäude insgesamt: " + board.getTotalBuildings());
            }
            
            // Teste Straßenplatzierung
            EdgeCoordinate firstEdge = board.getValidEdges().iterator().next();
            boolean canPlaceRoad = board.canPlaceRoad(firstEdge, alice);
            System.out.println("Kann Straße platzieren: " + canPlaceRoad);
            
            if (canPlaceRoad) {
                boolean placedRoad = board.placeRoad(firstEdge, alice);
                System.out.println("Straße platziert: " + placedRoad);
                System.out.println("Straßen insgesamt: " + board.getTotalRoads());
            }
            
            System.out.println("\n=== BOARD VALIDIERUNG ===");
            
            // Validiere CATAN-Regeln
            if (board.getValidVertices().size() == 54) {
                System.out.println("✓ Exakt 54 Siedlungsplätze - KORREKT");
            } else {
                System.out.println("✗ " + board.getValidVertices().size() + " Siedlungsplätze - FEHLER");
            }
            
            if (board.getValidEdges().size() == 72) {
                System.out.println("✓ Exakt 72 Straßenpositionen - KORREKT");
            } else {
                System.out.println("✗ " + board.getValidEdges().size() + " Straßenpositionen - FEHLER");
            }
            
            if (board.getAllTiles().size() == 19) {
                System.out.println("✓ Exakt 19 Hexagon-Tiles - KORREKT");
            } else {
                System.out.println("✗ " + board.getAllTiles().size() + " Hexagon-Tiles - FEHLER");
            }
            
            System.out.println("\n=== DEMO ERFOLGREICH ABGESCHLOSSEN ===");
            
        } catch (Exception e) {
            System.err.println("Fehler beim Erstellen des authentischen Boards:");
            e.printStackTrace();
        }
    }
}
*/