package com.kti.lagrange;

import java.io.File;
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
            worldAsStringList.add(0, filename);

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
}
