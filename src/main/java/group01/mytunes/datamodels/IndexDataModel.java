package group01.mytunes.datamodels;

import group01.mytunes.Models.Playlist;
import group01.mytunes.Models.PlaylistSong;
import group01.mytunes.Models.Song;
import group01.mytunes.dao.interfaces.IArtistDAO;
import group01.mytunes.dao.interfaces.IPlaylistDAO;
import group01.mytunes.dao.interfaces.ISongDAO;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;

public class IndexDataModel {

    private IPlaylistDAO playlistDAO;
    private ISongDAO songDAO;

    private IArtistDAO artistDAO;

    private ObservableList<Playlist> playlistsObservableList;

    private ObservableList<Song> songObservableList;
    private ObservableList<PlaylistSong> songPlaylistObservableList;

    private SimpleObjectProperty<Playlist> selectedPlaylistObservable;


    public IndexDataModel(IPlaylistDAO playlistDAO, ISongDAO songDAO, IArtistDAO artistDAO) {
        this.playlistDAO = playlistDAO;
        this.songDAO = songDAO;
        this.artistDAO = artistDAO;

        playlistsObservableList = FXCollections.observableArrayList(playlistDAO.getPlaylists());
        songObservableList = FXCollections.observableArrayList(songDAO.getAllSongInfo());
        songPlaylistObservableList = FXCollections.observableArrayList();
        selectedPlaylistObservable = new SimpleObjectProperty<>(null);
    }

    public ObservableList<Playlist> getPlaylistsObservableList() {
        return playlistsObservableList;
    }

    public SimpleObjectProperty<Playlist> getSelectedPlaylistObservable() {
        return selectedPlaylistObservable;
    }

    /**
     * Adds a playlist.
     * @param name The name of the playlist.
     */
    public void addPlaylist(String name) {
        var playlist = playlistDAO.createPlaylist(name);

        if(playlist == null) return;

        playlistsObservableList.add(playlist);
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

        int index = playlistsObservableList.indexOf(playlist);
        System.out.println(index);
        playlist.setName(newName);
        playlistsObservableList.set(index, playlist);

        return true;
    }

    /**
     * Deletes a playlist.
     * @param playlist The playlist to be deleted.
     */
    public void deletePlaylist(Playlist playlist) {
        playlistDAO.deletePlaylist(playlist.getId());

        playlistsObservableList.remove(playlist);
    }

    public ObservableList getSongInfoObservableList() {
        return songObservableList;
    }

    public ObservableList getSongPlaylistInfoObservableList() {
        return songPlaylistObservableList;
    }
    public void setSelectedPlaylistObservable(Playlist playlist) {
        var result = playlistDAO.getSongsInPlaylist(playlist);
        songPlaylistObservableList.setAll(result);
        selectedPlaylistObservable.setValue(playlist);

    }

    public void addSong(Song song) {
        var result = songDAO.createSong(song);
        if(result == null) return;

        System.out.println(result.getId());

        songObservableList.add(result);
    }

    public void addArtist(String artistName) {
        if(artistName == null) return;
        if(artistName.isEmpty()) return;

        artistDAO.createArtist(artistName);
    }

    public void editArtist() {
    }

    public void deleteArtist() {
    }

    public void addSongToPlaylist(Song selectedSong, Playlist selectedPlaylist) {
        var playlistSong = playlistDAO.addSongToPlaylist(selectedSong, selectedPlaylist);

        if(playlistSong == null) return;

        System.out.println(playlistSong.getRelationId());

        songPlaylistObservableList.add(playlistSong);
    }
}
