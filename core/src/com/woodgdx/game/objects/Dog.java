package com.woodgdx.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.woodgdx.game.Assets;

/**
 * Dog object class
 * @author carson
 *
 */
public class Dog extends AbstractGameObject
{
    private TextureRegion regDog;

    //State of visibility when collected gold coin is just invisible
    public boolean collected;

    public Dog()
    {
        init();
    }

    /**
     * Bounding box set to same size
     * as the object in the world
     */
    private void init()
    {
        dimension.set(0.5f, 0.5f);
        regDog = Assets.instance.dog_item.dog_item;
        // Set bounding box for collision detection
        bounds.set(0, 0, dimension.x, dimension.y);
        collected = false;
    }

    /**
     * Checks if the Dog has been collected or not.
     * If it has been collected, don't render it.
     * @param batch
     */
    public void render(SpriteBatch batch)
    {
        if (collected)
            return;
        TextureRegion reg = null;
        reg = regDog;
        batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(),
                reg.getRegionHeight(), false, false);
    }

    /**
     * Value of a gold coin 
     * for scoring purposes is 100.
     * @return value of gold coin
     */
    public int getScore()
    {
        return 200;
    }
}