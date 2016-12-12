package com.woodgdx.game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.woodgdx.game.util.CameraHelper;
import com.woodgdx.game.util.CollisionHandler;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.woodgdx.game.objects.Ground;
import com.woodgdx.game.util.Constants;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.woodgdx.game.objects.AbstractGameObject;
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
    //For printing out to console
    private static final String TAG = WorldController.class.getName();

    //Constant value for checking bone total to progress game
    private static final int TOTAL_BONES = 10;

    //Count for bones in the current level
    private int boneCount = 0;

    //Current level 
    private String currentLevel = Constants.LEVEL_01;

    //Reference to level
    public Level level;

    //Reference to game
    private Game game;

    //Current lives
    public int lives;

    //Current score
    public int score;

    //Delay for when to respawn.
    private float timeLeftGameOverDelay;

    //Tracks lives for GUI animation of lost life
    public float livesVisual;

    //Objects to remove as they are collected
    public Array<AbstractGameObject> objectsToRemove;

    // Box2D Collisions
    public World myWorld;

    //Camera helper
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
     * Initializes level
     */
    private void initLevel()
    {
        score = 0;
        level = new Level(currentLevel);
        cameraHelper.setTarget(level.mainChar);
        initPhysics();
    }

    /**
     * Initializes physics in world
     */
    private void initPhysics()
    {
        if (myWorld != null)
            myWorld.dispose();
        myWorld = new World(new Vector2(0, -9.81f), true);
        myWorld.setContactListener(new CollisionHandler(this));
        Vector2 origin = new Vector2();
        for (Ground pieceOfGround : level.rocks)
        {
            BodyDef bodyDef = new BodyDef();
            bodyDef.position.set(pieceOfGround.position);
            bodyDef.type = BodyType.KinematicBody;
            Body body = myWorld.createBody(bodyDef);
            //body.setType(BodyType.DynamicBody);
            body.setUserData(pieceOfGround);
            pieceOfGround.body = body;
            PolygonShape polygonShape = new PolygonShape();
            origin.x = pieceOfGround.bounds.width / 2.0f;
            origin.y = pieceOfGround.bounds.height / 2.0f;
            polygonShape.setAsBox(pieceOfGround.bounds.width / 2.0f, (pieceOfGround.bounds.height - 0.04f) / 2.0f, origin, 0);
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = polygonShape;
            body.createFixture(fixtureDef);
            polygonShape.dispose();
        }

        // For PLayer
        MainChar player = level.mainChar;
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(player.position);
        bodyDef.fixedRotation = true;

        Body body = myWorld.createBody(bodyDef);
        body.setType(BodyType.DynamicBody);
        body.setGravityScale(0.0f);
        body.setUserData(player);
        player.body = body;

        PolygonShape polygonShape = new PolygonShape();
        origin.x = (player.bounds.width) / 2.0f;
        origin.y = (player.bounds.height) / 2.0f;
        polygonShape.setAsBox((player.bounds.width - 0.7f) / 2.0f, (player.bounds.height - 0.15f) / 2.0f, origin, 0);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        // fixtureDef.friction = 0.5f;
        body.createFixture(fixtureDef);
        polygonShape.dispose();
    }

    /**
     * Basically the constructors
     */
    // Outside of constructor so we don't reset the object too much.
    private void init()
    {
        objectsToRemove = new Array<AbstractGameObject>();
        Gdx.input.setInputProcessor(this);
        cameraHelper = new CameraHelper();
        lives = Constants.LIVES_START;
        livesVisual = lives;
        timeLeftGameOverDelay = 0;
        initLevel();
    }

    /**
     * Level creation (don't use?)
     * @param width
     * @param height
     * @return
     */
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
        //Box2D way of removing objects
        if (objectsToRemove.size > 0)
        {
            //Cycle through objects to check if collected
            for (AbstractGameObject obj : objectsToRemove)
            {
                if (obj instanceof Bone)
                {
                    int index = level.bones.indexOf((Bone) obj, true);
                    if (index != -1)
                    {
                        level.bones.removeIndex(index);
                        myWorld.destroyBody(obj.body);
                    }
                }
                else if (obj instanceof Cat)
                {
                    int index = level.cats.indexOf((Cat) obj, true);
                    if (index != -1)
                    {
                        level.cats.removeIndex(index);
                        myWorld.destroyBody(obj.body);
                    }
                }
                else if (obj instanceof Dog)
                {
                    int index = level.dogs.indexOf((Dog) obj, true);
                    if (index != -1)
                    {
                        level.dogs.removeIndex(index);
                        myWorld.destroyBody(obj.body);
                    }
                }
                else if (obj instanceof Chicken)
                {
                    int index = level.chickens.indexOf((Chicken) obj, true);
                    if (index != -1)
                    {
                        level.chickens.removeIndex(index);
                        myWorld.destroyBody(obj.body);
                    }
                }
                else if (obj instanceof DogFoodBowl)
                {
                    int index = level.dogFoodBowls.indexOf((DogFoodBowl) obj, true);
                    if (index != -1)
                    {
                        level.dogFoodBowls.removeIndex(index);
                        myWorld.destroyBody(obj.body);
                    }
                }
                else if (obj instanceof Flame)
                {
                    int index = level.flames.indexOf((Flame) obj, true);
                    if (index != -1)
                    {
                        level.flames.removeIndex(index);
                        myWorld.destroyBody(obj.body);
                    }
                }
            }
            objectsToRemove.removeRange(0, objectsToRemove.size - 1);
        }

        //CHecks if game is over
        handleDebugInput(deltaTime);
        if (isGameOver())
        {
            timeLeftGameOverDelay -= deltaTime;
            //If it is, then record the score and go back to the menu
            if (timeLeftGameOverDelay < 0)
            {
                try
                {
                    recordScore();
                }
                catch (IOException e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                backToMenu();
            }
        }
        //Otherwise keep this disaster rolling!
        else
        {
            handleInputGame(deltaTime);
        }
        level.update(deltaTime);
        testCollisions();
        cameraHelper.update(deltaTime);
        //If not out of lives and in water, respawn and record lives lost.
        if (!isGameOver() && isPlayerInWater())
        {
            AudioManager.instance.play(Assets.instance.sounds.liveLost);
            lives--;
            if (isGameOver())
                timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
            else
                initLevel();
        }//Updates GUI lives
        if (livesVisual > lives)
            livesVisual = Math.max(lives, livesVisual - 1 * deltaTime);

        myWorld.step(deltaTime, 8, 3); // Tell the Box2D world to update.
        level.update(deltaTime);
        checkForCollisions();
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
        if (bone.collected)
        {
            Gdx.app.log(TAG, "boneCount: " + this.boneCount);
            Gdx.app.log(TAG, "TOTAL BONES: " + this.TOTAL_BONES);
            if (this.boneCount == TOTAL_BONES)
            {
                this.boneCount = 0;
                if (this.currentLevel.equals(Constants.LEVEL_01))
                {
                    try
                    {
                        recordScore();
                    }
                    catch (IOException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    this.currentLevel = Constants.LEVEL_02;
                    initLevel();
                }
                else
                {
                    try
                    {
                        recordScore();
                    }
                    catch (IOException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    backToMenu();
                }
            }
            else
            {
                //Gdx.app.log(TAG, "boneCount: " + this.boneCount);
                // Gdx.app.log(TAG, "boneCount: " + this.TOTAL_BONES);
                this.boneCount++;
            }
        }
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
            {
                continue;
            }
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
            MainChar mainChar = level.mainChar;
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

    /**
     * Removes objects when collected
     * @param obj
     */
    public void flagForRemoval(AbstractGameObject obj)
    {
        objectsToRemove.add(obj);
    }

    /**
     * Records the high score.
     * @throws IOException 
     */
    public void recordScore() throws IOException
    {
        File highScoreFile = new File("score.txt");
        FileWriter fw = new FileWriter(highScoreFile, true);
        fw.write(score + "" + "\n");
        fw.close();

        ArrayList<String> rows = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new FileReader("score.txt"));

        String s;
        while ((s = reader.readLine()) != null)
            rows.add(s);

        Collections.sort(rows);
        Collections.reverse(rows);

        FileWriter writer = new FileWriter("score.txt");
        for (String cur : rows)
            writer.write(cur + "\n");

        reader.close();
        writer.close();
    }

    /**
     * Checks for collisions with Box2D
     */
    private void checkForCollisions()
    {
        r1.set(level.mainChar.position.x, level.mainChar.position.y, level.mainChar.bounds.width, level.mainChar.bounds.height);

        for (Ground g : level.rocks)
        {
            r2.set(g.position.x, g.position.y, g.bounds.width, g.bounds.height);
            if (!r1.overlaps(r2))
                continue;
            onCollisionmaincharWithRock(g);
        }
    }
}
