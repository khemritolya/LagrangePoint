package com.kti.lagrange;

import com.badlogic.gdx.graphics.Color;

public enum Biome {
    Sea("Ocean", '~', Color.BLUE), Grassland("Plains", ';', Color.GREEN), Shore("Shore", '-', Color.YELLOW),
    Mountain("Mountain", '^', Color.GRAY), HighMountain("Peak", '^', Color.LIGHT_GRAY),
    Ice("Ice Shelf", '-', Color.CYAN);

    public String name;
    public char sigchar;
    public Color sigcolor;

    Biome(String name, char s, Color c) {
        this.name = name;
        sigchar = s;
        sigcolor = c;
    }
}
