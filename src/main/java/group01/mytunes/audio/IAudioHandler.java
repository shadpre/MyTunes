package group01.mytunes.audio;

import group01.mytunes.entities.Song;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

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

    Media getMedia();
    MediaPlayer getMediaPlayer();

    void playPreviousSong();
    void restartSong();
    void setTime(double songProgress);
    void stop();
    void start();

}
