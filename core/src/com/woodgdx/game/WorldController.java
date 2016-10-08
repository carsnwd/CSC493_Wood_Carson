package com.woodgdx.game;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.woodgdx.game.util.CameraHelper;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.woodgdx.game.objects.Rock;
import com.woodgdx.game.util.Constants;
import com.badlogic.gdx.math.Rectangle;
import com.woodgdx.game.objects.BunnyHead;
import com.woodgdx.game.objects.BunnyHead.JUMP_STATE;
import com.woodgdx.game.objects.Feather;
import com.woodgdx.game.objects.GoldCoin;
import com.woodgdx.game.objects.Rock;
import com.badlogic.gdx.Game;
import com.woodgdx.game.screens.MenuScreen;
import com.woodgdx.game.util.GamePreferences;

public class WorldController extends InputAdapter
{
    private static final String TAG = WorldController.class.getName();

    public Level level;

    //Current lives
    public int lives;

    //Current score
    public int score;

    //Time left after game over delay
    private float timeLeftGameOverDelay;

    //Instance of Game, switch between screens.
    private Game game;

    /**
     * Initializes level
     */
    private void initLevel()
    {
        score = 0;
        level = new Level(Constants.LEVEL_01);
        cameraHelper.setTarget(level.bunnyHead); //Camera follows bunny head
    }

    public CameraHelper cameraHelper;

    /**
     * Constructor that calls on init()
     */
    public WorldController(Game game)
    {
        this.game = game;
        init();
    }

    /**
     * Basically the constructors
     */
    // Outside of constructor so we don't reset the object too much.
    private void init()
    {
        Gdx.input.setInputProcessor(this);
        cameraHelper = new CameraHelper();
        lives = Constants.LIVES_START;
        timeLeftGameOverDelay = 0;
        initLevel();
    }

    private Pixmap createProceduralPixmap(int width, int height)
    {
        Pixmap pixmap = new Pixmap(width, height, Format.RGBA8888);
        // Fill square with red color at 50% opacity
        pixmap.setColor(1, 0, 0, 0.5f);
        pixmap.fill();
        // Draw a yellow-colored X shape on square
        pixmap.setColor(1, 1, 0, 1);
        pixmap.drawLine(0, 0, width, height);
        pixmap.drawLine(width, 0, 0, height);
        // Draw a cyan-colored border around square
        pixmap.setColor(0, 1, 1, 1);
        pixmap.drawRectangle(0, 0, width, height);
        return pixmap;
    }

    /**
     * Contains game logic, called on quite a bit.
     * 
     * @param delataTime
     *            //so it can stay with game time
     */
    public void update(float deltaTime)
    {
        handleDebugInput(deltaTime);
        //If the game is over
        if (isGameOver())
        {
            timeLeftGameOverDelay -= deltaTime;
            if (timeLeftGameOverDelay < 0)
                backToMenu();
        }
        else
        {
            handleInputGame(deltaTime);
        }
        level.update(deltaTime);
        testCollisions();
        cameraHelper.update(deltaTime);
        //Takes away lives when fallen in water
        if (!isGameOver() && isPlayerInWater())
        {
            lives--;
            if (isGameOver())
                timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
            else
                initLevel();
        }
    }

    /**
     * Input handler
     * @param deltaTime
     */
    private void handleDebugInput(float deltaTime)
    {
        if (Gdx.app.getType() != ApplicationType.Desktop)
            return;
        if (!cameraHelper.hasTarget(level.bunnyHead))
        {
            // Camera Controls (move)
            float camMoveSpeed = 5 * deltaTime;
            float camMoveSpeedAccelerationFactor = 5;
            if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
                camMoveSpeed *= camMoveSpeedAccelerationFactor;
            if (Gdx.input.isKeyPressed(Keys.LEFT))
                moveCamera(-camMoveSpeed, 0);
            if (Gdx.input.isKeyPressed(Keys.RIGHT))
                moveCamera(camMoveSpeed, 0);
            if (Gdx.input.isKeyPressed(Keys.UP))
                moveCamera(0, camMoveSpeed);
            if (Gdx.input.isKeyPressed(Keys.DOWN))
                moveCamera(0, -camMoveSpeed);
            if (Gdx.input.isKeyPressed(Keys.BACKSPACE))
                cameraHelper.setPosition(0, 0);

            // Camera Controls (zoom)
            float camZoomSpeed = 1 * deltaTime;
            float camZoomSpeedAccelerationFactor = 5;
            if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
                camZoomSpeed *= camZoomSpeedAccelerationFactor;
            if (Gdx.input.isKeyPressed(Keys.COMMA))
                cameraHelper.addZoom(camZoomSpeed);
            if (Gdx.input.isKeyPressed(Keys.PERIOD))
                cameraHelper.addZoom(-camZoomSpeed);
            if (Gdx.input.isKeyPressed(Keys.SLASH))
                cameraHelper.setZoom(1);
        }
    }

    /**
     * Moves the camera/fov
     * @param x
     * @param y
     */
    private void moveCamera(float x, float y)
    {
        x += cameraHelper.getPosition().x;
        y += cameraHelper.getPosition().y;
        cameraHelper.setPosition(x, y);
    }

    /**
     * Responds to various keys with debug
     */
    @Override
    public boolean keyUp(int keycode)
    {
        // Reset game world
        if (keycode == Keys.R)
        {
            init();
            Gdx.app.debug(TAG, "Game world resetted");
        }
        // Toggle camera follow (bunny head or free camera)
        else if (keycode == Keys.ENTER)
        {
            cameraHelper.setTarget(cameraHelper.hasTarget() ? null : level.bunnyHead);
            Gdx.app.debug(TAG, "Camera follow enabled: " + cameraHelper.hasTarget());
        }
        // Back to Menu
        else if (keycode == Keys.ESCAPE || keycode == Keys.BACK)
        {
            backToMenu();
        }
        return false;
    }

    // Rectangles for collision detection
    private Rectangle r1 = new Rectangle();

    private Rectangle r2 = new Rectangle();

    /**
     * When the rock and bunny collide
     * Prevents bunny head from falling through the rock
     * @param rock
     */
    private void onCollisionBunnyHeadWithRock(Rock rock)
    {
        BunnyHead bunnyHead = level.bunnyHead;
        float heightDifference = Math.abs(bunnyHead.position.y - (rock.position.y + rock.bounds.height));
        if (heightDifference > 0.25f)
        {
            boolean hitRightEdge = bunnyHead.position.x > (rock.position.x + rock.bounds.width / 2.0f);
            if (hitRightEdge)
            {
                bunnyHead.position.x = rock.position.x + rock.bounds.width;
            }
            else
            {
                bunnyHead.position.x = rock.position.x - bunnyHead.bounds.width;
            }
            return;
        }
        switch (bunnyHead.jumpState)
        {
        case GROUNDED:
            break;
        case FALLING:
        case JUMP_FALLING:
            bunnyHead.position.y = rock.position.y + bunnyHead.bounds.height + bunnyHead.origin.y;
            bunnyHead.jumpState = JUMP_STATE.GROUNDED;
            break;
        case JUMP_RISING:
            bunnyHead.position.y = rock.position.y + bunnyHead.bounds.height + bunnyHead.origin.y;
            break;
        }
    }

    /**
     * When the bunny and coin collide.
     * Increase score and makes coin invisible
     * @param goldcoin
     */
    private void onCollisionBunnyWithGoldCoin(GoldCoin goldcoin)
    {
        goldcoin.collected = true;
        score += goldcoin.getScore();
        Gdx.app.log(TAG, "Gold coin collected");
    }

    /**
     * When the bunny and feather collide
     * Increase score and makes feather invisible
     * @param feather
     */
    private void onCollisionBunnyWithFeather(Feather feather)
    {
        feather.collected = true;
        score += feather.getScore();
        level.bunnyHead.setFeatherPowerup(true);
        Gdx.app.log(TAG, "Feather collected");
    }

    /**
     * Tests if there is a collision with each object and the bunny head
     */
    private void testCollisions()
    {
        r1.set(level.bunnyHead.position.x, level.bunnyHead.position.y, level.bunnyHead.bounds.width, level.bunnyHead.bounds.height);
        // Test collision: Bunny Head <-> Rocks
        for (Rock rock : level.rocks)
        {
            r2.set(rock.position.x, rock.position.y, rock.bounds.width, rock.bounds.height);
            if (!r1.overlaps(r2))
                continue;
            onCollisionBunnyHeadWithRock(rock);
            // IMPORTANT: must do all collisions for valid
            // edge testing on rocks.
        }
        // Test collision: Bunny Head <-> Gold Coins
        for (GoldCoin goldcoin : level.goldcoins)
        {
            if (goldcoin.collected)
                continue;
            r2.set(goldcoin.position.x, goldcoin.position.y, goldcoin.bounds.width, goldcoin.bounds.height);
            if (!r1.overlaps(r2))
                continue;
            onCollisionBunnyWithGoldCoin(goldcoin);
            break;
        }
        // Test collision: Bunny Head <-> Feathers
        for (Feather feather : level.feathers)
        {
            if (feather.collected)
                continue;
            r2.set(feather.position.x, feather.position.y, feather.bounds.width, feather.bounds.height);
            if (!r1.overlaps(r2))
                continue;
            onCollisionBunnyWithFeather(feather);
            break;
        }
    }

    /**
     * Handles the input for the game
     * Movement of bunny head in particular
     * @param deltaTime
     */
    private void handleInputGame(float deltaTime)
    {
        if (cameraHelper.hasTarget(level.bunnyHead))
        {
            // Player Movement
            if (Gdx.input.isKeyPressed(Keys.LEFT))
            {
                level.bunnyHead.velocity.x = -level.bunnyHead.terminalVelocity.x;
            }
            else if (Gdx.input.isKeyPressed(Keys.RIGHT))
            {
                level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x;
            }
            else
            {
                // Execute auto-forward movement on non-desktop platform
                if (Gdx.app.getType() != ApplicationType.Desktop)
                {
                    level.bunnyHead.velocity.x = level.bunnyHead.terminalVelocity.x;
                }
            }
            // Bunny Jump
            if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Keys.SPACE))
            {
                level.bunnyHead.setJumping(true);
            }
            else
            {
                level.bunnyHead.setJumping(false);
            }
        }
    }

    /**
     * Checks if the player
     * has anymore lives
     * @return
     */
    public boolean isGameOver()
    {
        return lives < 0;
    }

    /**
     * If the player is at the bottom
     * of the screen, then it is under water
     * @return
     */
    public boolean isPlayerInWater()
    {
        return level.bunnyHead.position.y < -5;
    }

    /**
     * Goes back to the menu.
     */
    private void backToMenu()
    {
        // switch to menu screen
        game.setScreen(new MenuScreen(game));
    }
}
