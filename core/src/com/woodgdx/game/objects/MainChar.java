package com.woodgdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.woodgdx.game.Assets;
import com.woodgdx.game.util.Constants;

/**
 * Mainchar object class
 * @author carson
 *
 */
public class MainChar extends AbstractGameObject
{
    public static final String TAG = MainChar.class.getName();

    private final float JUMP_TIME_MAX = 0.3f;

    private final float JUMP_TIME_MIN = 0.1f;

    private final float JUMP_TIME_OFFSET_FLYING = JUMP_TIME_MAX - 0.018f;

    public enum VIEW_DIRECTION
    {
        LEFT, RIGHT
    }

    /**
     * GROUNDED: Standing on platform
     * FALLING: Falling down from air
     * JUMP_RISING: Jump initiated and max height of jump not reached
     * JUMP_FALLING: Max height reached or jump key released if none, decreasing height.
     * 
     * Page 207 for more info on jump states
     *
     */
    public enum JUMP_STATE
    {
        GROUNDED, FALLING, JUMP_RISING, JUMP_FALLING
    }

    private TextureRegion regMainChar;

    public VIEW_DIRECTION viewDirection;

    public float timeJumping;

    public JUMP_STATE jumpState;

    public boolean hasDogFoodPowerup;

    public float timeLeftDogFoodPowerup;

    public MainChar()
    {
        init();
    }

    /**
     * Sets the mainchar starting physics values,
     * jumping state, and view direction. Also deactivates
     * the dogfood powerup.
     */
    public void init()
    {
        dimension.set(1, 1);
        regMainChar = Assets.instance.main_character.main_character;
        // Center image on game object
        origin.set(dimension.x / 2, dimension.y / 2);
        // Bounding box for collision detection
        bounds.set(0, 0, dimension.x, dimension.y);
        // Set physics values
        terminalVelocity.set(3.0f, 4.0f);
        friction.set(12.0f, 0.0f);
        acceleration.set(0.0f, -25.0f);
        // View direction
        viewDirection = VIEW_DIRECTION.RIGHT;
        // Jump state
        jumpState = JUMP_STATE.FALLING;
        timeJumping = 0;
        // Power-ups
        hasDogFoodPowerup = false;
        timeLeftDogFoodPowerup = 0;
    }

    /**
     * Alters the jump state. Decides if
     * jumping is possible and if it is
     * a multi-jump
     * @param jumpKeyPressed
     */
    public void setJumping(boolean jumpKeyPressed)
    {
        switch (jumpState)
        {
        case GROUNDED: // Character is standing on a platform
            if (jumpKeyPressed)
            {
                // Start counting jump time from the beginning
                timeJumping = 0;
                jumpState = JUMP_STATE.JUMP_RISING;
            }
            break;
        case JUMP_RISING: // Rising in the air
            if (!jumpKeyPressed)
                jumpState = JUMP_STATE.JUMP_FALLING;
            break;
        case FALLING:// Falling down
        case JUMP_FALLING: // Falling down after jump
            if (jumpKeyPressed && hasDogFoodPowerup)
            {
                timeJumping = JUMP_TIME_OFFSET_FLYING;
                jumpState = JUMP_STATE.JUMP_RISING;
            }
            break;
        }
    }

    /**
     * Toggles the dog food power up. 
     * @param pickedUp
     */
    public void setDogFoodPowerup(boolean pickedUp)
    {
        hasDogFoodPowerup = pickedUp;
        if (pickedUp)
        {
            timeLeftDogFoodPowerup = Constants.ITEM_DOGFOOD_POWERUP_DURATION;
        }
    }

    /**
     * Checks to see if the mainchar
     * is powered up.
     * @return
     */
    public boolean hasDogFoodPowerup()
    {
        return hasDogFoodPowerup && timeLeftDogFoodPowerup > 0;
    }

    /**
     * Changes the view direction depending
     * on if it is switched. Also checks to
     * see if the power up is still active or not.
     * @param deltaTime
     */
    @Override
    public void update(float deltaTime)
    {
        super.update(deltaTime);
        if (velocity.x != 0)
        {
            viewDirection = velocity.x < 0 ? VIEW_DIRECTION.LEFT : VIEW_DIRECTION.RIGHT;
        }
        if (timeLeftDogFoodPowerup > 0)
        {
            timeLeftDogFoodPowerup -= deltaTime;
            if (timeLeftDogFoodPowerup < 0)
            {
                // disable power-up
                timeLeftDogFoodPowerup = 0;
                setDogFoodPowerup(false);
            }
        }
    }

    /**
     * Handles unique jumping conditions
     * for the mainchar head. 
     */
    @Override
    protected void updateMotionY(float deltaTime)
    {
        switch (jumpState)
        {
        case GROUNDED:
            jumpState = JUMP_STATE.FALLING;
            break;
        case JUMP_RISING:
            // Keep track of jump time
            timeJumping += deltaTime;
            // Jump time left?
            if (timeJumping <= JUMP_TIME_MAX)
            {
                // Still jumping
                velocity.y = terminalVelocity.y;
            }
            break;
        case FALLING:
            break;
        case JUMP_FALLING:
            // Add delta times to track jump time
            timeJumping += deltaTime;
            // Jump to minimal height if jump key was pressed too short
            if (timeJumping > 0 && timeJumping <= JUMP_TIME_MIN)
            {
                // Still jumping
                velocity.y = terminalVelocity.y;
            }
        }
        if (jumpState != JUMP_STATE.GROUNDED)
            super.updateMotionY(deltaTime);
    }

    /**
     * Renders the mainchar. Tinted
     * orange if the power up is selected
     */
    @Override
    public void render(SpriteBatch batch)
    {
        TextureRegion reg = null;
        // Set special color when game object has a Dog Food power-up
        if (hasDogFoodPowerup)
        {
            batch.setColor(1.0f, 0.8f, 0.0f, 1.0f);
        }
        // Draw image
        reg = regMainChar;
        batch.draw(reg.getTexture(), position.x, position.y, origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(),
                reg.getRegionHeight(), viewDirection == VIEW_DIRECTION.LEFT, false);
        // Reset color to white
        batch.setColor(1, 1, 1, 1);
    }
}