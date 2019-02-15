package com.kti.lagrange.map;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringTokenizer;

public class WorldConstant {
    private static HashMap<String, Float> vals;

    public static int init() {
        System.out.println("Loading world consts...");
        vals = new HashMap<>();

        try {
            Scanner reader = new Scanner(new File("assets/worldconstant.txt"));
            while (reader.hasNext()) {
                String line = reader.nextLine();
                System.out.println("" + line);
                if (!line.startsWith("#") && !(line.length() == 0)) {
                    StringTokenizer st = new StringTokenizer(line);
                    vals.put(st.nextToken(), Float.parseFloat(st.nextToken()));
                }
            }

        } catch (Exception e) {
            System.out.println("Critical error in world const load");
            System.err.println("Critical error in world const load");

            e.printStackTrace();


            return 0;
        }

        System.out.println("OK" + vals.toString());


        return 1;
    }

    public static float requestConstant(String id) {
        if (vals.get(id) == null) System.out.println("Unable to find constant " + id);

        return vals.get(id);
    }
}
