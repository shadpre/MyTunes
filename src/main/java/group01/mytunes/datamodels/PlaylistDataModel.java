package group01.mytunes.datamodels;

import group01.mytunes.Models.Playlist;
import group01.mytunes.dao.interfaces.IPlaylistDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class PlaylistDataModel {

    private IPlaylistDAO playlistDAO;

    private List<Playlist> playlists;

    public PlaylistDataModel(IPlaylistDAO playlistDAO) {
        this.playlistDAO = playlistDAO;
        playlists = playlistDAO.getPlaylists();
    }

    public ObservableList<Playlist> getPlaylists() {
        return FXCollections.observableArrayList(playlists);
    }

}
