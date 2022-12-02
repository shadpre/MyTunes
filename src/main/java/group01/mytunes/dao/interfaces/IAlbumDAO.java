package group01.mytunes.dao.interfaces;

import group01.mytunes.entities.Album;

import java.util.List;

public interface IAlbumDAO {

    /**
     * Gets all the albums.
     * @return A List of the playlists
     */
    List<Album> getAlbums();

    /**
     * Creates an album with the given name
     * @param name The name of the album
     * @return The newly created album
     */
    Album createAlbum(String name);

    /**
     * Deletes an album
     * @param id The id of the album
     */
    void deleteAlbum(int id);

    /**
     * Updates an album.
     * @param album The album to update. This reference will automatically update with the new name.
     * @param newName The new name of the album.
     */
    void updateAlbum(Album album, String newName);
}
