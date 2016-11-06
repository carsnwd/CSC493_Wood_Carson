package com.woodgdx.game.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.woodgdx.game.Assets;
import com.woodgdx.game.util.Constants;
import com.woodgdx.game.util.CharacterSkin;
import com.woodgdx.game.util.GamePreferences;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.MathUtils;
import com.woodgdx.game.util.AudioManager;

/**
 * Mainchar object class
 * @author carson
 *
 */
public class MainChar extends AbstractGameObject
{
    public static final String TAG = MainChar.class.getName();

    private final float JUMP_TIME_MAX = 0.6f;

    private final float JUMP_TIME_MIN = 0.1f;

    private final float JUMP_TIME_OFFSET_FLYING = JUMP_TIME_MAX - 0.018f;

    public ParticleEffect dustParticles = new ParticleEffect();

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
        // Particles
        dustParticles.load(Gdx.files.internal("../core/assets/particles/dust.pfx"), Gdx.files.internal("../core/assets/particles"));
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
                AudioManager.instance.play(Assets.instance.sounds.jump);
                // Start counting jump time from the beginning
                timeJumping = 0;
                jumpState = JUMP_STATE.JUMP_RISING;
            }
            else if (velocity.x != 0)
            {
                //Gdx.app.log(TAG, "starting particles");
                dustParticles.setPosition(position.x + dimension.x / 2, position.y + 0.1f);
                dustParticles.start();
            }
            else if (velocity.x == 0)
            {
                dustParticles.allowCompletion();
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
                AudioManager.instance.play(Assets.instance.sounds.jumpWithFoodBowl, 1, MathUtils.random(1.0f, 1.1f));
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
            // Set physics values
            //terminalVelocity.set(6.0f, 8.0f);
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
        updateMotionX(deltaTime);
        updateMotionY(deltaTime);
        if (body != null)
        {
            // Gdx.app.log(TAG, "velY: "+velocity.y+" state: "+jumpState);
            body.setLinearVelocity(velocity);
            position.set(body.getPosition());
        }
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
                terminalVelocity.set(3.0f, 4.0f);
            }
        }
        dustParticles.update(deltaTime);
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
            if (velocity.x != 0)
            {
                dustParticles.setPosition(position.x + dimension.x / 2, position.y);
                dustParticles.start();
            }
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
            else
            {
                jumpState = JUMP_STATE.JUMP_FALLING;
            }
            break;
        case FALLING:
            jumpState = JUMP_STATE.GROUNDED;
            break;
        case JUMP_FALLING:
            //            // Add delta times to track jump time
            //            timeJumping += deltaTime;
            //            // Jump to minimal height if jump key was pressed too short
            //            if (timeJumping > 0 && timeJumping <= JUMP_TIME_MIN)
            //            {
            //                // Still jumping
            //                velocity.y = terminalVelocity.y;
            //            }
            //        }
            //        if (jumpState != JUMP_STATE.GROUNDED)
            //        {
            //            dustParticles.allowCompletion();
            //            super.updateMotionY(deltaTime);
            //        }
            velocity.y = -terminalVelocity.y;
            break;
        }
        if (jumpState != JUMP_STATE.GROUNDED)
        {
            //Gdx.app.log(TAG, "stoppinparticles");
            dustParticles.allowCompletion();
            super.updateMotionY(deltaTime);
        }
    }

    /**
     * Renders the mainchar. Tinted
     * orange if the power up is selected
     */
    @Override
    public void render(SpriteBatch batch)
    {
        TextureRegion reg = null;
        // Draw Particles
        dustParticles.draw(batch);

        // Apply Skin Color
        batch.setColor(CharacterSkin.values()[GamePreferences.instance.charSkin].getColor());

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