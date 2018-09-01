package com.kti.lagrange;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    private float[][] heightmap;

    private int mapmode;

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

        mapmode = 0;
    }


    private void generate() {
        biomes = new Biome[100][200];
        heightmap = new float[100][200];

        for (int i = 0; i < biomes.length; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                if (eval(j / x_s, i / y_s) > SHORE_HEIGHT) {
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
                    biomes[i][j] = Biome.Sea;
                }
            }
        }

        for (int i = 0; i < biomes.length; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                heightmap[i][j] = (float) eval(j / x_s, i / y_s)+1;
            }
        }

        float m = max(heightmap);

        for (int i = 0; i < biomes.length; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                heightmap[i][j] = heightmap[i][j] / m;
            }
        }

        for (int i = 0; i < biomes.length; i++) {
            flood(i, 0);
            flood(i, biomes[0].length - 1);
        }

        for (int i = 0; i < biomes[0].length; i++) {
            for (int j = 0; j < (eval(i / 3.0f, 0)+1) * 2 + 1; j++) {
                biomes[j][i] = Biome.Ice;
            }

            for (int j = 0; j < (eval(i / 3.0f, biomes.length - 1) + 1) * 2 + 1; j++) {
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
                if (biomes[i][j] == Biome.Grassland && osn.eval(i, j) > 0.6) {
                    biomes[i][j] = Biome.Lake;
                }

                if (biomes[i][j] == Biome.Grassland && osn.eval(i, j) < -0.25) {
                    biomes[i][j] = Biome.Forest;
                }

                if (biomes[i][j] == Biome.Desert && osn.eval(i, j) > 0.75) {
                    biomes[i][j] = Biome.Oasis;
                }

                if (biomes[i][j] == Biome.HighMountain && osn.eval(i, j) > 0.5) {
                    biomes[i][j] = Biome.Volcano;
                }
            }
        }

        for (int i = 0; i < biomes.length; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                if (biomes[i][j] == Biome.Lake || biomes[i][j] == Biome.Oasis) {
                    if (biomes[i-1][j] == Biome.Sea || biomes[i+1][j] == Biome.Sea ||
                            biomes[i][j-1] == Biome.Sea || biomes[i][j+1] == Biome.Sea)
                        biomes[i][j] = Biome.Sea;
                }
            }
        }

        /*for (int i = 0; i < biomes.length; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                if (biomes[i][j] == Biome.Lake) {
                    lake(i, j);
                    biomes[i][j] = Biome.Lake;
                }
            }
        }*/
    }

    // working on it https://blog.habrador.com/2013/02/how-to-generate-random-terrain.html

    private void generate2(int seed) {
        biomes = new Biome[100][200];
        heightmap = new float[100][200];

        float[][] initHeightMap = new float[100][200];
        Random r = new Random(seed);

        raiseRandom(r, initHeightMap, 10, 20, 90, 180);

        for (int i = 0; i < biomes.length; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                biomes[i][j] = Biome.Sea;
            }
        }

        /*for (int i = 0; i < biomes.length; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                initHeightMap[i][j] += 12*osn.eval(j / x_s, i / y_s);
            }
        }*/

        float m = max(initHeightMap);

        for (int i = 0; i < biomes.length; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                heightmap[i][j] = initHeightMap[i][j] / m;
            }
        }
    }

    private void raiseRandom(Random r, float[][] initHeightMap, int i1, int j1, int i2, int j2) {
        if (Math.abs(i1 - i2) < 2 || Math.abs(j1 - j2) < 2)
            return;

        float m = (r.nextFloat() * 5 - 2) + Math.max(0, Math.min(10, 100 / dst((i1+i2)/2, (j1+j2)/2)));

        //System.out.println(100 / dst((i1+i2)/2, (j1+j2)/2));

        for (int i = i1; i < i2; i++) {
            for (int j = j1; j < j2; j++) {
                initHeightMap[i][j] += m;
            }
        }

        raiseRandom(r, initHeightMap, i1+f(r), j1+f(r), (i1+i2)/2, (j1+j2)/2);
        raiseRandom(r, initHeightMap, i1+f(r), (j1+j2)/2, (i1+i2)/2, j2+f(r));
        raiseRandom(r, initHeightMap, (i1+i2)/2, j1+f(r), i2+f(r), (j1+j2)/2);
        raiseRandom(r, initHeightMap, (i1+i2)/2, (j1+j2)/2, i2+f(r), j2+f(r));
    }

    private float dst(int i, int j) {
        return (float) Math.sqrt((i - biomes.length / 2) * (i - biomes.length / 2) +
                (j - biomes[0].length / 2) * (j - biomes[0].length / 2));
    }

    private int f(Random r) {
        return r.nextInt(3) - 1;
    }

    /*private void lake(int i, int j) {
        if (i < 0 || j < 0 || i >= biomes.length || j >= biomes[0].length) return;
        if (biomes[i][j] == Biome.Sea || biomes[i][j] == Biome.Oasis ||
                biomes[i][j] == Biome.Ice || biomes[i][j] == Biome.RIVER_EAST_WEST ||
                biomes[i][j] == Biome.RIVER_NORTH_SOUTH) return;

        double[] vals = new double[4];
        vals[0] = osn.eval((j-1) / x_s, i / y_s);
        vals[1] = osn.eval((j+1) / x_s, i / y_s);
        vals[2] = osn.eval(j / x_s, (i-1) / y_s);
        vals[3] = osn.eval(j / x_s, (i+1) / y_s);

        Arrays.sort(vals);

        if (osn.eval((j-1) / x_s, i / y_s) == vals[0]) {
            biomes[i][j] = Biome.RIVER_EAST_WEST;
            lake(i, j-1);
            return;
        }

        if (osn.eval((j+1) / x_s, i / y_s) == vals[0]) {
            biomes[i][j] = Biome.RIVER_EAST_WEST;
            lake(i, j+1);
            return;
        }

        if (osn.eval(j / x_s, (i-1) / y_s) == vals[0]) {
            biomes[i][j] = Biome.RIVER_NORTH_SOUTH;
            lake(i-1, j);
            return;
        }

        if (osn.eval(j / x_s, (i+1) / y_s) == vals[0]) {
            biomes[i][j] = Biome.RIVER_NORTH_SOUTH;
            lake(i+1, j);
            return;
        }
    }*/

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
            if (mapmode == 0) {
                charBuffer[i+y][j+x] = biomes[i][j].sigchar;
                fontColorBuffer[i+y][j+x] = biomes[i][j].sigcolor;
            } else {
                charBuffer[i+y][j+x] = 'X';
                fontColorBuffer[i+y][j+x] = new Color(heightmap[i][j], 0,0,1);
            }
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

    private float max(float[][] heightmap) {
        float max = Integer.MIN_VALUE;

        for (int i = 0; i < biomes.length; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                max = Math.max(max, heightmap[i][j]);
            }
        }

        return max;
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

    public void setMapmode(int m) {
        mapmode = m;
    }

    public int getMapmode() {
        return mapmode;
    }
}
