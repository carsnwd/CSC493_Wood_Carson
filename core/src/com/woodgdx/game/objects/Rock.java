package com.woodgdx.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.woodgdx.game.Assets;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 *  Rock objected created based on the
 *  abstract game object. 
 * @author carson wood
 *
 */
public class Rock extends AbstractGameObject
{
    //Left and right edge texture (Same thing, mirror images)
    private TextureRegion regEdge;

    //Middle texture for the rock
    private TextureRegion regMiddle;

    //How many middle rocks we will make aka the length
    private int length;

    //Rock floating on water
    private final float FLOAT_CYCLE_TIME = 2.0f;

    private final float FLOAT_AMPLITUDE = 0.25f;

    private float floatCycleTimeLeft;

    private boolean floatingDownwards;

    private Vector2 floatTargetPosition;

    public Rock()
    {
        init();
    }

    private void init()
    {
        dimension.set(1, 1.5f);
        regEdge = Assets.instance.rock.edge;
        regMiddle = Assets.instance.rock.middle;
        // Start length of this rock
        setLength(1);

        //Init the floating on water
        floatingDownwards = false;
        floatCycleTimeLeft = MathUtils.random(0, FLOAT_CYCLE_TIME / 2);
        floatTargetPosition = null;
    }

    /**
     * Amends the length of the rock.
     * @param length
     */
    public void setLength(int length)
    {
        this.length = length;
        // Update bounding box for collision detection
        bounds.set(0, 0, dimension.x * length, dimension.y);
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
        reg = regEdge;
        relX -= dimension.x / 4;
        batch.draw(reg.getTexture(), position.x + relX, position.y + relY, origin.x, origin.y, dimension.x / 4, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
                reg.getRegionWidth(), reg.getRegionHeight(), false, false);
        // Draw middle
        relX = 0;
        reg = regMiddle;
        for (int i = 0; i < length; i++)
        {
            batch.draw(reg.getTexture(), position.x + relX, position.y + relY, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
                    reg.getRegionWidth(), reg.getRegionHeight(), false, false);
            relX += dimension.x;
        }
        // Draw right edge
        reg = regEdge;
        batch.draw(reg.getTexture(), position.x + relX, position.y + relY, origin.x + dimension.x / 8, origin.y, dimension.x / 4, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(),
                reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(), true, false);
    }

    /**
     * Used for rocks floating
     */
    @Override
    public void update(float deltaTime)
    {
        super.update(deltaTime);
        floatCycleTimeLeft -= deltaTime;
        if (floatCycleTimeLeft <= 0)
        {
            floatCycleTimeLeft = FLOAT_CYCLE_TIME;
            floatingDownwards = !floatingDownwards;
            body.setLinearVelocity(0, FLOAT_AMPLITUDE * (floatingDownwards ? -1 : 1));
        }
        else
        {
            body.setLinearVelocity(body.getLinearVelocity().scl(0.98f));
        }
    }
}