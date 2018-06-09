package com.afts.editor.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.afts.editor.Core;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "AFTS Level Editor";
		config.width = 1280;
		config.samples = 4;
		config.height = 720;
		config.resizable = false;
		config.foregroundFPS = 60;
		new LwjglApplication(new Core(), config);
	}
}
