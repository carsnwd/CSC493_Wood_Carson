package com.woodgdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.woodgdx.game.Assets;

/**
 * Tree object created
 * based on AbstractGameObject
 * @author carson wood
 *
 */
public class Background_Decoration extends AbstractGameObject
{
    //Tree texture
    private TextureRegion tree;
    //How long to make the tree texture
    private int length;

    public Background_Decoration(int length)
    {
        this.length = length;
        init();
    }

    private void init()
    {
        dimension.set(10, 2);
        tree = Assets.instance.levelDecoration.background;
        // shift mountain and extend length
        origin.x = -dimension.x * 2;
        length += dimension.x * 2;
    }

    private void drawTree(SpriteBatch batch, float offsetX, float offsetY, float tintColor)
    {
        TextureRegion reg = tree;
//        batch.setColor(tintColor, tintColor, tintColor, 1); //Sets the tint color of mountain
//        float xRel = dimension.x * offsetX;
//        float yRel = dimension.y * offsetY;
//        // mountains span the whole level
//        int mountainLength = 0;
//        mountainLength += MathUtils.ceil(length / (2 * dimension.x));
//        mountainLength += MathUtils.ceil(0.5f + offsetX);
//        for (int i = 0; i < mountainLength; i++)
//        {
//            reg = tree;
//            batch.draw(reg.getTexture(), origin.x + xRel, position.y + origin.y + yRel, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
//                    reg.getRegionWidth(), reg.getRegionHeight(), false, false);
//            xRel += dimension.x;
//        }
//        // reset color to white
//        batch.setColor(1, 1, 1, 1);
        
        /**
         * Draws the background spanning entire screen.
         */
        batch.draw(reg,0,0,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void render(SpriteBatch batch)
    {
        
        drawTree(batch, 0.5f, 0.5f, 0.5f);
//        // distant mountains (gray)
//        drawTree(batch, 0.25f, 0.25f, 0.7f);
//        // distant mountains (light gray)
//        drawTree(batch, 0.0f, 0.0f, 0.9f);
    }
}