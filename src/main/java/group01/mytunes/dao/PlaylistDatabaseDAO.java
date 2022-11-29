package group01.mytunes.dao;

import group01.mytunes.Models.Artist;
import group01.mytunes.Models.Playlist;
import group01.mytunes.Models.User;
import group01.mytunes.dao.interfaces.IPlaylistDAO;
import group01.mytunes.database.DatabaseConnectionHandler;
import group01.mytunes.exceptions.SQLDeleteException;

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
            String query = "SELECT * FROM [Artists]";
            Statement statement = connection.createStatement();
            var resultSet = statement.executeQuery(query);
            var resultList = new ArrayList<Playlist>();

            while(resultSet.next()) {
                resultList.add(new Playlist(
                        resultSet.getInt("Id"),
                        resultSet.getInt("UserId"),
                        resultSet.getString("Name"),
                        resultSet.getDate("Date")
                ));
            }
            return resultList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Playlist createPlaylist(String name, User user) {
        if(name == null) return null;
        if(name.isEmpty()) return null;
        if(user == null) return null;

        try(Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "INSERT INTO [Playlists] ([UserID], [Name]) VALUES (?, ?)";

            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, user.getId());
            statement.setString(2, name);

            statement.executeUpdate();

            var res = statement.getGeneratedKeys();
            if(res.next()) {
                return new Playlist(
                        res.getInt("Id"),
                        res.getInt("UserID"),
                        res.getString("Name"),
                        res.getDate("Date")
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
            String query = "DELETE FROM [Playlists] WHERE [Id]=?";

            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, id);

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
    public void updatePlaylist(Playlist playlist, String newName, User user) {
        if(playlist == null) return;
        if(newName == null) return;
        if(user == null) return;

        try(Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "UPDATE [Playlists] set [UserID]=?, [Name]=? WHERE [Id]=?;";

            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, user.getId());
            statement.setString(2, newName);
            statement.setInt(3, playlist.getId());

            var affectedRows = statement.executeUpdate();

            if(affectedRows != 1) throw new SQLDeleteException(); // If not worked

            playlist.setUserId(user.getId());
            playlist.setName(newName);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLDeleteException();
        }
    }
}
