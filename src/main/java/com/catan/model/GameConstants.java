package com.catan.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for managing game constants and helper methods.
 */
public class GameConstants {
    
    // Victory conditions
    public static final int VICTORY_POINTS_TO_WIN = 10;
    public static final int LONGEST_ROAD_BONUS = 2;
    public static final int LARGEST_ARMY_BONUS = 2;
    
    // Resource management
    public static final int MAX_HAND_SIZE_BEFORE_ROBBER = 7;
    public static final int BANK_RESOURCE_LIMIT = 19; // Per resource type
    
    // Building limits per player
    public static final int MAX_SETTLEMENTS_PER_PLAYER = 5;
    public static final int MAX_CITIES_PER_PLAYER = 4;
    public static final int MAX_ROADS_PER_PLAYER = 15;
    
    // Trade ratios
    public static final int HARBOR_TRADE_RATIO = 2; // 2:1 specialized harbor
    public static final int GENERIC_HARBOR_RATIO = 3; // 3:1 generic harbor
    public static final int BANK_TRADE_RATIO = 4; // 4:1 with bank
    
    // Dice probabilities (for AI or statistical analysis)
    private static final Map<Integer, Integer> DICE_PROBABILITY_DOTS = createDiceProbabilityMap();
    
    // Entwicklungskarten-Konstanten
    public static final int INITIAL_KNIGHT_CARDS = 14;
    public static final int INITIAL_VICTORY_POINT_CARDS = 5;
    public static final int INITIAL_ROAD_BUILDING_CARDS = 2;
    public static final int INITIAL_YEAR_OF_PLENTY_CARDS = 2;
    public static final int INITIAL_MONOPOLY_CARDS = 2;
    public static final int TOTAL_DEVELOPMENT_CARDS = 25;
    
    /**
     * Get the probability dots (number of ways to roll) for a given dice sum.
     */
    public static int getDiceProbabilityDots(int diceSum) {
        return DICE_PROBABILITY_DOTS.getOrDefault(diceSum, 0);
    }
    
    /**
     * Get the probability (as percentage) of rolling a specific number.
     */
    public static double getDiceProbability(int diceSum) {
        return (getDiceProbabilityDots(diceSum) / 36.0) * 100.0;
    }
    
    /**
     * Validate if a player name is acceptable.
     */
    public static boolean isValidPlayerName(String name) {
        return name != null && !name.trim().isEmpty() && name.length() <= 20;
    }
    
    /**
     * Get the standard CATAN terrain distribution for board setup.
     */
    public static List<TerrainType> getStandardTerrainDistribution() {
        return Arrays.asList(
            TerrainType.FOREST, TerrainType.FOREST, TerrainType.FOREST, TerrainType.FOREST,
            TerrainType.HILLS, TerrainType.HILLS, TerrainType.HILLS,
            TerrainType.PASTURE, TerrainType.PASTURE, TerrainType.PASTURE, TerrainType.PASTURE,
            TerrainType.FIELDS, TerrainType.FIELDS, TerrainType.FIELDS, TerrainType.FIELDS,
            TerrainType.MOUNTAINS, TerrainType.MOUNTAINS, TerrainType.MOUNTAINS,
            TerrainType.DESERT
        );
    }
    
    /**
     * Get the standard CATAN number token distribution.
     */
    public static List<Integer> getStandardNumberDistribution() {
        return Arrays.asList(2, 3, 3, 4, 4, 5, 5, 6, 6, 8, 8, 9, 9, 10, 10, 11, 11, 12);
    }
    
    /**
     * Create the dice probability map. Helper method to avoid Map.of() parameter limit.
     */
    private static Map<Integer, Integer> createDiceProbabilityMap() {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(2, 1);
        map.put(3, 2);
        map.put(4, 3);
        map.put(5, 4);
        map.put(6, 5);
        map.put(7, 6);
        map.put(8, 5);
        map.put(9, 4);
        map.put(10, 3);
        map.put(11, 2);
        map.put(12, 1);
        return Collections.unmodifiableMap(map);
    }
}
