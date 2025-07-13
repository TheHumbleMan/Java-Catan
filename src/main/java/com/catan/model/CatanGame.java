package com.catan.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;

/**
 * Hauptklasse für die CATAN-Spiellogik, die den Spielzustand und die Regeln verwaltet.
 * Behandelt Zurverwaltung, Würfeln, Ressourcenproduktion und Siegbedingungen.
 */
public class CatanGame {
    // Spielkonstanten
    public static final int VICTORY_POINTS_TO_WIN = 7; // Siegpunkte zum Gewinnen (Testzwecke - normal 10)
    public static final int MAX_HAND_SIZE_ON_SEVEN = 7; // Maximale Handkarten bei gewürfelter 7
    
    // Grundlegende Spielkomponenten
    private final List<Player> players; // Liste aller Spieler
    private final AuthenticCatanBoard board; // Das Spielbrett
    private final Random random; // Zufallsgenerator für Würfel
    private int currentPlayerIndex; // Index des aktuellen Spielers
    private GamePhase currentPhase; // Aktuelle Spielphase
    private boolean firstNormalRound; // Flag für erste normale Runde
    private boolean gameFinished; // Flag für Spielende
    private Player winner; // Gewinner des Spiels
    private int lastDiceRoll; // Letztes Würfelergebnis
    private boolean hasRolledDice; // Flag ob Spieler bereits gewürfelt hat
    private boolean hasMovedRobber; // Flag ob Räuber bewegt wurde
    private String stolenResourcesLog; // Log für gestohlene Ressourcen
    
    // Entwicklungskarten-Vorrat
    private int knightCardsRemaining; // Verbleibende Ritterkarten
    private int victoryPointCardsRemaining; // Verbleibende Siegpunktkarten
    private int roadBuildingCardsRemaining; // Verbleibende Straßenbau-Karten
    private int yearOfPlentyCardsRemaining; // Verbleibende Erfindung-Karten
    private int monopolyCardsRemaining; // Verbleibende Monopol-Karten
    
    /**
     * Enum für die verschiedenen Spielphasen
     */
    public enum GamePhase {
        INITIAL_PLACEMENT_1,    // Erste Aufbauphase
        INITIAL_PLACEMENT_2,    // Zweite Aufbauphase  
        PLAYING                 // Normale Spielphase
    }
    
    /**
     * Konstruktor für ein neues CATAN-Spiel
     * @param playerNames Liste der Spielernamen
     */
    public CatanGame(List<String> playerNames) {
        // Spieleranzahl validieren
        if (playerNames.size() < 2 || playerNames.size() > 4) {
            throw new IllegalArgumentException("CATAN requires 2-4 players");
        }
        
        // Spieler mit Farben initialisieren
        this.players = new ArrayList<>();
        PlayerColor[] colors = PlayerColor.values();
        
        for (int i = 0; i < playerNames.size(); i++) {
            players.add(new Player(playerNames.get(i), colors[i]));
        }
        
        // Spielbrett und Grundzustand initialisieren
        this.board = new AuthenticCatanBoard();
        this.random = new Random();
        this.currentPlayerIndex = 0;
        this.currentPhase = GamePhase.INITIAL_PLACEMENT_1;
        this.gameFinished = false;
        this.firstNormalRound = false;
        this.hasRolledDice = true; // Anfangs auf true für Aufbauphase
        this.hasMovedRobber = true; // Anfangs auf true für Aufbauphase
        this.lastDiceRoll = 0;
        this.initializeDevelopmentCards();
    }
    
    // Getter-Methoden für Spielzustand
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }
    
    public List<Player> getPlayers() {
        return new ArrayList<>(players);
    }
    
    /**
     * Findet einen Spieler anhand seines Namens
     */
    public Player getPlayerByName(String name) {
        return players.stream()
            .filter(p -> p.getName().equals(name))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Kein Spieler mit Name: " + name));
    }
    
    // Weitere Getter und Setter für Spielzustand
    public String getStolenResourcesLog() {
        return stolenResourcesLog;
    }

    public void setStolenResourcesLog(String stolenResourcesLog) {
        this.stolenResourcesLog = stolenResourcesLog;
    }
    
    public AuthenticCatanBoard getBoard() {
        return board;
    }
    
    public boolean getIsFirstNormalRound() {
        return firstNormalRound;
    }

    public void setIsFirstNormalRound(boolean firstNormalRound) {
        this.firstNormalRound = firstNormalRound;
    }
    
    public GamePhase getCurrentPhase() {
        return currentPhase;
    }
    
    public int getCurrentPlayerIndex() {
    	return this.currentPlayerIndex;
    }
    
    public boolean isGameFinished() {
        return gameFinished;
    }
    
    public Player getWinner() {
        return winner;
    }
    
    public int getLastDiceRoll() {
        return lastDiceRoll;
    }
    
    public boolean hasRolledDice() {
    	return hasRolledDice;
    }
    
    public void setHasRolledDice(boolean hasRolledDice) {
        this.hasRolledDice = hasRolledDice;
    }
    
    public boolean hasMovedRobber() {
        return hasMovedRobber;
    }

    public void setHasMovedRobber(boolean hasMovedRobber) {
        this.hasMovedRobber = hasMovedRobber;
    }
    
    // Getter für Entwicklungskarten-Vorrat
    public int getKnightCardsRemaining() {
        return knightCardsRemaining;
    }
    
    public int getVictoryPointCardsRemaining() {
        return victoryPointCardsRemaining;
    }
    
    public int getRoadBuildingCardsRemaining() {
        return roadBuildingCardsRemaining;
    }
    
    public int getYearOfPlentyCardsRemaining() {
        return yearOfPlentyCardsRemaining;
    }
    
    /**
     * Initialisiert den Entwicklungskarten-Vorrat mit Standard-CATAN-Werten
     */
    private void initializeDevelopmentCards() {
        this.knightCardsRemaining = 14;          // 14 Ritterkarten
        this.victoryPointCardsRemaining = 5;     // 5 Siegpunktkarten
        this.roadBuildingCardsRemaining = 2;     // 2 Straßenbau-Karten
        this.yearOfPlentyCardsRemaining = 2;     // 2 Erfindung-Karten
        this.monopolyCardsRemaining = 2;         // 2 Monopol-Karten
    }

    /**
     * Würfelt zwei Würfel und behandelt das Ergebnis
     * @return Summe der beiden Würfel
     */
    public int rollDice() {
        // Würfeln nur in normaler Spielphase erlaubt
        if (currentPhase != GamePhase.PLAYING) {
            return 0;
        }
        
        // Zwei Würfel würfeln
        int die1 = random.nextInt(6) + 1;
        int die2 = random.nextInt(6) + 1;
        lastDiceRoll = die1 + die2;
        
        // Spezialbehandlung für gewürfelte 7
        if (lastDiceRoll == 7) {
            handleSevenRolled();
        }
        
        return lastDiceRoll;
    }
    
    /**
     * Behandelt die Auswirkungen einer gewürfelten 7
     * Spieler mit mehr als 7 Karten müssen die Hälfte abgeben, Räuber wird bewegt
     */
    private void handleSevenRolled() {
        // Alle Spieler mit mehr als 7 Karten müssen die Hälfte abgeben
        for (Player player : players) {
            int totalCards = player.getTotalResourceCount();
            if (totalCards > MAX_HAND_SIZE_ON_SEVEN) {
                int cardsToDiscard = totalCards / 2;
                // In echter Implementierung würde hier UI für Kartenauswahl aufgerufen
                discardRandomCards(player, cardsToDiscard);
            }
        }
        
        // Räuber muss bewegt werden
        hasMovedRobber = false;
        System.out.println("7 GEWÜRFELT (in handleSevenRolled aktuell)");
        robberAlert();
    }
    
    /**
     * Zeigt Alert für Räuber-Bewegung bei gewürfelter 7
     */
    private void robberAlert() {
    	Alert alert = new Alert(Alert.AlertType.INFORMATION);
    	alert.setTitle("7 gewürfelt");
        alert.setContentText("Räuber muss versetzt werden vor weiteren Handlungen!\nKlicke dafür auf ein gewünschtes Feld und verschiebe somit den Räuber");
        alert.showAndWait();
    }
    
    /**
     * Lässt einen Spieler zufällig Karten abgeben
     * @param player Spieler der Karten abgeben muss
     * @param count Anzahl der abzugebenden Karten
     */
    private void discardRandomCards(Player player, int count) {
        // Alle verfügbaren Ressourcen sammeln
        List<ResourceType> availableResources = new ArrayList<>();
        
        for (ResourceType type : ResourceType.values()) {
            int amount = player.getResourceCount(type);
            for (int i = 0; i < amount; i++) {
                availableResources.add(type);
            }
        }
        
        // Zufällig mischen und entfernen
        Collections.shuffle(availableResources);
        
        for (int i = 0; i < Math.min(count, availableResources.size()); i++) {
            player.removeResource(availableResources.get(i), 1);
        }
    }
    
    /**
     * Verteilt Ressourcen an alle Spieler basierend auf Anfangsgebäuden
     * Wird nach der Aufbauphase einmalig aufgerufen
     */
    private void initialResourceDistribution() {
        // Für jeden Spieler Ressourcen basierend auf Gebäuden verteilen
    	for (Player player : getPlayers()) {
            // Alle Gebäude des Spielers finden
        	Map<VertexCoordinate, Building> playerBuildings = board.getBuildings().entrySet().stream()
        		    .filter(entry -> entry.getValue().getOwner().equals(player))
        		    .collect(Collectors.toMap(
        		        Map.Entry::getKey,
        		        Map.Entry::getValue
        		    ));
        	
        	Map<HexCoordinate, TerrainTile> tiles = board.getAllTiles();
        	
        	// Für jedes Gebäude die angrenzenden Hexe prüfen
        	for (Building building : playerBuildings.values()) {
        		List<HexCoordinate> neighbourHexes = board.getHexNeighbours(building.getVertexCoordinate());
        		for (HexCoordinate neighbourHex : neighbourHexes) {
        			TerrainTile tile = tiles.get(neighbourHex);
        			// Ressource vom Terrain-Typ hinzufügen
        			player.setResources(tile.getTerrainType().getResourceType(), 1);
        		}
        	}
        }
    }
    
    /**
     * Verteilt Ressourcen basierend auf Würfelergebnis während des Spiels
     * @param roll Das Würfelergebnis
     */
    public void ingameResourceDistribution(int roll){
        // Für jeden Spieler Ressourcen basierend auf Würfelergebnis verteilen
    	for (Player player : getPlayers()) {
            // Alle Gebäude des Spielers finden
        	Map<VertexCoordinate, Building> playerBuildings = board.getBuildings().entrySet().stream()
        		    .filter(entry -> entry.getValue().getOwner().equals(player))
        		    .collect(Collectors.toMap(
        		        Map.Entry::getKey,
        		        Map.Entry::getValue
        		    ));
        	
        	Map<HexCoordinate, TerrainTile> tiles = board.getAllTiles();
        	
        	// Für jedes Gebäude die angrenzenden Hexe prüfen
        	for (Building building : playerBuildings.values()) {
        		List<HexCoordinate> neighbourHexes = board.getHexNeighbours(building.getVertexCoordinate());
        		for (HexCoordinate neighbourHex : neighbourHexes) {
        			// Räuber blockiert Ressourcenproduktion
        			if (neighbourHex != board.getRobberPosition()) {
        				TerrainTile tile = tiles.get(neighbourHex);
        				
        				// Prüfen ob Würfelergebnis mit Tile-Token übereinstimmt
        				TerrainType type = tile.compareTokens(roll);
        				if (type != null && !tile.hasRobber()) {
        					// Ressourcen entsprechend Gebäudetyp hinzufügen
        					player.setResources(type.getResourceType(), building.getResourceProduction());
        				}
        			}
        		}
        	}
    	}
    }
    
    /**
     * Debug-Methode: Gibt alle Spieler-Ressourcen in der Konsole aus
     */
    public void listPlayersResources() {
    	for (Player player : getPlayers()) {    	   	
        	Map<ResourceType, Integer> playerResources = player.getResources();
    		System.out.println("Resources for player " + player.getName() + ":");
    		playerResources.forEach((resource, amount) -> 
    		    System.out.println(" - " + resource + ": " + amount));
    	}
    }
    
    /**
     * Debug-Methode: Gibt alle Spieler-Siegpunkte in der Konsole aus
     */
    public void listPlayersVictoryPoints() {
        for (Player player : getPlayers()) {
            System.out.println("Victory points for player " + player.getName() + ":");
            System.out.println(" - " + player.getVictoryPoints());
        }
    }
    
    /**
     * Führt einen Handel zwischen zwei Spielern durch
     * @param otherPlayer Der andere Spieler
     * @param give Ressourcen die der aktuelle Spieler gibt
     * @param receive Ressourcen die der aktuelle Spieler erhält
     * @return true wenn Handel erfolgreich
     */
    public boolean offerTrade(Player otherPlayer, Map<ResourceType, Integer> give, Map<ResourceType, Integer> receive) {
        Player currentPlayer = getCurrentPlayer();
        
        // Validierung: Nur in normaler Spielphase und nicht mit sich selbst
        if (currentPhase != GamePhase.PLAYING || otherPlayer == currentPlayer) {
            return false;
        }
        
        // Prüfen ob beide Spieler genügend Ressourcen haben
        for (Map.Entry<ResourceType, Integer> entry : give.entrySet()) {
            if (currentPlayer.getResourceCount(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        
        for (Map.Entry<ResourceType, Integer> entry : receive.entrySet()) {
            if (otherPlayer.getResourceCount(entry.getKey()) < entry.getValue()) {
                return false;
            }
        }
        
        // Handel durchführen
        for (Map.Entry<ResourceType, Integer> entry : give.entrySet()) {
            currentPlayer.removeResource(entry.getKey(), entry.getValue());
            otherPlayer.addResource(entry.getKey(), entry.getValue());
        }
        
        for (Map.Entry<ResourceType, Integer> entry : receive.entrySet()) {
            otherPlayer.removeResource(entry.getKey(), entry.getValue());
            currentPlayer.addResource(entry.getKey(), entry.getValue());
        }
        
        return true;
    }
    
    /**
     * Beendet den aktuellen Spielerzug und wechselt zum nächsten Spieler
     */
    public void endTurn() {
    	boolean skipInkrement = false;
    	
        if (currentPhase == GamePhase.INITIAL_PLACEMENT_1) {
            // Erste Aufbauphase: Vorwärts durch Spieler
        	this.getCurrentPlayer().setInitialSettlementPlaced(false);
        	this.getCurrentPlayer().setInitialRoadPlaced(false);
            currentPlayerIndex = (currentPlayerIndex + 1);
            System.out.println("index: " + getCurrentPlayerIndex());
            
            // Wenn alle Spieler dran waren, zur zweiten Phase
            if (currentPlayerIndex == players.size()) {
                currentPhase = GamePhase.INITIAL_PLACEMENT_2;
                currentPlayerIndex = players.size() - 2; // Rückwärts-Reihenfolge
                System.out.println("Geht jetzt in Phase 2");
            }
        } else if (currentPhase == GamePhase.INITIAL_PLACEMENT_2) {
            // Zweite Aufbauphase: Rückwärts durch Spieler
            currentPlayerIndex = (currentPlayerIndex - 1);
            
            // Wenn alle Spieler dran waren, zur normalen Spielphase
            if (currentPlayerIndex == -1) {
                currentPhase = GamePhase.PLAYING;
                currentPlayerIndex = 0;
                skipInkrement = true;
                
                // Initiale Ressourcenverteilung
                initialResourceDistribution();
                
                // Debug-Ausgabe der Startressourcen
                for (Player player : getPlayers()) {
                    System.out.println("=== Ressourcen des Spielers ===" + player.getName());
                    player.getResources().forEach((type, amount) -> {
                        System.out.println(type + ": " + amount);
                    });
                }                   
                
                System.out.println("geht jetzt in normale 'Spielphase'");
                firstNormalRound = true;
                setHasRolledDice(false);
            }
        } else {
            // Normale Spielphase: Reihum
        	if (!skipInkrement) {
        	    currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        	}
        }
        
        skipInkrement = false;
    }
    
    /**
     * Prüft ob ein Spieler die Siegbedingungen erfüllt hat
     */
    public void checkVictoryCondition() {
        for (Player player : players) {
            if (player.getVictoryPoints() >= VICTORY_POINTS_TO_WIN) {
                gameFinished = true;
                winner = player;
                break;
            }
        }
    }
    
    // Hilfsmethoden für Ressourcenprüfungen
    public boolean hasSufficientResourcesForRoad() {
    	Player currentPlayer = getCurrentPlayer();
    	return currentPlayer.hasSufficientResources(Player.ROAD_COST);
    }
    
    public boolean hasSufficientResourcesForSettlement() {
    	Player currentPlayer = getCurrentPlayer();
    	return currentPlayer.hasSufficientResources(Player.SETTLEMENT_COST);
    }
    
    public boolean hasSufficientResourcesForCity() {
    	Player currentPlayer = getCurrentPlayer();
    	return currentPlayer.hasSufficientResources(Player.CITY_COST);
    }

    public boolean hasSufficientResourcesForDevelopmentCard() {
        Player currentPlayer = getCurrentPlayer();
        return currentPlayer.hasSufficientResources(Player.DEVELOPMENT_CARD_COST);
    }
    
    /**
     * Prüft ob sich das Spiel in der Aufbauphase befindet
     */
    public boolean isBeginning() {
        return currentPhase == GamePhase.INITIAL_PLACEMENT_1 || currentPhase == GamePhase.INITIAL_PLACEMENT_2;
    }
    
    /**
     * Prüft ob an einem Vertex grundsätzlich ein Gebäude platziert werden kann
     * @param vertex Die Vertex-Koordinate
     * @param player Der Spieler
     * @param isBeginning Ob Aufbauphase aktiv ist
     * @return true wenn Gebäude platziert werden kann
     */
    public boolean canPlaceAnyBuilding(VertexCoordinate vertex, Player player, boolean isBeginning) {
        // Vertex muss gültig sein
        if (!board.getValidVertices().containsKey(vertex)) {
            return false;
        }
        
        // Position darf nicht besetzt sein
        if (board.getBuildings().containsKey(vertex)) {
            return false;
        }
        
        // Distanzregel: Keine angrenzenden Gebäude
        for (VertexCoordinate vert : vertex.getAdjacentVertices(AuthenticCatanBoard.getHexRadius(), AuthenticCatanBoard.getBoardCenterX(), AuthenticCatanBoard.getBoardCenterY(), board.getNormalizedCatanCoordMap(), board.getValidVertices())) {
        	if (board.getBuildings().containsKey(vert)) {
        		return false;
        	}
        }
        
        // Prüfung auf Anbindung zu eigener Straße (außer in Aufbauphase)
        List<Road> ownedRoads = board.getRoads().values().stream()
        	    .filter(b -> b.getOwner().equals(player))
        	    .collect(Collectors.toList());
        
        for (Road road : ownedRoads) {
            List<VertexCoordinate> roadVertices = road.getEdgeCoordinate().getConnectedVertices();
            for (VertexCoordinate roadVertex : roadVertices) {
                if (roadVertex.equals(vertex) && !isBeginning()) {
                    return true;
                }
            }
        }
        
        // In Aufbauphase immer erlaubt
        if (isBeginning()) {
        	return true;
        }
        
        return false;
    }
    
    /**
     * Legacy-Methode: Prüft ob spezifisches Gebäude platziert werden kann
     * (Wird für Zukunft beibehalten, aktuell nicht verwendet)
     */
    public boolean canPlaceSpecificBuilding(VertexCoordinate vertex, Player player, boolean isBeginning, Building.Type type) {
    	boolean canBuildCity = hasSufficientResourcesForCity();
    	boolean canBuildSettlement = hasSufficientResourcesForSettlement() || isBeginning;
    	
    	// Städte nicht in Aufbauphase erlaubt
    	if (isBeginning && type == Building.Type.CITY) {
    		return false;
    	}
    	
        // Grundvalidierung
        if (!board.getValidVertices().containsKey(vertex) || board.getBuildings().containsKey(vertex)) {
            return false;
        }  
        
        // Distanzregel prüfen
        for (VertexCoordinate vert : vertex.getAdjacentVertices(AuthenticCatanBoard.getHexRadius(), AuthenticCatanBoard.getBoardCenterX(), AuthenticCatanBoard.getBoardCenterY(), board.getNormalizedCatanCoordMap(), board.getValidVertices())) {
        	if (board.getBuildings().containsKey(vert)) {
        		return false;
        	}
        }
        
        // Ressourcenprüfung
        if (type == Building.Type.CITY && !canBuildCity) {
        	return false;
        }
        else if (type == Building.Type.SETTLEMENT && !canBuildSettlement) {
        	return false;
        }
        
        return true;
    }
    
    /**
     * Prüft ob eine Stadt an einem Vertex platziert werden kann
     * @param vertex Die Vertex-Koordinate
     * @param player Der Spieler
     * @param isBeginning Ob Aufbauphase aktiv ist
     * @return true wenn Stadt platziert werden kann
     */
    public boolean canPlaceCity(VertexCoordinate vertex, Player player, boolean isBeginning) {
        // Prüfung ob Spieler bereits eine Siedlung an dieser Position besitzt
    	boolean ownsSettlementAt = board.getBuildings().entrySet().stream()
    		    .anyMatch(entry ->
    		        entry.getKey().equals(vertex) &&
    		        entry.getValue().getOwner().equals(player) &&
    		        entry.getValue().getType() == Building.Type.SETTLEMENT
    		    );

    	// Stadt nur möglich wenn: eigene Siedlung vorhanden, genügend Ressourcen, nicht Aufbauphase, genügend Städte verfügbar
    	if (!ownsSettlementAt || !hasSufficientResourcesForCity() || isBeginning || player.getCityCount() <= 0) {
    		return false;
    	}
    	return true;
    }
    
    /**
     * Prüft ob eine Siedlung an einem Vertex platziert werden kann
     * @param vertex Die Vertex-Koordinate
     * @param player Der Spieler
     * @param isBeginning Ob Aufbauphase aktiv ist
     * @return true wenn Siedlung platziert werden kann
     */
    public boolean canPlaceSettlement(VertexCoordinate vertex, Player player, boolean isBeginning) {
        // Grundvalidierung + Ressourcenprüfung + Verfügbarkeit
    	if (!canPlaceAnyBuilding(vertex, player, isBeginning) || 
    	    (!hasSufficientResourcesForSettlement() && !isBeginning) || 
    	    player.getSettlementCount() <= 0) {
    		return false;
    	}
    	return true;
    }
    
    /**
     * Platziert ein Gebäude an einem Vertex
     * @param type Typ des Gebäudes
     * @param vertex Die Vertex-Koordinate
     * @param player Der Spieler
     */
    public void placeBuilding(Building.Type type, VertexCoordinate vertex, Player player) {
        // Gebäude zum Board hinzufügen
        board.getBuildings().put(vertex, new Building(type, player, vertex));
        player.addVictoryPoints(1); // Siegpunkt für beide Gebäudetypen
        
        if (type == Building.Type.SETTLEMENT && !isBeginning()) {
            // Normale Spielphase: Ressourcen abziehen
        	player.removeResource(Player.SETTLEMENT_COST);
        	player.setSettlementCount(player.getSettlementCount() - 1);
        	System.out.println("Settlement count: " + player.getSettlementCount());
        }
        else if (type == Building.Type.SETTLEMENT && isBeginning()) {
            // Aufbauphase: Nur Anzahl reduzieren
        	player.setSettlementCount(player.getSettlementCount() - 1);
        	System.out.println("Settlement count: " + player.getSettlementCount());
        }
        else if (type == Building.Type.CITY && !isBeginning()) {
            // Stadt-Upgrade: Ressourcen abziehen, Städte-Anzahl reduzieren
        	player.removeResource(Player.CITY_COST);
        	player.setCityCount(player.getCityCount() - 1);
        }
    }
    
    /**
     * Prüft ob eine Straße an einer Edge platziert werden kann
     * @param edge Die Edge-Koordinate
     * @param player Der Spieler
     * @return true wenn Straße platziert werden kann
     */
    public boolean canPlaceRoad(EdgeCoordinate edge, Player player) {
        // Edge muss gültig sein
        if (!board.getValidEdges().contains(edge)) {
            return false;
        }
        
        // Genügend Straßen verfügbar
        if (player.getRoadCount() <= 0) {
        	return false;
        }
        
        // Straße darf nicht bereits existieren
        if (board.getRoads().containsKey(edge)){
        	return false;
        }
        
        // Spezialfall Aufbauphase: Anbindung zu Gebäude aber nicht zu anderer Straße
        if (isBuildingClose(edge, player) && isBeginning()) {
        	return !isAdjacentRoad(edge, player);
        }
        
        // Normale Spielphase: Genügend Ressourcen
        if (!hasSufficientResourcesForRoad()) {
        	return false;
        }
        
        // Anbindung zu eigenem Gebäude
        if (isBuildingClose(edge, player)) {
        	return true;
        }
        
        // Anbindung zu eigener Straße (nur normale Spielphase)
        List<Road> ownedRoads = board.getRoads().values().stream()
        	    .filter(b -> b.getOwner().equals(player))
        	    .collect(Collectors.toList());
        
        for (Road road : ownedRoads) {
            List<VertexCoordinate> roadVertices = road.getEdgeCoordinate().getConnectedVertices();
            for (VertexCoordinate roadVertex : roadVertices) {
                if ((roadVertex.equals(edge.getVertexA()) || roadVertex.equals(edge.getVertexB())) && !isBeginning()) {
                    return true;
                }
            }
        }       
        
        return false;
    }
    
    /**
     * Platziert eine Straße an einer Edge
     * @param edge Die Edge-Koordinate
     * @param player Der Spieler
     * @return true wenn erfolgreich platziert
     */
    public boolean placeRoad(EdgeCoordinate edge, Player player) {
        if (canPlaceRoad(edge, player)) {
            // Straße zum Board hinzufügen
            board.getRoads().put(edge, new Road(player, edge));
            player.setRoadCount(player.getRoadCount() - 1);
            
            // Ressourcen nur in normaler Spielphase abziehen
            if (!isBeginning()) {
                player.removeResource(Player.ROAD_COST);
            }
            return true;
        }
        return false;
    }
    
    /**
     * Prüft ob ein Spieler ein Gebäude nahe einer Edge hat
     * @param edge Die Edge-Koordinate
     * @param player Der Spieler
     * @return true wenn Gebäude in der Nähe
     */
    boolean isBuildingClose(EdgeCoordinate edge, Player player) {
        // Alle eigenen Gebäude finden
    	List<Building> ownedBuildings = board.getBuildings().values().stream()
        	    .filter(b -> b.getOwner().equals(player))
        	    .collect(Collectors.toList());
    	
        // Prüfen ob Gebäude an einem der Edge-Endpunkte liegt
        for (Building building : ownedBuildings) {
			if (building.getVertexCoordinate().equals(edge.getVertexA()) || 
			    building.getVertexCoordinate().equals(edge.getVertexB())) {	
				return true;
			}
        }
        return false;
    }
    
    /**
     * Prüft ob bereits eine Straße an einem Gebäude anliegt (für Aufbauphase)
     * @param edge Die Edge-Koordinate
     * @param player Der Spieler
     * @return true wenn bereits angrenzende Straße existiert
     */
    boolean isAdjacentRoad(EdgeCoordinate edge, Player player) {
        // Alle eigenen Straßen finden
    	List<Road> ownedRoads = board.getRoads().values().stream()
        	    .filter(b -> b.getOwner().equals(player))
        	    .collect(Collectors.toList());
    	
        // Gebäude an einem der Edge-Endpunkte finden
    	Optional<Building> building = board.getBuildings().values().stream()
    		    .filter(b -> b.getVertexCoordinate().equals(edge.getVertexA()) || 
    		                 b.getVertexCoordinate().equals(edge.getVertexB()))
    		    .findFirst();
    	
    	if (building.isPresent()){
    		Building b = building.get();
    		// Prüfen ob bereits Straße an diesem Gebäude liegt
    		for (Road road : ownedRoads) {
    			if (b.getVertexCoordinate() == road.getEdgeCoordinate().getVertexA() || 
    			    b.getVertexCoordinate() == road.getEdgeCoordinate().getVertexB()) {
    				return true;
    			}
    		}
    	}
    	return false;
    }
    
    /**
     * Prüft ob der aktuelle Spieler seine Platzierungen für die aktuelle Phase abgeschlossen hat
     * @return true wenn Phase abgeschlossen
     */
    public boolean hasCompletedPlacementForCurrentPhase() {
    	Player player = getCurrentPlayer();
    	
        // Anzahl der platzierten Gebäude und Straßen zählen
        long settlementCount = board.getBuildings().values().stream()
            .filter(b -> b.getOwner().equals(player))
            .count();

        long roadCount = board.getRoads().values().stream()
            .filter(r -> r.getOwner().equals(player))
            .count();
        
        // Bedingungen je nach Phase
        if (currentPhase == GamePhase.INITIAL_PLACEMENT_1 && getCurrentPlayerIndex() == getPlayers().size() - 1) {
            // Letzter Spieler in Phase 1: 2 Siedlungen, 2 Straßen
            return settlementCount >= 2 && roadCount >= 2;
        } else if (currentPhase == GamePhase.INITIAL_PLACEMENT_1) {
            // Andere Spieler in Phase 1: 1 Siedlung, 1 Straße
            return settlementCount >= 1 && roadCount >= 1; 
        } else if (currentPhase == GamePhase.INITIAL_PLACEMENT_2) {
            // Phase 2: 2 Siedlungen, 2 Straßen
            return settlementCount >= 2 && roadCount >= 2;
        } 

        // Normale Spielphase: Immer abgeschlossen
        return true;
    }
    
    /**
     * Erweitert eine Siedlung zu einer Stadt (Legacy-Methode)
     * @param vertex Die Vertex-Koordinate
     * @param player Der Spieler
     * @return true wenn erfolgreich
     */
    public boolean upgradeToCity(VertexCoordinate vertex, Player player) {
        Building existing = board.getBuildings().get(vertex);
        
        if (existing != null && existing.getOwner() == player && 
            existing.getType() == Building.Type.SETTLEMENT) {
            existing.setType(Building.Type.CITY);
            return true;
        }
        return false;
    }
    
    /**
     * Bewegt den Räuber zu einer neuen Position
     * @param newPosition Die neue Hex-Koordinate für den Räuber
     */
    public void moveRobber(HexCoordinate newPosition) {
        // Räuber von aktueller Position entfernen
        TerrainTile currentTile = board.getHexTile(board.getRobberPosition());
        if (currentTile != null) {
            currentTile.setRobber(false);
        }
        
        // Räuber auf neue Position setzen
        TerrainTile newTile = board.getHexTile(newPosition);
        if (newTile != null) {
            newTile.setRobber(true);
            setHasMovedRobber(true);
            board.setRobberPosition(newPosition);
            
            // Betroffene Spieler bestimmen und Ressource stehlen
            Set<Player> adjacentPlayers = determinePlayers(newPosition);
            System.out.println("Betroffene Spieler gefunden, Anzahl: " + adjacentPlayers.size());
            
            Player selectedPlayer = choosePlayer(adjacentPlayers);
            if (selectedPlayer != null) {
	            Map<ResourceType, Integer> stolenResources = stealFromPlayer(selectedPlayer);
	            stolenResourcesLog = getSingleStolenResourceAsString(selectedPlayer, stolenResources);
            } else {
            	stolenResourcesLog = getSingleStolenResourceAsString(selectedPlayer, null);
            }
        }
    }

    /**
     * Bestimmt alle Spieler die von einer Räuber-Position betroffen sind
     * @param position Die Hex-Koordinate des Räubers
     * @return Set der betroffenen Spieler
     */
    private Set<Player> determinePlayers(HexCoordinate position) {
    	Set<Player> adjacentPlayers = new HashSet<>();
    	
    	// Alle Gebäude durchgehen
    	for (Building building : board.getBuildings().values()) {
    		VertexCoordinate normalizedVertex = building.getVertexCoordinate();
    		List<VertexCoordinate> unnormalizedVertices = board.getNormalizedToUnnormalized().get(normalizedVertex);
    		
    		// Prüfen ob Vertex an der Räuber-Position liegt
    		for (VertexCoordinate vertex : unnormalizedVertices) {
				if (vertex.getX() == position.getQ() && vertex.getY() == position.getR() && 
				    building.getOwner() != getCurrentPlayer()) {
					adjacentPlayers.add(building.getOwner());
				}
			}
    	}
    	return adjacentPlayers;
    }
    
    /**
     * Lässt den Spieler einen Spieler zum Bestehlen auswählen
     * @param adjacentPlayers Set der verfügbaren Spieler
     * @return Der ausgewählte Spieler oder null
     */
    private Player choosePlayer(Set<Player> adjacentPlayers) {
    	 if (adjacentPlayers == null || adjacentPlayers.isEmpty()) return null;
    	 
    	 // Wenn nur ein Spieler verfügbar
    	 if (adjacentPlayers.size() == 1) {
    	        Player onlyPlayer = adjacentPlayers.iterator().next();
    	        System.out.println("Nur ein Spieler verfügbar: " + onlyPlayer.getName());
    	        return onlyPlayer;
    	 }
    	 
    	 // Auswahl-Dialog für mehrere Spieler
    	 List<Player> playerList = new ArrayList<>(adjacentPlayers);
    	 Optional<Player> result = Optional.empty();

    	 // Wiederholung bis gültige Auswahl
    	 while (result.isEmpty()) {
    	        ChoiceDialog<Player> dialog = new ChoiceDialog<>(playerList.get(0), playerList);
    	        dialog.setTitle("Spieler auswählen");
    	        dialog.setHeaderText("Wähle einen Spieler aus, von dem du klauen willst:");
    	        dialog.setContentText("Spieler:");

    	        result = dialog.showAndWait();

    	        if (result.isEmpty()) {
    	            // Warnung bei fehlender Auswahl
    	            Alert alert = new Alert(Alert.AlertType.WARNING);
    	            alert.setTitle("Auswahl erforderlich");
    	            alert.setHeaderText("Du musst einen Spieler auswählen!");
    	            alert.setContentText("Bitte wähle einen Spieler aus der Liste.");
    	            alert.showAndWait();
    	        }
    	 }

    	 Player selectedPlayer = result.get();
    	 System.out.println("Du hast gewählt: " + selectedPlayer.getName());
    	 return selectedPlayer;
    }
    
    /**
     * Stiehlt eine zufällige Ressource von einem Spieler
     * @param selectedPlayer Der Spieler dem gestohlen wird
     * @return Map mit gestohlenen Ressourcen
     */
    private Map<ResourceType, Integer> stealFromPlayer(Player selectedPlayer) {
    	Map<ResourceType, Integer> stolenResources = selectedPlayer.stealRandomResource();
    	getCurrentPlayer().addResources(stolenResources);
    	return stolenResources;
    }
    
    /**
     * Erstellt eine Textbeschreibung für gestohlene Ressourcen
     * @param selectedPlayer Der bestohlene Spieler
     * @param stolenResources Die gestohlenen Ressourcen
     * @return Beschreibungstext
     */
    public String getSingleStolenResourceAsString(Player selectedPlayer, Map<ResourceType, Integer> stolenResources) {
        if (stolenResources == null || stolenResources.isEmpty()) {
            return getCurrentPlayer().getName() + " hat in dieser Runde keine Ressource gestohlen.";
        }

        Map.Entry<ResourceType, Integer> entry = stolenResources.entrySet().iterator().next();
        ResourceType type = entry.getKey();
        int amount = entry.getValue();

        return String.format("%s hat in dieser Runde von %s %d %s gestohlen",
        		getCurrentPlayer().getName(),
        		selectedPlayer.getName(),
        		amount,
        		type.getGermanName());
    }
    
    /**
     * Führt eine Ressourcen-Transaktion für einen Spieler durch
     * @param player Der Spieler
     * @param cost Kosten (abzuziehende Ressourcen)
     * @param add Erhaltene Ressourcen
     * @return Beschreibungstext der Transaktion
     */
    public String subtractPlayerRessources(Player player, Map<ResourceType, Integer> cost, Map<ResourceType, Integer> add) {
    	if (player.hasSufficientResources(cost)) {
    		// Transaktion durchführen
			player.spendResources(cost);
			player.addResources(add);
			
			// Beschreibungstext erstellen
			StringBuilder message = new StringBuilder();
	        message.append(player.getName()).append(" hat ");

	        // Erhaltene Ressourcen auflisten
	        List<String> added = add.entrySet().stream()
	            .map(e -> e.getValue() + " " + e.getKey().getGermanName())
	            .toList();
	        message.append(String.join(", ", added));
	        message.append(" erhalten und ");

	        // Ausgegebene Ressourcen auflisten
	        List<String> removed = cost.entrySet().stream()
	            .map(e -> e.getValue() + " " + e.getKey().getGermanName())
	            .toList();
	        message.append(String.join(", ", removed));
	        message.append(" ausgegeben.");

	        return message.toString();
    	} else {
    		return player.getName() + " hat nicht genügend Ressourcen für diesen Handel.";
    	}
    }
    
    /**
     * Führt einen Handel zwischen zwei Spielern durch (mit Bestätigung)
     * @param otherPlayer Der andere Spieler
     * @param amountCurrentPlayer Ressourcen vom aktuellen Spieler
     * @param amountOtherPlayer Ressourcen vom anderen Spieler
     * @return Beschreibungstext des Handels
     */
    public String handlePlayerTrade(Player otherPlayer, Map<ResourceType, Integer> amountCurrentPlayer, Map<ResourceType, Integer> amountOtherPlayer) {
    	Player currentPlayer = getCurrentPlayer();
    	
    	// Prüfen ob beide Spieler genügend Ressourcen haben
    	if (otherPlayer.hasSufficientResources(amountOtherPlayer) && currentPlayer.hasSufficientResources(amountCurrentPlayer)) {
    		// Handelstext für Bestätigung erstellen
    		List<String> fromCurrentToOther = amountCurrentPlayer.entrySet().stream()
    	        .map(e -> e.getValue() + " " + e.getKey().getGermanName())
    	        .toList();

    	    List<String> fromOtherToCurrent = amountOtherPlayer.entrySet().stream()
    	        .map(e -> e.getValue() + " " + e.getKey().getGermanName())
    	        .toList();

    	    String tradeMessage = currentPlayer.getName() + " möchte dir, " + otherPlayer.getName() + ", "
    	        + String.join(", ", fromCurrentToOther)
    	        + " geben und dafür "
    	        + String.join(", ", fromOtherToCurrent)
    	        + " von dir erhalten.\n\nMöchtest du diesen Handel annehmen?";

    	    // Bestätigungs-Dialog anzeigen
    	    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    	    alert.setTitle("Handelsanfrage");
    	    alert.setHeaderText("Handel mit " + currentPlayer.getName());
    	    alert.setContentText(tradeMessage);

    	    Optional<ButtonType> result = alert.showAndWait();

    	    if (result.isPresent() && result.get() == ButtonType.OK) {
    	        // Handel durchführen
    	        currentPlayer.spendResources(amountCurrentPlayer);
    	        currentPlayer.addResources(amountOtherPlayer);
    	        otherPlayer.spendResources(amountOtherPlayer);
    	        otherPlayer.addResources(amountCurrentPlayer);

    	        // Erfolgstext erstellen
    	        StringBuilder message = new StringBuilder();
    	        message.append(currentPlayer.getName())
    	               .append(" hat ")
    	               .append(otherPlayer.getName())
    	               .append(" ")
    	               .append(String.join(", ", fromCurrentToOther))
    	               .append(" gegeben und dafür ")
    	               .append(String.join(", ", fromOtherToCurrent))
    	               .append(" erhalten.");

    	        return message.toString();
    	    } else {
    	        return otherPlayer.getName() + " hat den Handel abgelehnt.";
    	    }
    	} else {
    		return currentPlayer.getName() + " oder " + otherPlayer.getName() + " hat nicht genügend Ressourcen.";
    	}
    }
    
    /**
     * Gibt die Gesamtanzahl verbleibender Entwicklungskarten zurück
     * @return Anzahl verbleibender Entwicklungskarten
     */
    public int getTotalDevelopmentCardsRemaining() {
        return knightCardsRemaining + victoryPointCardsRemaining +
               roadBuildingCardsRemaining + yearOfPlentyCardsRemaining +
               monopolyCardsRemaining;
    }
    
    /**
     * Prüft ob noch Entwicklungskarten verfügbar sind
     * @return true wenn noch Karten vorhanden
     */
    public boolean hasDevelopmentCardsRemaining() {
        return getTotalDevelopmentCardsRemaining() > 0;
    }
}
