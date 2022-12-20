package group01.mytunes.dao.interfaces;

import group01.mytunes.entities.Playlist;
import group01.mytunes.entities.PlaylistSong;
import group01.mytunes.entities.Song;

import java.sql.SQLException;
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

    /**
     * Adds a song to the playlist.
     * @param song The song to be added.
     * @param playlist The playlist the song should be added to.
     * @return The PlaylistSong object created.
     */
    PlaylistSong addSongToPlaylist(Song song, Playlist playlist);

    /**
     * Gets a list of all the songs in a given playlist.
     * @param playlist The playlist to get the songs from.
     * @return A list of songs in the playlist,.
     */
    List<PlaylistSong> getSongsInPlaylist(Playlist playlist);

    void moveSongDownInPlaylist(Playlist playlist, PlaylistSong pls) throws SQLException;
    void moveSongUpInPlaylist(Playlist playlist, PlaylistSong pls) throws SQLException;


    void removeSongFromPlaylist(Playlist selectedPlaylist, PlaylistSong selectedItem) throws SQLException;
}
