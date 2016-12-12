package com.woodgdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.woodgdx.game.objects.AbstractGameObject;
import com.woodgdx.game.objects.Cat;
import com.woodgdx.game.objects.Chicken;
import com.woodgdx.game.objects.Cloud_Decoration;
import com.woodgdx.game.objects.Dog;
import com.woodgdx.game.objects.Flame;
import com.woodgdx.game.objects.Background_Decoration;
import com.woodgdx.game.objects.Ground;
import com.woodgdx.game.objects.Water_Decoration;
import com.woodgdx.game.objects.MainChar;
import com.woodgdx.game.objects.DogFoodBowl;
import com.woodgdx.game.objects.Bone;

/**
 * Imports the level data
 * based on the image created
 * with the color values
 * @author carson
 *
 */
public class Level
{
    //For printing to console
    public static final String TAG = Level.class.getName();
    
    // Array containing all objects in level
    public Array<Ground> rocks;

    public MainChar mainChar;

    public Array<Bone> bones;

    public Array<Cat> cats;

    public Array<Dog> dogs;

    public Array<Chicken> chickens;

    public Array<DogFoodBowl> dogFoodBowls;

    public Array<Flame> flames;

    // decoration
    public Cloud_Decoration clouds;

    //Background
    public Background_Decoration background;

    //Water
    public Water_Decoration waterOverlay;

    /**
     * Colors stored as an enum type with
     * the RGBA values that is used to
     * identify each object.
     * @author carson
     *
     */
    public enum BLOCK_TYPE
    {
        EMPTY(0, 0, 0), // black
        ROCK(0, 255, 0), // green
        PLAYER_SPAWNPOINT(255, 255, 255), // white
        ITEM_FEATHER(255, 0, 255), // purple
        ITEM_GOLD_COIN(255, 255, 0), // yellow
        ITEM_CAT(0, 255, 255), //cyan
        ITEM_DOG(255, 0, 0), //red
        ITEM_CHICKEN(0, 0, 255), //blue
        ITEM_FLAME(100, 100, 100); //grey
        /**
         * Represented as an int as sum of RGBA values is 4 bytes.
         */
        private int color;

        private BLOCK_TYPE(int r, int g, int b)
        {
            color = r << 24 | g << 16 | b << 8 | 0xff;
        }

        /**
         * Finds out if the level grid color
         * is the same as current color.
         * @param color
         * @return
         */
        public boolean sameColor(int color)
        {
            return this.color == color;
        }

        public int getColor()
        {
            return color;
        }
    }

    /**
     * Constructor
     * @param filename
     */
    public Level(String filename)
    {
        init(filename);
    }

    /**
     * Starts from the top left corner of the 
     * level image and reads in each color
     * input for each object according to the 
     * BLOCK_TYPE enum
     * @param filename
     */
    private void init(String filename)
    {
        //player character
        mainChar = null;
        rocks = new Array<Ground>();
        bones = new Array<Bone>();
        cats = new Array<Cat>();
        dogs = new Array<Dog>();
        chickens = new Array<Chicken>();
        dogFoodBowls = new Array<DogFoodBowl>();
        flames = new Array<Flame>();
        // load image file that represents the level data
        Pixmap pixmap = new Pixmap(Gdx.files.internal(filename));
        // scan pixels from top-left to bottom-right
        int lastPixel = -1;
        for (int pixelY = 0; pixelY < pixmap.getHeight(); pixelY++)
        {
            for (int pixelX = 0; pixelX < pixmap.getWidth(); pixelX++)
            {
                AbstractGameObject obj = null;
                float offsetHeight = 0;
                // height grows from bottom to top
                float baseHeight = pixmap.getHeight() - pixelY;
                // get color of current pixel as 32-bit RGBA value
                int currentPixel = pixmap.getPixel(pixelX, pixelY);
                // find matching color value to identify block type at (x,y)
                // point and create the corresponding game object if there is
                // a match
                // empty space
                if (BLOCK_TYPE.EMPTY.sameColor(currentPixel))
                {
                    // do nothing
                }
                // rock
                else if (BLOCK_TYPE.ROCK.sameColor(currentPixel))
                {
                    if (lastPixel != currentPixel)
                    {
                        obj = new Ground();
                        float heightIncreaseFactor = 0.25f;
                        offsetHeight = -2.5f;
                        obj.position.set(pixelX, baseHeight * obj.dimension.y * heightIncreaseFactor + offsetHeight);
                        rocks.add((Ground) obj);
                    }
                    else
                    {
                        rocks.get(rocks.size - 1).increaseLength(1);
                    }
                }
                // player spawn point
                else if (BLOCK_TYPE.PLAYER_SPAWNPOINT.sameColor(currentPixel))
                {
                    obj = new MainChar();
                    offsetHeight = -3.0f;
                    obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
                    mainChar = (MainChar) obj;
                }
                // dog food bowl
                else if (BLOCK_TYPE.ITEM_FEATHER.sameColor(currentPixel))
                {
                    obj = new DogFoodBowl();
                    offsetHeight = -1.5f;
                    obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
                    dogFoodBowls.add((DogFoodBowl) obj);
                }
                // bone
                else if (BLOCK_TYPE.ITEM_GOLD_COIN.sameColor(currentPixel))
                {
                    obj = new Bone();
                    offsetHeight = -1.5f;
                    obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
                    bones.add((Bone) obj);
                }
                // cat
                else if (BLOCK_TYPE.ITEM_CAT.sameColor(currentPixel))
                {
                    obj = new Cat();
                    offsetHeight = -1.5f;
                    obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
                    cats.add((Cat) obj);
                }
                // dog
                else if (BLOCK_TYPE.ITEM_DOG.sameColor(currentPixel))
                {
                    obj = new Dog();
                    offsetHeight = -1.5f;
                    obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
                    dogs.add((Dog) obj);
                }
                // chicken
                else if (BLOCK_TYPE.ITEM_CHICKEN.sameColor(currentPixel))
                {
                    obj = new Chicken();
                    offsetHeight = -1.5f;
                    obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
                    chickens.add((Chicken) obj);
                }
                // chicken
                else if (BLOCK_TYPE.ITEM_FLAME.sameColor(currentPixel))
                {
                    obj = new Flame();
                    offsetHeight = -1.5f;
                    obj.position.set(pixelX, baseHeight * obj.dimension.y + offsetHeight);
                    flames.add((Flame) obj);
                }
                // unknown object/pixel color
                else
                {
                    int r = 0xff & (currentPixel >>> 24); //red color channel
                    int g = 0xff & (currentPixel >>> 16); //green color channel
                    int b = 0xff & (currentPixel >>> 8); //blue color channel
                    int a = 0xff & currentPixel; //alpha channel
                    Gdx.app.error(TAG, "Unknown object at x<" + pixelX + "> y<" + pixelY + ">: r<" + r + "> g<" + g + "> b<" + b + "> a<" + a + ">");
                }
                lastPixel = currentPixel;
            }
        }
        // decoration
        clouds = new Cloud_Decoration(pixmap.getWidth());
        clouds.position.set(0, 2);
        background = new Background_Decoration(pixmap.getWidth());
        background.position.set(-1, -1);
        waterOverlay = new Water_Decoration(pixmap.getWidth());
        waterOverlay.position.set(0, -3.75f);
        // free memory
        pixmap.dispose();
        Gdx.app.debug(TAG, "level '" + filename + "' loaded");
    }

    /**
     * The objects drawn first are in 
     * the back.
     * @param batch
     */
    public void render(SpriteBatch batch)
    {
        // Draw Mountains
        background.render(batch);
        // Draw Rocks
        for (Ground rock : rocks)
            rock.render(batch);
        // Draw Bones
        for (Bone bone : bones)
            bone.render(batch);
        // Draw Dog Food Bowl
        for (DogFoodBowl dogFoodBowl : dogFoodBowls)
            dogFoodBowl.render(batch);
        // Draw Dog 
        for (Dog dog : dogs)
            dog.render(batch);
        // Draw Cat 
        for (Cat cat : cats)
            cat.render(batch);
        // Draw Chicken 
        for (Chicken chicken : chickens)
            chicken.render(batch);
        // Draw Flame 
        for (Flame flame : flames)
            flame.render(batch);
        // Draw Player Character
        mainChar.render(batch);
        // Draw Water Overlay
        waterOverlay.render(batch);
    }

    /**
     * Update all the game objects 
     * in the level at once.
     * @param deltaTime
     */
    public void update(float deltaTime)
    {
        mainChar.update(deltaTime);
        for (Ground ground : rocks)
            ground.update(deltaTime);
        for (Bone bone : bones)
            bone.update(deltaTime);
        for (DogFoodBowl dogFoodBowl : dogFoodBowls)
            dogFoodBowl.update(deltaTime);
        for (Cat cat : cats)
            cat.update(deltaTime);
        for (Dog dog : dogs)
            dog.update(deltaTime);
        for (Chicken chicken : chickens)
            chicken.update(deltaTime);
        for (Flame flame : flames)
            flame.update(deltaTime);
        clouds.update(deltaTime);
    }
}