package com.me.submarine;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class Main {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Submarine";
		cfg.useGL20 = true;
		cfg.width = 1280;
		cfg.height = 800;
		
		new LwjglApplication(new Submarine(), cfg);
	}
}
