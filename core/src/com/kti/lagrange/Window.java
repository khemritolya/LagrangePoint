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
	private int state;

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
			soundID = sound.play(0.0f);
			if (soundID == -1) throw new IOException("Could not init music properly");
			sound.setLooping(soundID, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

		canvas = new Canvas(font);
		world = new World(666);

		batch = new SpriteBatch();
		shapeRenderer = new ShapeRenderer();

		state = 0;
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
        }

		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		canvas.renderBackground(shapeRenderer);
		shapeRenderer.end();

		batch.begin();
		canvas.renderText(batch);
		batch.end();

		if (Gdx.input.isKeyPressed(BACKSPACE)) {
			System.exit(0);
		}

		if (state == INTRO) {
			if (Gdx.input.isKeyJustPressed(ENTER)) {
				state = GAME;
			}
		} else if (state == PAUSE) {
			if (Gdx.input.isKeyJustPressed(ENTER)) {
				state = GAME;
			}
		} else if (state == GAME) {
			if (Gdx.input.isKeyJustPressed(ESCAPE)) {
				state = PAUSE;
			}

			if (Gdx.input.isKeyJustPressed(Q)) {
			    state = MAP_HELP;
            }

			if (Gdx.input.isKeyPressed(UP)) {
				world.dy(1);
			}

			if (Gdx.input.isKeyPressed(DOWN)) {
				world.dy(-1);
			}

			if (Gdx.input.isKeyPressed(LEFT)) {
				world.dx(1);
			}

			if (Gdx.input.isKeyPressed(RIGHT)) {
				world.dx(-1);
			}

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

	public Canvas getCanvas() {
		return canvas;
	}
}