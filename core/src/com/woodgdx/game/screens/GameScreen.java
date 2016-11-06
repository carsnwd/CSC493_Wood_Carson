package com.woodgdx.game.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.woodgdx.game.WorldController;
import com.woodgdx.game.WorldRenderer;
import com.woodgdx.game.util.GamePreferences;

/**
 * GameScreen for our game.
 * Serving a lot of the same purposes
 * that our mainbunny class did.
 * @author carson
 *
 */
public class GameScreen extends AbstractGameScreen
{
    private static final String TAG = GameScreen.class.getName();

    private WorldController worldController;

    private WorldRenderer worldRenderer;

    private boolean paused;

    public GameScreen(Game game)
    {
        super(game);
    }

    /**
     * Draws game.
     */
    @Override
    public void render(float deltaTime)
    {
        // Do not update game world when paused.
        if (!paused)
        {
            // Update game world by the time that has passed
            // since last rendered frame.
            worldController.update(deltaTime);
        }
        // Sets the clear screen color to: Cornflower Blue
        Gdx.gl.glClearColor(0x64 / 255.0f, 0x95 / 255.0f, 0xed / 255.0f, 0xff / 255.0f);
        // Clears the screen
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        // Render game world to screen
        worldRenderer.render();
    }

    /**
     * Resize window
     */
    @Override
    public void resize(int width, int height)
    {
        worldRenderer.resize(width, height);
    }

    /**
     * Acts as create() method in
     * our main class.
     */
    @Override
    public void show()
    {
        GamePreferences.instance.load();
        worldController = new WorldController(game);
        worldRenderer = new WorldRenderer(worldController);
        Gdx.input.setCatchBackKey(true);
    }

    /**
     * Acts as dispose() method
     * in main class.
     */
    @Override
    public void hide()
    {
        worldController.dispose();
        worldRenderer.dispose();
        Gdx.input.setCatchBackKey(false);
    }

    /**
     * Pause game
     */
    @Override
    public void pause()
    {
        paused = true;
    }

    /**
     * Resume froma pause
     */
    @Override
    public void resume()
    {
        super.resume();
        // Only called on Android!
        paused = false;
    }
}