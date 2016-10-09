package com.woodgdx.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.woodgdx.game.Assets;
import com.woodgdx.game.screens.MenuScreen;

/**
 * The main game file with the main
 * engine methods
 * @author carson
 */
public class woodGdxGame extends Game
{
    @Override
    public void create()
    {
        // Set Libgdx log level 
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        // Load assets
        Assets.instance.init(new AssetManager());
        // Start game at menu screen
        setScreen(new MenuScreen(this));
    }
}