package com.kti.lagrange.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.kti.lagrange.Window;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class DesktopLauncher extends JFrame implements ActionListener {
	private static final int width = (int)Toolkit.getDefaultToolkit().getScreenSize().getWidth();
	private static final int height = (int)Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	private static final String[] resolutions = {"1920x1080", "1280x720", "1024x720", "800x640", "custom"};
	private static final String version = "0.02 - \"Elipson\"";

	private JComboBox resolutionSelector;
	private JCheckBox fullscreenBox;
	private JCheckBox vsyncBox;
	private JButton launchButton;

	public DesktopLauncher() {
		super("Launcher No. VII");

		resolutionSelector = new JComboBox();
		resolutionSelector.addItem(width + "x" + height);

		for (int i = 0; i < resolutions.length; i++) {
			if (!resolutions[i].equals(width + "x" + height))
				resolutionSelector.addItem(resolutions[i]);
		}

		resolutionSelector.addActionListener(this);
		resolutionSelector.setVisible(true);

		fullscreenBox = new JCheckBox();
		fullscreenBox.setSelected(true);
		vsyncBox = new JCheckBox();

		launchButton = new JButton("Ad Terram");
		launchButton.setActionCommand("Launch");
		launchButton.addActionListener(this);

		getContentPane().setLayout(new FlowLayout());
		getContentPane().add(new JLabel("Resolution: "));
		getContentPane().add(resolutionSelector);

		try {
			BufferedImage myPicture = ImageIO.read(new File("assets/LagrangePointLogo128x128.png"));
			JLabel picLabel = new JLabel(new ImageIcon(myPicture));
			getContentPane().add(picLabel);
		} catch (IOException e) {
			e.printStackTrace();
		}

		getContentPane().add(new JLabel("Fullscreen: "));
		getContentPane().add(fullscreenBox);
		getContentPane().add(new JLabel("Vsync: "));
		getContentPane().add(vsyncBox);
		getContentPane().add(launchButton);

		setIconImage(new ImageIcon("assets/LagrangePointLogo128x128.png").getImage());

		setSize(200, 270);
		setLocation(width / 2 - 110, height / 2 - 60);
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}


	public void launch() {
		System.out.println("Version: " + version);
		System.out.println("Starting on: " + System.getProperty("os.name"));

		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.title = "Lagrange Point " + version;

		if (resolutionSelector.getSelectedItem().equals("custom")) {
			String name;
			while (!canSplit(name = JOptionPane.showInputDialog(null, "Enter Resolution:"))) { }

			String[] res = name.split("x");
			config.width = Integer.parseInt(res[0]);
			config.height = Integer.parseInt(res[1]);
		} else {
			String[] res = ((String) resolutionSelector.getSelectedItem()).split("x");
			config.width = Integer.parseInt(res[0]);
			config.height = Integer.parseInt(res[1]);
		}
		config.fullscreen = fullscreenBox.isSelected();
		if (!fullscreenBox.isSelected())
			config.resizable = false;
		config.vSyncEnabled = vsyncBox.isSelected();
		config.backgroundFPS = 60;
		config.foregroundFPS = 60;

		if (System.getProperty("os.name").contains("Linux") || System.getProperty("os.name").contains("Win"))
			config.addIcon("assets/LagrangePointLogo128x128.png", com.badlogic.gdx.Files.FileType.Absolute);

		new LwjglApplication(new Window(), config);
	}

	private boolean canSplit(String name) {
		try {
			String[] res = name.split("x");
			if (res.length > 2 || res.length < 1) return false;
			Integer.parseInt(res[0]);
			Integer.parseInt(res[1]);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("Launch")) launch();
	}

	public static void main(String[] arg) {
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

		DesktopLauncher d = new DesktopLauncher();
	}
}
