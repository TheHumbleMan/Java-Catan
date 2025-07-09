#!/bin/bash
# CATAN Game Launcher Script
# Dieses Script startet das CATAN-Spiel mit den richtigen JavaFX-Parametern

echo "🎲 CATAN - Das Spiel wird gestartet..."
echo "======================================"

# Prüfe ob JavaFX verfügbar ist
if ! command -v java &> /dev/null; then
    echo "❌ Java ist nicht installiert oder nicht im PATH verfügbar"
    exit 1
fi

# Erstelle das JAR falls es nicht existiert
if [ ! -f "target/java-catan-1.0.0.jar" ]; then
    echo "📦 Erstelle JAR-Datei..."
    mvn clean package -q
fi

# Versuche das Spiel zu starten
echo "🚀 Starte CATAN..."

# Option 1: Mit Maven JavaFX Plugin
if command -v mvn &> /dev/null; then
    echo "Verwende Maven JavaFX Plugin..."
    mvn javafx:run
else
    echo "❌ Maven nicht gefunden"
    
    # Option 2: Direkt mit Java (benötigt JavaFX im Classpath)
    echo "Versuche direkten Java-Start..."
    java -jar target/java-catan-1.0.0.jar
fi
