package com.woodgdx.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.woodgdx.game.Assets;

/**
 * Trophy object class
 * @author carson
 *
 */
public class Goal extends AbstractGameObject
{
    //Points to the trophy on the texture atlas
    private TextureRegion regGoal;

    /**
     * Constructor
     */
    public Goal()
    {
        init();
    }

    /**
     * Initalizes the trophy object
     */
    private void init()
    {
        dimension.set(3.0f, 3.0f);
        regGoal = Assets.instance.levelDecoration.goal;
        // Set bounding box for collision detection
        bounds.set(1, Float.MIN_VALUE, 10, Float.MAX_VALUE);
        origin.set(dimension.x / 2.0f, 0.0f);
    }

    /**
     * Draws the trophy object.
     * Height is infinite so the player will for sure
     * hit it and end the level.
     */
    public void render(SpriteBatch batch)
    {
        TextureRegion reg = null;
        reg = regGoal;
        batch.draw(reg.getTexture(), position.x - origin.x, position.y - origin.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
                reg.getRegionWidth(), reg.getRegionHeight(), false, false);
    }
}