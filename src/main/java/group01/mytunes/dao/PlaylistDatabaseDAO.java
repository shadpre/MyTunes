package group01.mytunes.dao;

import group01.mytunes.Main;
import group01.mytunes.Models.Playlist;
import group01.mytunes.Models.Song;
import group01.mytunes.dao.interfaces.IPlaylistDAO;
import group01.mytunes.database.DatabaseConnectionHandler;
import group01.mytunes.exceptions.SQLDeleteException;
import group01.mytunes.exceptions.SQLUpdateException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDatabaseDAO implements IPlaylistDAO {

    @Override
    public List<Playlist> getPlaylists() {
        try(Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "exec spGetUserPlaylists ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, Main.currentUser.getId());
            var resultSet = statement.executeQuery();
            var resultList = new ArrayList<Playlist>();

            while(resultSet.next()) {
                resultList.add(new Playlist(resultSet.getInt("Id"),
                //resultSet.getInt("UserID"),
                Main.currentUser.getId(),
                resultSet.getString("Name"),
                resultSet.getDate("Date")));
                resultSet.getInt("Playtime");
            }
            return resultList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Playlist createPlaylist(String name) {
        if(name == null) return null;
        if(name.isEmpty()) return null;
        if(Main.currentUser == null) return null;

        try(Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "exec spNewPlaylist ?, ?";

            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, Main.currentUser.getId());
            statement.setString(2, name);

            var res = statement.executeQuery();

            if(res.next()) {
                return new Playlist(
                    res.getInt("Id"),
                    Main.currentUser.getId(),
                    name,
                    null
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public void deletePlaylist(int id) {
        if(id < 0) return;

        try(Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "exec spDeletePlaylistById ?, ?";

            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, id);
            statement.setInt(2, Main.currentUser.getId());

            var affectedRows = statement.executeUpdate();

            if(affectedRows != 1) {
                throw new SQLDeleteException();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLDeleteException();
        }
    }

    @Override
    public void updatePlaylist(Playlist playlist, String newName) {

        if(playlist == null) return;
        if(newName == null) return;

        try(Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "spUpdatePlaylistById ?, ?, ?";

            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1,playlist.getId());
            statement.setInt(2, Main.currentUser.getId());
            statement.setString(3, newName);

            var affectedRows = statement.executeUpdate();

            if(affectedRows != 1) throw new SQLUpdateException(); // If not worked

            playlist.setName(newName);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLDeleteException();
        }
    }

    @Override
    public boolean addSongToPlaylist(Song song, Playlist playlist) {
        if(song == null) return false;
        if(playlist == null) return false;

        try(Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "EXEC spAddSongToPlaylist ?, ?";

            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, song.getId());
            statement.setInt(2, playlist.getId());

            var res = statement.executeUpdate(); // rows affected

            if(res < 1) return false;

            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Song> getSongsInPlaylist(Playlist playlist) {

        if(playlist == null) return null;

        try(Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "EXEC spGetAllSongsInPlaylist ?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, playlist.getId());

            var resultSet = statement.executeQuery();
            var resultList = new ArrayList<Song>();

            while(resultSet.next()) {
                resultList.add(new Song(
                        resultSet.getInt("Id"),
                        resultSet.getString("Title"),
                        null,
                        resultSet.getInt("Playtime")
                ));
            }

            return resultList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
