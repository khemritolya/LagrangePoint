package com.kti.lagrange.util;

import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class CC {

    private static List<Color> registered = new ArrayList<>();
    private static Random random = new Random();

    public static final Color BLACK = new Color(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, 0.5f);

    private static Color newRandomColor() {
        return new Color((random.nextInt(246)+10)/256f,(random.nextInt(246)+10)/256f,
                (random.nextInt(246)+10)/256f, 0.5f);
    }

    public static Color randomColor() {
        Color c;
        while (registered.contains(c = newRandomColor()));

        registered.add(c);
        return c;
    }

}
