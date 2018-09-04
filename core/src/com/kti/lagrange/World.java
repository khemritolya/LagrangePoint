package com.kti.lagrange;

import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.Arrays;
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

        generate(seed);

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

    /*
    So that posterity may see my humble beggingins!

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
                            biomes[i][j] = Biome.HIGH_MOUNTAIN;
                        } else {
                            biomes[i][j] = Biome.MOUNTAIN;
                        }
                    } else {
                        biomes[i][j] = Biome.PLAINS;
                    }
                } else {
                    biomes[i][j] = Biome.SEA;
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
            isOcean(i, 0);
            isOcean(i, biomes[0].length - 1);
        }

        for (int i = 0; i < biomes[0].length; i++) {
            for (int j = 0; j < (eval(i / 3.0f, 0)+1) * 2 + 1; j++) {
                biomes[j][i] = Biome.ICE;
            }

            for (int j = 0; j < (eval(i / 3.0f, biomes.length - 1) + 1) * 2 + 1; j++) {
                biomes[biomes.length - j - 1][i] = Biome.ICE;
            }
        }

        for (int i = 2 * biomes.length / 5; i < 3 * biomes.length / 5; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                if (biomes[i][j] == Biome.PLAINS) {
                    biomes[i][j] = Biome.DESERT;
                }
            }
        }

        for (int i = 0; i < biomes[0].length; i++) {
            if (osn.eval(2 * biomes.length / 5, i) > 0 && biomes[2 * biomes.length / 5][i] == Biome.DESERT) {
                biomes[2 * biomes.length / 5][i] = Biome.PLAINS;
            }

            if (osn.eval(3 * biomes.length / 5, i) > 0 && biomes[3 * biomes.length / 5 - 1][i] == Biome.DESERT) {
                biomes[3 * biomes.length / 5 - 1][i] = Biome.PLAINS;
            }
        }

        for (int i = 0; i < biomes.length; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                if (biomes[i][j] == Biome.PLAINS && osn.eval(i, j) > 0.6) {
                    biomes[i][j] = Biome.LAKE;
                }

                if (biomes[i][j] == Biome.PLAINS && osn.eval(i, j) < -0.25) {
                    biomes[i][j] = Biome.FOREST;
                }

                if (biomes[i][j] == Biome.DESERT && osn.eval(i, j) > 0.75) {
                    biomes[i][j] = Biome.OASIS;
                }

                if (biomes[i][j] == Biome.HIGH_MOUNTAIN && osn.eval(i, j) > 0.5) {
                    biomes[i][j] = Biome.VOLCANO;
                }
            }
        }

        for (int i = 0; i < biomes.length; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                if (biomes[i][j] == Biome.LAKE || biomes[i][j] == Biome.OASIS) {
                    if (biomes[i-1][j] == Biome.SEA || biomes[i+1][j] == Biome.SEA ||
                            biomes[i][j-1] == Biome.SEA || biomes[i][j+1] == Biome.SEA)
                        biomes[i][j] = Biome.SEA;
                }
            }
        }

        for (int i = 0; i < biomes.length; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                if (biomes[i][j] == Biome.LAKE) {
                    lake(i, j);
                    biomes[i][j] = Biome.LAKE;
                }
            }
        }
    }*/

    private void generate(int seed) {
        int w = 250;
        int h = 100;

        biomes = new Biome[h][w];
        heightmap = new float[h][w];
        float[][] initHeightMap = new float[h][w];

        Random r = new Random(seed);

        int divisions = 5;

        for (int i = 0; i < divisions; i++) {
            for (int j = 0; j < divisions; j++) {
                int top = i * (initHeightMap.length - 1) / divisions;
                int bottom = (i+1) * (initHeightMap.length - 1) / divisions;
                int left = j * (initHeightMap[0].length - 1) / divisions;
                int right = (j+1) * (initHeightMap[0].length - 1) / divisions;

                int centerX = (left + right) / 2;
                int centerY = (top + bottom) / 2;

                initHeightMap[centerY][centerX] = 20 * r.nextInt(2);

                if (i > 0 && j > 0 && i < divisions - 1 && j < divisions - 1) {
                    initHeightMap[top][left] = 20 * r.nextInt(2);
                    initHeightMap[bottom][left] = 20 * r.nextInt(2);
                    initHeightMap[top][right] = 20 * r.nextInt(2);
                    initHeightMap[bottom][right] = 20 * r.nextInt(2);

                    initHeightMap[top][centerX] = 20 * r.nextInt(2);
                    initHeightMap[bottom][centerX] = 20 * r.nextInt(2);
                    initHeightMap[centerY][left] = 20 * r.nextInt(2);
                    initHeightMap[centerY][right] = 20 * r.nextInt(2);
                }
            }
        }

        for (int i = 0; i < divisions; i++) {
            for (int j = 0; j < divisions; j++) {
                int top = i * (initHeightMap.length - 1) / divisions;
                int bottom = (i+1) * (initHeightMap.length - 1) / divisions;
                int left = j * (initHeightMap[0].length - 1) / divisions;
                int right = (j+1) * (initHeightMap[0].length - 1) / divisions;

                int centerX = (left + right) / 2;
                int centerY = (top + bottom) / 2;

                step(initHeightMap, top, left, centerY, centerX);
                step(initHeightMap, centerY, left, bottom, centerX);
                step(initHeightMap, top, centerX, centerY, right);
                step(initHeightMap, centerY, centerX, bottom, right);
            }
        }

        for (int i = 0; i < biomes.length; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                initHeightMap[i][j] += 7*osn.eval(j / x_s, i / y_s);
            }
        }

        float m1 = min(initHeightMap);

        for (int i = 0; i < biomes.length; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                initHeightMap[i][j] -= m1;
            }
        }

        float m2 = max(initHeightMap);

        for (int i = 0; i < biomes.length; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                heightmap[i][j] = initHeightMap[i][j] / m2;
            }
        }

        for (int i = 0; i < biomes.length; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                if (heightmap[i][j] > 0.45) {
                    if (heightmap[i][j] < 0.75) {
                        biomes[i][j] = Biome.PLAINS;
                    } else {
                        biomes[i][j] = Biome.MOUNTAIN;
                    }
                } else {
                    biomes[i][j] = Biome.SEA;
                }
            }
        }

        for (int i = 0; i < biomes.length; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                if (biomes[i][j] == Biome.SEA) {
                    if (!isOcean(i, j)) fillLakes(i, j);
                }
            }
        }

        for (int i = 0; i < biomes[0].length; i++) {
            for (int j = 0; j < (eval(i / 4.0f, 0)+1) * 3 + 2; j++) {
                biomes[j][i] = Biome.ICE;
            }

            for (int j = 0; j < (eval(i / 4.0f, biomes.length - 1) + 1) * 3 + 2; j++) {
                biomes[biomes.length - j - 1][i] = Biome.ICE;
            }
        }

        for (int i = 1; i < biomes.length-1; i++) {
            for (int j = 1; j < biomes[0].length-1; j++) {
                if (biomes[i][j] == Biome.MOUNTAIN &&
                        (biomes[i-1][j] == Biome.MOUNTAIN || biomes[i-1][j] == Biome.HIGH_MOUNTAIN) &&
                        (biomes[i+1][j] == Biome.MOUNTAIN || biomes[i+1][j] == Biome.HIGH_MOUNTAIN) &&
                        (biomes[i][j-1] == Biome.MOUNTAIN || biomes[i][j-1] == Biome.HIGH_MOUNTAIN) &&
                        (biomes[i][j+1] == Biome.MOUNTAIN || biomes[i][j+1] == Biome.HIGH_MOUNTAIN)) {
                    biomes[i][j] = Biome.HIGH_MOUNTAIN;
                }
            }
        }

        /*for (int i = 0; i < biomes.length; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                if ((biomes[i][j] == Biome.MOUNTAIN || biomes[i][j] == Biome.HIGH_MOUNTAIN) &&
                        osn.eval(i, j) > 0.6) {
                    createRiver(i, j);
                    biomes[i][j] = Biome.MOUNTAIN;
                }
            }
        }*/

        for (int i = 5 * biomes.length / 11; i < 6 * biomes.length / 11; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                if (biomes[i][j] == Biome.PLAINS) {
                    biomes[i][j] = Biome.DESERT;
                }
            }
        }

        for (int i = 0; i < biomes[0].length; i++) {
            if (osn.eval(5 * biomes.length / 11, i / 3.0f) > 0 &&
                    biomes[5 * biomes.length / 11][i] == Biome.DESERT) {
                biomes[5 * biomes.length / 11][i] = Biome.PLAINS;
            }

            if (osn.eval(6 * biomes.length / 11, i / 3.0f) > 0 &&
                    biomes[6 * biomes.length / 11 - 1][i] == Biome.DESERT) {
                biomes[6 * biomes.length / 11 - 1][i] = Biome.PLAINS;
            }
        }

        for (int i = 0; i < biomes.length / 11; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                if (biomes[i][j] == Biome.PLAINS) {
                    biomes[i][j] = Biome.NORTH_TUNDRA;
                }
            }
        }

        for (int i = 10 * biomes.length / 11; i < biomes.length; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                if (biomes[i][j] == Biome.PLAINS) {
                    biomes[i][j] = Biome.NORTH_TUNDRA;
                }
            }
        }

        for (int i = 0; i < biomes[0].length; i++) {
            if (osn.eval(0, 5+i / 3.0f) > 0 &&
                    biomes[biomes.length / 11 - 1][i] == Biome.NORTH_TUNDRA) {
                biomes[biomes.length / 11 - 1][i] = Biome.PLAINS;
            }

            if (osn.eval(biomes.length, 5+i / 3.0f) > 0 &&
                    biomes[10 * biomes.length / 11][i] == Biome.NORTH_TUNDRA) {
                biomes[10 * biomes.length / 11][i] = Biome.PLAINS;
            }
        }

        for (int i = 1; i < biomes.length - 1; i++) {
            for (int j = 1; j < biomes[0].length - 1; j++) {
                if (biomes[i][j] == Biome.PLAINS && osn.eval(i / 5.0f, j / 5.0f) > 0.5) {
                    biomes[i][j] = Biome.FIELD;
                }

                if (biomes[i][j] == Biome.PLAINS && osn.eval(i, j) < -0.25) {
                    biomes[i][j] = Biome.FOREST;
                }

                if (biomes[i][j] == Biome.DESERT && osn.eval(i, j) > 0.75 &&
                        biomes[i-1][j] != Biome.SEA && biomes[i+1][j] != Biome.SEA &&
                        biomes[i][j-1] != Biome.SEA && biomes[i][j+1] != Biome.SEA) {
                    biomes[i][j] = Biome.OASIS;
                }

                if (biomes[i][j] == Biome.PLAINS && osn.eval(i, j) > 0.75 &&
                        biomes[i-1][j] != Biome.SEA && biomes[i+1][j] != Biome.SEA &&
                        biomes[i][j-1] != Biome.SEA && biomes[i][j+1] != Biome.SEA) {
                    biomes[i][j] = Biome.LAKE;
                }

                if (biomes[i][j] == Biome.HIGH_MOUNTAIN && osn.eval(i, j) > 0.5) {
                    biomes[i][j] = Biome.VOLCANO;
                }
            }
        }
    }

    private void step(float[][] initHeightMap, int a, int b, int c, int d) {
        if (a+1 >= c && b+1 >= d) return;

        int centerX = (a+c) / 2;
        int centerY = (b+d) / 2;

        initHeightMap[a][centerY] = (initHeightMap[a][b] + initHeightMap[a][d]) / 2 + eval(a, centerY);
        initHeightMap[c][centerY] = (initHeightMap[c][b] + initHeightMap[c][d]) / 2 + eval(c, centerY);
        initHeightMap[centerX][b] = (initHeightMap[a][b] + initHeightMap[c][b]) / 2 + eval(centerX, b);
        initHeightMap[centerX][d] = (initHeightMap[a][d] + initHeightMap[c][d]) / 2 + eval(centerX, d);

        initHeightMap[centerX][centerY] = (initHeightMap[a][centerY] + initHeightMap[c][centerY] +
                initHeightMap[centerX][b] + initHeightMap[centerX][d]) / 4 + eval(centerX, centerY);

        step(initHeightMap, a, b, centerX, centerY);
        step(initHeightMap, a, centerY, centerX, d);
        step(initHeightMap, centerX, b, c, centerY);
        step(initHeightMap, centerX, centerY, c, d);
    }

    private boolean isOcean(int i, int j) {
        boolean res = isOceanH(i, j);

        for (int a = 0; a < biomes.length; a++) {
            for (int b = 0; b < biomes[0].length; b++) {
                if (biomes[a][b] == Biome.TMP) biomes[a][b] = Biome.SEA;
            }
        }

        return res;
    }

    private boolean isOceanH(int i, int j) {
        if(i <= 0 || j <= 0 || i >= biomes.length-1 || j >= biomes[0].length-1) return true;

        if(biomes[i][j] != Biome.SEA) return false;

        biomes[i][j] = Biome.TMP;

        if(isOceanH(i + 1, j)) return true;
        if(isOceanH(i - 1, j)) return true;
        if(isOceanH(i, j + 1)) return true;
        if(isOceanH(i, j - 1)) return true;

        return false;
    }

    private void fillLakes(int i, int j) {
        if(i < 0 || j < 0 || i >= biomes.length || j >= biomes[0].length) return;
        if(biomes[i][j] != Biome.SEA) return;

        biomes[i][j] = Biome.LAKE;

        fillLakes(i-1, j);
        fillLakes(i+1, j);
        fillLakes(i, j-1);
        fillLakes(i, j+1);
    }

    private void createRiver(int i, int j) {
        if (i < 1 || j < 1 || i >= biomes.length-1 || j >= biomes[0].length-1) return;
        if (biomes[i][j] == Biome.SEA || biomes[i][j] == Biome.OASIS || biomes[i][j] == Biome.LAKE ||
                biomes[i][j] == Biome.ICE || biomes[i][j] == Biome.RIVER_EAST_WEST ||
                biomes[i][j] == Biome.RIVER_NORTH_SOUTH) return;

        double[] vals = new double[4];
        vals[0] = heightmap[i-1][j];
        vals[1] = heightmap[i+1][j];
        vals[2] = heightmap[i][j-1];
        vals[3] = heightmap[i][j+1];

        Arrays.sort(vals);

        if (vals[0] > heightmap[i][j]) {
            biomes[i][j] = Biome.LAKE;
            return;
        }

        if (heightmap[i-1][j] == vals[0]) {
            biomes[i][j] = Biome.RIVER_NORTH_SOUTH;
            createRiver(i-1, j);
            return;
        }

        if (heightmap[i+1][j] == vals[0]) {
            biomes[i][j] = Biome.RIVER_NORTH_SOUTH;
            createRiver(i+1, j);
            return;
        }

        if (heightmap[i][j-1] == vals[0]) {
            biomes[i][j] = Biome.RIVER_EAST_WEST;
            createRiver(i, j-1);
            return;
        }

        if (heightmap[i][j+1] == vals[0]) {
            biomes[i][j] = Biome.RIVER_EAST_WEST;
            createRiver(i, j+1);
            return;
        }
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

    private float min(float[][] heightmap) {
        float min = Integer.MAX_VALUE;

        for (int i = 0; i < biomes.length; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                min = Math.min(min, heightmap[i][j]);
            }
        }

        return min;
    }

    private float eval(double a, double b) {
        return (float) osn.eval(a / 4, b / 4);
    }

    public List<String> getAsStringList() {
        List<String> out = new ArrayList<>();

        out.add(biomes.length + "x" + biomes[0].length);

        for (int i = 0; i < biomes.length; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                out.add(Biome.getIDByBiome(biomes[i][j]) + ":" + heightmap[i][j]);
            }
        }

        return out;
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
