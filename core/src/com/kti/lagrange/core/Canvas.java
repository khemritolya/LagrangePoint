package com.kti.lagrange.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.kti.lagrange.map.Biome;
import com.kti.lagrange.map.World;
import com.kti.lagrange.util.CC;

import java.util.List;


public class Canvas {
    // Border constants
    private static final char BORDER_CHAR = '=';
    private static final Color BORDER_COLOR = Color.DARK_GRAY;
    private static final Color BORDER_BACK_COLOR = Color.LIGHT_GRAY;

    // the buffers that will be rendered
    private char[][] charBuffer;
    private Color[][] fontColorBuffer;
    private Color[][] backColorBuffer;

    // Keeping a reference never hurt anybody
    private BitmapFont f;

    // for the settings windows
    public int optionSelected;

    /**
     * Create the canvas and init buffers
     * @param f the font to use to draw, should be monospace
     */
    public Canvas(BitmapFont f) {
        this.f = f;

        charBuffer = new char[(int) (Gdx.graphics.getHeight() / f.getLineHeight())]
                [(int) (Gdx.graphics.getWidth() / f.getSpaceWidth())];
        fontColorBuffer = new Color[(int) (Gdx.graphics.getHeight() / f.getLineHeight())]
                [(int) (Gdx.graphics.getWidth() / f.getSpaceWidth())];
        backColorBuffer = new Color[(int) (Gdx.graphics.getHeight() / f.getLineHeight())]
                [(int) (Gdx.graphics.getWidth() / f.getSpaceWidth())];

        optionSelected = 0;
    }

    /**
     * Generate a buffer that represents a world
     * @param world the world to be displaying
     */
    public void generateWorldBuffer(World world) {
        clear();

        world.loadWorld(charBuffer, fontColorBuffer, backColorBuffer);
        //world.loadMiniMap(charBuffer, fontColorBuffer);

        loadString("World Name: ", 2, charBuffer[0].length - 28, Color.WHITE, Color.BLACK);
        loadString(world.getName(), 3, charBuffer[0].length - 28, Color.WHITE, Color.BLACK);
        loadString("Time since arrival: ", 4, charBuffer[0].length - 28, Color.WHITE, Color.BLACK);
        loadString(world.getTime() / 1000d + " days", 5, charBuffer[0].length - 28, Color.WHITE, Color.BLACK);
        loadString("World Population: ", 6, charBuffer[0].length - 28, Color.WHITE, Color.BLACK);
        loadString(world.getPopulation()/1000/1000d + "M people", 7, charBuffer[0].length - 28, Color.WHITE, Color.BLACK);

        loadString("Biome Info: ", 9, charBuffer[0].length - 28, Color.WHITE, Color.BLACK);
        loadString(world.getBiomeInfo(), 10, charBuffer[0].length - 28, Color.WHITE, Color.BLACK);
        loadString(world.getPopInfo(), 11, charBuffer[0].length - 28, Color.WHITE, Color.BLACK);

        loadString("Empires: " + world.getCivCount(), 13, charBuffer[0].length - 28, Color.WHITE, Color.BLACK);
        if (world.getCivInfo() != null) {
            loadString("Empire Manpower:" + world.getCivInfo().getManpower(), 14, charBuffer[0].length - 28, Color.WHITE, Color.BLACK);
        }

        loadString("\'m\' to cycle mapmode", 18, charBuffer[0].length - 28, Color.WHITE, Color.BLACK);
        loadString("\'e\' to toggle civs", 19, charBuffer[0].length - 28, Color.WHITE, Color.BLACK);
        loadString("\'q\' for map legend page", 20, charBuffer[0].length - 28, Color.WHITE, Color.BLACK);

        // the extra border line that needs to be drawn
        for (int i = 0; i < charBuffer.length; i++) {
            charBuffer[i][charBuffer[0].length - 30] = BORDER_CHAR;
            fontColorBuffer[i][charBuffer[0].length - 30] = BORDER_COLOR;
            backColorBuffer[i][charBuffer[0].length - 30] = BORDER_BACK_COLOR;
        }

        /*for (int i = 0; i < 30; i++) {
            charBuffer[charBuffer.length - 17][charBuffer[0].length - 30 + i] = BORDER_CHAR;
            fontColorBuffer[charBuffer.length - 17][charBuffer[0].length - 30 + i] = BORDER_COLOR;
            backColorBuffer[charBuffer.length - 17][charBuffer[0].length - 30 + i] = BORDER_BACK_COLOR;

        }*/

        generateBorder();
    }

    /**
     * Create the buffer for the intro screen
     */
    public void generateIntroBuffer() {
        clear();

        loadString("Lagrange Point Version 0.01", charBuffer.length / 2 - 10,
                charBuffer[0].length / 2 - "Lagrange Point Version 0.01".length() / 2,
                Color.WHITE, Color.BLACK);
        loadString("Author: Khemri Tolya", charBuffer.length / 2 - 9,
                charBuffer[0].length / 2 - "Author: Khemri Tolya".length() / 2,
                Color.WHITE, Color.BLACK);
        loadString("Help from: ", charBuffer.length / 2 - 7,
                charBuffer[0].length / 2 - 5, Color.WHITE, Color.BLACK);
        loadString("Mr. Sours", charBuffer.length / 2 - 6,
                charBuffer[0].length / 2 - 5, Color.WHITE, Color.BLACK);
        loadString("Ben Lepper", charBuffer.length / 2 - 5,
                charBuffer[0].length / 2 - 5, Color.WHITE, Color.BLACK);

        loadString("Press ENTER to start...", charBuffer.length / 2,
                charBuffer[0].length / 2 - "Press ENTER to continue.".length() / 2,
                Color.GRAY, Color.BLACK);

        generateBorder();
    }

    /**
     * Create the buffer for the pause screen
     */
    public void generatePauseBuffer() {
        clear();

        loadString("Lagrange Point Version 0.01", charBuffer.length / 2 - 10,
                charBuffer[0].length / 2 - "Lagrange Point Version 0.01".length() / 2,
                Color.WHITE, Color.BLACK);
        loadString("Author: Khemri Tolya", charBuffer.length / 2 - 9,
                charBuffer[0].length / 2 - "Author: Khemri Tolya".length() / 2,
                Color.WHITE, Color.BLACK);

        loadString("<< P A U S E D >>", charBuffer.length / 2 - 5,
                charBuffer[0].length / 2 - "<< P A U S E D >>".length() / 2,
                Color.WHITE, Color.BLACK);

        loadString("Press ESCAPE to continue...", charBuffer.length / 2,
                charBuffer[0].length / 2 - "Press ESCAPE to continue.".length() / 2,
                Color.GRAY, Color.BLACK);
        loadString("Press ENTER to select...", charBuffer.length / 2 + 1,
                charBuffer[0].length / 2 - "Press ENTER to select...".length() / 2,
                Color.GRAY, Color.BLACK);
        loadString("Use ARROWS to move selection...", charBuffer.length / 2 + 2,
                charBuffer[0].length / 2 - "Use ARROWS to move selection".length() / 2,
                Color.GRAY, Color.BLACK);

        loadString("New game", charBuffer.length / 2 + 6, charBuffer[0].length / 2 - 4,
                Color.WHITE, Color.BLACK);

        loadString("Save game", charBuffer.length / 2 + 7, charBuffer[0].length / 2 - 4,
                Color.WHITE, Color.BLACK);

        loadString("Load game", charBuffer.length / 2 + 8, charBuffer[0].length / 2 - 4
                , Color.WHITE, Color.BLACK);

        loadString("Quit game", charBuffer.length / 2 + 9, charBuffer[0].length / 2 - 4,
                Color.WHITE, Color.BLACK);

        if (optionSelected == 0) {
            charBuffer[charBuffer.length / 2 + 6][charBuffer[0].length / 2 - 6] = '*';
            fontColorBuffer[charBuffer.length / 2 + 6][charBuffer[0].length / 2 - 6] =
                    Color.WHITE;
        } else if (optionSelected == 1) {
            charBuffer[charBuffer.length / 2 + 7][charBuffer[0].length / 2 - 6] = '*';
            fontColorBuffer[charBuffer.length / 2 + 7][charBuffer[0].length / 2 - 6] =
                    Color.WHITE;
        } else if (optionSelected == 2) {
            charBuffer[charBuffer.length / 2 + 8][charBuffer[0].length / 2 - 6] = '*';
            fontColorBuffer[charBuffer.length / 2 + 8][charBuffer[0].length / 2 - 6] =
                    Color.WHITE;
        } else if (optionSelected == 3) {
            charBuffer[charBuffer.length / 2 + 9][charBuffer[0].length / 2 - 6] = '*';
            fontColorBuffer[charBuffer.length / 2 + 9][charBuffer[0].length / 2 - 6] =
                    Color.WHITE;
        }

        generateBorder();
    }

    /**
     * Create the buffer for the map legent/help screen
     */
    public void generateHelpBuffer() {
        clear();

        loadString("Lagrange Point Map Legent / Help Page",2,2, Color.WHITE, Color.BLACK);
        loadString("Lagrange Point (c) 2018 Khemri Tolya",3,2, Color.WHITE, Color.BLACK);
        loadString("\'ESC\' to return to game",5,2, Color.WHITE, Color.BLACK);

        loadString("Biomes: ", 8, 2, Color.WHITE, Color.BLACK);
        loadBiomeAndInfo(Biome.SEA, 10, 2, Color.WHITE, Color.BLACK);
        loadBiomeAndInfo(Biome.LAKE, 11, 2, Color.WHITE, Color.BLACK);
        loadBiomeAndInfo(Biome.OASIS, 12, 2, Color.WHITE, Color.BLACK);
        loadBiomeAndInfo(Biome.RIVER_NORTH_SOUTH, 13, 2, Color.WHITE, Color.BLACK);

        loadBiomeAndInfo(Biome.PLAINS, 15, 2, Color.WHITE, Color.BLACK);
        loadBiomeAndInfo(Biome.FIELD, 16, 2, Color.WHITE, Color.BLACK);
        loadBiomeAndInfo(Biome.FOREST, 17, 2, Color.WHITE, Color.BLACK);

        loadBiomeAndInfo(Biome.MOUNTAIN, 19, 2, Color.WHITE, Color.BLACK);
        loadBiomeAndInfo(Biome.HIGH_MOUNTAIN, 20, 2, Color.WHITE, Color.BLACK);
        loadBiomeAndInfo(Biome.VOLCANO, 21, 2, Color.WHITE, Color.BLACK);

        loadBiomeAndInfo(Biome.DESERT, 23, 2, Color.WHITE, Color.BLACK);
        loadBiomeAndInfo(Biome.NORTH_TUNDRA, 24, 2, Color.WHITE, Color.BLACK);
        loadBiomeAndInfo(Biome.ICE, 25, 2, Color.WHITE, Color.BLACK);

        generateBorder();
    }

    /**
     * Create the buffer for the new game screen
     */
    public void generateNewGameBuffer() {
        clear();

        loadString("Lagrange Point Map Generation Page",2,2, Color.WHITE, Color.BLACK);
        loadString("Lagrange Point (c) 2018 Khemri Tolya",3,2, Color.WHITE, Color.BLACK);

        loadString("New Game Seed: ", 5, 2, Color.WHITE, Color.BLACK);
        loadString(Window.w.inputBuffer, 6, 2, Color.WHITE, Color.BLACK);

        if (Window.w.getFrame() / 25 % 2 == 0) {
            charBuffer[6][2 + Window.w.inputBuffer.length()] = '_';
            fontColorBuffer[6][2 + Window.w.inputBuffer.length()] = Color.WHITE;
        }
        generateBorder();
    }

    /**
     * Create the buffer for the new game screen 2 electric bungaloo
     */
    public void generateNewGame2Buffer() {
        clear();

        String xpls = Window.w.inputBuffer.charAt(Window.w.inputBuffer.length() - 1) == '#' ?
                "": Window.w.inputBuffer.split("#")[1];
        //System.out.println(xpls);
        //System.out.print(Window.w.inputBuffer);

        loadString("Lagrange Point Map Generation Page",2,2, Color.WHITE, Color.BLACK);
        loadString("Lagrange Point (c) 2018 Khemri Tolya",3,2, Color.WHITE, Color.BLACK);

        loadString("New Game Size (w x h): ", 5, 2, Color.WHITE, Color.BLACK);
        loadString(xpls, 6, 2, Color.WHITE, Color.BLACK);

        if (Window.w.getFrame() / 25 % 2 == 0) {
            charBuffer[6][2 + xpls.length()] = '_';
            fontColorBuffer[6][2 + xpls.length()] = Color.WHITE;
        }

        generateBorder();
    }


    /**
     * Create the buffer for the save game screen
     */

    public void generateSaveGameBuffer() {
        clear();

        loadString("Lagrange Point Map Save Page",2,2, Color.WHITE, Color.BLACK);
        loadString("Lagrange Point (c) 2018 Khemri Tolya",3,2, Color.WHITE, Color.BLACK);

        loadString("Save Game Name: ", 5, 2, Color.WHITE, Color.BLACK);
        loadString(Window.w.inputBuffer, 6, 2, Color.WHITE, Color.BLACK);

        if (Window.w.getFrame() / 25 % 2 == 0) {
            charBuffer[6][2 + Window.w.inputBuffer.length()] = '_';
            fontColorBuffer[6][2 + Window.w.inputBuffer.length()] = Color.WHITE;
        }
        generateBorder();
    }

    /**
     * Generate the buffer for the load game screen
     * @param saves the file names of all files in save/
     */
    public void generateLoadGameBuffer(List<String> saves) {
        clear();

        loadString("Lagrange Point Map Load Page",2,2, Color.WHITE, Color.BLACK);
        loadString("Lagrange Point (c) 2018 Khemri Tolya",3,2, Color.WHITE, Color.BLACK);

        if (saves.size() > 1) {
            loadString("Available saves: ", 5, 2, Color.WHITE, Color.BLACK);

            for (int i = 1; i < saves.size(); i++) {
                loadString(saves.get(i), 5 + i, 2, Color.WHITE, Color.BLACK);
            }

            loadString("Load Game Name: ", saves.size() + 6, 2, Color.WHITE, Color.BLACK);
            loadString(Window.w.inputBuffer, saves.size() + 7, 2, Color.WHITE, Color.BLACK);

            if (Window.w.getFrame() / 25 % 2 == 0) {
                charBuffer[saves.size() + 7][2 + Window.w.inputBuffer.length()] = '_';
                fontColorBuffer[saves.size() + 7][2 + Window.w.inputBuffer.length()] = Color.WHITE;

            }
        } else {
            loadString("No available saves to load!", 5, 2, Color.WHITE, Color.BLACK);
            loadString("Press ESCAPE to go back!", 6, 2, Color.WHITE, Color.BLACK);

        }
        generateBorder();
    }

    /**
     * Generates a border
     */
    private void generateBorder() {
        for (int i = 0; i < charBuffer[0].length; i++) {
            charBuffer[0][i] = BORDER_CHAR;
            fontColorBuffer[0][i] = BORDER_COLOR;
            backColorBuffer[0][i] = BORDER_BACK_COLOR;

            charBuffer[charBuffer.length - 1][i] = BORDER_CHAR;
            fontColorBuffer[fontColorBuffer.length - 1][i] = BORDER_COLOR;
            backColorBuffer[backColorBuffer.length - 1][i] = BORDER_BACK_COLOR;
        }

        for (int i = 0; i < charBuffer.length; i++) {
            charBuffer[i][0] = BORDER_CHAR;
            fontColorBuffer[i][0]= BORDER_COLOR;
            backColorBuffer[i][0] = BORDER_BACK_COLOR;

            charBuffer[i][charBuffer[0].length - 1] = BORDER_CHAR;
            fontColorBuffer[i][charBuffer[0].length - 1] = BORDER_COLOR;
            backColorBuffer[i][charBuffer[0].length - 1] = BORDER_BACK_COLOR;
        }

        loadString("< Lagrange Point "+ charBuffer.length + "x" + charBuffer[0].length +" FPS " +
                Window.w.averageFPS() + " FRAME " + Window.w.getFrame() + " >", 0,
                charBuffer[0].length / 2 - ("< Lagrange Point "+ charBuffer.length + "x" +
                        charBuffer[0].length +" FPS " + Window.w.averageFPS() + " FRAME " + Window.w.getFrame() +
                        " >").length() / 2, Color.BLACK, Color.LIGHT_GRAY);
    }

    /**
     * Renders the background color buffer
     * @param r the ShapeRenderer that will be drawing the background buffer
     */
    public void renderBackground(ShapeRenderer r) {
        for (int i = 0; i < backColorBuffer.length; i++) {
            for (int j = 0; j < backColorBuffer[0].length; j++) {
                if (backColorBuffer[i][j] != null) {
                    r.setColor(backColorBuffer[i][j]);
                    r.rect(j * f.getSpaceWidth(), Window.WIN_HEIGHT - (i+1) * f.getLineHeight(),
                            f.getSpaceWidth()+1, f.getLineHeight());
                }
            }
        }
    }

    /**
     * Draw the charBuffer with the colors in fontColorBuffer
     * @param b the SpriteBatch to use for drawing the characters
     */
    public void renderText(SpriteBatch b) {
        for (int i = 0; i < charBuffer.length; i++) {
            for (int j = 0; j < charBuffer[0].length; j++) {
                if (fontColorBuffer[i][j] == null) {
                    f.setColor(Color.WHITE);
                } else {
                    f.setColor(fontColorBuffer[i][j]);
                }

                f.draw(b, String.valueOf(charBuffer[i][j]),
                       j * f.getSpaceWidth(), Window.WIN_HEIGHT - i * f.getLineHeight() - 3);
            }
        }
    }

    /**
     * Utility method for loading text into the buffers
     * @param s the String to be loaded
     * @param row the row index to begin at
     * @param start the collumn index to begin at
     * @param fontColor the font color
     * @param backColor the background color
     */
    private void loadString(String s, int row, int start, Color fontColor, Color backColor) {
        if (row >= charBuffer.length) return;

        for(int i = 0; i < (s.length() + start > charBuffer[row].length ?
                charBuffer[row].length - start : s.length()); i++) {
            charBuffer[row][i + start] = s.charAt(i);
            fontColorBuffer[row][i + start] = fontColor;
            backColorBuffer[row][i + start] = backColor;
        }
    }

    /**
     * Utility method to load up the text for a biome in the legend/help mapmode
     * @param b the Biome to display
     * @param row the row index to begin
     * @param start the collumn index to begin at
     * @param fontColor the font color of the text
     * @param backColor the background color of the text
     */
    private void loadBiomeAndInfo(Biome b, int row, int start, Color fontColor, Color backColor) {
        charBuffer[row][start] = b.sigchar;
        fontColorBuffer[row][start] = b.sigcolor;

        if (b == Biome.SEA) {
            loadString(" represents the sea. There are many fish here.",
                    row, start+1, fontColor, backColor);
        } else if (b == Biome.PLAINS) {
            loadString(" represents a plains biome. There may be various critters around here.",
                    row, start+1, fontColor, backColor);
        } else if (b == Biome.MOUNTAIN) {
            loadString(" represents a mountain. High altitude and a beautiful view.",
                    row, start+1, fontColor, backColor);
        } else if (b == Biome.FIELD) {
            loadString(" represents prime grade farmland. Expect developed civilization here.",
                    row, start+1, fontColor, backColor);
        } else if (b == Biome.FOREST) {
            loadString(" represents a forest. Any developed civilization gets it\'s wood here.",
                    row, start+1, fontColor, backColor);
        } else if (b == Biome.LAKE) {
            loadString(" represents a lake. Like an ocean, except smaller.",
                    row, start+1, fontColor, backColor);
        } else if (b == Biome.OASIS) {
            loadString(" represents an oasis. Like a lake, except in the desert.",
                    row, start+1, fontColor, backColor);
        } else if (b == Biome.HIGH_MOUNTAIN) {
            loadString(" represents the peaks of the highest mountains. Extremely high altitude and a beautiful view.",
                    row, start+1, fontColor, backColor);
        } else if (b == Biome.VOLCANO) {
            loadString(" represents the more volatile cousin of Peaks, Volcanos",
                    row, start+1, fontColor, backColor);
        } else if (b == Biome.DESERT) {
            loadString(" represents a desert. Expect nasty creepy-crawlies.",
                    row, start+1, fontColor, backColor);
        } else if (b == Biome.NORTH_TUNDRA) {
            loadString(" represents the northernmost tundra. There is little of value here.",
                    row, start+1, fontColor, backColor);
        } else if (b == Biome.ICE) {
            loadString(" represents the polar ice caps. Nice view, but no life whatsoever.",
                    row, start+1, fontColor, backColor);
        } else if (b == Biome.RIVER_NORTH_SOUTH) {
            loadString(" represents a river. Water flows along here, allowing civilization to draw water.",
                    row, start+1, fontColor, backColor);
        }
    }

    /**
     * Clear that buffer before a new frame
     */
    private void clear() {
        for (int i = 0; i < charBuffer.length; i++) {
            for (int j = 0; j < charBuffer[0].length; j++) {
                charBuffer[i][j] = ' ';
                fontColorBuffer[i][j] = null;
                backColorBuffer[i][j] = null;
            }
        }
    }

    /**
     * Just a humble getter for buffer dimension
     * @return the buffer Y size
     */
    public int getBufferY() {
        return charBuffer.length;
    }

    /**
     * Just a humble getter for buffer dimension
     * @return the buffer X size
     */
    public int getBufferX() {
        return charBuffer[0].length;
    }
}
