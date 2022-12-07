package group01.mytunes.dao.interfaces;

import group01.mytunes.entities.Artist;

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

}
