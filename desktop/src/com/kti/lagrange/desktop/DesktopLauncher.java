package com.kti.lagrange.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.kti.lagrange.Window;

import javax.swing.*;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "Lagrange Point 0.01";
		config.width = 1024;
		config.height = 720;

		config.resizable = false;

		PrintStream backup = System.out;

		try {
		    if (Files.exists(Paths.get("log.txt"))) Files.delete(Paths.get("log.txt"));

		    PrintStream p = new PrintStream(new BufferedOutputStream(new FileOutputStream("log.txt")));
			System.setOut(p);
			System.setErr(p);
		} catch (IOException e) {
			System.setOut(backup);
			e.printStackTrace();
		}

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Dumping logs...");
            System.out.close();
        }));

		System.out.println("Starting " + config.title);

		new LwjglApplication(new Window(), config);
	}
}
