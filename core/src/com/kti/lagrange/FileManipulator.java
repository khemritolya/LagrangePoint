package com.kti.lagrange;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class FileManipulator {
    public static void save(World world, String filename) {
        try {
            if (!Files.exists(Paths.get("save/"))) {
                Files.createDirectories(Paths.get("save/"));
            }

            Path f = Paths.get("save/" + filename);

            if (Files.exists(f)) {
                Files.delete(f);
            }

            List<String> worldAsStringList = world.getAsStringList();

            Files.write(f, worldAsStringList, Charset.forName("ASCII"), StandardOpenOption.CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> getSaves() {
        List<String> out = new ArrayList<>();

        try {
            if (!Files.exists(Paths.get("save/"))) {
                return out;
            }

            Stream<Path> paths = Files.walk(Paths.get("save/"));

            for (Iterator<Path> pi = paths.iterator(); pi.hasNext();) {
                Path p = pi.next();
                out.add(p.toFile().getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out;
    }

    public static World load(String filename) {
        World world = null;

        try {
            Path f = Paths.get("save/" + filename);
            if (!Files.exists(f)) throw new IOException("Could not find save file " + filename);

            List<String> l = Files.readAllLines(f);

            String name = l.get(0);
            int h = Integer.parseInt(l.get(1).split("x")[0]);
            int w = Integer.parseInt(l.get(1).split("x")[1]);

            Biome[][] biomes = new Biome[h][w];
            float[][] heightmap = new float[h][w];

            int iter = 2;

            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w; j++) {
                    biomes[i][j] = Biome.getBiomeByID(Integer.parseInt(l.get(iter).split(":")[0]));
                    heightmap[i][j] = Float.parseFloat(l.get(iter).split(":")[1]);
                    iter++;
                }
            }

            world = new World(name, biomes, heightmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return world;
    }
}
