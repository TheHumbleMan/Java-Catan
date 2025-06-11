package com.catan.model;

/**
 * Enum representing the different resource types in CATAN.
 * Each resource corresponds to a terrain type on the board.
 */
public enum ResourceType {
    LUMBER("Holz", "lumber"),
    BRICK("Lehm", "brick"), 
    WOOL("Wolle", "wool"),
    GRAIN("Getreide", "grain"),
    ORE("Erz", "ore");
    
    private final String germanName;
    private final String englishName;
    
    ResourceType(String germanName, String englishName) {
        this.germanName = germanName;
        this.englishName = englishName;
    }
    
    public String getGermanName() {
        return germanName;
    }
    
    public String getEnglishName() {
        return englishName;
    }
}
