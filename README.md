# Java CATAN - Das Spiel

Eine Java-Implementierung des beliebten Brettspiels CATAN mit JavaFX als grafische Benutzeroberfläche.

## 📋 Überblick

Dieses Projekt implementiert die Grundregeln des 1995 erschienenen Brettspiels CATAN (ursprünglich "Die Siedler von Catan") in Java. Das Spiel verwendet JavaFX für die grafische Benutzeroberfläche und Maven als Build-System.

## 🎯 Hauptmerkmale

- ✅ **Authentisches hexagonales Spielbrett** mit 19 Hexagon-Feldern im originalen CATAN-Layout
- ✅ **Präzise Baupositions-Visualisierung** mit 54 Siedlungsplätzen und 72 Straßenpositionen
- ✅ **Vollständige Spiellogik** nach den Original-CATAN-Regeln von 1995
- ✅ **2-4 Spieler-Unterstützung** mit deutscher und englischer Lokalisierung
- ✅ **Umfangreiche Test-Suite** mit über 20 Validierungs- und Demo-Tools

## 📈 Aktuelle Entwicklungen

### 🎯 Erhebliche Verbesserungen am hexagonalen Spielbrett
- **Vollständig überarbeitetes Board-System** mit präziser Kantenberechnung
- **Optimierte Edge- und Vertex-Positionierung** für exakte Straßen- und Siedlungsplatzierung
- **Mathematische Duplikatentfernung** zur korrekten Anzahl von Baufeldern
- **Erweiterte Debug-Tools** für Board-Validierung und Geometrie-Tests

### 📈 Technische Verbesserungen
- **[`AuthenticCatanBoard`](src/main/java/com/catan/model/AuthenticCatanBoard.java)**: Komplett überarbeitete hexagonale Board-Implementierung
- **[`EdgeCoordinate`](src/main/java/com/catan/model/EdgeCoordinate.java) & [`VertexCoordinate`](src/main/java/com/catan/model/VertexCoordinate.java)**: Neue Koordinatenklassen für präzise Positionierung
- **Präzise Koordinatenberechnung**: Millimetergenaue Positionierung mit Pixel-Konvertierung
- **Umfangreiches Test-System**: 20+ spezialisierte Test- und Demo-Klassen

## 🎮 Funktionen

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

### Spielbrett-Modi
Das Spiel unterstützt zwei Spielbrett-Modi:
1. **Hexagonales Brett** (Standard): Authentische sechseckige Felder wie im Original CATAN
   - 19 Hexagon-Felder in klassischer CATAN-Anordnung
   - Optimaler Abstand zwischen Feldern für Straßen und Städte
   - Präzise Platzierung von Gebäuden an Schnittpunkten
   - Einheitliches Koordinatensystem für perfekte Ausrichtung

### Vereinfachungen
- Keine Entwicklungskarten (in Vorbereitung)
- Keine Sonderkarten  
- Keine Erweiterungen
- Keine Soundeffekte oder Animationen
- Keine Netzwerkfunktionalität
- Keine KI-Gegner

## 🔧 Technische Anforderungen

- **Java Version**: JDK 17 oder höher
- **Build-System**: Maven 3.6+
- **GUI-Framework**: JavaFX 17
- **IDE**: Eclipse (empfohlen)
- **Betriebssystem**: Windows, macOS, Linux

## 🚀 Installation und Ausführung

### Voraussetzungen
1. Java JDK 17 oder höher installieren
2. Maven installieren
3. JavaFX Runtime (wird automatisch über Maven geladen)

### Projekt kompilieren
```bash
cd Java-Catan
mvn clean compile