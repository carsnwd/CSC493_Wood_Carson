package com.woodgdx.game.util;

/**
 * To keep track of constant variables.
 * @author carson
 */

public class Constants
{
    // Visible game world is 5 meters wide
    public static final float VIEWPORT_WIDTH = 5.0f;

    // Visible game world is 5 meters tall
    public static final float VIEWPORT_HEIGHT = 5.0f;

    // Location of description file for texture atlas
    public static final String TEXTURE_ATLAS_OBJECTS = "../core/assets/canyonbunny.pack.atlas";

    // GUI Width
    public static final float VIEWPORT_GUI_WIDTH = 800.0f;

    // GUI Height
    public static final float VIEWPORT_GUI_HEIGHT = 480.0f;

    // Location of image file for level 01
    public static final String LEVEL_01 = "../core/assets/levels/level-01.png";
    
    // Location of image file for level 02
    public static final String LEVEL_02 = "../core/assets/levels/level-02.png";

    // Amount of extra lives at level start
    public static final int LIVES_START = 3;

    // Duration of dog food bowl power-up in seconds
    public static final float ITEM_DOGFOOD_POWERUP_DURATION = 9;

    // Delay after game over
    public static final float TIME_DELAY_GAME_OVER = 3;
    
    //UI texture atlas
    public static final String TEXTURE_ATLAS_UI = "../core/assets/canyonbunny-ui.pack.atlas";

    //UI texture atlas
    public static final String TEXTURE_ATLAS_LIBGDX_UI = "../core/assets/uiskin.atlas";

    // Location of description file for skins
    public static final String SKIN_LIBGDX_UI = "../core/assets/uiskin.json";

    //Cannon bunny UI json file
    public static final String SKIN_CANYONBUNNY_UI = "../core/assets/canyonbunny-ui.json";

    //Stored preferences
    public static final String PREFERENCES = "cannonbunny.prefs";
}
