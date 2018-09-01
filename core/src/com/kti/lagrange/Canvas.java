package com.kti.lagrange;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;


public class Canvas {
    private static final char BORDER_CHAR = '=';
    private static final Color BORDER_COLOR = Color.DARK_GRAY;
    private static final Color BORDER_BACK_COLOR = Color.LIGHT_GRAY;

    private char[][] charBuffer;
    private Color[][] fontColorBuffer;
    private Color[][] backColorBuffer;

    private BitmapFont f;

    public Canvas(BitmapFont f) {
        this.f = f;

        charBuffer = new char[(int) (Gdx.graphics.getHeight() / f.getLineHeight())]
                [(int) (Gdx.graphics.getWidth() / f.getSpaceWidth())];
        fontColorBuffer = new Color[(int) (Gdx.graphics.getHeight() / f.getLineHeight())]
                [(int) (Gdx.graphics.getWidth() / f.getSpaceWidth())];
        backColorBuffer = new Color[(int) (Gdx.graphics.getHeight() / f.getLineHeight())]
                [(int) (Gdx.graphics.getWidth() / f.getSpaceWidth())];
    }

    public void generateBuffer(World world) {
        clear();

        world.loadWorld(charBuffer, fontColorBuffer);
        //world.loadMiniMap(charBuffer, fontColorBuffer);

        loadString("World Name: ", 2, charBuffer[0].length - 28, Color.WHITE, Color.BLACK);
        loadString(world.getName(), 3, charBuffer[0].length - 28, Color.WHITE, Color.BLACK);

        loadString("Biome Info: ", 5, charBuffer[0].length - 28, Color.WHITE, Color.BLACK);
        loadString(world.getSelectedInfo(), 6, charBuffer[0].length - 28, Color.WHITE, Color.BLACK);

        loadString("\'m\' to cycle mapmode", 8, charBuffer[0].length - 28, Color.WHITE, Color.BLACK);

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

        loadString("Press ENTER to continue...", charBuffer.length / 2,
                charBuffer[0].length / 2 - "Press ENTER to continue...".length() / 2,
                Color.GRAY, Color.BLACK);

        generateBorder();
    }

    public void generatePauseBuffer() {
        clear();

        loadString("Lagrange Point Version 0.01", charBuffer.length / 2 - 10,
                charBuffer[0].length / 2 - "Lagrange Point Version 0.01".length() / 2,
                Color.WHITE, Color.BLACK);
        loadString("Author: Khemri Tolya", charBuffer.length / 2 - 9,
                charBuffer[0].length / 2 - "Author: Khemri Tolya".length() / 2,
                Color.WHITE, Color.BLACK);

        loadString("Press ENTER to continue...", charBuffer.length / 2,
                charBuffer[0].length / 2 - "Press ENTER to continue...".length() / 2,
                Color.GRAY, Color.BLACK);

        generateBorder();
    }

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

        loadString("< Lagrange Point FPS " + Window.w.averageFPS() + " FRAME " + charBuffer.length + "x" +
                charBuffer[0].length + " >", 0, charBuffer[0].length / 2 - ("< Lagrange Point FPS "
                + Window.w.averageFPS() + " FRAME " + charBuffer.length + "x" + charBuffer[0].length + " >").length()
                / 2, Color.BLACK, Color.LIGHT_GRAY);
    }

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

    private void loadString(String s, int row, int start, Color fontColor, Color backColor) {
        if (row >= charBuffer.length) return;

        for(int i = 0; i < (s.length() + start > charBuffer[row].length ?
                charBuffer[row].length - start : s.length()); i++) {
            charBuffer[row][i + start] = s.charAt(i);
            fontColorBuffer[row][i + start] = fontColor;
            backColorBuffer[row][i + start] = backColor;
        }
    }

    private void clear() {
        for (int i = 0; i < charBuffer.length; i++) {
            for (int j = 0; j < charBuffer[0].length; j++) {
                charBuffer[i][j] = ' ';
                fontColorBuffer[i][j] = null;
                backColorBuffer[i][j] = null;
            }
        }
    }

    public int getBufferY() {
        return charBuffer.length;
    }

    public int getBufferX() {
        return charBuffer[0].length;
    }
}
