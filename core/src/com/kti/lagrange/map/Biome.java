package com.kti.lagrange.map;

import com.badlogic.gdx.graphics.Color;

public enum Biome {
    SEA("Ocean", '~', Color.BLUE, 0), PLAINS("Plains", 'm', CC.DARK_GREEN, 1000), OASIS("Oasis", '~', Color.TEAL, 200),
    MOUNTAIN("Mountain", '^', Color.GRAY, 50), HIGH_MOUNTAIN("Peak", '^', Color.LIGHT_GRAY, 10),
    ICE("Ice Shelf", '+', Color.WHITE, 5), LAKE("Lake", '~', Color.CYAN, 0),
    DESERT("Desert", '-', Color.GOLDENROD, 50), FOREST("Forest", '#', CC.ALMOST_DARK_GREEN, 1000),
    VOLCANO("Volcano", '^', Color.RED, 0), FIELD("Farmland", 'm', CC.ALMOST_DARK_GREEN, 2000),
    NORTH_TUNDRA("Tundra", '-', Color.LIGHT_GRAY, 100), RIVER_EAST_WEST("River", '-', Color.CYAN, 100),
    RIVER_NORTH_SOUTH("River", '|', Color.CYAN, 1000),
    CITY("City", 'W', Color.GRAY, 4000), TMP("ERROR", '$', Color.RED, -1);

    private static class CC {
        private static final Color DARK_GREEN = new Color(0, 0.5f, 0, 1);
        private static final Color ALMOST_DARK_GREEN = new Color(0, 0.6f, 0, 1);
    }

    public String name;
    public char sigchar;
    public Color sigcolor;
    public int carrycap;

    Biome(String name, char s, Color c, int carrycap) {
        this.name = name;
        sigchar = s;
        sigcolor = c;
        this.carrycap = carrycap;
    }

    public static int getIDByBiome(Biome b) {
        for (int i = 0; i < Biome.values().length; i++) {
            if (Biome.values()[i] == b) return i;
        }

        return -1;
    }

    public static Biome getBiomeByID(int id) {
        return Biome.values()[id];
    }
}
