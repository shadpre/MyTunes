package group01.mytunes.dao;

import group01.mytunes.entities.Album;
import group01.mytunes.dao.interfaces.IAlbumDAO;
import group01.mytunes.database.DatabaseConnectionHandler;
import group01.mytunes.exceptions.SQLDeleteException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AlbumDatabaseDAO implements IAlbumDAO {

    @Override
    public List<Album> getAlbums() {
        try(Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "EXEC spGetAllAlbums";
            Statement statement = connection.createStatement();

            var resultSet = statement.executeQuery(query);
            var resultList = new ArrayList<Album>();

            while(resultSet.next()) {
                resultList.add(new Album(
                    resultSet.getInt("Id"),
                    resultSet.getString("Name")
                ));
            }
            return resultList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Album createAlbum(String name) {
        if(name == null) return null;
        if(name.isEmpty()) return null;

        try(Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "exec spNewAlbum ?";

            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, name);

            statement.executeUpdate();

            var res = statement.getGeneratedKeys();
            if(res.next()) {
                return new Album(
                        res.getInt(1),
                        name
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public void deleteAlbum(int id) {
        if(id < 0) return;

        try(Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "exec spDeleteAlbum ?";

            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, id);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLDeleteException();
        }
    }

    @Override
    public void updateAlbum(Album album, String newName) {
        if(album == null) return;
        if(newName == null) return;

        try(Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "spUpdateAlbum ?, ?";

            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, album.getId());
            statement.setString(2, newName);

            var affectedRows = statement.executeUpdate();

            if(affectedRows != 1) throw new SQLDeleteException(); // If not worked

            album.setName(newName);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLDeleteException();
        }
    }
}
