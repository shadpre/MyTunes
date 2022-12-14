package group01.mytunes.dao;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import group01.mytunes.Main;
import group01.mytunes.entities.Playlist;
import group01.mytunes.entities.PlaylistSong;
import group01.mytunes.entities.Song;
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

            statement.executeUpdate();


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
    public PlaylistSong addSongToPlaylist(Song song, Playlist playlist) {
        if(song == null) return null;
        if(playlist == null) return null;

        try(Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "EXEC spAddSongToPlaylist ?, ?";

            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, song.getId());
            statement.setInt(2, playlist.getId());

            var res = statement.executeQuery();

            if(res.next()) {
                return new PlaylistSong(
                        res.getInt(1),
                        song,
                        res.getInt("Position")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public List<PlaylistSong> getSongsInPlaylist(Playlist playlist) {

        if(playlist == null) return null;

        try(Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "EXEC spGetAllSongsInPlaylist ?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, playlist.getId());

            var resultSet = statement.executeQuery();
            var resultList = new ArrayList<PlaylistSong>();

            while(resultSet.next()) {
                resultList.add(new PlaylistSong(
                        resultSet.getInt("rId"),
                            new Song(
                            resultSet.getInt("Id"),
                            resultSet.getString("Title"),
                            null,
                            resultSet.getInt("Playtime")
                        ),
                        resultSet.getInt("Position")
                ));
            }

            return resultList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void moveSongUpInPlaylist(Playlist playlist, PlaylistSong pls) throws SQLException {
        try(Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "EXEC spMoveSongUpInPlaylist ?, ?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, playlist.getId());
            statement.setInt(2, pls.getRelationId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public void removeSongFromPlaylist(PlaylistSong selectedItem) throws SQLException {
        try(Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "EXEC spRemoveSongFromPlaylist ?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, selectedItem.getRelationId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }

    @Override
    public void moveSongDownInPlaylist(Playlist playlist, PlaylistSong pls) throws SQLException {
        try(Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "EXEC spMoveSongDownInPlaylist ?, ?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, playlist.getId());
            statement.setInt(2, pls.getRelationId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }
}
