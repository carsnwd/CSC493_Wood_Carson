package com.woodgdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.woodgdx.game.util.Constants;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * Organizes and structures our assets
 * @author carson
 *
 */

public class Assets implements Disposable, AssetErrorListener
{
    public static final String TAG = Assets.class.getName();

    public static final Assets instance = new Assets();

    //Game assets 
    public AssetBone bone;

    public AssetCat cat_item;

    public AssetChicken chicken_item;

    //    public AssetCloud cloud;
    public AssetDogFoodBowl dog_food_bowl;

    public AssetDog dog_item;

    public AssetFlame flame;

    public AssetGround ground;

    public AssetMainCharacter main_character;

    public AssetLevelDecoration levelDecoration;

    private AssetManager assetManager;

    // singleton: prevent instantiation from other classes
    private Assets()
    {
    }

    public AssetFonts fonts;

    public class AssetFonts
    {
        public final BitmapFont defaultSmall;

        public final BitmapFont defaultNormal;

        public final BitmapFont defaultBig;

        public AssetFonts()
        {
            // create three fonts using Libgdx's 15px bitmap font
            defaultSmall = new BitmapFont(Gdx.files.internal("assets-raw/images/arial-15.fnt"), true);
            defaultNormal = new BitmapFont(Gdx.files.internal("assets-raw/images/arial-15.fnt"), true);
            defaultBig = new BitmapFont(Gdx.files.internal("assets-raw/images/arial-15.fnt"), true);
            // set font sizes
            defaultSmall.getData().setScale(0.75f);
            defaultNormal.getData().setScale(1.0f);
            defaultBig.getData().setScale(2.0f);
            // enable linear texture filtering for smooth fonts
            defaultSmall.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
            defaultNormal.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
            defaultBig.getRegion().getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
        }
    }

    /**
     * Initialize assetmanager
     * @param assetManager
     */
    public void init(AssetManager assetManager)
    {
        this.assetManager = assetManager;
        // set asset manager error handler
        assetManager.setErrorListener(this);
        // load texture atlas
        assetManager.load(Constants.TEXTURE_ATLAS_OBJECTS, TextureAtlas.class);
        // load sounds
        assetManager.load("../core/assets/sounds/bone.wav", Sound.class);
        assetManager.load("../core/assets/sounds/food_bowl.wav", Sound.class);
        assetManager.load("../core/assets/sounds/jump.wav", Sound.class);
        assetManager.load("../core/assets/sounds/jump_with_effect.wav", Sound.class);
        assetManager.load("../core/assets/sounds/live_lost.wav", Sound.class);
        assetManager.load("../core/assets/sounds/cat_meow.wav", Sound.class);
        assetManager.load("../core/assets/sounds/dog_bark.wav", Sound.class);
        assetManager.load("../core/assets/sounds/chicken.wav", Sound.class);
        assetManager.load("../core/assets/sounds/flame.wav", Sound.class);
        // load music
        assetManager.load("../core/assets/music/heart_of_gold.mp3", Music.class);
        // start loading assets and wait until finished
        assetManager.finishLoading();
        Gdx.app.debug(TAG, "# of assets loaded: " + assetManager.getAssetNames().size);
        for (String a : assetManager.getAssetNames())
        {
            Gdx.app.debug(TAG, "asset: " + a);
        }
        TextureAtlas atlas = assetManager.get(Constants.TEXTURE_ATLAS_OBJECTS);
        // enable texture filtering for pixel smoothing
        for (Texture t : atlas.getTextures())
        {
            t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        }

        // create game resource objects
        fonts = new AssetFonts();
        bone = new AssetBone(atlas);
        cat_item = new AssetCat(atlas);
        chicken_item = new AssetChicken(atlas);
        //        cloud = new AssetCloud(atlas);
        dog_food_bowl = new AssetDogFoodBowl(atlas);
        dog_item = new AssetDog(atlas);
        flame = new AssetFlame(atlas);
        ground = new AssetGround(atlas);
        main_character = new AssetMainCharacter(atlas);
        levelDecoration = new AssetLevelDecoration(atlas);
        sounds = new AssetSounds(assetManager);
        music = new AssetMusic(assetManager);
    }

    /**
     * Dispose of assetmanager
     */
    @Override
    public void dispose()
    {
        assetManager.dispose();
        fonts.defaultSmall.dispose();
        fonts.defaultNormal.dispose();
        fonts.defaultBig.dispose();
    }

    /**
     * Throws an error when asset couldn't be loaded.
     * @param filename
     * @param type
     * @param throwable
     */
    //@Override <--- book had this but eclipse didn't like that.
    public void error(String filename, Class type, Throwable throwable)
    {
        Gdx.app.error(TAG, "Couldn't load asset '" + filename + "'", (Exception) throwable);
    }

    /**
     * An overload of previous method
     */
    @Override
    public void error(AssetDescriptor asset, Throwable throwable)
    {
        Gdx.app.error(TAG, "Couldn't load asset '" + asset.fileName + "'", (Exception) throwable);
    }

    /**
     * Loads dog food bowl asset from textureatlas
     * @author carson
     */
    public class AssetDogFoodBowl
    {
        public final AtlasRegion dogFoodBowl;

        public AssetDogFoodBowl(TextureAtlas atlas)
        {
            //Just file name no extension
            dogFoodBowl = atlas.findRegion("dog_food_bowl");
        }
    }

    /**
     * Loads bone asset from textureatlas
     * @author carson
     */
    public class AssetBone
    {
        public final AtlasRegion bone;

        public final Animation animBone;

        public AssetBone(TextureAtlas atlas)
        {
            //Just file name no extension
            bone = atlas.findRegion("bone");

            // Animation: Bone
            Array<AtlasRegion> regions = atlas.findRegions("anim_bone");
            AtlasRegion region = regions.first();
            for (int i = 0; i < 10; i++)
                regions.insert(0, region);
            animBone = new Animation(1.0f / 20.0f, regions, Animation.PlayMode.LOOP_PINGPONG);
        }
    }

    /**
     * Loads cat asset from textureatlas
     * @author carson
     */
    public class AssetCat
    {
        public final AtlasRegion cat_item;

        public AssetCat(TextureAtlas atlas)
        {
            //Just file name no extension
            cat_item = atlas.findRegion("cat_item");
        }
    }

    /**
     * Loads chicken asset from textureatlas
     * @author carson
     */
    public class AssetChicken
    {
        public final AtlasRegion chicken_item;

        public AssetChicken(TextureAtlas atlas)
        {
            //Just file name no extension
            chicken_item = atlas.findRegion("chicken_item");
        }
    }

    /**
     * Loads flame asset from textureatlas
     * @author carson
     */
    public class AssetFlame
    {
        public final AtlasRegion flame;

        public AssetFlame(TextureAtlas atlas)
        {
            //Just file name no extension
            flame = atlas.findRegion("flame");
        }
    }

    //    /**
    //     * Loads cloud asset from textureatlas
    //     * @author carson
    //     */
    //    public class AssetCloud
    //    {
    //        public final AtlasRegion cloud;
    //
    //        public AssetCloud(TextureAtlas atlas)
    //        {
    //            //Just file name no extension
    //            cloud = atlas.findRegion("cloud");
    //        }
    //    }

    /**
     * Loads dog item asset from textureatlas
     * @author carson
     */
    public class AssetDog
    {
        public final AtlasRegion dog_item;

        public AssetDog(TextureAtlas atlas)
        {
            //Just file name no extension
            dog_item = atlas.findRegion("dog_item");
        }
    }

    /**
     * Loads ground asset from textureatlas
     * @author carson
     */
    public class AssetGround
    {
        //public final AtlasRegion edge;

        public final AtlasRegion ground;

        public AssetGround(TextureAtlas atlas)
        {
            //edge = atlas.findRegion("rock_edge");
            ground = atlas.findRegion("ground");
        }
    }

    /**
     * Loads main character asset from texture atlas
     */
    public class AssetMainCharacter
    {
        public final AtlasRegion main_character;

        public AssetMainCharacter(TextureAtlas atlas)
        {
            main_character = atlas.findRegion("main_character");
        }
    }

    /**
     * Loads the cloud, mountain, and overlay decorations
     * @author carson
     *
     */
    public class AssetLevelDecoration
    {
        public final AtlasRegion cloud_decoration;

        public final AtlasRegion water_decoration;

        public final AtlasRegion tree_decoration;

        /**
         * Finds items on texture atlas
         * @param atlas
         */
        public AssetLevelDecoration(TextureAtlas atlas)
        {
            cloud_decoration = atlas.findRegion("cloud_decoration");
            water_decoration = atlas.findRegion("water_decoration");
            tree_decoration = atlas.findRegion("tree_decoration");
        }
    }

    public AssetSounds sounds;

    public AssetMusic music;

    /**
     * Manages the sound effects within the game.
     * @author carson
     *
     */
    public class AssetSounds
    {
        //Respective sounds for each effect.
        public final Sound jump;

        public final Sound jumpWithFoodBowl;

        public final Sound pickupBone;

        public final Sound pickupFoodBowl;

        public final Sound liveLost;

        //        public final Sound catMeow;
        //        
        //        public final Sound dogBark;
        //        
        //        public final Sound chicken;

        public final Sound flame;

        public AssetSounds(AssetManager am)
        {
            jump = am.get("../core/assets/sounds/jump.wav", Sound.class);
            jumpWithFoodBowl = am.get("../core/assets/sounds/jump_with_effect.wav", Sound.class);
            pickupBone = am.get("../core/assets/sounds/bone.wav", Sound.class);
            pickupFoodBowl = am.get("../core/assets/sounds/food_bowl.wav", Sound.class);
            liveLost = am.get("../core/assets/sounds/live_lost.wav", Sound.class);
            //            catMeow = am.get("../core/assets/sounds/cat_meow.wav", Sound.class);
            //            dogBark = am.get("../core/assets/sounds/dog_bark.wav", Sound.class);
            //            chicken = am.get("../core/assets/sounds/chicken.wav", Sound.class);
            flame = am.get("../core/assets/sounds/flame.wav", Sound.class);
        }
    }

    /**
     * Manages the music (loops in background) for the game.
     * @author carson
     *
     */
    public class AssetMusic
    {
        //We only have one song that loops.
        public final Music song01;

        public AssetMusic(AssetManager am)
        {
            song01 = am.get("../core/assets/music/heart_of_gold.mp3", Music.class);
        }
    }
}