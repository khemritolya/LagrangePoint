package com.kti.lagrange;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.io.IOException;
import java.util.List;

import static com.badlogic.gdx.Input.Keys.*;

public class Window extends ApplicationAdapter {
	private SpriteBatch batch;
	private ShapeRenderer shapeRenderer;
	private BitmapFont font;

	private Canvas canvas;
	private World world;

	public static Window w;
	public static float WIN_WIDTH, WIN_HEIGHT;

	private long[] lastFrameTimes;

	private Sound sound;
	private long soundID;

	private static final int INTRO = 0;
	private static final int PAUSE = 1;
	private static final int GAME = 2;
	private static final int MAP_HELP = 3;
	private static final int NEW_GAME = 4;
	private static final int SAVE_GAME = 5;
	private static final int LOAD_GAME = 6;
	private int state;
	private List<String> localsaves;
	public String inputBuffer;

	@Override
	public void create () {
		w = this;

		lastFrameTimes = new long[10];

		WIN_WIDTH = Gdx.graphics.getWidth();
		WIN_HEIGHT = Gdx.graphics.getHeight();

		FreeTypeFontGenerator f = new FreeTypeFontGenerator(Gdx.files.absolute("assets/font.ttf"));
		FreeTypeFontGenerator.FreeTypeFontParameter ftfp = new FreeTypeFontGenerator.FreeTypeFontParameter();
		ftfp.size = 12;
		font = f.generateFont(ftfp);
		font.setUseIntegerPositions(false);
		f.dispose();

		try {
			sound = Gdx.audio.newSound(Gdx.files.internal("assets/audio.mp3"));
			soundID = sound.play(0.5f);
			if (soundID == -1) throw new IOException("Could not init music properly");
			sound.setLooping(soundID, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		canvas = new Canvas(font);

		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

		state = 0;
		inputBuffer = new String();
	}

	@Override
	public void render () {
		updatesFrameTimes();

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (state == INTRO) {
			canvas.generateIntroBuffer();
		} else if (state == PAUSE) {
            canvas.generatePauseBuffer();
        } else if (state == GAME) {
			canvas.generateBuffer(world);
		} else if (state == MAP_HELP) {
		    canvas.generateHelpBuffer();
        } else if (state == NEW_GAME) {
		    canvas.generateNewGameBuffer();
        } else if (state == SAVE_GAME) {
		    canvas.generateSaveGameBuffer();
        } else if (state == LOAD_GAME) {
            canvas.generateLoadGameBuffer(localsaves);
        }

		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		canvas.renderBackground(shapeRenderer);
		shapeRenderer.end();

		batch.begin();
		canvas.renderText(batch);
		batch.end();

		if (Gdx.input.isKeyPressed(TAB)) System.exit(0);


		if (state == INTRO) {
			if (Gdx.input.isKeyJustPressed(ENTER)) state = PAUSE;

		} else if (state == PAUSE) {
			if (Gdx.input.isKeyJustPressed(ESCAPE) && world != null) state = GAME;

			if (Gdx.input.isKeyJustPressed(ENTER) && canvas.optionSelected == 0) {
                inputBuffer = new String();
                state = NEW_GAME;
            } else if (Gdx.input.isKeyJustPressed(ENTER) && canvas.optionSelected == 1 &&
                    world != null) {
                inputBuffer = new String();
                state = SAVE_GAME;
            } else if (Gdx.input.isKeyJustPressed(ENTER) && canvas.optionSelected == 2) {
                inputBuffer = new String();
                localsaves = FileManipulator.getSaves();
                state = LOAD_GAME;
            } else if (Gdx.input.isKeyJustPressed(ENTER) && canvas.optionSelected == 3) {
			    System.exit(0);
            }

			if (Gdx.input.isKeyJustPressed(UP)) {
			    canvas.optionSelected--;

			    if (canvas.optionSelected < 0) canvas.optionSelected = 0;
            }

            if (Gdx.input.isKeyJustPressed(DOWN)) {
                canvas.optionSelected++;

                if (canvas.optionSelected > 3) canvas.optionSelected = 3;
            }
		} else if (state == GAME) {
			if (Gdx.input.isKeyJustPressed(ESCAPE)) state = PAUSE;

			if (Gdx.input.isKeyJustPressed(Q)) state = MAP_HELP;

			if (Gdx.input.isKeyPressed(UP)) world.dy(1);

			if (Gdx.input.isKeyPressed(DOWN)) world.dy(-1);

			if (Gdx.input.isKeyPressed(LEFT)) world.dx(1);

			if (Gdx.input.isKeyPressed(RIGHT)) world.dx(-1);


			if (Gdx.input.isKeyJustPressed(M)) {
				if (world.getMapmode() == 0) {
					world.setMapmode(1);
				} else {
					world.setMapmode(0);
				}
			}
		} else if (state == MAP_HELP) {
		    if (Gdx.input.isKeyJustPressed(ESCAPE)) {
		        state = GAME;
            }
        } else if (state == NEW_GAME) {
            if (Gdx.input.isKeyJustPressed(ESCAPE)) state = PAUSE;

            addToInputBuffer();

            if (Gdx.input.isKeyJustPressed(BACKSPACE) && inputBuffer.length() > 0)
                inputBuffer = inputBuffer.substring(0, inputBuffer.length() - 1);

            if (Gdx.input.isKeyJustPressed(ENTER)) {
                world = new World(inputBuffer.hashCode());
                state = GAME;
            }
		} else if (state == SAVE_GAME) {
            if (Gdx.input.isKeyJustPressed(ESCAPE)) state = PAUSE;

            addToInputBuffer();

            if (Gdx.input.isKeyJustPressed(BACKSPACE) && inputBuffer.length() > 0)
                inputBuffer = inputBuffer.substring(0, inputBuffer.length() - 1);

            if (Gdx.input.isKeyJustPressed(ENTER)) {
                FileManipulator.save(world, inputBuffer);

                state = GAME;
            }
        } else if (state == LOAD_GAME) {
            if (Gdx.input.isKeyJustPressed(ESCAPE)) state = PAUSE;

            addToInputBuffer();

            if (Gdx.input.isKeyJustPressed(BACKSPACE) && inputBuffer.length() > 0)
                inputBuffer = inputBuffer.substring(0, inputBuffer.length() - 1);

            if (Gdx.input.isKeyJustPressed(ENTER)) {
                localsaves = FileManipulator.getSaves();

                if (localsaves.contains(inputBuffer) && !inputBuffer.equals("save")) {
                    world = FileManipulator.load(inputBuffer);
                    state = GAME;
                }
            }
        }
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}

	private void updatesFrameTimes() {
		for (int i = 0; i < lastFrameTimes.length - 1; i++) {
			lastFrameTimes[i] = lastFrameTimes[i + 1];
		}

		lastFrameTimes[lastFrameTimes.length - 1] = System.currentTimeMillis();
	}

	public long averageFPS() {
		long[] diffs = new long[lastFrameTimes.length - 1];

		for (int i = 0; i < lastFrameTimes.length - 1; i++) {
			diffs[i] =  lastFrameTimes[i + 1] - lastFrameTimes[i];
		}

		long sum = 0;

		for (int i = 0; i < diffs.length; i++) {
			sum += diffs[i];
		}

		return (long) (1000.d * diffs.length / sum);
	}

	private void addToInputBuffer() {
	    if (Gdx.input.isKeyPressed(SHIFT_LEFT) || Gdx.input.isKeyPressed(SHIFT_RIGHT)) {
            if (Gdx.input.isKeyJustPressed(A)) inputBuffer += 'A';
            if (Gdx.input.isKeyJustPressed(B)) inputBuffer += 'B';
            if (Gdx.input.isKeyJustPressed(C)) inputBuffer += 'C';
            if (Gdx.input.isKeyJustPressed(D)) inputBuffer += 'D';
            if (Gdx.input.isKeyJustPressed(E)) inputBuffer += 'E';
            if (Gdx.input.isKeyJustPressed(F)) inputBuffer += 'F';
            if (Gdx.input.isKeyJustPressed(G)) inputBuffer += 'G';
            if (Gdx.input.isKeyJustPressed(H)) inputBuffer += 'H';
            if (Gdx.input.isKeyJustPressed(I)) inputBuffer += 'I';
            if (Gdx.input.isKeyJustPressed(J)) inputBuffer += 'J';
            if (Gdx.input.isKeyJustPressed(K)) inputBuffer += 'K';
            if (Gdx.input.isKeyJustPressed(L)) inputBuffer += 'L';
            if (Gdx.input.isKeyJustPressed(M)) inputBuffer += 'M';
            if (Gdx.input.isKeyJustPressed(N)) inputBuffer += 'N';
            if (Gdx.input.isKeyJustPressed(O)) inputBuffer += 'O';
            if (Gdx.input.isKeyJustPressed(P)) inputBuffer += 'P';
            if (Gdx.input.isKeyJustPressed(Q)) inputBuffer += 'Q';
            if (Gdx.input.isKeyJustPressed(R)) inputBuffer += 'R';
            if (Gdx.input.isKeyJustPressed(S)) inputBuffer += 'S';
            if (Gdx.input.isKeyJustPressed(T)) inputBuffer += 'T';
            if (Gdx.input.isKeyJustPressed(U)) inputBuffer += 'U';
            if (Gdx.input.isKeyJustPressed(V)) inputBuffer += 'V';
            if (Gdx.input.isKeyJustPressed(W)) inputBuffer += 'W';
            if (Gdx.input.isKeyJustPressed(X)) inputBuffer += 'X';
            if (Gdx.input.isKeyJustPressed(Y)) inputBuffer += 'Y';
            if (Gdx.input.isKeyJustPressed(Z)) inputBuffer += 'Z';
        } else {
            if (Gdx.input.isKeyJustPressed(A)) inputBuffer += 'a';
            if (Gdx.input.isKeyJustPressed(B)) inputBuffer += 'b';
            if (Gdx.input.isKeyJustPressed(C)) inputBuffer += 'c';
            if (Gdx.input.isKeyJustPressed(D)) inputBuffer += 'd';
            if (Gdx.input.isKeyJustPressed(E)) inputBuffer += 'e';
            if (Gdx.input.isKeyJustPressed(F)) inputBuffer += 'f';
            if (Gdx.input.isKeyJustPressed(G)) inputBuffer += 'g';
            if (Gdx.input.isKeyJustPressed(H)) inputBuffer += 'h';
            if (Gdx.input.isKeyJustPressed(I)) inputBuffer += 'i';
            if (Gdx.input.isKeyJustPressed(J)) inputBuffer += 'j';
            if (Gdx.input.isKeyJustPressed(K)) inputBuffer += 'k';
            if (Gdx.input.isKeyJustPressed(L)) inputBuffer += 'l';
            if (Gdx.input.isKeyJustPressed(M)) inputBuffer += 'm';
            if (Gdx.input.isKeyJustPressed(N)) inputBuffer += 'n';
            if (Gdx.input.isKeyJustPressed(O)) inputBuffer += 'o';
            if (Gdx.input.isKeyJustPressed(P)) inputBuffer += 'p';
            if (Gdx.input.isKeyJustPressed(Q)) inputBuffer += 'q';
            if (Gdx.input.isKeyJustPressed(R)) inputBuffer += 'r';
            if (Gdx.input.isKeyJustPressed(S)) inputBuffer += 's';
            if (Gdx.input.isKeyJustPressed(T)) inputBuffer += 't';
            if (Gdx.input.isKeyJustPressed(U)) inputBuffer += 'u';
            if (Gdx.input.isKeyJustPressed(V)) inputBuffer += 'v';
            if (Gdx.input.isKeyJustPressed(W)) inputBuffer += 'w';
            if (Gdx.input.isKeyJustPressed(X)) inputBuffer += 'x';
            if (Gdx.input.isKeyJustPressed(Y)) inputBuffer += 'y';
            if (Gdx.input.isKeyJustPressed(Z)) inputBuffer += 'z';
        }

        if (Gdx.input.isKeyJustPressed(NUM_0)) inputBuffer += '0';
        if (Gdx.input.isKeyJustPressed(NUM_1)) inputBuffer += '1';
        if (Gdx.input.isKeyJustPressed(NUM_2)) inputBuffer += '2';
        if (Gdx.input.isKeyJustPressed(NUM_3)) inputBuffer += '3';
        if (Gdx.input.isKeyJustPressed(NUM_4)) inputBuffer += '4';
        if (Gdx.input.isKeyJustPressed(NUM_5)) inputBuffer += '5';
        if (Gdx.input.isKeyJustPressed(NUM_6)) inputBuffer += '6';
        if (Gdx.input.isKeyJustPressed(NUM_7)) inputBuffer += '7';
        if (Gdx.input.isKeyJustPressed(NUM_8)) inputBuffer += '8';
        if (Gdx.input.isKeyJustPressed(NUM_9)) inputBuffer += '9';
    }

	public Canvas getCanvas() {
		return canvas;
	}
}