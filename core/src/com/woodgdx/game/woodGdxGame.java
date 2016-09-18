package com.woodgdx.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.assets.AssetManager;
import com.woodgdx.game.Assets;


/**
 * The main game file with the main
 * engine methods
 * @author carson
 */
public class woodGdxGame implements ApplicationListener
{
    private static final String TAG = woodGdxGame.class.getName();

    private WorldController worldController;

    private WorldRenderer worldRenderer;

    /**
     * Creates game.
     */
    @Override
    public void create()
    {
        //Set LIbGDX log level to DEBUG so it prints to console.
        Gdx.app.setLogLevel(Application.LOG_DEBUG); //LOG_NONE cancels it.
        // Load assets
        Assets.instance.init(new AssetManager());
        //Initialize controler and renderer
        worldController = new WorldController();
        worldRenderer = new WorldRenderer(worldController);
    }

    /**
     * Window size
     */
    @Override
    public void resize(int width, int height)
    {
        worldRenderer.resize(width, height);

    }

    /**
     * Renders the assets
     */
    @Override
    public void render()
    {
        //Update game world by the time that has passed
        //since last rendered frame.
        worldController.update(Gdx.graphics.getDeltaTime());
        //Sets the clear screen color to Cornflower Blue
        Gdx.gl.glClearColor(0x64 / 255.0f, 0x95 / 255.0f, 0xed / 255.0f, 0xff / 255.0f);
        //Clears the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //Render game world to screen
        worldRenderer.render();
    }

    /**
     * For mobile, we don't use.
     */
    @Override
    public void pause()
    {
        // TODO Auto-generated method stub

    }

    /**
     * For mobile, we don't use.
     */
    @Override
    public void resume()
    {
        // TODO Auto-generated method stub

    }

    /**
     * Memory management
     */
    @Override
    public void dispose()
    {
        worldRenderer.dispose();
        Assets.instance.dispose();
    }
}
