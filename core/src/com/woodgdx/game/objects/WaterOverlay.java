package com.woodgdx.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.woodgdx.game.Assets;

/**
 * Water object constructed with
 * AbstractGameObject in mind
 * @author carson wood
 */
public class WaterOverlay extends AbstractGameObject
{
    //Just one texture for water unlike mts and rock
    private TextureRegion regWaterOverlay;

    //Length of the water object
    private float length;

    public WaterOverlay(float length)
    {
        this.length = length;
        init();
    }

    private void init()
    {
        dimension.set(length * 10, 3);
        regWaterOverlay = Assets.instance.levelDecoration.waterOverlay;
        origin.x = -dimension.x / 2;
    }

    /**
     * More simple than mts and rock as just
     * one texture is being rendered.
     */
    @Override
    public void render(SpriteBatch batch)
    {
        TextureRegion reg = null;
        reg = regWaterOverlay;
        batch.draw(reg.getTexture(), position.x + origin.x, position.y + origin.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
                reg.getRegionWidth(), reg.getRegionHeight(), false, false);
    }
}