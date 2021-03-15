package com.forgestorm.bitmasking.demo.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.forgestorm.bitmasking.demo.MapRenderer;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = MapRenderer.WINDOW_SIZE;
        config.height = MapRenderer.WINDOW_SIZE;
        new LwjglApplication(new MapRenderer(), config);
    }
}
