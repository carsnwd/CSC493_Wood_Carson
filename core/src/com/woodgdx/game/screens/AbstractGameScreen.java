package com.woodgdx.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.woodgdx.game.Assets;

/**
 * Abstract class in which the menu and game
 * screen will be based on
 * @author carson
 *
 */
public abstract class AbstractGameScreen implements Screen
{
    //To access setScreen();
    protected Game game;

    public AbstractGameScreen(Game game)
    {
        this.game = game;
    }
    
    /**
     * Renders screen
     */
    public abstract void render (float deltaTime);
    /**
     * Resizes the screen
     */
    public abstract void resize (int width, int height);
    /**
     * Show the screen (switch between menu, game, etc)
     */
    public abstract void show();
    /**
     * Hide the screen (switch between menu, game, etc)
     */
    public abstract void hide();
    public abstract void pause();

    public void resume()
    {
        Assets.instance.init(new AssetManager());
    }

    public void dispose()
    {
        Assets.instance.dispose();
    }
}