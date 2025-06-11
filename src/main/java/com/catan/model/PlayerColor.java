package com.catan.model;

/**
 * Enum representing the different player colors in CATAN.
 */
public enum PlayerColor {
    RED("Rot", "#E53E3E"),
    BLUE("Blau", "#3182CE"),
    ORANGE("Orange", "#DD6B20"),
    WHITE("Weiß", "#FFFFFF");
    
    private final String germanName;
    private final String hexColor;
    
    PlayerColor(String germanName, String hexColor) {
        this.germanName = germanName;
        this.hexColor = hexColor;
    }
    
    public String getGermanName() {
        return germanName;
    }
    
    public String getHexColor() {
        return hexColor;
    }
}
