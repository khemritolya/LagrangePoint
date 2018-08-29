package com.kti.lagrange;

import com.badlogic.gdx.graphics.Color;

public class World {
    private static final float SHORE_HEIGHT = 0.3f;
    private static final float MNTN_HEIGHT = 0.65f;

    private static final float x_s = 9f;
    private static final float y_s = 6f;
    private static OpenSimplexNoise osn;

    private String name;
    private int x, y;
    private int mbX, mbY;
    private int selX, selY;
    private Biome[][] biomes;

    public World() {
        NameFactory nm = new NameFactory("assets/constellations.txt", 2, 6);
        name = nm.generateNew();

        osn = new OpenSimplexNoise();

        generate();

        x = 1;
        y = 1;

        selX = (Window.w.getCanvas().getBufferX() - 30) / 2;
        selY = Window.w.getCanvas().getBufferY() / 2;

        if (selX < 0) selX = 0;
        if (selX >= biomes[0].length) selX = biomes[0].length - 1;

        if (selY < 0) selY = 0;
        if (selY >= biomes.length) selY = biomes.length - 1;

        mbX = 0;
        mbY = 0;
    }

    private void generate() {
        biomes = new Biome[58][180];

        for (int i = 0; i < biomes.length; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                if (osn.eval(j / x_s, i / y_s) > SHORE_HEIGHT) {
                    if (osn.eval((j-1) / x_s, i / y_s) > SHORE_HEIGHT &&
                            osn.eval((j+1) / x_s, i / y_s) > SHORE_HEIGHT &&
                            osn.eval(j / x_s, (i-1) / y_s) > SHORE_HEIGHT &&
                            osn.eval(j / x_s, (i+1) / y_s) > SHORE_HEIGHT) {
                        if (osn.eval(j / x_s, i / y_s) > MNTN_HEIGHT) {
                            if (osn.eval((j-1) / x_s, i / y_s) > MNTN_HEIGHT &&
                                    osn.eval((j+1) / x_s, i / y_s) > MNTN_HEIGHT &&
                                    osn.eval(j / x_s, (i-1) / y_s) > MNTN_HEIGHT &&
                                    osn.eval(j / x_s, (i+1) / y_s) > MNTN_HEIGHT) {
                                biomes[i][j] = Biome.HighMountain;
                            } else {
                                biomes[i][j] = Biome.Mountain;
                            }
                        } else {
                            biomes[i][j] = Biome.Grassland;
                        }
                    } else {
                        biomes[i][j] = Biome.Shore;
                    }
                } else {
                    biomes[i][j] = Biome.Sea;
                }
            }
        }

        for (int i = 0; i < biomes.length; i++) {
            flood(i, 0);
            flood(i, biomes[0].length - 1);
        }

        for (int i = 0; i < biomes[0].length; i++) {
            flood(0, i);
            flood(biomes.length - 1, i);
        }

        for (int i = 0; i < biomes[0].length; i++) {
            for (int j = 0; j < (osn.eval(i, 0)+1) * 2 + 1; j++) {
                biomes[j][i] = Biome.Ice;
            }

            for (int j = 0; j < (osn.eval(i, biomes.length - 1) + 1) * 2 + 1; j++) {
                biomes[biomes.length - j - 1][i] = Biome.Ice;
            }
        }
    }

    private void flood(int i, int j) {
        if(i < 0 || j < 0 || i >= biomes.length || j >= biomes[0].length) return;
        if (biomes[i][j] == Biome.Sea) return;

        biomes[i][j] = Biome.Sea;

        flood(i + 1, j);
        flood(i - 1, j);
        flood(i, j + 1);
        flood(i, j - 1);
    }

    public void loadWorld(char[][] charBuffer, Color[][] fontColorBuffer) {
        for (int i = 0; i < biomes.length; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                put(charBuffer, fontColorBuffer, i, j);
            }
        }
    }

    private void put(char[][] charBuffer, Color[][] fontColorBuffer, int i, int j) {
        if (i+y < 0 || j+x < 0 || i+y >= charBuffer.length || j+x >= charBuffer[0].length - 30) return;

        if (i == selY && j == selX) {
            charBuffer[i+y][j+x] = 'X';
            fontColorBuffer[i+y][j+x] = Color.GOLDENROD;
        } else {
            charBuffer[i+y][j+x] = biomes[i][j].sigchar;
            fontColorBuffer[i+y][j+x] = biomes[i][j].sigcolor;
        }
    }

    public void dx(int dx) {
        if (mbX >= 2) {
            mbX = 0;

            if (x + dx <= 1 && x + dx >= -(biomes[0].length - Window.w.getCanvas().getBufferX() + 30))
                x += dx;

            selX -= dx;

            if (selX < 0) selX = 0;
            if (selX >= biomes[0].length) selX = biomes[0].length - 1;
        } else {
            mbX++;
        }
    }

    public void dy(int dy) {
        if (mbY >= 2) {
            mbY = 0;

            if (y+dy <= 1 && y+dy >= -(biomes.length - Window.w.getCanvas().getBufferY() + 1))
                y += dy;

            selY -= dy;

            if (selY < 0) selY = 0;
            if (selY >= biomes.length) selY = biomes.length - 1;
        } else {
            mbY++;
        }
    }

    public String getSelectedInfo() {
        return selX / 10.0f + ", " + selY / 10.0f + ": " + biomes[selY][selX].name;
    }

    public String getName() {
        return name;
    }
}
