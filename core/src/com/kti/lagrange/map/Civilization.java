package com.kti.lagrange.map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Civilization {
    private Color c;

    private static List<Color> registered;
    private static final Color[] chooseFrom = {
            Color.RED, Color.BLUE, Color.TAN, Color.YELLOW, Color.GREEN, Color.GRAY
    };

    private Civilization(Random r) {
        if (registered == null) {
            registered = new ArrayList<>();
        }

        for (int i = 0; i < chooseFrom.length; i++) {
            if (!registered.contains(chooseFrom[i])) {
                c = chooseFrom[i];
                registered.add(chooseFrom[i]);
                break;
            }
        }

        if (c == null) {
            c = Color.BROWN;
        }

        c = new Color(c.r, c.g, c.b, 0.1f);

    }

    public static Civilization generateNewCivilization(int seed, Biome[][] world) {
        Random r = new Random(seed);
        Civilization c = new Civilization(r);




        return c;
    }

    public static void clearRegistered() {
        registered = new ArrayList<>();
    }
}
