package com.woodgdx.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.woodgdx.game.util.Constants;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Draws the world
 * @author carson
 *
 */
public class WorldRenderer implements Disposable
{
    private OrthographicCamera camera;

    private SpriteBatch batch;

    private WorldController worldController;

    /**
     * Constructor
     * @param worldController
     */
    public WorldRenderer(WorldController worldController)
    {
        this.worldController = worldController;
        init();
    }

    /**
     * Outside of constructor so
     * it does not get called over 
     * and over
     */
    private void init()
    {
        batch = new SpriteBatch();
        camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH, Constants.VIEWPORT_HEIGHT);
        camera.position.set(0, 0, 0);
        camera.update();
    }

    /**
     * Renders game objects
     */
    public void render()
    {
        renderTestObjects();
    }

    /**
     * Renders the test objects
     */
    private void renderTestObjects()
    {
        worldController.cameraHelper.applyTo(camera);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (Sprite sprite : worldController.testSprites)
        {
            sprite.draw(batch);
        }
        batch.end();
    }

    /**
     * Window resizing
     * @param width
     * @param height
     */
    public void resize(int width, int height)
    {
        camera.viewportWidth = (Constants.VIEWPORT_HEIGHT / height) * width;
        camera.update();
    }

    /**
     * Memory management
     */
    @Override
    public void dispose()
    {
        batch.dispose();
    }
}
