package com.kti.lagrange.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.kti.lagrange.Window;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "Lagrange Point 0.01";
		config.width = 1024;
		config.height = 720;
		config.resizable = false;

		new LwjglApplication(new Window(), config);
	}
}
