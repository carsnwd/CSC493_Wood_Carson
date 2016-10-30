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
import com.woodgdx.game.objects.Ground;
import com.woodgdx.game.util.Constants;
import com.badlogic.gdx.math.Rectangle;
import com.woodgdx.game.objects.Cat;
import com.woodgdx.game.objects.Chicken;
import com.woodgdx.game.objects.Dog;
import com.woodgdx.game.objects.Flame;
import com.woodgdx.game.objects.MainChar;
import com.woodgdx.game.objects.MainChar.JUMP_STATE;
import com.woodgdx.game.objects.DogFoodBowl;
import com.woodgdx.game.objects.Bone;
import com.woodgdx.game.objects.Ground;
import com.badlogic.gdx.Game;
import com.woodgdx.game.screens.MenuScreen;
import com.woodgdx.game.util.AudioManager;

public class WorldController extends InputAdapter
{
    private static final String TAG = WorldController.class.getName();

    public Level level;

    private Game game;

    //Current lives
    public int lives;

    //Current score
    public int score;

    private float timeLeftGameOverDelay;

    //Tracks lives for GUI animation of lost life
    public float livesVisual;

    /**
     * Initializes level
     */
    private void initLevel()
    {
        score = 0;
        level = new Level(Constants.LEVEL_01);
        cameraHelper.setTarget(level.mainChar);
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
        livesVisual = lives;
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
        if (!isGameOver() && isPlayerInWater())
        {
            AudioManager.instance.play(Assets.instance.sounds.liveLost);
            lives--;
            if (isGameOver())
                timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
            else
                initLevel();
        }
        if (livesVisual > lives)
            livesVisual = Math.max(lives, livesVisual - 1 * deltaTime);
    }

    /**
     * Input handler
     * @param deltaTime
     */
    private void handleDebugInput(float deltaTime)
    {
        if (Gdx.app.getType() != ApplicationType.Desktop)
            return;
        if (!cameraHelper.hasTarget(level.mainChar))
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
        }

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
        // Toggle camera follow (mainchar or free camera)
        else if (keycode == Keys.ENTER)
        {
            cameraHelper.setTarget(cameraHelper.hasTarget() ? null : level.mainChar);
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
     * When the rock and mainchar collide
     * Prevents mainchar  from falling through the rock
     * @param ground
     */
    private void onCollisionmaincharWithRock(Ground ground)
    {
        MainChar mainchar = level.mainChar;
        float heightDifference = Math.abs(mainchar.position.y - (ground.position.y + ground.bounds.height));
        if (heightDifference > 0.25f)
        {
            boolean hitRightEdge = mainchar.position.x > (ground.position.x + ground.bounds.width / 2.0f);
            if (hitRightEdge)
            {
                mainchar.position.x = ground.position.x + ground.bounds.width;
            }
            else
            {
                mainchar.position.x = ground.position.x - mainchar.bounds.width;
            }
            return;
        }
        switch (mainchar.jumpState)
        {
        case GROUNDED:
            break;
        case FALLING:
        case JUMP_FALLING:
            mainchar.position.y = ground.position.y + mainchar.bounds.height + mainchar.origin.y;
            mainchar.jumpState = JUMP_STATE.GROUNDED;
            break;
        case JUMP_RISING:
            mainchar.position.y = ground.position.y + mainchar.bounds.height + mainchar.origin.y;
            break;
        }
    }

    /**
     * When the main char and bone collide.
     * Increase score and makes bone invisible
     * @param bone
     */
    private void onCollisionMainCharWithBone(Bone bone)
    {
        bone.collected = true;
        AudioManager.instance.play(Assets.instance.sounds.pickupBone);
        score += bone.getScore();
        Gdx.app.log(TAG, "bone collected");
    }

    /**
     * When the main char and dog collide.
     * Increase score and makes bone invisible
     * @param bone
     */
    private void onCollisionMainCharWithDog(Dog dog)
    {
        dog.collected = true;
//        AudioManager.instance.play(Assets.instance.sounds.dogBark);
        score += dog.getScore();
        Gdx.app.log(TAG, "Dog collected");
    }

    /**
     * When the main char and chicken collide.
     * Increase score and makes bone invisible
     * @param bone
     */
    private void onCollisionMainCharWithCat(Cat cat)
    {
        cat.collected = true;
//        AudioManager.instance.play(Assets.instance.sounds.catMeow);
        score += cat.getScore();
        Gdx.app.log(TAG, "Cat collected");
    }

    /**
     * When the main char and chicken collide.
     * Increase score and makes bone invisible
     * @param bone
     */
    private void onCollisionMainCharWithChicken(Chicken chicken)
    {
        chicken.collected = true;
//        AudioManager.instance.play(Assets.instance.sounds.chicken);
        score += chicken.getScore();
        Gdx.app.log(TAG, "Chicken collected");
    }

    /**
     * When the main char and flame collide.
     * Increase score and makes bone invisible
     * @param bone
     */
    private void onCollisionMainCharWithFlame(Flame flame)
    {
        flame.collected = true;
        AudioManager.instance.play(Assets.instance.sounds.flame);
        score += flame.getScore();
        Gdx.app.log(TAG, "Flame collected");
    }

    /**
     * When the mainchar and dogfoodbowl collide
     * Increase score and makes dogfoodbowl invisible
     * @param dogFoodBowl
     */
    private void onCollisionMainCharWithDogFoodBowl(DogFoodBowl dogFoodBowl)
    {
        dogFoodBowl.collected = true;
        AudioManager.instance.play(Assets.instance.sounds.pickupFoodBowl);
        score += dogFoodBowl.getScore();
        level.mainChar.setDogFoodPowerup(true);
        Gdx.app.log(TAG, "dogfoodbowl collected");
    }

    /**
     * Tests if there is a collision with each object and the mainchar 
     */
    private void testCollisions()
    {
        r1.set(level.mainChar.position.x, level.mainChar.position.y, level.mainChar.bounds.width, level.mainChar.bounds.height);
        // Test collision: mainchar  <-> Rocks
        for (Ground ground : level.rocks)
        {
            r2.set(ground.position.x, ground.position.y, ground.bounds.width, ground.bounds.height);
            if (!r1.overlaps(r2))
                continue;
            onCollisionmaincharWithRock(ground);
            // IMPORTANT: must do all collisions for valid
            // edge testing on rocks.
        }
        // Test collision: mainchar  <-> bones
        for (Bone bone : level.bones)
        {
            if (bone.collected)
                continue;
            r2.set(bone.position.x, bone.position.y, bone.bounds.width, bone.bounds.height);
            if (!r1.overlaps(r2))
                continue;
            onCollisionMainCharWithBone(bone);
            break;
        }
        // Test collision: mainchar  <-> dogs
        for (Dog dog : level.dogs)
        {
            if (dog.collected)
                continue;
            r2.set(dog.position.x, dog.position.y, dog.bounds.width, dog.bounds.height);
            if (!r1.overlaps(r2))
                continue;
            onCollisionMainCharWithDog(dog);
            break;
        }
        // Test collision: mainchar  <-> cats
        for (Cat cat : level.cats)
        {
            if (cat.collected)
                continue;
            r2.set(cat.position.x, cat.position.y, cat.bounds.width, cat.bounds.height);
            if (!r1.overlaps(r2))
                continue;
            onCollisionMainCharWithCat(cat);
            break;
        }
        // Test collision: mainchar  <-> chickens
        for (Chicken chicken : level.chickens)
        {
            if (chicken.collected)
                continue;
            r2.set(chicken.position.x, chicken.position.y, chicken.bounds.width, chicken.bounds.height);
            if (!r1.overlaps(r2))
                continue;
            onCollisionMainCharWithChicken(chicken);
            break;
        }
        // Test collision: mainchar  <-> flames
        for (Flame flame : level.flames)
        {
            if (flame.collected)
                continue;
            r2.set(flame.position.x, flame.position.y, flame.bounds.width, flame.bounds.height);
            if (!r1.overlaps(r2))
                continue;
            onCollisionMainCharWithFlame(flame);
            break;
        }
        // Test collision: mainchar  <-> dogfoodbowls
        for (DogFoodBowl dogFoodBowl : level.dogFoodBowls)
        {
            if (dogFoodBowl.collected)
                continue;
            r2.set(dogFoodBowl.position.x, dogFoodBowl.position.y, dogFoodBowl.bounds.width, dogFoodBowl.bounds.height);
            if (!r1.overlaps(r2))
                continue;
            onCollisionMainCharWithDogFoodBowl(dogFoodBowl);
            break;
        }
    }

    /**
     * Handles the input for the game
     * Movement of mainchar  in particular
     * @param deltaTime
     */
    private void handleInputGame(float deltaTime)
    {
        if (cameraHelper.hasTarget(level.mainChar))
        {
            // Player Movement
            if (Gdx.input.isKeyPressed(Keys.LEFT))
            {
                level.mainChar.velocity.x = -level.mainChar.terminalVelocity.x;
            }
            else if (Gdx.input.isKeyPressed(Keys.RIGHT))
            {
                level.mainChar.velocity.x = level.mainChar.terminalVelocity.x;
            }
            else
            {
                // Execute auto-forward movement on non-desktop platform
                if (Gdx.app.getType() != ApplicationType.Desktop)
                {
                    level.mainChar.velocity.x = level.mainChar.terminalVelocity.x;
                }
            }
            // mainchar Jump
            if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Keys.SPACE))
            {
                level.mainChar.setJumping(true);
            }
            else
            {
                level.mainChar.setJumping(false);
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
        return level.mainChar.position.y < -5;
    }

    /**
     * Switch back and forth from game to menu
     */
    private void backToMenu()
    {
        // switch to menu screen
        game.setScreen(new MenuScreen(game));
    }

}
