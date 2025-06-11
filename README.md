# Java CATAN - Das Spiel

Eine Java-Implementierung des beliebten Brettspiels CATAN mit JavaFX als grafische Benutzeroberfläche.

## Überblick

Dieses Projekt implementiert die Grundregeln des 1995 erschienenen Brettspiels CATAN (ursprünglich "Die Siedler von Catan") in Java. Das Spiel verwendet JavaFX für die grafische Benutzeroberfläche und Maven als Build-System.

## Funktionen

### Implementierte Features
- ✅ Vollständige Spiellogik nach den Original-CATAN-Regeln von 1995
- ✅ **Authentische hexagonale Spielfelder** wie im Original CATAN
- ✅ **Optimierte Baupositions-Sichtbarkeit** - Straßen und Städte klar erkennbar
- ✅ **Verbessertes Spielbrett-Layout** mit korrekt positionierten Hexagons
- ✅ Hexagonales Koordinatensystem mit Axial-Koordinaten (q,r)
- ✅ Originalgetreue 19-Feld CATAN Brettaufteilung
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

## Projektstruktur

```
src/
├── main/
│   ├── java/com/catan/
│   │   ├── CatanApplication.java        # Haupt-Anwendungsklasse
│   │   ├── controller/
│   │   │   └── MainController.java      # UI-Controller
│   │   ├── model/                       # Spiel-Logik
│   │   │   ├── Building.java
│   │   │   ├── CatanGame.java
│   │   │   ├── GameBoard.java
│   │   │   ├── GameConstants.java
│   │   │   ├── Player.java
│   │   │   ├── PlayerColor.java
│   │   │   ├── ResourceType.java
│   │   │   ├── Road.java
│   │   │   ├── TerrainTile.java
│   │   │   └── TerrainType.java
│   │   └── view/                        # UI-Komponenten
│   │       └── UIComponents.java
│   └── resources/
│       └── main-view.fxml               # JavaFX UI-Layout
└── test/
    └── java/com/catan/model/            # Unit-Tests
        ├── CatanGameTest.java
        └── PlayerTest.java
```

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

### Board-Klassen
- `HexCoordinate`: Axial-Koordinatensystem (q,r)
- `HexGameBoard`: Hexagonale Spielbrett-Implementierung  
- `GameBoard`: Wrapper mit automatischer Board-Typ Erkennung
- `UIComponents`: Hexagonale Tile-Darstellung mit JavaFX Polygonen
