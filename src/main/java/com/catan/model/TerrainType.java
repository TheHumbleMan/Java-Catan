package com.catan.model;

/**
 * Enum representing the different terrain types in CATAN.
 * Each terrain type produces a specific resource when its number is rolled.
 */
public enum TerrainType {
    FOREST(ResourceType.LUMBER, "Wald", "forest"),
    HILLS(ResourceType.BRICK, "Hügel", "hills"),
    PASTURE(ResourceType.WOOL, "Weideland", "pasture"),
    FIELDS(ResourceType.GRAIN, "Acker", "fields"),
    MOUNTAINS(ResourceType.ORE, "Gebirge", "mountains"),
    DESERT(null, "Wüste", "desert");
    
    private final ResourceType resourceType;
    private final String germanName;
    private final String englishName;
    
    TerrainType(ResourceType resourceType, String germanName, String englishName) {
        this.resourceType = resourceType;
        this.germanName = germanName;
        this.englishName = englishName;
    }
    
    public ResourceType getResourceType() {
        return resourceType;
    }
    
    public String getGermanName() {
        return germanName;
    }
    
    public String getEnglishName() {
        return englishName;
    }
    
    public boolean producesResource() {
        return resourceType != null;
    }
}
