package com.kti.lagrange;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Civilization {
    private Color c;
    private List<Vector2> cities;

    private static List<Color> registered;
    private static final Color[] chooseFrom = {
            Color.RED, Color.BLUE, Color.TAN, Color.YELLOW, Color.GREEN, Color.GRAY
    };

    private Civilization(Random r) {
        if (registered == null) {
            registered = new ArrayList<>();
        }

        cities = new ArrayList<>();

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

        cities = new ArrayList<>();
    }

    public static Civilization generateNewCivilization(int seed, Biome[][] world) {
        Random r = new Random(seed);
        Civilization c = new Civilization(r);

        int type = r. nextInt(2);

        switch (type) {
            case 0:
                // plains
                placeCapitol(c, r, Biome.PLAINS, world);
                break;
            case 1:
                // mountain
                placeCapitol(c, r, Biome.FIELD, world);
                break;
        }

        return c;
    }

    public static void clearRegistered() {
        registered = new ArrayList<>();
    }

    private static void placeCapitol(Civilization c, Random r, Biome on, Biome[][] world) {
        List<Vector2> locations = new ArrayList<>();

        for (int i = 3; i < world.length - 4; i++) {
            for (int j = 3; j < world.length - 4; j++) {
                if (world[i][j] == on && check(world, i, j)) {
                    locations.add(new Vector2(i, j));
                }
            }
        }

        Vector2 loc = locations.get(r.nextInt(locations.size()));

        world[(int)loc.x][(int)loc.y] = Biome.CITY;

        c.cities.add(loc);
    }

    private static boolean check(Biome[][] world, int a, int b) {
        for (int i = a - 3; i < a + 4; i++) {
            for (int j = b - 3; j < b + 4; j++) {
                if (world[i][j] == Biome.CITY) return false;
            }
        }

        return true;
    }

    public void render(Color[][] backColorBuffer, int y, int x) {
        for (Vector2 a:cities) {
            for (int i = -7; i < 8; i++) {
                for (int j =  -7; j < 8; j++) {
                    if (i * i + j * j / 1.5f < 25) {
                        //fill(backColorBuffer, (int) a.x + y + i, (int) a.y + x + j);
                    }
                }
            }

            flood(backColorBuffer, (int)a.x + y, (int)a.y + x);
        }
    }

    private void flood(Color[][] backColorBuffer, int y, int x) {
        if (y < 1 || x < 1 || y >= backColorBuffer.length-1 || y >= backColorBuffer[0].length - 31) return;

        if (backColorBuffer[y][x] == Color.BLACK || backColorBuffer[y][x] == null) return;

        if (backColorBuffer[y-1][x] != null && backColorBuffer[y+1][x] != null &&
                backColorBuffer[y][x-1] != null && backColorBuffer[y][x+1] != null) {
            backColorBuffer[y][x]= Color.BLACK;

            flood(backColorBuffer, y-1, x);
            flood(backColorBuffer, y+1, x);
            flood(backColorBuffer, y, x-1);
            flood(backColorBuffer, y, x+1);
        }

    }

    private void fill(Color[][] backColorBuffer, int y, int x) {
        if (y < 0 || x < 0 || y >= backColorBuffer.length || y >= backColorBuffer[0].length - 30) return;

        if (backColorBuffer[y][x] == null)
            backColorBuffer[y][x] = c;
        else
            backColorBuffer[y][x] = Color.ORANGE;
    }
}
