package group01.mytunes.dao.interfaces;

import group01.mytunes.Models.Playlist;
import group01.mytunes.Models.User;

import java.util.List;

/**
 * Interface for PlaylistDAO's
 */
public interface IPlaylistDAO {

    /**
     * Gets all the playlist.
     * @return A List of the playlists
     */
    List<Playlist> getPlaylists();

    /**
     * Creates a playlist with the given name
     * @param name The name of the plaulist
     * @param user The user the playlist should be added to
     * @return The newly created playlist
     */
    Playlist createPlaylist(String name, User user);

    /**
     * Deletes a playlist
     * @param id The id of the playlist
     */
    void deletePlaylist(int id);

    /**
     * Updates a playlist.
     * @param playlist The playlist to update. This reference will automatically update with the new name.
     * @param user The user to update (new owner)
     * @param newName The new name of the playlist.
     */
    void updatePlaylist(Playlist playlist, String newName, User user);

}
