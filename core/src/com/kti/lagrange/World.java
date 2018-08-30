package com.kti.lagrange;

import com.badlogic.gdx.graphics.Color;

public class World {
    private static final float SHORE_HEIGHT = 0.3f;
    private static final float MNTN_HEIGHT = 0.65f;

    private static final float x_s = 15f;
    private static final float y_s = 10f;
    private static OpenSimplexNoise osn;

    private String name;
    private int x, y;
    private int mbX, mbY;
    private int selX, selY;
    private Biome[][] biomes;

    public World(int seed) {
        NameFactory nm = new NameFactory("assets/planet-names.txt", 2, 6, seed);
        name = nm.generateNew();

        osn = new OpenSimplexNoise(seed);

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
        biomes = new Biome[100][300];

        for (int i = 0; i < biomes.length; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                if (eval(j / x_s, i / y_s) > SHORE_HEIGHT) {
                    if (eval((j-1) / x_s, i / y_s) > SHORE_HEIGHT &&
                            eval((j+1) / x_s, i / y_s) > SHORE_HEIGHT &&
                            eval(j / x_s, (i-1) / y_s) > SHORE_HEIGHT &&
                            eval(j / x_s, (i+1) / y_s) > SHORE_HEIGHT) {
                        if (eval(j / x_s, i / y_s) > MNTN_HEIGHT) {
                            if (eval((j-1) / x_s, i / y_s) > MNTN_HEIGHT &&
                                    eval((j+1) / x_s, i / y_s) > MNTN_HEIGHT &&
                                    eval(j / x_s, (i-1) / y_s) > MNTN_HEIGHT &&
                                    eval(j / x_s, (i+1) / y_s) > MNTN_HEIGHT) {
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
            for (int j = 0; j < (eval(i, 0)+1) * 2 + 1; j++) {
                biomes[j][i] = Biome.Ice;
            }

            for (int j = 0; j < (eval(i, biomes.length - 1) + 1) * 2 + 1; j++) {
                biomes[biomes.length - j - 1][i] = Biome.Ice;
            }
        }

        for (int i = 2 * biomes.length / 5; i < 3 * biomes.length / 5; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                if (biomes[i][j] == Biome.Grassland) {
                    biomes[i][j] = Biome.Desert;
                }
            }
        }

        for (int i = 0; i < biomes[0].length; i++) {
            if (osn.eval(2 * biomes.length / 5, i) > 0 && biomes[2 * biomes.length / 5][i] == Biome.Desert) {
                biomes[2 * biomes.length / 5][i] = Biome.Grassland;
            }

            if (osn.eval(3 * biomes.length / 5, i) > 0 && biomes[3 * biomes.length / 5 - 1][i] == Biome.Desert) {
                biomes[3 * biomes.length / 5 - 1][i] = Biome.Grassland;
            }
        }

        for (int i = 0; i < biomes.length; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                if (biomes[i][j] == Biome.Grassland && osn.eval(i, j) > 0.5) {
                    biomes[i][j] = Biome.Lake;
                }

                if (biomes[i][j] == Biome.Grassland && osn.eval(i, j) < -0.25) {
                    biomes[i][j] = Biome.Forest;
                }

                if (biomes[i][j] == Biome.HighMountain && osn.eval(i, j) > 0.5) {
                    biomes[i][j] = Biome.Volcano;
                }
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

    /*public void loadMiniMap(char[][] charBuffer, Color[][] fontColorBuffer) {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 28; j++) {
                put2(charBuffer, fontColorBuffer, i, j);
            }
        }
    }*/

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

    /*private void put2(char[][] charBuffer, Color[][] fontColorBuffer, int i, int j) {
        charBuffer[charBuffer.length - 16 + i][charBuffer[0].length - 29 + j] =
                biomes[i * biomes.length / 15][j * biomes[0].length / 28].sigchar;
        fontColorBuffer[charBuffer.length - 16 + i][charBuffer[0].length - 29 + j] =
                biomes[i * biomes.length / 15][j * biomes[0].length / 28].sigcolor;
    }*/

    public void dx(int dx) {
        if (mbX >= 2) {
            mbX = 0;

            x += dx;

            if (x > 1 || selX < (Window.w.getCanvas().getBufferX() - 30) / 2) x = 1;
            if (x < -biomes[0].length + Window.w.getCanvas().getBufferX() - 30 ||
                    selX > biomes[0].length - (Window.w.getCanvas().getBufferX() - 30) / 2)
                x = -biomes[0].length + Window.w.getCanvas().getBufferX() - 30;

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

            y += dy;

            if (y > 1 || selY < Window.w.getCanvas().getBufferY() / 2) y = 1;
            if (y < -biomes.length + Window.w.getCanvas().getBufferY() - 1 ||
                    selY > biomes.length - Window.w.getCanvas().getBufferY() / 2)
                y = -biomes.length + Window.w.getCanvas().getBufferY() - 1;

            selY -= dy;

            if (selY < 0) selY = 0;
            if (selY >= biomes.length) selY = biomes.length - 1;
        } else {
            mbY++;
        }
    }

    private double eval(double a, double b) {
        return osn.eval(a, b);
    }

    public String getSelectedInfo() {
        return selX / 10.0f + ", " + selY / 10.0f + ": " + biomes[selY][selX].name;
    }

    public String getName() {
        return name;
    }
}
