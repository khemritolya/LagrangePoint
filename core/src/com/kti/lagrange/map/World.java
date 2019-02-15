package com.kti.lagrange.map;

import com.badlogic.gdx.graphics.Color;
import com.kti.lagrange.util.CC;
import com.kti.lagrange.util.NameFactory;
import com.kti.lagrange.util.OpenSimplexNoise;
import com.kti.lagrange.core.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class World {
    private static final float x_s = 15f;
    private static final float y_s = 10f;
    private static OpenSimplexNoise osn;

    private int x, y;
    private int mbX, mbY;
    private int selX, selY;

    private String name;
    private Biome[][] biomes;
    private float[][] heightmap;
    private float[][] popmap;
    private Civilization[][] civmap;

    List<Civilization> civs;
    private long time;

    private int mapmode;
    private boolean showCiv = true;

    public World(String config) {
        showCiv = true;
        String[] res = config.split("#");
        int seed = res[0].hashCode();
        int w = Integer.parseInt(res[1].split("x")[0]);
        int h = Integer.parseInt(res[1].split("x")[1]);

        NameFactory nm = new NameFactory("assets/planet-names.txt", 2, 6, seed);
        name = nm.generateNew();

        osn = new OpenSimplexNoise(seed);

        generate(seed, w, h);

        time = 0;

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

    public World(String name, Biome[][] biomes, float[][] heightmap, float[][] popmap, long time) {
        showCiv = true;
        this.name  = name;

        this.biomes = biomes;
        this.heightmap = heightmap;
        this.popmap = popmap;

        this.time = time;

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

    private void generate(int seed, int w, int h) {
        biomes = new Biome[h][w];
        heightmap = new float[h][w];
        float[][] initHeightMap = new float[h][w];

        Random r = new Random(seed);

        for (int i = 0; i < 1000; i++) {
            raise(initHeightMap, r);
        }

        for (int i = 0; i < biomes.length; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                initHeightMap[i][j] += 0.75*osn.eval(j / x_s, i / y_s);
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

        biomes = new Biome[h][w];

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

        int ddvis = r.nextInt(10) * 2 + 3;
        int ddvis_min = (ddvis / 2) * biomes.length / ddvis;
        int ddvis_max = (ddvis / 2 + 1) * biomes.length / ddvis;

        for (int i = ddvis_min; i < ddvis_max; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                if (biomes[i][j] == Biome.PLAINS) {
                    biomes[i][j] = Biome.DESERT;
                }
            }
        }

        for (int i = 0; i < biomes[0].length; i++) {
            if (osn.eval(ddvis_min, i / 3.0f) > 0 &&
                    biomes[ddvis_min][i] == Biome.DESERT) {
                biomes[ddvis_min][i] = Biome.PLAINS;
            }

            if (osn.eval(ddvis_max, i / 3.0f) > 0 &&
                    biomes[ddvis_max - 1][i] == Biome.DESERT) {
                biomes[ddvis_max - 1][i] = Biome.PLAINS;
            }
        }

        for (int i = 0; i < biomes.length / 11; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                if (biomes[i][j] == Biome.PLAINS) {
                    biomes[i][j] = Biome.NORTH_TUNDRA;
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
            for (int j = 0; j < (eval(i / 4.0f, 0) + 1) * 3 + 2; j++) {
                biomes[j][i] = Biome.ICE;
            }

            for (int j = 0; j < (eval(i / 4.0f, biomes.length - 1) + 1) * 3 + 2; j++) {
                biomes[biomes.length - j - 1][i] = Biome.ICE;
            }
        }

        for (int i = 1; i < biomes.length - 1; i++) {
            for (int j = 1; j < biomes[0].length - 1; j++) {
                if (biomes[i][j] == Biome.MOUNTAIN &&
                        (biomes[i - 1][j] == Biome.MOUNTAIN || biomes[i - 1][j] == Biome.HIGH_MOUNTAIN) &&
                        (biomes[i + 1][j] == Biome.MOUNTAIN || biomes[i + 1][j] == Biome.HIGH_MOUNTAIN) &&
                        (biomes[i][j - 1] == Biome.MOUNTAIN || biomes[i][j - 1] == Biome.HIGH_MOUNTAIN) &&
                        (biomes[i][j + 1] == Biome.MOUNTAIN || biomes[i][j + 1] == Biome.HIGH_MOUNTAIN)) {
                    biomes[i][j] = Biome.HIGH_MOUNTAIN;
                }
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
            if (osn.eval(0, 5 + i / 3.0f) > 0 &&
                    biomes[biomes.length / 11 - 1][i] == Biome.NORTH_TUNDRA) {
                biomes[biomes.length / 11 - 1][i] = Biome.PLAINS;
            }

            if (osn.eval(biomes.length, 5 + i / 3.0f) > 0 &&
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
                        biomes[i - 1][j] != Biome.SEA && biomes[i + 1][j] != Biome.SEA &&
                        biomes[i][j - 1] != Biome.SEA && biomes[i][j + 1] != Biome.SEA) {
                    biomes[i][j] = Biome.OASIS;
                }

                if (biomes[i][j] == Biome.PLAINS && osn.eval(i, j) > 0.75 &&
                        biomes[i - 1][j] != Biome.SEA && biomes[i + 1][j] != Biome.SEA &&
                        biomes[i][j - 1] != Biome.SEA && biomes[i][j + 1] != Biome.SEA) {
                    biomes[i][j] = Biome.LAKE;
                }

                if (biomes[i][j] == Biome.HIGH_MOUNTAIN && osn.eval(i, j) > 0.5) {
                    biomes[i][j] = Biome.VOLCANO;
                }
            }
        }

        popmap = new float[h][w];

        for (int i = 0; i < biomes.length; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                popmap[i][j] = (int)(biomes[i][j].carrycap * (osn.eval(i, j)+1));
            }
        }

        civmap = new Civilization[h][w];
        civs = new ArrayList<>();

        for (int i = 0; i < biomes.length; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                if (biomes[i][j] != Biome.SEA && biomes[i][j] != Biome.ICE) {
                    civs.add(new Civilization(j, i, r.nextInt(), biomes, civmap));
                }
            }
        }

        for (int i = 0; i < 100; i++) {
            Civilization.spread(civs, biomes, civmap, popmap);
        }
    }

    public void update(float dt) {
        time += dt * 1000;

        if (Window.w.getFrame() % 10 == 0) Civilization.spread(civs, biomes, civmap, popmap);

        if (time % 10 == 0) {
            for (int i = 0; i < biomes.length; i++) {
                for (int j = 0; j < biomes[0].length; j++) {
                    if (popmap[i][j] != 0 && grow(i, j) < popmap[i][j]) {
                        popmap[i][j] += grow(i, j);
                    }

                    if (popmap[i][j] < 0) {
                        popmap[i][j] = 0;
                    }
                }
            }
        }
    }

    private double grow(int i, int j) {
        double effpopcap = biomes[i][j].carrycap * (osn.eval(i, j)+1) * (1 + 0.1*sin(time / 2000f));

        return popmap[i][j] * (1 - popmap[i][j] / effpopcap) / 1000f;
    }

    public void abduct() {
        popmap[selY][selX] /= 2;
    }

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

    private void raise(float[][] initHeightMap, Random r    ) {
        int w = initHeightMap[0].length;
        int h = initHeightMap.length;

        int y = r.nextInt(h-1) + 1;
        int x = r.nextInt(w-1) + 1;
        int rad = (w + h) / 20;
        int sl = r.nextInt(180);

        float[] vertx = new float[4];
        float[] verty = new float[4];

        vertx[0] = x;
        vertx[1] = x + rad * cos(sl);
        vertx[2] = x + rad * sqrt(2) * cos(sl + 45);
        vertx[3] = x - rad * cos(90 - sl);

        verty[0] = y;
        verty[1] = y + rad * sin(sl);
        verty[2] = y + rad * sqrt(2) * sin(sl + 45);
        verty[3] = y + rad * sin(90 - sl);

        for (int i = 0; i < initHeightMap.length; i++) {
            for (int j = 0; j < initHeightMap[0].length; j++) {
                if (in(j, i, vertx, verty) || in(j - w, i, vertx, verty) || in(j + w, i, vertx, verty)
                        || in(j, i+h, vertx, verty) || in(j, i-h, vertx, verty)
                        || in(j-w, i-h, vertx, verty) || in(j-w, i+h, vertx, verty)
                        || in(j+w, i-h, vertx, verty) || in(j+w, i+h, vertx, verty)) {
                    initHeightMap[i][j]++;
                }

            }
        }
    }

    private static boolean in(float x, float y, float[] polyX, float[] polyY) {
        int i, j = 4-1;
        boolean  oddNodes = false;

        for (i=0; i<4; i++) {
            if ((polyY[i]< y && polyY[j]>=y  || polyY[j]< y && polyY[i]>=y)
                    &&  (polyX[i]<=x || polyX[j]<=x)) {
                oddNodes ^= (polyX[i]+(y-polyY[i])/(polyY[j]-polyY[i])*(polyX[j]-polyX[i])<x);
            }
            j=i;
        }

        return oddNodes;
    }

    private static float sin(double theta) {
        return (float)Math.sin(theta/180*Math.PI);
    }

    private static float cos(double theta) {
        return (float)Math.cos(theta/180*Math.PI);
    }

    private static float sqrt(double v) {
        return (float)Math.sqrt(v);
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

    public void loadWorld(char[][] charBuffer, Color[][] fontColorBuffer, Color[][] backColorBuffer) {
        for (int i = 0; i < biomes.length; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                put(charBuffer, fontColorBuffer, backColorBuffer, i, j);
            }
        }
    }

    private void put(char[][] charBuffer, Color[][] fontColorBuffer, Color[][] backColorBuffer, int i, int j) {
        if (i+y < 0 || j+x < 0 || i+y >= charBuffer.length || j+x >= charBuffer[0].length - 30) return;

        if (i == selY && j == selX) {
            charBuffer[i+y][j+x] = 'X';
            fontColorBuffer[i+y][j+x] = Color.GOLDENROD;
        } else {
            if (mapmode == 0) {
                charBuffer[i + y][j + x] = biomes[i][j].sigchar;
                fontColorBuffer[i + y][j + x] = biomes[i][j].sigcolor;
            } else if (mapmode == 1) {
                charBuffer[i + y][j + x] = 'X';
                fontColorBuffer[i + y][j + x] = new Color(heightmap[i][j], heightmap[i][j], 0, 1);
            } else {
                charBuffer[i + y][j + x] = 'X';
                fontColorBuffer[i + y][j + x] = new Color(0, popmap[i][j] / 3000f, popmap[i][j] / 6000f, 1);
            }

            if (showCiv) {
                backColorBuffer[i + y][j + x] = civmap[i][j] == null ? CC.BLACK : c2c(i, j);
            }
        }

    }

    private Color c2c(int i, int j) {
        Civilization c = civmap[i][j];

        if (chk(i+1, j, c) && chk(i-1, j, c) && chk(i, j+1, c) && chk(i, j-1, c)) return CC.BLACK;

        return civmap[i][j].c;
    }

    private boolean chk(int i, int j, Civilization c) {
        if (i < 0 || j < 0 || i >= biomes.length || j >= biomes[0].length) {
            return false;
        }

        return civmap[i][j] == c;
    }

    private float eval(double a, double b) {
        return (float) osn.eval(a / 4, b / 4);
    }

    public List<String> getAsStringList() {
        List<String> out = new ArrayList<>();

        out.add(name);
        out.add(biomes.length + "x" + biomes[0].length);
        out.add(time + "");

        for (int i = 0; i < biomes.length; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                out.add(Biome.getIDByBiome(biomes[i][j]) + ":" + heightmap[i][j] + ":" + popmap[i][j]);
            }
        }

        return out;
    }

    public String getBiomeInfo() {
        return selX / 10.0f + ", " + selY / 10.0f + ": " + biomes[selY][selX].name;
    }

    public String getPopInfo() {
        return "Population: " + (int)popmap[selY][selX];
    }

    public int getCivCount() {
        return civs.size();
    }

    public Civilization getCivInfo() {
        return civmap[selY][selX];
    }

    public long getTime() {
        return time;
    }

    public int getPopulation() {
        int pop = 0;

        for (int i = 0; i < biomes.length; i++) {
            for (int j = 0; j < biomes[0].length; j++) {
                pop += (int)popmap[i][j];
            }
        }

        return pop;
    }

    public String getName() {
        return name;
    }

    public void flipCivSeen() {
        showCiv = !showCiv;
    }

    public int getMapmode() {
        return mapmode;
    }

    public void setMapmode(int m) {
        mapmode = m;
    }
}
