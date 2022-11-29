package group01.mytunes.datamodels;

import group01.mytunes.Models.Playlist;
import group01.mytunes.dao.interfaces.IPlaylistDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class PlaylistDataModel {

    private IPlaylistDAO playlistDAO;

    private ObservableList<Playlist> playlists;

    public PlaylistDataModel(IPlaylistDAO playlistDAO) {
        this.playlistDAO = playlistDAO;
        playlists = FXCollections.observableArrayList(playlistDAO.getPlaylists());
    }

    public ObservableList<Playlist> getPlaylists() {
        return playlists;
    }

    public void addPlaylist(String name) {
        var playlist = playlistDAO.createPlaylist(name);

        if(playlist == null) return;

        playlists.add(playlist);
    }

    /**
     * Edits a playlist.
     * @param playlist The playlist wanted to be edited
     * @param newName The new name
     * @return True of edited. False if failed.
     */
    public boolean editPlaylist(Playlist playlist, String newName) {
        if(newName == null) return false;
        if(newName.isEmpty()) return false;
        if(playlist.getName().equals(newName)) return false;

        playlistDAO.updatePlaylist(playlist, newName);
        int index = playlists.indexOf(playlist);
        System.out.println(index);
        playlist.setName(newName);
        playlists.set(index, playlist);

        return true;
    }

    public void deletePlaylist(Playlist playlist) {
        playlistDAO.deletePlaylist(playlist.getId());

        playlists.remove(playlist);
    }

}
