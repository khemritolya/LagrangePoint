package com.kti.lagrange.util;

import java.util.Random;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This file generates new world names using markov chains
 *
 * @Author Mr. Sours
 */

public class NameFactory {
    Random randy;
    HashMap<String, HashMap<Character, Double>> metaChances; // seems fine
    List<String> keysAsArray;

    int lookAhead;
    int targetLength;
    /**
     * @param filename name of file to initialize the generator to
     * @param look_ahead how many characters to analyze
     */
    public NameFactory(String filename, int look_ahead, int target_length, int seed) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            randy = new Random(seed);

            metaChances = new HashMap<String, HashMap<Character, Double>>();
            lookAhead = look_ahead;
            targetLength = target_length;

            // initialize metaChances by looking at each substring of length look_ahead
            // map them to the different possibilities of characters that could follow
            String line;
            while ((line = br.readLine()) != null) {

                for (int i = look_ahead; i < line.length(); i++) {

                    String sub = line.substring(i - look_ahead, i);
                    Character res = line.charAt(i);

                    if (metaChances.containsKey(sub)) {
                        HashMap<Character, Double> specificChances = metaChances.get(sub);
                        specificChances.replace(res, specificChances.getOrDefault(res, 1.0));
                    } else {
                        HashMap<Character, Double> specificChances = new HashMap<Character, Double>();
                        specificChances.put(res, 1.0);
                        metaChances.put(sub, specificChances);
                    }

                }
            }

            // normalize the probabilities in metaChances
            for (HashMap<Character, Double> specificChances : metaChances.values()) {
                double total = 0.0;
                for (double p : specificChances.values()) {
                    total += p;
                }

                for (Character c : specificChances.keySet()) {
                    specificChances.replace(c, specificChances.get(c) / total);
                }
            }

            keysAsArray = new ArrayList<String>(metaChances.keySet());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String generateNew() {
        String out = realNameGenerator();
        if (out.length() > 2) {
            return out + "-" + (randy.nextInt(9) + 1);
        } else {
            return generateNew();
        }
    }

    private String pad(int n) {
        if (n >= 10)
            return "" + n;
        else
            return "0" + n;
    }

    private String realNameGenerator() {
        StringBuilder name = new StringBuilder();
        name.append(generateInitial());
        for (int steps = (int) (randy.nextGaussian()+targetLength); steps > 0; steps--) {
            char next;
            try {
                next = getNext(name.substring(name.length()-lookAhead,name.length()));
            } catch (NullPointerException npe) {
                return name.toString();
            }
            name.append(next);
        }
        return name.toString();
    }

    private String generateInitial() {
        String input = keysAsArray.get(randy.nextInt(keysAsArray.size()));
        if (input.length() > 1)
            return input.substring(0, 1).toUpperCase() + input.substring(1);
        else
            return "";
    }

    private char getNext(String seed) {
        double threshhold = randy.nextDouble();
        double cumulative = 0.0;
        for (Character next : metaChances.get(seed).keySet()) {
            cumulative += metaChances.get(seed).get(next);
            if (cumulative > threshhold) {
                return next;
            }
        }
        throw new RuntimeException("could not come up with value for getNext()");
    }
}