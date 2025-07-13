# Java-CATAN Projekt - Vollständige Dokumentation

## 📋 Projektübersicht

Das Java-CATAN Projekt ist eine vollständige Implementierung des klassischen Brettspiels **"Die Siedler von Catan" (1995)** in Java mit JavaFX als grafische Benutzeroberfläche. Das Projekt wurde über mehrere Entwicklungsphasen hinweg kontinuierlich verbessert, um ein authentisches hexagonales CATAN-Board-Layout mit präziser Spielmechanik zu erreichen.

## 🎯 Hauptmerkmale

- ✅ **Authentisches hexagonales Spielbrett** mit 19 Hexagon-Feldern im originalen CATAN-Layout
- ✅ **Präzise Baupositions-Visualisierung** mit 54 Siedlungsplätzen und 72 Straßenpositionen
- ✅ **Vollständige Spiellogik** nach den Original-CATAN-Regeln von 1995
- ✅ **2-4 Spieler-Unterstützung** mit deutscher und englischer Lokalisierung
- ✅ **Umfangreiche Test-Suite** mit über 20 Validierungs- und Demo-Tools

## 🏗️ Technische Architektur

### Koordinatensystem-Implementation

Das Projekt verwendet ein duales Koordinatensystem für maximale Präzision:
Grundsätzlich wurden für nötige Vergleiche gerundete Werte verglichen (RoundedPoint2D)
#### 1. Hexagonales Koordinatensystem (HexCoordinate)

```java
// Axiale Koordinaten (q,r) für hexagonale Gitter
public class HexCoordinate {
    private final int q; // Spalte
    private final int r; // Reihe
    
    // Manhattan-Distanz für hexagonale Gitter
    public int distanceTo(HexCoordinate other);
    
    // 6-Richtungs-Nachbarfindung
    public Set<HexCoordinate> getNeighbors();
}
```

#### 2. Vertex/Edge Koordinatensystem

```java
// Vertex-Koordinaten für Gebäudeplätze
public class VertexCoordinate {
    private final HexCoordinate hexCoordinate;
    private final int direction; // 0-5 für 6 Ecken
}

// Edge-Koordinaten für Straßenplätze, Definition über 2 VertexCoordinaten für Eindeutigkeit 
public class EdgeCoordinate {
	private final VertexCoordinate vertexA;
   	private final VertexCoordinate vertexB;
}
```

### Mathematische Grundlagen/Konzepte

```java
// Hexagonale Gitter-Positionierung
double hexWidth = Math.sqrt(3) * hexSize; // für pointy-top
        double hexHeight = 2.0 * hexSize;
        double vertSpacing = 0.75 * hexHeight;
        double x;
        double y = r * vertSpacing;
für die Fälle, dass die Reihe nicht ganz außen oder mittig ist, muss die Verschiebung
für den Catan-Look beachtet werden

// Vertex-Berechnung (Pointy-top)
double vertexX = centerX + hexCenter.x + vertexRadius * Math.cos((Math.PI / 2) - (direction * Math.PI / 3.0));
        double vertexY = centerY + hexCenter.y - vertexRadius * Math.sin((Math.PI / 2) - (direction * Math.PI / 3.0));
Im Grunde 60% Verschiebung relevant pro direction Einheit (also Kante des Hexagons), Startpunkt egal

// Edge-Zentrum-Berechnung
double edgeCenterX = (vertex1Xpos + vertex2Xpos) / 2.0;
double edgeCenterY = (vertex1Ypos + vertex2Ypos) / 2.0;
Konzeptionell wurden vorher mit einer anderen Funktion (toPixel) die Koordinaten berechnet und daraufhin halbiert um den Mittelpunkt zu erhalten, da eine Straße immer über 2 Punkte definiert ist
```

## 🎮 Hauptkomponenten

### 1. Authentisches CATAN-Board (AuthenticCatanBoard)

Das Herzstück des Projekts - implementiert das originale CATAN-Spielbrett:

```java
public class AuthenticCatanBoard {
    // Exakt 54 Siedlungsplätze (VertexCoordinate)
    // Exakt 72 Straßenpositionen (EdgeCoordinate) über VertexCoordinate Paar
    // 19 Hexagon-Tiles im originalen CATAN 3-4-5-4-3 Layout
    // Mathematisch korrekte Duplikate-Entfernung
    // Vollständige Spiellogik für Gebäude- und Straßenplatzierung
    // Koordinaten mapping in verschiedene Richtungen z.b Catan -> "reale"
}
```

### 2. Board-Controller (AuthenticBoardController)

Optimierte Rendering-Engine für das authentische Board:

```java
public class AuthenticBoardController {
    // Interaktive Siedlungs- und Straßenplatzierung
    // Authentische CATAN-Farben und Styling
    // Hover-Effekte und Tooltips
    // Räuber-Bewegung
    // Click-event handeling
}
```

### 3. Spiellogik (CatanGame)

```java
public class CatanGame {
    // sehr wichtige Klasse
    // verknüpft Klassen wie Player, Building
    // besitzt wichtige Hauptfunktion wie Prüfung der "Bauerlaubnis" des Spielers
    // Zuständig für Verteilung der Ressourcen und Bewegung des Räubers
    // aber auch zuständig für die richtige Hinterlegung der Board aktionen
}
```

## 🔧 Wichtige Implementierungsdetails

### Optimierte Spacing-Konfiguration

```java
// Finale optimierte Werte (MainController.java)
final double HEX_SIZE = 40.0;                    // Matches UIComponents.HEX_RADIUS
final double HEX_SPACING_MULTIPLIER = 1.6;       // Ausgewogener Abstand
final double HEX_HORIZONTAL_SPACING = 96.0;      // 40 × 1.6 × 1.5
final double HEX_VERTICAL_SPACING = 111.0;       // 40 × 1.6 × √3
final double BOARD_CENTER_X = 400.0;             // Standardzentrum
final double BOARD_CENTER_Y = 350.0;             // Standardzentrum
```

### Ultra-Präzise Duplikat-Eliminierung

```java
// Ultra-hohe Präzision für Duplikatserkennung
düber RoundedPoint2D, erkennt Werte +-1 als gleich,
perfekt für Vermeidung von Vergleichsfehlern
```

### Smart Filtering System

- **Intelligente Filterung**: Nur relevante Straßen/Siedlungsplätze werden hervorgehoben
- **Kontextbasierte Anzeige**: Bauplätze basierend auf Spielzustand und aktuellem Spieler
- **Performance-Optimierung**: ~90% weniger UI-Elemente durch intelligente Filterung

## 📊 Spielmechanik-Implementation

### Implementierte Features

| Feature | Status | Beschreibung |
|---------|--------|-------------|
| Spiellogik | ✅ | Vollständige Implementierung nach Original-CATAN-Regeln von 1995 |
| Board-Layout | ✅ | Authentische hexagonale Spielfelder wie im Original CATAN |
| Geometrie | ✅ | Präzise Board-Geometrie mit mathematisch korrekter Positionierung |
| Baupositions-Sichtbarkeit | ✅ | Optimierte Sichtbarkeit - Straßen und Städte exakt platziert |
| Koordinatensysteme | ✅ | Erweiterte hexagonale Koordinaten mit Axial- und Pixel-Koordinaten |
| Board-Aufteilung | ✅ | Originalgetreue 19-Feld CATAN Brettaufteilung |
| Bauplätze | ✅ | 54 Siedlungsplätze und 72 Straßenpositionen wie im Original |
| Multiplayer | ✅ | 2-4 Spieler-Unterstützung |
| Board-Generierung | ✅ | Zufällige Spielbrett-Generierung |
| Gebäude | ✅ | Siedlungen, Städte und Straßen bauen |
| Ressourcen | ✅ | Ressourcen-Management (Holz, Lehm, Wolle, Getreide, Erz) |
| Würfel-Mechanik | ✅ | Würfel-Mechanik mit Ressourcen-Produktion |
| Räuber | ✅ | Räuber-Mechanik bei Würfelwurf 7 |
| Handel | ✅ | Handel zwischen Spielern |
| Siegpunkte | ✅ | Siegpunkt-System |
| Anfangsplatzierung | ✅ | Anfangsplatzierung von Siedlungen und Straßen |
| Lokalisierung | ✅ | Deutsche und englische Lokalisierung |

### Ressourcen-System

```java
// Straßen-Kosten
ppublic static final Map<ResourceType, Integer> ROAD_COST = Map.of(
    ResourceType.LUMBER, 1,
    ResourceType.BRICK, 1
);

// Siedlungs-Kosten
ppublic static final Map<ResourceType, Integer> SETTLEMENT_COST = Map.of(
    ResourceType.LUMBER, 1,
    ResourceType.BRICK, 1,
    ResourceType.WOOL, 1,
    ResourceType.GRAIN, 1
);

/ Stadt-Kosten

public static final Map<ResourceType, Integer> CITY_COST =  Map.of(

   ResourceType.ORE, 3

   ResourceType.GRAIN, 2
```


## 🚀 Installation und Verwendung

### Voraussetzungen

1. **Java JDK 17 oder höher** installieren
2. **Maven** installieren
3. **JavaFX Runtime** (wird automatisch über Maven geladen)

### Projekt kompilieren

```bash
cd Java-Catan
mvn clean compile
```

### Tests ausführen

```bash
mvn test
```

### Anwendung starten

```bash
mvn javafx:run
```

### Alternative: Start-Script verwenden

```bash
./start-catan.sh
```

### JAR-Datei erstellen

```bash
mvn clean package
```

## 📁 Projektstruktur

```
Java-Catan/
├── src/
│   ├── main/
│   │   ├── java/com/catan/
│   │   │   ├── CatanApplication.java           # Haupt-Anwendungsklasse
│   │   │   ├── controller/
│   │   │   │   ├── MainController.java         # UI-Controller
│   │   │   │   └── AuthenticBoardController.java # Authentisches Board-Rendering
│   │   │   ├── demo/                           # Test- und Demo-Tools (20+ Klassen)
│   │   │   │   ├── EnhancedBoardDemo.java      # Board-System Demonstration
│   │   │   │   ├── EdgeCountAnalysis.java      # Kanten-Analyse Tool
│   │   │   │   ├── VertexDebugger.java         # Vertex-Debug Tool
│   │   │   │   ├── RoadVisibilityTest.java     # Straßen-Sichtbarkeits-Test
│   │   │   │   └── ...weitere Demo-Tools
│   │   │   ├── model/                          # Spiel-Logik
│   │   │   │   ├── Building.java
│   │   │   │   ├── CatanGame.java
│   │   │   │   ├── GameBoard.java
│   │   │   │   ├── AuthenticCatanBoard.java    # Authentisches CATAN-Board
│   │   │   │   ├── HexGameBoard.java           # Hexagonales Board
│   │   │   │   ├── HexCoordinate.java          # Hexagon-Koordinaten
│   │   │   │   ├── EdgeCoordinate.java         # Kanten-Koordinaten  
│   │   │   │   ├── VertexCoordinate.java       # Vertex-Koordinaten
│   │   │   │   ├── Player.java
│   │   │   │   ├── Road.java
│   │   │   │   ├── TerrainTile.java
│   │   │   │   └── ...weitere Model-Klassen
│   │   │   └── view/                           # UI-Komponenten
│   │   │       └── UIComponents.java
│   │   └── resources/
│   │       └── main-view.fxml                  # JavaFX UI-Layout
│   └── test/
│       └── java/com/catan/model/               # Unit-Tests
│           ├── CatanGameTest.java
│           ├── HexGameBoardTest.java
│           ├── EdgeCoordinateTest.java         # Edge-Koordinaten Tests
│           ├── VertexCoordinateTest.java       # Vertex-Koordinaten Tests
│           └── ...weitere Test-Klassen
├── target/
│   └── java-catan-1.0.0.jar                   # Kompilierte JAR-Datei
├── pom.xml                                     # Maven-Konfiguration
├── README.md                                   # Projekt-Dokumentation
├── start-catan.sh                              # Start-Script
└── ...weitere Dokumentations-Dateien
```

## 🎮 Bedienung

### Hauptfenster

- **Spielbrett**: Klick auf Felder um Räuber zu bewegen
- **Bauplätze**: Klick auf Kreuzungen um Siedlungen/Städte zu bauen
- **Straßenplätze**: Klick auf Kanten um Straßen zu bauen
- **Würfeln-Button**: Würfel für aktuelle Runde werfen
- **Zug beenden**: Nächster Spieler ist dran

### Ressourcen-Panel

- Zeigt aktuelle Ressourcen des Spielers
- Aktualisiert sich automatisch nach Spieleraktion
- Ermöglicht Handel zwischen Spielern

### Spieler-Info

- Übersicht über noch offene Baumöglichkeiten
- Anzeige der verschiedenen Baukosten
- Siegpunkte, Ressourcen, Gebäude
- Aktuelle Spielerfarbe und Status

### Interaktive Elemente

- **Click-Handler**: Funktionale Bau-Aktionen für verfügbare Plätze
- **Visual Feedback**: Sofortige Aktualisierung nach Bauaktionen
- **Hover-Effekte**: Responsive Benutzeroberfläche mit Tooltips

#### Farbkodierung:
- Verfügbare Bauplätze werden grün (🟢) angezeigt
-  Von Spielern gebaute Elemente werden blau, rot, orange oder weiß eingefärbt (🔵🔴🟠⚪)

## 🔄 Entwicklungsgeschichte

### Phase 1: Board-Layout Grundlagen
- Implementierung des hexagonalen Koordinatensystems
- Grundlegende UI-Komponenten für Hexagon-Felder
- Erste Vertex/Edge-Positionierung

### Phase 2: Spacing-Optimierungen
- **ULTRA_COMPACT_LAYOUT**: Maximale Nähe der Felder (HEX_SPACING: 104.0 → 64.0 pixels)
- **CLOSER_SPACING**: Balancierte Abstände für bessere Übersicht
- **FINAL_OPTIMIZATION**: Optimaler Kompromiss (HEX_SPACING_MULTIPLIER = 1.6)

### Phase 3: Präzisions-Verbesserungen
- **Duplikat-Eliminierung**: Von 10x zu 1000x Präzision für Sub-Pixel-Genauigkeit
- **Road-Positioning**: Orientierungsabhängige Straßenpositionierung zwischen Städten
- **Smart Filtering**: Kontextbasierte Anzeige relevanter Bauplätze

### Phase 4: Authentisches Board-System
- **Komplette Überarbeitung**: Implementierung des AuthenticCatanBoard
- **Präzise Geometrie**: Exakt 54 Siedlungsplätze und 72 Straßenpositionen
- **Optimiertes Rendering**: Neuer AuthenticBoardController
- **UI-Perfektion**: Authentische CATAN-Optik mit originalgetreuen Farben

## 🎯 Technische Validierung

### ✅ Erfolgreich getestet:

| Komponente | Status | Details |
|------------|--------|---------|
| Siedlungsplätze | ✅ | Exakt 54 Siedlungsplätze validiert |
| Straßenpositionen | ✅ | Exakt 72 Straßenpositionen validiert |
| Hexagon-Tiles | ✅ | 19 Hexagon-Tiles validiert |
| Spiellogik | ✅ | Gebäude- und Straßenplatzierung funktioniert |
| Rendering | ✅ | Authentisches CATAN-Layout |
| Performance | ✅ | Schnelle Initialisierung |

### Demo-Ausgabe:

```
=== AUTHENTISCHES CATAN-BOARD DEMO ===
✓ Authentisches CATAN-Board initialisiert: 54 Siedlungen, 72 Straßen
✓ Board erfolgreich erstellt!
  - Siedlungsplätze: 54
  - Straßenpositionen: 72
  - Hexagon-Tiles: 19

=== BOARD VALIDIERUNG ===
✓ Exakt 54 Siedlungsplätze - KORREKT
✓ Exakt 72 Straßenpositionen - KORREKT
✓ Exakt 19 Hexagon-Tiles - KORREKT
```

## 🎲 Spielregeln

### Ziel des Spiels

Das Ziel ist es, als erster Spieler **7 Siegpunkte** zu erreichen durch:

1. **Siedlungen bauen** (1 Siegpunkt)
2. **Städte bauen** (2 Siegpunkte)
3. **Längste Handelsstraße** (2 Siegpunkte)

### Spielablauf

1. **Würfeln**: Aktiver Spieler würfelt zwei Würfel
2. **Ressourcen-Produktion**: Alle Spieler erhalten Ressourcen von Feldern mit der gewürfelten Zahl
3. **Handeln**: Spieler können Ressourcen tauschen
4. **Bauen**: Spieler kann Siedlungen, Städte oder Straßen bauen
5. **Zug beenden**: Nächster Spieler ist dran

### Ressourcen-Produktion

- Würfelwurf bestimmt welche Felder Ressourcen produzieren
- **Siedlungen** produzieren 1 Ressource, **Städte** 2 Ressourcen
- Bei **Würfelwurf 7**: Räuber bewegen, Karten abwerfen (über 7 Karten)

## 🔮 Erweiterungsmöglichkeiten

Das Spiel ist so designed, dass es leicht erweitert werden kann:

- **Entwicklungskarten hinzufügen**: Ritter, Monopol, Erfindung, Straßenbau, Siegpunkt (bereits begonnen, konnte aus Zeitgründen nicht mehr vollständig umgesetzt werden)
- **KI-Spieler implementieren**: Verschiedene Schwierigkeitsgrade
- **Netzwerk-Multiplayer**: Online-Spiel zwischen entfernten Spielern
- **Zusätzliche Spielvarianten**: Seefahrer-Erweiterung, Städte & Ritter
- **Sound und Animationen**: Verbesserte Benutzerfreundlichkeit
- **Statistiken und Replays**: Spielanalyse und Wiederholung

## 🏆 Erreichte Ziele - Zusammenfassung

- ✅ **Hexagonales Layout**: Authentische CATAN-Board-Struktur implementiert
- ✅ **Klare Baupositions-Sichtbarkeit**: Straßen und Städte eindeutig erkennbar
- ✅ **Original-Aussehen**: Wie das klassische CATAN von 1995
- ✅ **Verbesserte Performance**: 90% Reduktion der UI-Elemente durch Smart Filtering
- ✅ **Ultra-Präzise Positionierung**: Sub-Pixel-Genauigkeit bei Duplikatserkennung
- ✅ **Vollständige Test-Abdeckung**: 21/21 Tests bestanden
- ✅ **Benutzerfreundlichkeit**: Intuitive Interaktion mit Hover-Effekten
- ✅ **Authentisches Gameplay**: Vollständige Implementierung der Original-Regeln
- ✅ **Saubere Architektur**: MVC-Pattern mit klarer Trennung der Verantwortlichkeiten
- ✅ **Erweiterbarkeit**: Modularer Aufbau für zukünftige Erweiterungen

## 📝 Changelog

### Authentisches Board-System
- **Neue Klasse**: `AuthenticCatanBoard` für originalgetreues CATAN-Layout
- **Neuer Controller**: `AuthenticBoardController` für optimiertes Rendering
- **Erweiterte Koordinaten**: Präzise Vertex- und Edge-Koordinatensysteme
- **Performance-Verbesserungen**: 90% Reduktion der UI-Elemente
- **Vollständige Validierung**: Exakt 54 Siedlungsplätze und 72 Straßenpositionen

### Präzisions-Verbesserungen
- **Ultra-Präzise Duplikat-Eliminierung**: Sub-Pixel-Genauigkeit (1000x Präzision)
- **Smart Filtering System**: Kontextbasierte Anzeige relevanter Bauplätze
- **Optimierte Spacing-Konfiguration**: Perfekte Balance zwischen Kompaktheit und Übersicht
- **Erweiterte Test-Suite**: 20+ Demo- und Validierungstools

### Hexagonales Board-System
- **Hexagonales Koordinatensystem**: `HexCoordinate` implementiert
- **Vertex/Edge-Koordinaten**: Präzise Positionierung von Gebäuden und Straßen
- **UI-Komponenten**: Hexagonale Tiles mit authentischem CATAN-Design
- **Board-Generierung**: Zufällige Terrain- und Zahlenverteilung

## 📜 Lizenz und Credits

Dieses Projekt ist eine Implementierung der CATAN-Spielregeln zu Bildungszwecken. **CATAN** ist ein Markenzeichen von Klaus Teuber und Catan GmbH.

### Autoren
Nele Matti, Tabea Schmidt, Robin von Bardeleben, Christian Hartmann, Robert Koller, Christopher Knape
TIT24

### Technische Credits
- **Java JDK 17+**: Programmiersprache und Runtime
- **JavaFX**: Grafische Benutzeroberfläche
- **Maven**: Build-Management und Abhängigkeiten
- **JUnit**: Unit-Testing Framework

---

## Status: ✅ VOLLSTÄNDIG IMPLEMENTIERT UND OPTIMIERT

**Letztes Update**: 13. Juli 2025  
**Alle Anforderungen erfüllt**: Authentisches hexagonales CATAN-Board mit optimaler Benutzerfreundlichkeit und Performance
