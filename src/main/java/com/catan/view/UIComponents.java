package com.catan.view;

import com.catan.model.Building;
import com.catan.model.PlayerColor;
import com.catan.model.ResourceType;
import com.catan.model.TerrainTile;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Hilfklasse für die Erstellung und Gestaltung von JavaFX UI-Komponenten für das CATAN-Spiel.
 * Diese Klasse stellt statische Methoden zur Verfügung, um verschiedene Spielelemente
 * wie Sechsecke, Gebäude, Straßen und interaktive Bauplätze zu erstellen.
 */
public class UIComponents {
    
    // Farbkonstanten für verschiedene Geländetypen
    public static final Color FOREST_COLOR = Color.DARKGREEN;      // Waldfarbe
    public static final Color HILLS_COLOR = Color.SADDLEBROWN;     // Hügelfarbe
    public static final Color PASTURE_COLOR = Color.LIGHTGREEN;    // Weidelandfarbe
    public static final Color FIELDS_COLOR = Color.GOLD;           // Ackerlandfarbe
    public static final Color MOUNTAINS_COLOR = Color.GRAY;        // Gebirgsfarbe
    public static final Color DESERT_COLOR = Color.SANDYBROWN;     // Wüstenfarbe
    public static final Color OCEAN_COLOR = Color.LIGHTBLUE;       // Ozeanfarbe
    
    // Größenkonstanten für Spielelemente
    public static final double TILE_SIZE = 80.0;              // Größe der Spielfelder
    public static final double HEX_RADIUS = 45.0;             // Radius der Sechsecke
    public static final double BUILDING_SIZE = 12.0;          // Größe der Gebäude
    public static final double ROAD_WIDTH = 6.0;              // Breite der Straßen
    
    // Konstanten für Bauplätze - optimiert für kompaktes Layout
    public static final double BUILDING_SPOT_SIZE = 10.0;     // Größe der Bauplätze für Klickbarkeit
    public static final double ROAD_SPOT_LENGTH = 16.0;       // Länge der Straßenbauplätze
    public static final double ROAD_SPOT_WIDTH = 2.5;         // Breite der Straßenbauplätze
    
    // Farben für verschiedene Bauplatz-Zustände
    public static final Color AVAILABLE_SPOT_COLOR = Color.LIGHTBLUE;     // Verfügbare Bauplätze
    public static final Color UNAVAILABLE_SPOT_COLOR = Color.LIGHTGRAY;   // Nicht verfügbare Bauplätze
    public static final Color HOVER_SPOT_COLOR = Color.YELLOW;            // Bauplätze bei Maus-Hover
    public static final Color OCCUPIED_SPOT_COLOR = Color.DARKGRAY;       // Besetzte Bauplätze
    
    /**
     * Erstellt ein sechseckiges Geländefeld mit angemessener Gestaltung.
     * Verwendet den Standard-Radius für die Sechsecke.
     */
    public static Polygon createHexagonalTerrainTile(TerrainTile tile) {
        return createHexagonalTerrainTile(tile, HEX_RADIUS);
    }
    
    /**
     * Erstellt ein sechseckiges Geländefeld mit angemessener Gestaltung und benutzerdefiniertem Radius.
     * @param tile Das Geländefeld-Objekt mit den Eigenschaften
     * @param radius Der Radius des Sechsecks
     * @return Ein gestaltetes Sechseck-Polygon
     */
    public static Polygon createHexagonalTerrainTile(TerrainTile tile, double radius) {
        Polygon hexagon = createHexagon(radius);
        hexagon.setStroke(Color.BLACK);            // Schwarzer Rand
        hexagon.setStrokeWidth(2.0);              // Randbreite
        
        // Hover-Effekt hinzufügen - Hervorhebung bei Mausberührung
        hexagon.setOnMouseEntered(e -> hexagon.setStroke(Color.YELLOW));
        hexagon.setOnMouseExited(e -> hexagon.setStroke(Color.BLACK));
        
        return hexagon;
    }
    
    /**
     * Erstellt ein Sechseck-Polygon mit gegebenem Radius.
     * Verwendet spitze-Spitze-Ausrichtung für authentisches CATAN-Aussehen.
     * @param radius Der Radius des Sechsecks
     * @return Ein Sechseck-Polygon mit 6 Eckpunkten
     */
    public static Polygon createHexagon(double radius) {
        Polygon hexagon = new Polygon();
        
        // Berechne die 6 Eckpunkte des Sechsecks mit spitze-Spitze-Ausrichtung
        for (int i = 0; i < 6; i++) {
            // 30° Versatz für spitze-Spitze-Ausrichtung (wie bei CATAN)
            double angle = (2 * Math.PI * i / 6) + Math.PI / 6;
            double x = radius * Math.cos(angle);      // X-Koordinate
            double y = radius * Math.sin(angle);      // Y-Koordinate
            hexagon.getPoints().addAll(x, y);
        }
        
        return hexagon;
    }
    
    /**
     * Erstellt ein rechteckiges Geländefeld mit angemessener Gestaltung.
     * @deprecated Verwende stattdessen createHexagonalTerrainTile für authentische CATAN-Erfahrung
     */
    @Deprecated
    public static Rectangle createTerrainTile(TerrainTile tile) {
        Rectangle rect = new Rectangle(TILE_SIZE, TILE_SIZE);
        
        // Setze Farbe basierend auf Geländetyp
        Color fillColor = switch (tile.getTerrainType()) {
            case FOREST -> FOREST_COLOR;
            case HILLS -> HILLS_COLOR;
            case PASTURE -> PASTURE_COLOR;
            case FIELDS -> FIELDS_COLOR;
            case MOUNTAINS -> MOUNTAINS_COLOR;
            case DESERT -> DESERT_COLOR;
        };
        
        rect.setFill(fillColor);              // Füllfarbe setzen
        rect.setStroke(Color.BLACK);          // Schwarzer Rand
        rect.setStrokeWidth(2.0);
        
        // Hover-Effekt hinzufügen
        rect.setOnMouseEntered(e -> rect.setStroke(Color.YELLOW));
        rect.setOnMouseExited(e -> rect.setStroke(Color.BLACK));
        
        return rect;
    }
    
    /**
     * Erstellt einen Zahlen-Token-Text für Geländefelder.
     * Hebt Zahlen mit hoher Wahrscheinlichkeit (6 und 8) hervor.
     * @param number Die Zahl, die auf dem Token angezeigt werden soll
     * @return Ein gestaltetes Text-Element
     */
    public static Text createNumberToken(int number) {
        Text text = new Text(String.valueOf(number));
        text.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        text.setFill(Color.BLACK);
        
        // Hebe Zahlen mit hoher Wahrscheinlichkeit hervor
        if (number == 6 || number == 8) {
            text.setFill(Color.RED);                          // Rote Farbe für wichtige Zahlen
            text.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        }
        
        // Text zentrieren
        text.setTextOrigin(javafx.geometry.VPos.CENTER);
        text.setX(0);
        text.setY(0);
        
        return text;
    }
    
    /**
     * Erstellt eine Gebäude-Form basierend auf Typ und Spielerfarbe.
     * @param type Der Gebäudetyp (SETTLEMENT oder CITY)
     * @param playerColor Die Farbe des Spielers
     * @return Ein Polygon, das das Gebäude darstellt
     */
    public static Polygon createBuilding(Building.Type type, PlayerColor playerColor) {
        Polygon building = new Polygon();
        
        if (type == Building.Type.SETTLEMENT) {
            // Erstelle Hausform (Fünfeck) für Siedlung
            building.getPoints().addAll(new Double[]{
                0.0, BUILDING_SIZE,                    // unten links
                BUILDING_SIZE, BUILDING_SIZE,          // unten rechts
                BUILDING_SIZE, BUILDING_SIZE/2,        // mitte rechts
                BUILDING_SIZE/2, 0.0,                  // Dachspitze
                0.0, BUILDING_SIZE/2                   // mitte links
            });
        } else { // CITY
            // Erstelle größere, komplexere Form für Stadt
            building.getPoints().addAll(new Double[]{
                0.0, BUILDING_SIZE * 1.5,                    // unten links
                BUILDING_SIZE * 1.2, BUILDING_SIZE * 1.5,    // unten rechts
                BUILDING_SIZE * 1.2, BUILDING_SIZE * 0.3,    // oben rechts
                BUILDING_SIZE * 0.8, 0.0,                    // Spitze rechts
                BUILDING_SIZE * 0.4, BUILDING_SIZE * 0.3,    // Tal
                0.0, BUILDING_SIZE * 0.6                     // oben links
            });
        }
        
        // Setze Spielerfarbe und Rand
        building.setFill(Color.web(playerColor.getHexColor()));
        building.setStroke(Color.BLACK);
        building.setStrokeWidth(1.5);
        
        return building;
    }
    
    /**
     * Erstellt ein Straßen-Rechteck mit Spielerfarbe.
     * @param playerColor Die Farbe des Spielers
     * @param length Die Länge der Straße
     * @return Ein Rechteck, das die Straße darstellt
     */
    public static Rectangle createRoad(PlayerColor playerColor, double length) {
        Rectangle road = new Rectangle(length, ROAD_WIDTH);
        road.setFill(Color.web(playerColor.getHexColor()));    // Spielerfarbe
        road.setStroke(Color.BLACK);                           // Schwarzer Rand
        road.setStrokeWidth(1.0);
        
        return road;
    }
    
    /**
     * Erstellt eine Räuber-Form (schwarze Figur).
     * @return Ein Polygon, das den Räuber darstellt
     */
    public static Polygon createRobber() {
        Polygon robber = new Polygon();
        
        // Erstelle einfache Räuber-Figur
        robber.getPoints().addAll(new Double[]{
            BUILDING_SIZE/2, 0.0,                      // Kopf oben
            BUILDING_SIZE * 0.8, BUILDING_SIZE * 0.3,  // Kopf rechts
            BUILDING_SIZE, BUILDING_SIZE,              // Körper unten rechts
            0.0, BUILDING_SIZE,                        // Körper unten links
            BUILDING_SIZE * 0.2, BUILDING_SIZE * 0.3   // Kopf links
        });
        
        robber.setFill(Color.BLACK);           // Schwarze Füllung
        robber.setStroke(Color.DARKRED);       // Dunkelroter Rand
        robber.setStrokeWidth(2.0);
        
        return robber;
    }
    
    /**
     * Erstellt einen formatierten String für die Ressourcen-Anzeige.
     * @param type Der Ressourcentyp
     * @param count Die Anzahl der Ressourcen
     * @return Ein formatierter String mit Emoji und Anzahl
     */
    public static String formatResourceText(ResourceType type, int count) {
        // Emoji für verschiedene Ressourcentypen
        String icon = switch (type) {
            case LUMBER -> "🌲";    // Holz
            case BRICK -> "🧱";     // Lehm
            case WOOL -> "🐑";      // Wolle
            case GRAIN -> "🌾";     // Getreide
            case ORE -> "⛰️";       // Erz
        };
        
        // Kompakte Formatierung für das Ressourcen-Panel
        return String.format("%sx%d", icon, count);
    }
    
    /**
     * Konvertiert PlayerColor zu JavaFX Color-Objekt.
     * @param playerColor Die Spielerfarbe
     * @return JavaFX Color-Objekt
     */
    public static Color getPlayerColor(PlayerColor playerColor) {
        return Color.web(playerColor.getHexColor());
    }
    
    /**
     * Erstellt einen verbesserten Bauplatz mit ordnungsgemäßer Gestaltung und Hover-Effekten.
     * @param canBuild Ob an diesem Platz gebaut werden kann
     * @param isOccupied Ob dieser Platz bereits besetzt ist
     * @return Ein Kreis, der den Bauplatz darstellt
     */
    public static javafx.scene.shape.Circle createBuildingSpot(boolean canBuild, boolean isOccupied) {
        javafx.scene.shape.Circle spot = new javafx.scene.shape.Circle(BUILDING_SPOT_SIZE / 2);
        
        // Setze Aussehen basierend auf Zustand
        if (isOccupied) {
            spot.setFill(OCCUPIED_SPOT_COLOR);        // Grau für besetzte Plätze
            spot.setStroke(Color.BLACK);
        } else if (canBuild) {
            spot.setFill(AVAILABLE_SPOT_COLOR);       // Hellblau für verfügbare Plätze
            spot.setStroke(Color.DARKBLUE);
        } else {
            spot.setFill(UNAVAILABLE_SPOT_COLOR);     // Hellgrau für nicht verfügbare Plätze
            spot.setStroke(Color.GRAY);
        }
        
        spot.setStrokeWidth(2.0);
        
        // Hover-Effekte nur für verfügbare Plätze
        if (canBuild && !isOccupied) {
            spot.setOnMouseEntered(e -> {
                spot.setFill(HOVER_SPOT_COLOR);       // Gelb bei Mausberührung
                spot.setStroke(Color.ORANGE);
                spot.setStrokeWidth(3.0);
            });
            spot.setOnMouseExited(e -> {
                spot.setFill(AVAILABLE_SPOT_COLOR);   // Zurück zu Originalfarbe
                spot.setStroke(Color.DARKBLUE);
                spot.setStrokeWidth(2.0);
            });
        }
        
        return spot;
    }
    
    /**
     * Erstellt einen verbesserten Straßen-Bauplatz mit ordnungsgemäßer Gestaltung.
     * @param canBuild Ob an diesem Platz gebaut werden kann
     * @param isOccupied Ob dieser Platz bereits besetzt ist
     * @param rotation Die Rotation des Bauplatz-Rechtecks
     * @return Ein Rechteck, das den Straßen-Bauplatz darstellt
     */
    public static Rectangle createRoadSpot(boolean canBuild, boolean isOccupied, double rotation) {
        Rectangle roadSpot = new Rectangle(ROAD_SPOT_LENGTH, ROAD_SPOT_WIDTH);
        
        // Setze Aussehen basierend auf Zustand
        if (isOccupied) {
            roadSpot.setFill(OCCUPIED_SPOT_COLOR);
            roadSpot.setStroke(Color.BLACK);
        } else if (canBuild) {
            roadSpot.setFill(AVAILABLE_SPOT_COLOR.brighter());   // Hellere Farbe für Straßen
            roadSpot.setStroke(Color.DARKBLUE);
        } else {
            roadSpot.setFill(UNAVAILABLE_SPOT_COLOR);
            roadSpot.setStroke(Color.GRAY);
        }
        
        roadSpot.setStrokeWidth(1.5);
        roadSpot.setRotate(rotation);           // Rotation für verschiedene Straßenrichtungen
        
        // Hover-Effekte nur für verfügbare Plätze
        if (canBuild && !isOccupied) {
            roadSpot.setOnMouseEntered(e -> {
                roadSpot.setFill(HOVER_SPOT_COLOR.brighter());
                roadSpot.setStroke(Color.ORANGE);
                roadSpot.setStrokeWidth(2.5);
            });
            roadSpot.setOnMouseExited(e -> {
                roadSpot.setFill(AVAILABLE_SPOT_COLOR.brighter());
                roadSpot.setStroke(Color.DARKBLUE);
                roadSpot.setStrokeWidth(1.5);
            });
        }
        
        return roadSpot;
    }
    
    /**
     * Erstellt ein verbessertes sechseckiges Feld mit Zahlen-Token-Overlay.
     * @param tile Das Geländefeld-Objekt
     * @return Eine Gruppe mit Sechseck und Token
     */
    public static javafx.scene.Group createEnhancedHexagonalTile(TerrainTile tile) {
        return createEnhancedHexagonalTile(tile, HEX_RADIUS);
    }
    
    /**
     * Erstellt ein verbessertes sechseckiges Feld mit Zahlen-Token-Overlay und benutzerdefiniertem Radius.
     * @param tile Das Geländefeld-Objekt
     * @param radius Der Radius des Sechsecks
     * @return Eine Gruppe mit Sechseck, Token und eventuell Räuber
     */
    public static javafx.scene.Group createEnhancedHexagonalTile(TerrainTile tile, double radius) {
        javafx.scene.Group tileGroup = new javafx.scene.Group();
        
        // Erstelle das Sechseck - zentriert bei (0,0) mit benutzerdefiniertem Radius
        Polygon hexagon = createHexagonalTerrainTile(tile, radius);
        tileGroup.getChildren().add(hexagon);
        
        // Füge Zahlen-Token hinzu, wenn das Feld einen hat
        if (tile.getNumberToken() > 0) {
            // Erstelle Token-Hintergrund-Kreis - optimiert für kompaktes Layout
            double tokenRadius = Math.max(13, radius * 0.30);  // Kleinere Skalierung für kompaktes Layout
            javafx.scene.shape.Circle tokenBackground = new javafx.scene.shape.Circle(tokenRadius);
            tokenBackground.setFill(Color.WHITE);              // Weißer Hintergrund
            tokenBackground.setStroke(Color.BLACK);            // Schwarzer Rand
            tokenBackground.setStrokeWidth(2.0);
            
            // Erstelle Zahlen-Text - zentriert bei (0,0)
            Text numberText = createNumberToken(tile.getNumberToken());
            
            tileGroup.getChildren().addAll(tokenBackground, numberText);
        }
        
        // Füge Räuber hinzu, wenn vorhanden
        if (tile.hasRobber()) {
            Polygon robber = createRobber();
            robber.setTranslateX(-BUILDING_SIZE/2);    // Zentriere den Räuber
            robber.setTranslateY(-BUILDING_SIZE/2);
            tileGroup.getChildren().add(robber);
        }
        
        return tileGroup;
    }
    
    /**
     * Erstellt einen Tooltip-Text für Bauplätze.
     * @param canBuild Ob an diesem Platz gebaut werden kann
     * @param isOccupied Ob dieser Platz bereits besetzt ist
     * @param buildingType Der Typ des Gebäudes (falls besetzt)
     * @return Ein beschreibender Text für den Tooltip
     */
    public static String createBuildingTooltip(boolean canBuild, boolean isOccupied, Building.Type buildingType) {
        if (isOccupied) {
            return buildingType == Building.Type.SETTLEMENT ? "Siedlung vorhanden" : "Stadt vorhanden";
        } else if (canBuild) {
            return "Klicken zum Bauen";
        } else {
            return "Bauen hier nicht möglich";
        }
    }
    
    /**
     * Aktualisiert das Aussehen eines Bauplatz-Kreises basierend auf dem Spielzustand.
     * @param spot Der Bauplatz-Kreis
     * @param canBuild Ob an diesem Platz gebaut werden kann
     * @param isOccupied Ob dieser Platz bereits besetzt ist
     */
    public static void updateBuildingSpotAppearance(javafx.scene.shape.Circle spot, boolean canBuild, boolean isOccupied) {
        if (isOccupied) {
            spot.setFill(OCCUPIED_SPOT_COLOR);
            spot.setStroke(Color.BLACK);
        } else if (canBuild) {
            spot.setFill(AVAILABLE_SPOT_COLOR);
            spot.setStroke(Color.DARKBLUE);
        } else {
            spot.setFill(UNAVAILABLE_SPOT_COLOR);
            spot.setStroke(Color.GRAY);
        }
    }
    
    /**
     * Gestaltet ein existierendes Sechseck-Polygon basierend auf einem Geländefeld.
     * @param hexagon Das zu gestaltende Sechseck-Polygon
     * @param tile Das Geländefeld mit den Styling-Informationen
     */
    public static void styleTerrainTile(Polygon hexagon, TerrainTile tile) {
        hexagon.setStroke(Color.BLACK);      // Schwarzer Rand
        hexagon.setStrokeWidth(2.0);         // Randbreite
        
        // Hover-Effekt hinzufügen
        hexagon.setOnMouseEntered(e -> hexagon.setStroke(Color.YELLOW));
        hexagon.setOnMouseExited(e -> hexagon.setStroke(Color.BLACK));
    }
}
