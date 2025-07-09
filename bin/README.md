# Java CATAN - Das Spiel

Eine Java-Implementierung des beliebten Brettspiels CATAN mit JavaFX als grafische Benutzeroberfläche.

## Überblick

Dieses Projekt implementiert die Grundregeln des 1995 erschienenen Brettspiels CATAN (ursprünglich "Die Siedler von Catan") in Java. Das Spiel verwendet JavaFX für die grafische Benutzeroberfläche und Maven als Build-System.

## Aktuelle Entwicklungen (2025)

### 🎯 Erhebliche Verbesserungen am hexagonalen Spielbrett
- **Vollständig überarbeitetes Board-System** mit präziser Kantenberechnung
- **Optimierte Edge- und Vertex-Positionierung** für exakte Straßen- und Siedlungsplatzierung
- **Mathematische Duplikatentfernung** zur korrekten Anzahl von Baufeldern
- **Erweiterte Debug-Tools** für Board-Validierung und Geometrie-Tests
- **Zahlreiche Test-Implementierungen** zur Qualitätssicherung

### 📈 Technische Verbesserungen
- **EnhancedHexGameBoard**: Komplett überarbeitete hexagonale Board-Implementierung
- **EdgeCoordinate & VertexCoordinate**: Neue Koordinatenklassen für präzise Positionierung
- **Präzise Koordinatenberechnung**: Millimetergenaue Positionierung mit Pixel-Konvertierung
- **Edge-Generation-Algorithmus**: Mathematischer Ansatz für korrekte Straßenpositionen
- **Vertex-Deduplication**: Intelligente Entfernung doppelter Bauplätze mit Toleranz-Check
- **Umfangreiches Test-System**: 20+ spezialisierte Test- und Demo-Klassen
- **Legacy-Kompatibilität**: Vollständige Rückwärtskompatibilität zu bestehenden Koordinaten

## Funktionen

### Implementierte Features
- ✅ Vollständige Spiellogik nach den Original-CATAN-Regeln von 1995
- ✅ **Authentische hexagonale Spielfelder** wie im Original CATAN
- ✅ **Präzise Board-Geometrie** mit mathematisch korrekter Positionierung
- ✅ **Optimierte Baupositions-Sichtbarkeit** - Straßen und Städte exakt platziert
- ✅ **Erweiterte hexagonale Koordinatensysteme** mit Axial- und Pixel-Koordinaten
- ✅ Originalgetreue 19-Feld CATAN Brettaufteilung
- ✅ **54 Siedlungsplätze und 72 Straßenpositions** wie im Original
- ✅ 2-4 Spieler-Unterstützung
- ✅ Zufällige Spielbrett-Generierung
- ✅ Siedlungen, Städte und Straßen bauen
- ✅ Ressourcen-Management (Holz, Lehm, Wolle, Getreide, Erz)
- ✅ Würfel-Mechanik mit Ressourcen-Produktion
- ✅ Räuber-Mechanik bei Würfelwurf 7
- ✅ Handel zwischen Spielern
- ✅ Siegpunkt-System
- ✅ Anfangsplatzierung von Siedlungen und Straßen
- ✅ Deutsche und englische Lokalisierung
- ✅ Rückwärtskompatibilität zu quadratischen Feldern
- ✅ **Umfangreiche Test-Suite** für Board-Validierung

### Spielbrett-Modi
Das Spiel unterstützt zwei Spielbrett-Modi:
1. **Hexagonales Brett** (Standard): Authentische sechseckige Felder wie im Original CATAN
   - 19 Hexagon-Felder in klassischer CATAN-Anordnung
   - Optimaler Abstand zwischen Feldern für Straßen und Städte
   - Präzise Platzierung von Gebäuden an Schnittpunkten
   - Einheitliches Koordinatensystem für perfekte Ausrichtung
2. **Quadratisches Brett** (Legacy): Vereinfachte quadratische Felder für Kompatibilität

### Vereinfachungen
- Keine Entwicklungskarten
- Keine Sonderkarten  
- Keine Erweiterungen
- Keine Soundeffekte oder Animationen
- Keine Netzwerkfunktionalität
- Keine KI-Gegner

## Technische Anforderungen

- **Java Version**: JDK 17 oder höher
- **Build-System**: Maven 3.6+
- **GUI-Framework**: JavaFX 17
- **IDE**: Eclipse (empfohlen)
- **Betriebssystem**: Windows, macOS, Linux

## Installation und Ausführung

### Voraussetzungen
1. Java JDK 17 oder höher installieren
2. Maven installieren
3. JavaFX Runtime (wird automatisch über Maven geladen)

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

### JAR-Datei erstellen
```bash
mvn clean package
```

### 🧪 Umfangreiche Test- und Demo-Suite

Das Projekt verfügt über eine umfassende Sammlung von Test- und Demonstrationstools:

#### Debug- und Validierungs-Tools
- **EdgeCountAnalysis**: Analysiert die Anzahl der generierten Kanten
- **EdgeMathTest**: Mathematische Validierung der Kantenberechnung 
- **DeduplicationTest**: Test der Duplikat-Eliminierung bei Vertices/Edges
- **VertexCoordinateTest**: Validierung des Vertex-Koordinatensystems
- **RoadVisibilityTest**: Test der Straßen-Sichtbarkeit und -Positionierung
- **SimpleEdgeTest**: Einfache Edge-Koordinaten Tests
- **RealDuplicateAnalysis**: Tiefgehende Analyse echter Duplikate

#### Visualisierungs- und Layout-Demos
- **EnhancedBoardDemo**: Demonstration des verbesserten Board-Systems
- **AuthenticCatanLayoutDemo**: Authentisches CATAN-Layout Showcase
- **LayoutComparison**: Vergleich verschiedener Layout-Ansätze
- **VisualLayoutTest**: Visuelle Darstellung der Board-Geometrie
- **HexPositionDebug**: Debug-Tool für Hexagon-Positionen
- **SymmetryTest**: Test der Board-Symmetrie

#### Spezialisierte Generatoren
- **CorrectEdgeGenerator**: Korrekte Edge-Generierung für das Board
- **VertexDebugger**: Detailliertes Vertex-Debugging
- **SimpleLayoutDemo**: Einfache Layout-Demonstration

## Projektstruktur

```text
src/
├── main/
│   ├── java/com/catan/
│   │   ├── CatanApplication.java        # Haupt-Anwendungsklasse
│   │   ├── controller/
│   │   │   └── MainController.java      # UI-Controller
│   │   ├── demo/                        # Test- und Demo-Tools (20+ Klassen)
│   │   │   ├── EnhancedBoardDemo.java   # Board-System Demonstration
│   │   │   ├── EdgeCountAnalysis.java   # Kanten-Analyse Tool
│   │   │   ├── VertexDebugger.java      # Vertex-Debug Tool
│   │   │   ├── RoadVisibilityTest.java  # Straßen-Sichtbarkeits-Test
│   │   │   └── ...weitere Demo-Tools
│   │   ├── model/                       # Spiel-Logik
│   │   │   ├── Building.java
│   │   │   ├── CatanGame.java
│   │   │   ├── GameBoard.java
│   │   │   ├── EnhancedHexGameBoard.java    # ⭐ Neue Enhanced Board-Klasse
│   │   │   ├── HexGameBoard.java            # Hexagonales Board
│   │   │   ├── HexCoordinate.java           # ⭐ Hexagon-Koordinaten
│   │   │   ├── EdgeCoordinate.java          # ⭐ Kanten-Koordinaten  
│   │   │   ├── VertexCoordinate.java        # ⭐ Vertex-Koordinaten
│   │   │   ├── Player.java
│   │   │   ├── Road.java
│   │   │   ├── TerrainTile.java
│   │   │   └── ...weitere Model-Klassen
│   │   └── view/                        # UI-Komponenten
│   │       └── UIComponents.java
│   └── resources/
│       └── main-view.fxml               # JavaFX UI-Layout
└── test/
    └── java/com/catan/model/            # Unit-Tests
        ├── CatanGameTest.java
        ├── EnhancedHexGameBoardTest.java    # ⭐ Enhanced Board Tests
        ├── EdgeCoordinateTest.java          # ⭐ Edge-Koordinaten Tests
        ├── VertexCoordinateTest.java        # ⭐ Vertex-Koordinaten Tests
        └── ...weitere Test-Klassen
```

⭐ = Neue Klassen in 2025

## Spielregeln

### Ziel des Spiels
Als erster Spieler 10 Siegpunkte erreichen durch:
- Siedlungen bauen (1 Siegpunkt)
- Städte bauen (2 Siegpunkte)
- Längste Handelsstraße (2 Siegpunkte)
- Größte Rittermacht (2 Siegpunkte) - nicht implementiert

### Spielablauf
1. **Anfangsphase**: Jeder Spieler platziert 2 Siedlungen und 2 Straßen
2. **Spielphase**: Spieler würfeln, erhalten Ressourcen und können bauen/handeln

### Bauen
- **Siedlung**: 1 Holz, 1 Lehm, 1 Wolle, 1 Getreide
- **Stadt**: 2 Getreide, 3 Erz (ersetzt Siedlung)
- **Straße**: 1 Holz, 1 Lehm

### Ressourcen-Produktion
- Würfelwurf bestimmt welche Felder Ressourcen produzieren
- Siedlungen produzieren 1 Ressource, Städte 2 Ressourcen
- Bei Würfelwurf 7: Räuber bewegen, Karten abwerfen

## Bedienung

### Hauptfenster
- **Spielbrett**: Klick auf Felder um Räuber zu bewegen
- **Bauplätze**: Klick auf Kreuzungen um Siedlungen/Städte zu bauen
- **Würfeln-Button**: Würfel für aktuelle Runde werfen
- **Zug beenden**: Nächster Spieler ist dran

### Ressourcen-Panel
- Zeigt aktuelle Ressourcen des Spielers
- Aktualisiert sich automatisch

### Spieler-Info
- Übersicht aller Spieler
- Siegpunkte, Ressourcen, Gebäude

## Architektur

Das Projekt folgt dem **Model-View-Controller (MVC)** Pattern:

- **Model**: Spiel-Logik und Datenstrukturen (`com.catan.model`)
- **View**: UI-Komponenten und Layout (`com.catan.view`, FXML)
- **Controller**: Verbindung zwischen Model und View (`com.catan.controller`)

### Wichtige Klassen

- `CatanGame`: Hauptspiel-Logik und Regeln
- `GameBoard`: Spielbrett-Verwaltung
- `Player`: Spieler-Zustand und Aktionen
- `MainController`: UI-Event-Handling
- `UIComponents`: UI-Hilfsfunktionen

## Erweiterungsmöglichkeiten

Das Spiel ist so designed, dass es leicht erweitert werden kann:

- Entwicklungskarten hinzufügen
- KI-Spieler implementieren
- Netzwerk-Multiplayer
- Zusätzliche Spielvarianten
- Verbessertes Board-Layout (Hexagone)
- Sound und Animationen

## Tests

Unit-Tests sind verfügbar für:
- Spieler-Funktionalität
- Spiel-Logik
- Ressourcen-Management
- Bau-Mechaniken

Tests ausführen mit: `mvn test`

## Changelog 2025

### Version 2.0 - Enhanced Hexagonal Board System
**Datum: Januar - Juni 2025**

#### 🔥 Hauptfeatures
- ✅ **Vollständiges Board-System Redesign** mit mathematisch korrekter Geometrie
- ✅ **EnhancedHexGameBoard** - Neue Board-Implementierung mit Vertex/Edge-Koordinaten
- ✅ **EdgeCoordinate & VertexCoordinate** - Spezialisierte Koordinatenklassen
- ✅ **Automatische Duplikat-Eliminierung** - Keine doppelten Kreuzungen mehr
- ✅ **Pixel-perfekte Positionierung** - Millimetergenaue Platzierung von Objekten

#### 🧪 Test-Infrastructure 
- ✅ **20+ Demo/Test-Klassen** für umfassende Validierung
- ✅ **EdgeCountAnalysis** - Mathematische Analyse der Kantenanzahl
- ✅ **VertexDebugger** - Detailliertes Debugging von Vertex-Positionen
- ✅ **RoadVisibilityTest** - Validierung der Straßen-Sichtbarkeit
- ✅ **AuthenticCatanLayoutDemo** - Showcase des authentischen CATAN-Layouts

#### 🔧 Technische Verbesserungen
- ✅ **Legacy-Kompatibilität** - Vollständige Rückwärtskompatibilität
- ✅ **Performance-Optimierungen** - Effiziente Duplikat-Erkennung
- ✅ **Robuste Koordinaten-Mathematik** - Präzise hexagonale Berechnungen
- ✅ **Umfassende Unit-Tests** - EnhancedHexGameBoardTest, EdgeCoordinateTest, etc.

#### 📐 Board-Geometrie Verbesserungen
- ✅ **Exakte 54 Vertices** - Wie im Original CATAN (keine Duplikate)
- ✅ **Präzise 72 Edges** - Korrekte Anzahl von Straßenpositionen  
- ✅ **Authentische 19-Feld Anordnung** - Original CATAN 5-Reihen Layout (3-4-5-4-3)
- ✅ **Millimeter-genaue Abstände** - Perfekte Positionierung für UI-Darstellung

## Lizenz und Credits

Dieses Projekt ist eine Implementierung der CATAN-Spielregeln zu Bildungszwecken.
CATAN ist ein Markenzeichen von Klaus Teuber und Catan GmbH.

## Autor

Entwickelt als Java-Anwendung mit JavaFX und Maven-Support.

## Hexagonales Spielbrett

### Technische Details
Das Spiel verwendet ein authentisches hexagonales Koordinatensystem:

- **Axial-Koordinaten**: (q, r) System für hexagonale Grids
- **19 Felder**: Standard CATAN Layout mit Zentrum + innerer Ring + äußerer Ring
- **Pixel-Konvertierung**: Automatische Umrechnung für JavaFX Darstellung
- **Nachbar-Berechnung**: Effiziente Berechnung angrenzender Hexagone
- **Distanz-Messung**: Manhattan-Distanz für hexagonale Koordinaten

### Verwendung
```java
// Hexagonales Spiel erstellen
CatanGame game = new CatanGame(playerNames, true);

// Oder explizit quadratisches Brett für Kompatibilität
CatanGame legacyGame = new CatanGame(playerNames, false);
```

### Neue Koordinaten-Systeme (2025)

Das Enhanced Board System führt drei spezialisierte Koordinatenklassen ein:

#### HexCoordinate
```java
// Axial-Koordinaten für hexagonale Felder
HexCoordinate hex = new HexCoordinate(q, r);
Point2D pixelPos = hex.toPixel(hexSize, centerX, centerY);
int distance = hex1.distanceTo(hex2);
```

#### VertexCoordinate  
```java
// Koordinaten für Gebäude-Positionen (Kreuzungen)
VertexCoordinate vertex = new VertexCoordinate(x, y, direction);
List<HexCoordinate> adjacentHexes = vertex.getAdjacentHexes();
List<EdgeCoordinate> adjacentEdges = vertex.getAdjacentEdges();
```

#### EdgeCoordinate
```java
// Koordinaten für Straßen-Positionen
EdgeCoordinate edge = new EdgeCoordinate(x, y, direction);
VertexCoordinate[] connectedVertices = edge.getConnectedVertices();
List<HexCoordinate> adjacentHexes = edge.getAdjacentHexes();
```

Diese Koordinatensysteme gewährleisten:
- ✅ **Exakt eine Kreuzung pro Schnittpunkt** (keine Duplikate)
- ✅ **Straßen genau zwischen Siedlungen** positioniert
- ✅ **Mathematisch korrekte Nachbarschaftsbeziehungen**
- ✅ **Pixel-genaue Darstellung** für die UI

### Board-Klassen
- `HexCoordinate`: Axial-Koordinatensystem (q,r)
- `HexGameBoard`: Hexagonale Spielbrett-Implementierung  
- `GameBoard`: Wrapper mit automatischer Board-Typ Erkennung
- `UIComponents`: Hexagonale Tile-Darstellung mit JavaFX Polygonen
