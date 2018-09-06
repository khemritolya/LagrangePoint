package com.kti.lagrange;

import com.badlogic.gdx.graphics.Color;

public enum Biome {
    SEA("Ocean", '~', Color.BLUE), PLAINS("Plains", 'm', CC.DARK_GREEN), OASIS("Oasis", '~', Color.TEAL),
    MOUNTAIN("Mountain", '^', Color.GRAY), HIGH_MOUNTAIN("Peak", '^', Color.LIGHT_GRAY),
    ICE("Ice Shelf", '+', Color.WHITE), LAKE("Lake", '~', Color.CYAN), DESERT("Desert", '-', Color.GOLDENROD),
    FOREST("Forest", '#', CC.ALMOST_DARK_GREEN), VOLCANO("Volcano", '^', Color.RED),
    FIELD("Fertile Land", 'm', Color.GOLDENROD), NORTH_TUNDRA("Tundra", '-', Color.LIGHT_GRAY),
    RIVER_EAST_WEST("River", '-', Color.CYAN), RIVER_NORTH_SOUTH("River", '|', Color.CYAN),
    TMP("ERROR", '$', Color.RED);

    private static class CC {
        private static final Color DARK_GREEN = new Color(0, 0.5f, 0, 1);
        private static final Color ALMOST_DARK_GREEN = new Color(0, 0.6f, 0, 1);
    }

    public String name;
    public char sigchar;
    public Color sigcolor;

    Biome(String name, char s, Color c) {
        this.name = name;
        sigchar = s;
        sigcolor = c;
    }

    public static int getIDByBiome(Biome b) {
        for (int i = 0; i < Biome.values().length; i++) {
            if(Biome.values()[i] == b) return i;
        }

        return -1;
    }

    public static Biome getBiomeByID(int id) {
        return Biome.values()[id];
    }
}
