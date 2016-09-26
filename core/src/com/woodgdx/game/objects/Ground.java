package com.woodgdx.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.woodgdx.game.Assets;

/**
 *  Rock objected created based on the
 *  abstract game object. 
 * @author carson wood
 *
 */
public class Ground extends AbstractGameObject
{
    //Left and right edge texture (Same thing, mirror images)
//    private TextureRegion regEdge;

    //Middle texture for the ground
    private TextureRegion regGround;

    //How many middle rocks we will make aka the length
    private int length;

    public Ground()
    {
        init();
    }

    private void init()
    {
        dimension.set(1, 1.5f);
        //regEdge = Assets.instance.rock.edge;
        regGround = Assets.instance.ground.ground;
        // Start length of this rock
        setLength(1);
    }

    /**
     * Amends the length of the rock.
     * @param length
     */
    public void setLength(int length)
    {
        this.length = length;
    }

    /**
     * Increases the length of the rock
     * by a set amount.
     * @param amount
     */
    public void increaseLength(int amount)
    {
        setLength(length + amount);
    }

    /**
     * Draws the rock within the world.
     * 1 left edge
     * length = the amount of middle rocks
     * 1 right edge
     * 
     * Is drawn.
     * 
     * x and y is where it is drawn according to
     * the origin. 
     */
    @Override
    public void render(SpriteBatch batch)
    {
        TextureRegion reg = null;
        float relX = 0;
        float relY = 0;
        // Draw left edge
        reg = regGround;
        relX -= dimension.x / 4;
        batch.draw(reg.getTexture(), position.x + relX, position.y + relY, origin.x, origin.y, dimension.x / 4, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
                reg.getRegionWidth(), reg.getRegionHeight(), false, false);
        // Draw middle
        relX = 0;
        reg = regGround;
        for (int i = 0; i < length; i++)
        {
            batch.draw(reg.getTexture(), position.x + relX, position.y + relY, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
                    reg.getRegionWidth(), reg.getRegionHeight(), false, false);
            relX += dimension.x;
        }
        // Draw right edge
        reg = regGround;
        batch.draw(reg.getTexture(), position.x + relX, position.y + relY, origin.x + dimension.x / 8, origin.y, dimension.x / 4, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(),
                reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), true, false);
    }
}