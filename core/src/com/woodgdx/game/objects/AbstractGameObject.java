package com.woodgdx.game.objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Abstract game object, abstract class for
 * which all game objects are built upon.
 * @author carson wood
 *
 */

public abstract class AbstractGameObject
{
    public Vector2 position;

    public Vector2 dimension;

    public Vector2 origin;

    public Vector2 scale;

    public float rotation;

    //Objects current speed
    public Vector2 velocity;

    //Objects fastest and slowest speed
    public Vector2 terminalVelocity;

    //Opposing force that slows down speed to 0 after a while so it doesn't go on forever
    public Vector2 friction;

    //Objects acceleration, rate the speed increases.
    public Vector2 acceleration;

    //Physical box surrounding the object for collision purposes
    public Rectangle bounds;

    //Box2D Body for physics
    public Body body;

    public AbstractGameObject()
    {
        position = new Vector2();
        dimension = new Vector2(1, 1);
        origin = new Vector2();
        scale = new Vector2(1, 1);
        rotation = 0;
        velocity = new Vector2();
        terminalVelocity = new Vector2(1, 1);
        friction = new Vector2();
        acceleration = new Vector2();
        bounds = new Rectangle();
    }

    /**
     * Basic physics engine for going horizontal (x axis)
     * @param deltaTime
     */
    protected void updateMotionX(float deltaTime)
    {
        //If our velocity is not 0, we are in motion.
        if (velocity.x != 0)
        {
            // Apply friction to slow down
            if (velocity.x > 0)
            {
                velocity.x = Math.max(velocity.x - friction.x * deltaTime, 0);
            }
            else
            {
                velocity.x = Math.min(velocity.x + friction.x * deltaTime, 0);
            }
        }
        // Apply acceleration
        velocity.x += acceleration.x * deltaTime;
        // Make sure the object's velocity does not exceed the
        // positive or negative terminal velocity
        velocity.x = MathUtils.clamp(velocity.x, -terminalVelocity.x, terminalVelocity.x);
    }

    /**
     * Basic physics engine for going vertical (y axis)
     * @param deltaTime
     */
    protected void updateMotionY(float deltaTime)
    {
        if (velocity.y != 0)
        {
            // Apply friction
            if (velocity.y > 0)
            {
                velocity.y = Math.max(velocity.y - friction.y * deltaTime, 0);
            }
            else
            {
                velocity.y = Math.min(velocity.y + friction.y * deltaTime, 0);
            }
        }
        // Apply acceleration
        velocity.y += acceleration.y * deltaTime;
        // Make sure the object's velocity does not exceed the
        // positive or negative terminal velocity
        velocity.y = MathUtils.clamp(velocity.y, -terminalVelocity.y, terminalVelocity.y);
    }

    public void update(float deltaTime)
    {
        if (body == null)
        {
            updateMotionX(deltaTime);
            updateMotionY(deltaTime);
            // Move to new position
            position.x += velocity.x * deltaTime;
            position.y += velocity.y * deltaTime;
        }
        else
        {
            position.set(body.getPosition());
            rotation = body.getAngle() * MathUtils.radiansToDegrees;
        }
    }

    public abstract void render(SpriteBatch batch);
}