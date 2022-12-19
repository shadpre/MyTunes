package group01.mytunes.datamodels;

import group01.mytunes.dao.interfaces.IAlbumDAO;
import group01.mytunes.entities.*;
import group01.mytunes.dao.interfaces.IArtistDAO;
import group01.mytunes.dao.interfaces.IPlaylistDAO;
import group01.mytunes.dao.interfaces.ISongDAO;
import group01.mytunes.exceptions.SQLDeleteException;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.sql.SQLException;
import java.util.*;

public class IndexDataModel {

    private IPlaylistDAO playlistDAO;
    private ISongDAO songDAO;

    private IArtistDAO artistDAO;
    private IAlbumDAO albumDAO;

    private ObservableMap<Integer, Artist> artistObservableMap;
    private ObservableMap<Integer, Album> albumObservableMap;
    private ObservableList<Playlist> playlistsObservableList;
    private List<Song> songList;
    private List<Album> albumList;
    private ObservableList<Song> songObservableList;
    private ObservableList<PlaylistSong> songPlaylistObservableList;

    private SimpleObjectProperty<Playlist> selectedPlaylistObservable;


    public IndexDataModel(IPlaylistDAO playlistDAO, ISongDAO songDAO, IArtistDAO artistDAO, IAlbumDAO albumDAO) {
        this.playlistDAO = playlistDAO;
        this.songDAO = songDAO;
        this.artistDAO = artistDAO;
        this.albumDAO = albumDAO;

        initArtistObservableMap();
        initAlbumObservableMap();
        playlistsObservableList = FXCollections.observableArrayList(playlistDAO.getPlaylists());

        songList = songDAO.getAllSongInfo();
        songObservableList = FXCollections.observableArrayList(songList);
        songPlaylistObservableList = FXCollections.observableArrayList();
        selectedPlaylistObservable = new SimpleObjectProperty<>(null);
    }

    private void initArtistObservableMap() {
        var artists = artistDAO.getArtists();
        Map<Integer, Artist> tempMap = new HashMap<>();
        for(Artist a : artists) {
            tempMap.put(a.getId(), a);
        }
        artistObservableMap = FXCollections.observableMap(tempMap);
    }

    private void initAlbumObservableMap(){
        var albums = albumDAO.getAlbums();
        Map<Integer, Album> tempAlbums = new HashMap<>();
        for (Album a : albums) {
            tempAlbums.put(a.getId(), a);
        }
        albumObservableMap = FXCollections.observableMap(tempAlbums);
    }

    public String getArtistsForSong(Song song) {
        var artistToSong = songDAO.getArtistsToSong(song.getId());

        if(artistToSong.size() == 0) return "";

        StringBuilder artists = new StringBuilder();

        for(int v : artistToSong) {
            artists.append(artistObservableMap.get(v) + " ");
        }

        return artists.toString();
    }

    public String getAlbumForSong(Song song){
        var albumToSong = songDAO.getAlbumToSong(song.getId());

        if (albumToSong.size() == 0) return "";

        StringBuilder album = new StringBuilder();

        for (int v : albumToSong) {
            album.append(albumObservableMap.get(v) + "");
        }

        return album.toString();
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
     *
     * @param playlist The playlist wanted to be edited.
     * @param newName  The new name.
     */
    public void editPlaylist(Playlist playlist, String newName) {
        if(newName == null) return;
        if(newName.isEmpty()) return;
        if(playlist == null) return;
        if(playlist.getName().equals(newName)) return;

        playlistDAO.updatePlaylist(playlist, newName);

        int index = playlistsObservableList.indexOf(playlist);
        System.out.println(index);
        playlist.setName(newName);
        playlistsObservableList.set(index, playlist);
    }

    public boolean editSong(Song selectedSong, String newSongName) {
        if(newSongName == null) return false;
        if(newSongName.isEmpty()) return false;
        if(selectedSong.getTitle().equals(newSongName)) return false;

        songDAO.updateSong(selectedSong, newSongName);

        songObservableList.clear();
        songObservableList.addAll(FXCollections.observableArrayList(songDAO.getAllSongInfo()));

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

    public ObservableList<Song> getSongInfoObservableList() {
        return songObservableList;
    }

    public ObservableList<PlaylistSong> getSongPlaylistInfoObservableList() {
        return songPlaylistObservableList;
    }
    public void setSelectedPlaylistObservable(Playlist playlist) {
        var result = playlistDAO.getSongsInPlaylist(playlist);
        songPlaylistObservableList.setAll(result);
        selectedPlaylistObservable.setValue(playlist);

    }

    public void searchForSong(String query) {
        var filtered = songList.stream().filter(x -> x.getTitle().toUpperCase().contains(query.toUpperCase())).toList();
        songObservableList.setAll(filtered);
    }

    public void addSong(Song song) {
        var result = songDAO.createSong(song);
        if(result == null) return;

        System.out.println(result.getId());

        songList.add(result);
        songObservableList.add(result);
    }

    public List<Artist> getArtistList() {
        return new ArrayList<Artist>(artistObservableMap.values());
    }

    public ArrayList<Album> getAlbumList() {
        return new ArrayList<Album>(albumObservableMap.values());
    }

    public void addArtist(String artistName) {
        if(artistName == null) return;
        if(artistName.isEmpty()) return;

        artistDAO.createArtist(artistName);
    }

    public void editArtist(Artist artist, String newName) {
        if (artist == null) return;

        if (newName == null || newName.isEmpty()) return;

        artistDAO.updateArtist(artist, newName);
    }

    public void editAlbum(Album album, String newName) {
        if (album == null) return;

        if (newName == null || newName.isEmpty()) return;

        albumDAO.updateAlbum(album, newName);
    }

    public List<Artist> getAllArtists() {
        return artistDAO.getArtists();
    }

    public void deleteArtist(Artist artist) {
        artistDAO.deleteArtist(artist);
    }

    public List<Album> getAllAlbums() {
        return albumDAO.getAlbums();
    }

    public void deleteAlbum(Album album) {
        albumDAO.deleteAlbum(album.getId());
    }

    public void addSongToPlaylist(Song selectedSong, Playlist playlistToAddTo, Playlist selectedPlaylist) {
        var playlistSong = playlistDAO.addSongToPlaylist(selectedSong, playlistToAddTo);

        if(playlistSong == null) return;

        if(playlistToAddTo.getId() == selectedPlaylist.getId()) songPlaylistObservableList.add(playlistSong);
    }

    /**
     * Deletes a song.
     * If  the song is in a playlist or album, it will also be deleted from those.
     * @param song The song to delete.
     */
    public void deleteSong(Song song) {
        try {
            songDAO.deleteSong(song.getId());

            List<PlaylistSong> toDelete = new ArrayList<>();
            for(PlaylistSong p : songPlaylistObservableList) {
                if(p.getId() == song.getId()) toDelete.add(p);
            }

            songPlaylistObservableList.removeAll(toDelete);

            songList.remove(song);
            songObservableList.remove(song);
        } catch (SQLDeleteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Moves a song up in a playlist
     * @param playlist The selected playlist
     * @param pls The song to move
     */
    public void moveSongUpInPlaylist(Playlist playlist, PlaylistSong pls) throws SQLException {
        try {
            playlistDAO.moveSongUpInPlaylist(playlist, pls);
            int index = songPlaylistObservableList.indexOf(pls); // Index of the PlaylistSong object in observable list
            Collections.swap(
                songPlaylistObservableList,
                index, // Swap index 1
                index-1 // Swap index 2
            );
        } catch (SQLException e) {
            System.out.println("Can not move song up!");
            throw e;
        }
    }

    /**
     * Moves a song down in a playlist
     * @param playlist The selected playlist
     * @param pls The song to move
     */
    public void moveSongDownInPlaylist(Playlist playlist, PlaylistSong pls) throws SQLException {
        try {
            playlistDAO.moveSongDownInPlaylist(playlist, pls);
            int index = songPlaylistObservableList.indexOf(pls); // Index of the PlaylistSong object in observable list
            Collections.swap(
                songPlaylistObservableList,
                index, // Swap index 1
                index+1 // Swap index 2
            );
        } catch (SQLException e) {
            System.out.println("Can not move song down!");
            throw e;
        }
    }

    /**
     * gives given String to DO class for it to be added to DB
     * @param album given string from controller
     */
    public void addAlbum(String album) {
        if(album == null) return;
        if(album.isEmpty()) return;

        //TODO add to db. Program crashes once reaches createAlbum
        albumDAO.createAlbum(album);
    }
}
