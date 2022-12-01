package group01.mytunes.audio;

import group01.mytunes.Models.Song;

public interface IAudioHandler {

    void playSong(Song song);

    void changeVolume(double volume);

    /**
     * Pauses the player if playing. Plays if paused.
     *
     * @return True if it is now playing. False otherwise.
     */
    boolean playPause();

    boolean isPlaying();
}
