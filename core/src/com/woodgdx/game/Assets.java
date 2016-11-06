package com.woodgdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
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
    public AssetBunny bunny;

    public AssetRock rock;

    public AssetGoldCoin goldCoin;

    public AssetFeather feather;

    public AssetLevelDecoration levelDecoration;

    private AssetManager assetManager;

    public AssetSounds sounds;

    public AssetMusic music;

    // singleton: prevent instantiation from other classes
    private Assets()
    {
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
        assetManager.load("../core/assets/sounds/jump.wav", Sound.class);
        assetManager.load("../core/assets/sounds/jump_with_feather.wav", Sound.class);
        assetManager.load("../core/assets/sounds/pickup_coin.wav", Sound.class);
        assetManager.load("../core/assets/sounds/pickup_feather.wav", Sound.class);
        assetManager.load("../core/assets/sounds/live_lost.wav", Sound.class);
        // load music
        assetManager.load("../core/assets/music/keith303_-_brand_new_highscore.mp3", Music.class);
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
        bunny = new AssetBunny(atlas);
        rock = new AssetRock(atlas);
        goldCoin = new AssetGoldCoin(atlas);
        feather = new AssetFeather(atlas);
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
     * Sounds for game loaded.
     * @author carson
     *
     */
    public class AssetSounds
    {
        //Following are sound objects for respective events
        public final Sound jump;

        public final Sound jumpWithFeather;

        public final Sound pickupCoin;

        public final Sound pickupFeather;

        public final Sound liveLost;

        /**
         * Retrieves audio files for sounds
         * @param am
         */
        public AssetSounds(AssetManager am)
        {
            jump = am.get("../core/assets/sounds/jump.wav", Sound.class);
            jumpWithFeather = am.get("../core/assets/sounds/jump_with_feather.wav", Sound.class);
            pickupCoin = am.get("../core/assets/sounds/pickup_coin.wav", Sound.class);
            pickupFeather = am.get("../core/assets/sounds/pickup_feather.wav", Sound.class);
            liveLost = am.get("../core/assets/sounds/live_lost.wav", Sound.class);
        }
    }

    /**
     * Music for game loaded
     * @author carson
     *
     */
    public class AssetMusic
    {
        //Our music object
        public final Music song01;

        /**
         * Retrieves the music file for the music
         * @param am
         */
        public AssetMusic(AssetManager am)
        {
            song01 = am.get("../core/assets/music/keith303_-_brand_new_highscore.mp3", Music.class);
        }
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
     * Loads bunnyhead asset from textureatlas
     * @author carson
     */
    public class AssetBunny
    {
        public final AtlasRegion head;

        public AssetBunny(TextureAtlas atlas)
        {
            //Just file name no extension
            head = atlas.findRegion("bunny_head");
        }
    }

    /**
     * Loads rock asset from textureatlas
     * @author carson
     */
    public class AssetRock
    {
        public final AtlasRegion edge;

        public final AtlasRegion middle;

        public AssetRock(TextureAtlas atlas)
        {
            edge = atlas.findRegion("rock_edge");
            middle = atlas.findRegion("rock_middle");
        }
    }

    /**
     * Loads coin asset from texture atlas
     */
    public class AssetGoldCoin
    {
        public final AtlasRegion goldCoin;

        public AssetGoldCoin(TextureAtlas atlas)
        {
            goldCoin = atlas.findRegion("item_gold_coin");
        }
    }

    /**
     * Loads feather asset from texture atlas
     */
    public class AssetFeather
    {
        public final AtlasRegion feather;

        public AssetFeather(TextureAtlas atlas)
        {
            feather = atlas.findRegion("item_feather");
        }
    }

    /**
     * Loads the cloud, mountain, and overlay decorations
     * @author carson
     *
     */
    public class AssetLevelDecoration
    {
        public final AtlasRegion cloud01;

        public final AtlasRegion cloud02;

        public final AtlasRegion cloud03;

        public final AtlasRegion mountainLeft;

        public final AtlasRegion mountainRight;

        public final AtlasRegion waterOverlay;

        public final AtlasRegion carrot;

        public final AtlasRegion goal;

        /**
         * Finds items on texture atlas
         * @param atlas
         */
        public AssetLevelDecoration(TextureAtlas atlas)
        {
            cloud01 = atlas.findRegion("cloud01");
            cloud02 = atlas.findRegion("cloud02");
            cloud03 = atlas.findRegion("cloud03");
            mountainLeft = atlas.findRegion("mountain_left");
            mountainRight = atlas.findRegion("mountain_right");
            goal = atlas.findRegion("goal");
            waterOverlay = atlas.findRegion("water_overlay");
            carrot = atlas.findRegion("carrot");
        }
    }
}