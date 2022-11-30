package group01.mytunes.datamodels;

import group01.mytunes.Models.Playlist;
import group01.mytunes.dao.interfaces.IPlaylistDAO;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLOutput;

public class IndexDataModel {

    private IPlaylistDAO playlistDAO;

    private ObservableList<Playlist> playlistsObservable;

    private SimpleObjectProperty<Playlist> selectedPlaylistObservable;

    public IndexDataModel(IPlaylistDAO playlistDAO) {
        this.playlistDAO = playlistDAO;
        playlistsObservable = FXCollections.observableArrayList(playlistDAO.getPlaylists());
        selectedPlaylistObservable = new SimpleObjectProperty<>(null);
    }

    public ObservableList<Playlist> getPlaylistsObservable() {
        return playlistsObservable;
    }

    public SimpleObjectProperty<Playlist> getSelectedPlaylistObservable() {
        return selectedPlaylistObservable;
    }

    public void setSelectedPlaylistObservable(Playlist playlist) {
        selectedPlaylistObservable.setValue(playlist);
    }

    /**
     * Adds a playlist.
     * @param name The name of the playlist.
     */
    public void addPlaylist(String name) {
        var playlist = playlistDAO.createPlaylist(name);

        if(playlist == null) return;

        playlistsObservable.add(playlist);
    }

    /**
     * Edits a playlist.
     * @param playlist The playlist wanted to be edited.
     * @param newName The new name.
     * @return True of edited. False if failed.
     */
    public boolean editPlaylist(Playlist playlist, String newName) {
        if(newName == null) return false;
        if(newName.isEmpty()) return false;
        if(playlist.getName().equals(newName)) return false;

        playlistDAO.updatePlaylist(playlist, newName);

        int index = playlistsObservable.indexOf(playlist);
        System.out.println(index);
        playlist.setName(newName);
        playlistsObservable.set(index, playlist);

        return true;
    }

    /**
     * Deletes a playlist.
     * @param playlist The playlist to be deleted.
     */
    public void deletePlaylist(Playlist playlist) {
        playlistDAO.deletePlaylist(playlist.getId());

        playlistsObservable.remove(playlist);
    }
}
