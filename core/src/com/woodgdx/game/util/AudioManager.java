package com.woodgdx.game.util;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * Manages the playing and organization
 * of audio files
 * @author carson
 *
 */
public class AudioManager
{
    public static final AudioManager instance = new AudioManager();

    private Music playingMusic;

    // singleton: prevent instantiation from other classes
    private AudioManager()
    {
    }

    /**
     * Play sound with default vol, pitch, and pan (0)
     * @param sound
     */
    public void play(Sound sound)
    {
        play(sound, 1);
    }

    /**
     * Play sound with default pitch and pan (0) but different vol
     * @param sound
     * @param volume
     */
    public void play(Sound sound, float volume)
    {
        play(sound, volume, 1);
    }

    /**
     * Play sound with default pan (0) with different pitch and vol
     * @param sound
     * @param volume
     * @param pitch
     */
    public void play(Sound sound, float volume, float pitch)
    {
        play(sound, volume, pitch, 0);
    }

    /**
     * Plays sound with a vol, pitch, and pan activated
     * @param sound
     * @param volume
     * @param pitch
     * @param pan
     */
    public void play(Sound sound, float volume, float pitch, float pan)
    {
        //If sound is muted, dont play
        if (!GamePreferences.instance.sound)
            return;
        sound.play(GamePreferences.instance.volSound * volume, pitch, pan);
    }

    /**
     * Plays music on loop
     * @param music
     */
    public void play(Music music)
    {
        stopMusic();
        playingMusic = music;
        if (GamePreferences.instance.music)
        {
            music.setLooping(true);
            music.setVolume(GamePreferences.instance.volMusic);
            music.play();
        }
    }

    /**
     * Ends the music loop of doom
     */
    public void stopMusic()
    {
        if (playingMusic != null)
            playingMusic.stop();
    }

    /**
     * Checks settings for muted music, vol level
     */
    public void onSettingsUpdated()
    {
        //No music available
        if (playingMusic == null)
            return;
        //Sets volume
        playingMusic.setVolume(GamePreferences.instance.volMusic);
        //Checks if music is not muted
        if (GamePreferences.instance.music)
        {
            //If music isn't playing, play it
            if (!playingMusic.isPlaying())
                playingMusic.play();
        }
        //Mute music if selected so
        else
        {
            playingMusic.pause();
        }
    }
}