package com.woodgdx.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.woodgdx.game.Assets;

/**
 * Carrot object within the game.
 * @author carson
 *
 */
public class Carrot extends AbstractGameObject
{
    //Points to region on atlas
    private TextureRegion regCarrot;

    /**
     * Carrot constructor
     */
    public Carrot()
    {
        init();
    }

    /**
     * Initalizes the carrot object
     */
    private void init()
    {
        dimension.set(0.25f, 0.5f);
        regCarrot = Assets.instance.levelDecoration.carrot;
        // Set bounding box for collision detection
        bounds.set(0, 0, dimension.x, dimension.y);
        origin.set(dimension.x / 2, dimension.y / 2);
    }

    /**
     * Renders the carrot object
     */
    public void render(SpriteBatch batch)
    {
        TextureRegion reg = null;
        reg = regCarrot;
        batch.draw(reg.getTexture(), position.x - origin.x, position.y - origin.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
                reg.getRegionWidth(), reg.getRegionHeight(), false, false);
    }
}