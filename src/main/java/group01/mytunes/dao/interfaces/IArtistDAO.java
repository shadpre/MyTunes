package group01.mytunes.dao.interfaces;

import group01.mytunes.entities.Album;
import group01.mytunes.entities.Artist;
import group01.mytunes.entities.Song;

import java.util.List;

/**
 * Interface for ArtistDAO's
 */
public interface IArtistDAO {

    /**
     * Gets all the artists.
     * @return A List of the artists.
     */
    List<Artist> getArtists();

    /**
     * Gets an artist by its ID.
     * @param id The artist ID.
     * @return The requested artist.
     */
    Artist getArtistById(int id);

    /**
     * Creates an artist with the given name.
     * @param name New artist name.
     * @return The artist created.
     */
    Artist createArtist(String name);

    /**
     * Deletes an artist
     * @param artist The artist to delete
     */
    void deleteArtist(Artist artist);

    /**
     * Updates an artist.
     * @param artist The artist to update. This reference will automatically update with the new name.
     * @param newName The new name of the artist.
     */
    void updateArtist(Artist artist, String newName);

    /**
     * Adds a song to an album.
     * @param song The song to add.
     * @param artist The artist of the song.
     */
    void addSongToArtist(Song song, Artist artist);

    void addSongToAlbum(Song song, Album album);
}
