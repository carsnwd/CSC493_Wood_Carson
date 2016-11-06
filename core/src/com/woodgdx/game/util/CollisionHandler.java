package com.woodgdx.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.ObjectMap;
import com.woodgdx.game.WorldController;
import com.woodgdx.game.Assets;
import com.woodgdx.game.objects.AbstractGameObject;
import com.woodgdx.game.objects.Bone;
import com.woodgdx.game.objects.Cat;
import com.woodgdx.game.objects.Chicken;
import com.woodgdx.game.objects.Dog;
import com.woodgdx.game.objects.DogFoodBowl;
import com.woodgdx.game.objects.Flame;
import com.woodgdx.game.objects.Ground; //Land in example
import com.woodgdx.game.objects.MainChar;
import com.woodgdx.game.objects.MainChar.JUMP_STATE;

public class CollisionHandler implements ContactListener
{
    private ObjectMap<Short, ObjectMap<Short, ContactListener>> listeners;

    private WorldController world;

    public CollisionHandler(WorldController w)
    {
        world = w;
        listeners = new ObjectMap<Short, ObjectMap<Short, ContactListener>>();
    }

    public void addListener(short categoryA, short categoryB, ContactListener listener)
    {
        addListenerInternal(categoryA, categoryB, listener);
        addListenerInternal(categoryB, categoryA, listener);
    }

    @Override
    public void beginContact(Contact contact)
    {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        //Gdx.app.log("CollisionHandler-begin A", "begin");

        // processContact(contact);

        ContactListener listener = getListener(fixtureA.getFilterData().categoryBits, fixtureB.getFilterData().categoryBits);
        if (listener != null)
        {
            listener.beginContact(contact);
        }
    }

    @Override
    public void endContact(Contact contact)
    {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        // Gdx.app.log("CollisionHandler-end A", "end");
        processContact(contact);

        // Gdx.app.log("CollisionHandler-end A", fixtureA.getBody().getLinearVelocity().x+" : "+fixtureA.getBody().getLinearVelocity().y);
        // Gdx.app.log("CollisionHandler-end B", fixtureB.getBody().getLinearVelocity().x+" : "+fixtureB.getBody().getLinearVelocity().y);
        ContactListener listener = getListener(fixtureA.getFilterData().categoryBits, fixtureB.getFilterData().categoryBits);
        if (listener != null)
        {
            listener.endContact(contact);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold)
    {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        ContactListener listener = getListener(fixtureA.getFilterData().categoryBits, fixtureB.getFilterData().categoryBits);
        if (listener != null)
        {
            listener.preSolve(contact, oldManifold);
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse)
    {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        ContactListener listener = getListener(fixtureA.getFilterData().categoryBits, fixtureB.getFilterData().categoryBits);
        if (listener != null)
        {
            listener.postSolve(contact, impulse);
        }
    }

    private void addListenerInternal(short categoryA, short categoryB, ContactListener listener)
    {
        ObjectMap<Short, ContactListener> listenerCollection = listeners.get(categoryA);
        if (listenerCollection == null)
        {
            listenerCollection = new ObjectMap<Short, ContactListener>();
            listeners.put(categoryA, listenerCollection);
        }
        listenerCollection.put(categoryB, listener);
    }

    private ContactListener getListener(short categoryA, short categoryB)
    {
        ObjectMap<Short, ContactListener> listenerCollection = listeners.get(categoryA);
        if (listenerCollection == null)
        {
            return null;
        }
        return listenerCollection.get(categoryB);
    }

    private void processContact(Contact contact)
    {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();
        AbstractGameObject objA = (AbstractGameObject) fixtureA.getBody().getUserData();
        AbstractGameObject objB = (AbstractGameObject) fixtureB.getBody().getUserData();

        if (objA instanceof MainChar)
        {
            processPlayerContact(fixtureA, fixtureB);
        }
        else if (objB instanceof MainChar)
        {
            processPlayerContact(fixtureB, fixtureA);
        }
    }

    private void processPlayerContact(Fixture playerFixture, Fixture objFixture)
    {
        if (objFixture.getBody().getUserData() instanceof Ground)
        {
            MainChar player = (MainChar) playerFixture.getBody().getUserData();
            player.acceleration.y = 0;
            player.velocity.y = 0;
            player.jumpState = JUMP_STATE.GROUNDED;
            playerFixture.getBody().setLinearVelocity(player.velocity);
        }
        else if (objFixture.getBody().getUserData() instanceof Bone)
        {

            AudioManager.instance.play(Assets.instance.sounds.pickupBone);
            AudioManager.instance.play(Assets.instance.sounds.jump);
            AudioManager.instance.play(Assets.instance.sounds.liveLost);

            Bone bone = (Bone) objFixture.getBody().getUserData();
            // Remove the block update the player's score by 1.
            world.score = +bone.getScore();
            world.flagForRemoval(bone);
        }
        else if (objFixture.getBody().getUserData() instanceof Cat)
        {

//            AudioManager.instance.play(Assets.instance.sounds.pickupCat);
            AudioManager.instance.play(Assets.instance.sounds.jump);
            AudioManager.instance.play(Assets.instance.sounds.liveLost);

            Cat cat = (Cat) objFixture.getBody().getUserData();
            // Remove the block update the player's score by 1.
            world.score = +cat.getScore();
            world.flagForRemoval(cat);
        }
        else if (objFixture.getBody().getUserData() instanceof Dog)
        {

//            AudioManager.instance.play(Assets.instance.sounds.pickupDog);
            AudioManager.instance.play(Assets.instance.sounds.jump);
            AudioManager.instance.play(Assets.instance.sounds.liveLost);

            Dog dog = (Dog) objFixture.getBody().getUserData();
            // Remove the block update the player's score by 1.
            world.score = +dog.getScore();
            world.flagForRemoval(dog);
        }
        else if (objFixture.getBody().getUserData() instanceof Chicken)
        {

//            AudioManager.instance.play(Assets.instance.sounds.pickupChicken);
            AudioManager.instance.play(Assets.instance.sounds.jump);
            AudioManager.instance.play(Assets.instance.sounds.liveLost);

            Chicken chicken = (Chicken) objFixture.getBody().getUserData();
            // Remove the block update the player's score by 1.
            world.score = +chicken.getScore();
            world.flagForRemoval(chicken);
        }
        else if (objFixture.getBody().getUserData() instanceof Flame)
        {

            AudioManager.instance.play(Assets.instance.sounds.flame);
            AudioManager.instance.play(Assets.instance.sounds.jump);
            AudioManager.instance.play(Assets.instance.sounds.liveLost);

            Flame flame = (Flame) objFixture.getBody().getUserData();
            // Remove the block update the player's score by 1.
            world.score = +flame.getScore();
            world.flagForRemoval(flame);
        }
        else if (objFixture.getBody().getUserData() instanceof DogFoodBowl)
        {

            AudioManager.instance.play(Assets.instance.sounds.pickupFoodBowl);
            AudioManager.instance.play(Assets.instance.sounds.jump);
            AudioManager.instance.play(Assets.instance.sounds.liveLost);

            DogFoodBowl dogFoodBowl = (DogFoodBowl) objFixture.getBody().getUserData();
            // Remove the block update the player's score by 1.
            world.score = +dogFoodBowl.getScore();
            world.flagForRemoval(dogFoodBowl);
        }
    }

}