package com.woodgdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
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

    private OrthographicCamera cameraGUI;

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
        cameraGUI = new OrthographicCamera(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);
        cameraGUI.position.set(0, 0, 0);
        cameraGUI.setToOrtho(true); // flip y-axis
        cameraGUI.update();
    }

    /**
     * Renders the GUI score in the upper left
     * @param batch
     */
    private void renderGuiScore(SpriteBatch batch)
    {
        float x = -15;
        float y = -15;
        batch.draw(Assets.instance.bone.bone, x, y, 50, 50, 100, 100, 0.35f, -0.35f, 0);
        Assets.instance.fonts.defaultBig.draw(batch, "" + worldController.score, x + 75, y + 37);
    }

    /**
     * Renders the GUI lives in the upper right
     * @param batch
     */
    private void renderGuiExtraLive(SpriteBatch batch)
    {
        float x = cameraGUI.viewportWidth - 50 - Constants.LIVES_START * 50;
        float y = -15;
        for (int i = 0; i < Constants.LIVES_START; i++)
        {
            if (worldController.lives <= i)
                batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
            batch.draw(Assets.instance.main_character.main_character, x + i * 50, y, 50, 50, 120, 100, 0.35f, -0.35f, 0);
            batch.setColor(1, 1, 1, 1);
        }
    }

    /**
     * Renders the GUIs FPS counter showing
     * the frames per second in the bottom
     * right corner.
     * @param batch
     */
    private void renderGuiFpsCounter(SpriteBatch batch)
    {
        float x = cameraGUI.viewportWidth - 55;
        float y = cameraGUI.viewportHeight - 15;
        int fps = Gdx.graphics.getFramesPerSecond();
        BitmapFont fpsFont = Assets.instance.fonts.defaultNormal;
        if (fps >= 45)
        {
            // 45 or more FPS show up in green
            fpsFont.setColor(0, 1, 0, 1);
        }
        else if (fps >= 30)
        {
            // 30 or more FPS show up in yellow
            fpsFont.setColor(1, 1, 0, 1);
        }
        else
        {
            // less than 30 FPS show up in red
            fpsFont.setColor(1, 0, 0, 1);
        }
        fpsFont.draw(batch, "FPS: " + fps, x, y);
        fpsFont.setColor(1, 1, 1, 1); // white
    }

    /**
     * Finds center of game window
     * and prints game over message
     * @param batch
     */
    private void renderGuiGameOverMessage(SpriteBatch batch)
    {
        float x = cameraGUI.viewportWidth / 2;
        float y = cameraGUI.viewportHeight / 2;
        if (worldController.isGameOver())
        {
            BitmapFont fontGameOver = Assets.instance.fonts.defaultBig;
            fontGameOver.setColor(1, 0.75f, 0.25f, 1);
            fontGameOver.draw(batch, "GAME OVER", x, y, 0, Align.center, false);
            fontGameOver.setColor(1, 1, 1, 1);
        }

    }

    /**
     * Renders the GUI camera and elements
     * @param batch
     */
    private void renderGui(SpriteBatch batch)
    {
        batch.setProjectionMatrix(cameraGUI.combined);
        batch.begin();
        // draw collected gold coins icon + text
        // (anchored to top left edge)
        renderGuiScore(batch);
        // draw collected feather icon (anchored to top left edge)
        renderGuiDogFoodPowerup(batch);
        // draw extra lives icon + text (anchored to top right edge)
        renderGuiExtraLive(batch);
        // draw FPS text (anchored to bottom right edge)
        renderGuiFpsCounter(batch);
        // draw game over text
        renderGuiGameOverMessage(batch);
        batch.end();
    }

    /**
     * Checks if there is still time on the power up
     * and will display remaining time. 
     * @param batch
     */
    private void renderGuiDogFoodPowerup(SpriteBatch batch)
    {
        float x = -15;
        float y = 30;
        float timeLeftDogFoodPowerup = worldController.level.mainChar.timeLeftDogFoodPowerup;
        if (timeLeftDogFoodPowerup > 0)
        {
            // Start icon fade in/out if the left power-up time
            // is less than 4 seconds. The fade interval is set
            // to 5 changes per second.
            if (timeLeftDogFoodPowerup < 4)
            {
                if (((int) (timeLeftDogFoodPowerup * 5) % 2) != 0)
                {
                    batch.setColor(1, 1, 1, 0.5f);
                }
            }
            batch.draw(Assets.instance.dog_food_bowl.dogFoodBowl, x, y, 50, 50, 100, 100, 0.35f, -0.35f, 0);
            batch.setColor(1, 1, 1, 1);
            Assets.instance.fonts.defaultSmall.draw(batch, "" + (int) timeLeftDogFoodPowerup, x + 60, y + 57);
        }
    }

    /**
     * Renders game objects (which in turn
     * calls the render method of level)
     */
    public void render()
    {
        renderWorld(batch);
        renderGui(batch);
    }

    /**
     * Renders the world now
     * @param batch
     */
    private void renderWorld(SpriteBatch batch)
    {
        worldController.cameraHelper.applyTo(camera);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        worldController.level.render(batch);
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
        cameraGUI.viewportHeight = Constants.VIEWPORT_GUI_HEIGHT;
        cameraGUI.viewportWidth = (Constants.VIEWPORT_GUI_HEIGHT / (float) height) * (float) width;
        cameraGUI.position.set(cameraGUI.viewportWidth / 2, cameraGUI.viewportHeight / 2, 0);
        cameraGUI.update();
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
