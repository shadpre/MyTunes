package group01.mytunes.dao.interfaces;

import group01.mytunes.Models.Playlist;
import group01.mytunes.Models.Song;

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
     * @param name The name of the playlist
     * @return The newly created playlist
     */
    Playlist createPlaylist(String name);

    /**
     * Deletes a playlist
     * @param id The id of the playlist
     */
    void deletePlaylist(int id);

    /**
     * Updates a playlist.
     * @param playlist The playlist to update. This reference will automatically update with the new name.
     * @param newName The new name of the playlist.
     */
    void updatePlaylist(Playlist playlist, String newName);

    boolean addSongToPlaylist(Song song, Playlist playlist);

    List<Song> getSongsInPlaylist(Playlist playlist);

}
