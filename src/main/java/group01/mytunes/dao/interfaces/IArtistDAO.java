package group01.mytunes.dao.interfaces;

import group01.mytunes.Models.Artist;

import java.util.List;

/**
 * Interface for ArtistDAO's
 */
public interface IArtistDAO {

    /**
     * Gets all the artists.
     * @return A List of the artists
     */
    List<Artist> getArtists();

    Artist getArtistById(int id);

    Artist createArtist(String name);

    void deleteArtist(int id);

    /**
     * Updates an artist.
     * @param artist The artist to update. This reference will automatically update with the new name.
     * @param newName The new name of the artist.
     */
    void updateArtist(Artist artist, String newName);

}
