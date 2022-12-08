package group01.mytunes.dao.interfaces;

import group01.mytunes.entities.Song;

import java.util.List;

/**
 * Interface for SongDAO
 */
public interface ISongDAO {

    /**
     * Gets a list of all the songs in a playlist.<br />
     * <b>NOTICE:</b> song data is <u>NOT</u> included.
     * @return A list of all songs.
     */
    List<Song> getAllSongInfo();

    /**
     * Gets a song by its id.
     * <b>NOTICE:</b> song data <u>IS</u> included.
     * @param id The id of the song.
     * @return The requested song.
     */
    Song getSongById(int id);

    /**
     * Deletes a song.
     * @param id The id of the song to delete.
     */
    void deleteSong(int id);

    /**
     * Creates a song.
     * @param song The song to create. The ID of the song will be set in this method.
     * @return The song created with an updated ID.
     */
    Song createSong(Song song);

    /**
     * Returns the HASH of a songs' data.
     * @param id If of the song.
     * @return The hash as HEX.
     */
    String getSongDataHash(int id);

    /**
     * Updates a song.
     * @param selectedSong The song to update.
     * @param newSongName The new name of the song.
     */
    void updateSong(Song selectedSong, String newSongName);

    /**
     * Gets a list of artists related to a song.
     * @param id The id of the song.
     * @return A list of the artists related to the song.
     */
    List<Integer> getArtistsToSong(int id);
}
