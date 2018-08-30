package com.kti.lagrange;

import com.badlogic.gdx.graphics.Color;

public enum Biome {
    Sea("Ocean", '~', Color.BLUE), Grassland("Plains", ';', Color.CHARTREUSE), Shore("Shore", '-', Color.YELLOW),
    Mountain("Mountain", '^', Color.GRAY), HighMountain("Peak", '^', Color.LIGHT_GRAY),
    Ice("Ice Shelf", '-', Color.CYAN), Lake("Lake", '~', Color.CYAN), Desert("Desert", '-', Color.GOLDENROD),
    Forest("Forest", '#', new Color(0, 0.5f, 0, 1)), Volcano("Volcano", '^', Color.RED);

    public String name;
    public char sigchar;
    public Color sigcolor;

    Biome(String name, char s, Color c) {
        this.name = name;
        sigchar = s;
        sigcolor = c;
    }
}
