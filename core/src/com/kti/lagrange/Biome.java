package com.kti.lagrange;

import com.badlogic.gdx.graphics.Color;

public enum Biome {
    Sea("Ocean", '~', Color.BLUE), Grassland("Plains", 'm', CC.DARK_GREEN), Oasis("Oasis", 'o', Color.TEAL),
    Mountain("Mountain", '^', Color.GRAY), HighMountain("Peak", '^', Color.LIGHT_GRAY),
    Ice("Ice Shelf", '-', Color.CYAN), Lake("Lake", 'o', Color.CYAN), Desert("Desert", '-', Color.GOLDENROD),
    Forest("Forest", '#', CC.ALMOST_DARK_GREEN), Volcano("Volcano", '^', Color.RED);
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
}
