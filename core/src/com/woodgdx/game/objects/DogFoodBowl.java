package com.woodgdx.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.woodgdx.game.Assets;

public class DogFoodBowl extends AbstractGameObject
{
    private TextureRegion regDogFoodBowl;

    //State of visibility when collected gold coin is just invisible
    public boolean collected;

    public DogFoodBowl()
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
        regDogFoodBowl = Assets.instance.dog_food_bowl.dogFoodBowl;
        // Set bounding box for collision detection
        bounds.set(0, 0, dimension.x, dimension.y);
        collected = false;
    }

    /**
     * Checks if the dog_food_bowl has been collected or not.
     * If it has been collected, don't render it.
     * @param batch
     */
    public void render(SpriteBatch batch)
    {
        if (collected)
            return;
        TextureRegion reg = null;
        reg = regDogFoodBowl;
        batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(),
                reg.getRegionHeight(), false, false);
    }

    /**
     * Value of a dog_food_bowl 
     * for scoring purposes is 200.
     * @return value of feather
     */
    public int getScore()
    {
        return 500;
    }
}