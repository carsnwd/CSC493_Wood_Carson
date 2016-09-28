package com.woodgdx.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.woodgdx.game.Assets;

/**
 * Cat object class
 * @author carson
 *
 */
public class Cat extends AbstractGameObject
{
    private TextureRegion regCat;

    //State of visibility when collected gold coin is just invisible
    public boolean collected;

    public Cat()
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
        regCat = Assets.instance.cat_item.cat_item;
        // Set bounding box for collision detection
        bounds.set(0, 0, dimension.x, dimension.y);
        collected = false;
    }

    /**
     * Checks if the cat has been collected or not.
     * If it has been collected, don't render it.
     * @param batch
     */
    public void render(SpriteBatch batch)
    {
        if (collected)
            return;
        TextureRegion reg = null;
        reg = regCat;
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