package com.woodgdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.woodgdx.game.woodGdxGame;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

/**
 * The launcher for the game. 
 * @author carson
 *
 */
public class DesktopLauncher
{
    //TexturePacker related
    private static boolean rebuildAtlas = false; //CHANGE WHEN YOU HAVE NEW ASSETS

    private static boolean drawDebugOutline = false;

    public static void main(String[] arg)
    {
        if (rebuildAtlas)
        {
            Settings settings = new Settings();
            settings.maxWidth = 1024;
            settings.maxHeight = 1024;
            settings.duplicatePadding = false;
            settings.debug = drawDebugOutline;
            TexturePacker.process(settings, "assets-raw/images", "../core/assets", "canyonbunny.pack");
            TexturePacker.process(settings, "assets-raw/images", "../core/assets", "canyonbunny-ui.pack");
        }

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        new LwjglApplication(new woodGdxGame(), config);
    }
}
