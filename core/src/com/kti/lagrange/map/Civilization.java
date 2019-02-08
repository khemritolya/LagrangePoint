package com.kti.lagrange.map;

import com.badlogic.gdx.graphics.Color;
import com.kti.lagrange.util.CC;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Civilization {

    public Color c;
    private boolean dead;
    private List<Integer> xown;
    private List<Integer> yown;
    private List<Boolean> cores;
    private int manpower;

    public Civilization(int x, int y, int seed, Biome[][] world, Civilization[][] civmap) {
        civmap[y][x] = this;
        dead = false;
        manpower = 0;
        c = CC.randomColor();

        xown = new ArrayList<>();
        yown = new ArrayList<>();
        cores = new ArrayList<>();

        add(x, y);
    }

    public static void spread(List<Civilization> civs, Biome[][] biomes, Civilization[][] civmap, float[][] popmap) {
        Collections.shuffle(civs);

        for (int k = 0; k < civs.size(); k++) {
            Civilization c = civs.get(k);
            if (c.dead) {
                civs.remove(c);
                k--;
            } else {
                c.manpower = 0;
                for (int i = 0; i < c.xown.size(); i++) {
                    if (c.cores.get(i)) {
                        spr(civmap, biomes, popmap, c, c.yown.get(i) + 1, c.xown.get(i));
                        spr(civmap, biomes, popmap, c, c.yown.get(i) - 1, c.xown.get(i));
                        spr(civmap, biomes, popmap, c, c.yown.get(i), c.xown.get(i) + 1);
                        spr(civmap, biomes, popmap, c, c.yown.get(i), c.xown.get(i) - 1);
                        c.manpower += (int)popmap[c.yown.get(i)][c.xown.get(i)];
                    }
                }
            }
        }

        for (int k = 0; k < civs.size(); k++) {
            Civilization c = civs.get(k);
            if (c.dead) {
                civs.remove(c);
                k--;
            } else {
                c.core();
            }
        }
    }

    private static void spr(Civilization[][] civmap, Biome[][] biomes, float[][] popmap, Civilization c, int i, int j) {
        if (j < 0 || i < 0 || j >= civmap[0].length || i >= civmap.length) return;

        if (civmap[i][j] == c) return;

        if (biomes[i][j] == Biome.SEA || biomes[i][j] == Biome.OASIS || biomes[i][j] == Biome.LAKE ||
                biomes[i][j] == Biome.ICE) return;

        Civilization k = civmap[i][j];

        if (c.str(j, i) >= k.str(j, i) - 5) {
            k.remove(j, i);
            k.checkDeath();

            civmap[i][j] = c;
            popmap[i][j] /= 1.1f;

            c.add(j, i);
        }
    }

    private void checkDeath() {
        if (this.xown.size() == 0) dead = true;
    }

    private void remove(int x, int y) {
        for (int i = 0; i < xown.size(); i++) {
            if (yown.get(i) == y && xown.get(i) == x) {
                yown.remove(i);
                xown.remove(i);
                cores.remove(i);
                return;
            }
        }
    }

    public int getManpower() {
        return manpower;
    }

    private void core() {
        for (int i = 0; i < cores.size(); i++) {
            cores.set(i, true);
        }
    }

    private int str(int x, int y) {
        if (manpower > 500) {
            return (int) Math.sqrt(manpower / 80) - 5 - dist(x, y) / 5;
        } else {
            return -500;
        }
    }

    private int dist(int x, int y) {
        return (xown.get(0) - x) * (xown.get(0) - x) + (yown.get(0) - y) * (yown.get(0) - y);
    }

    private void add(int x, int y) {
        xown.add(x);
        yown.add(y);
        cores.add(false);
    }
}
