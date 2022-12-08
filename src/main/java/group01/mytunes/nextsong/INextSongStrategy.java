package group01.mytunes.nextsong;

import group01.mytunes.entities.Song;

/**
 * Strategy interface for getting the next song to play.
 */
public interface INextSongStrategy {

    /**
     * The algorithm for getting the next song to play.
     * @return The next song to play.
     */
    Song getNextSong();

    /**
     * Changes the index of the current song.
     * @param index The new index.
     */
    void changeSong(int index);

}
