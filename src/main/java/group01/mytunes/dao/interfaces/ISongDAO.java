package group01.mytunes.dao.interfaces;

import group01.mytunes.Models.Song;

import java.util.List;

public interface ISongDAO {

    List<Song> getAllSongInfo();

    Song getSongById(int id);

    void deleteSong(int id);

    Song createSong(Song song);

    String getSongDataHash(int id);

    void updateSong(Song selectedSong, String newSongName);
}
