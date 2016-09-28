package com.woodgdx.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.woodgdx.game.Assets;

/**
 * Creates cloud objects according
 * to the abstractgameobject
 * @author carson wood
 *
 */
public class Cloud_Decoration extends AbstractGameObject
{
    //Size of the cloud objects
    private float length;

    private Array<TextureRegion> regClouds;

    private Array<Cloud> clouds;

    private class Cloud extends AbstractGameObject
    {
        private TextureRegion regCloud;

        public Cloud()
        {
        }

        public void setRegion(TextureRegion region)
        {
            regCloud = region;
        }

        @Override
        public void render(SpriteBatch batch)
        {
            TextureRegion reg = regCloud;
            batch.draw(reg.getTexture(), position.x + origin.x, position.y + origin.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
                    reg.getRegionWidth(), reg.getRegionHeight(), false, false);
        }
    }

    public Cloud_Decoration(float length)
    {
        this.length = length;
        init();
    }

    private void init()
    {
        dimension.set(3.0f, 1.5f);
        regClouds = new Array<TextureRegion>();
        regClouds.add(Assets.instance.levelDecoration.cloud_decoration);
        int distFac = 2; //How far apart the clouds should be (or how distributed they should be)
        int numClouds = (int) (length / distFac);
        clouds = new Array<Cloud>(2 * numClouds);
        for (int i = 0; i < numClouds; i++)
        {
            Cloud cloud = spawnCloud();
            cloud.position.x = i * distFac;
            clouds.add(cloud);
        }
    }

    /**
     * Spawns a cloud at a random position.
     * @return
     */
    private Cloud spawnCloud()
    {
        Cloud cloud = new Cloud();
        cloud.dimension.set(dimension);
        // select random cloud image
        cloud.setRegion(regClouds.random());
        // position
        Vector2 pos = new Vector2();
        pos.x = length + 10; // position after end of level
        pos.y += 1.75; // base position
        pos.y += MathUtils.random(0.0f, 0.2f) * (MathUtils.randomBoolean() ? 1 : -1); // random additional position
        cloud.position.set(pos);
        return cloud;
    }

    @Override
    public void render(SpriteBatch batch)
    {
        for (Cloud cloud : clouds)
            cloud.render(batch);
    }
}