package com.woodgdx.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.woodgdx.game.Assets;

public class Feather extends AbstractGameObject
{
    private TextureRegion regFeather;

    //State of visibility when collected gold coin is just invisible
    public boolean collected;

    public Feather()
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
        regFeather = Assets.instance.feather.feather;
        // Set bounding box for collision detection
        bounds.set(0, 0, dimension.x, dimension.y);
        collected = false;
    }

    /**
     * Checks if the feather has been collected or not.
     * If it has been collected, don't render it.
     * @param batch
     */
    public void render(SpriteBatch batch)
    {
        if (collected)
            return;
        TextureRegion reg = null;
        reg = regFeather;
        batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(),
                reg.getRegionHeight(), false, false);
    }
    /**
     * Value of a feather 
     * for scoring purposes is 200.
     * @return value of feather
     */
    public int getScore()
    {
        return 250;
    }
}