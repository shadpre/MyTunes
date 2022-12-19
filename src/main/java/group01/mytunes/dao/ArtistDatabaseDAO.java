package group01.mytunes.dao;

import group01.mytunes.entities.Artist;
import group01.mytunes.dao.interfaces.IArtistDAO;
import group01.mytunes.database.DatabaseConnectionHandler;
import group01.mytunes.entities.Song;
import group01.mytunes.exceptions.SQLDeleteException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ArtistDatabaseDAO implements IArtistDAO {

    @Override
    public List<Artist> getArtists() {
        try(Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "exec spGetAllArtists ";
            Statement statement = connection.createStatement();
            var resultSet = statement.executeQuery(query);
            var resultList = new ArrayList<Artist>();

            while(resultSet.next()) {
                resultList.add(new Artist(
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
    public Artist getArtistById(int id) {
        if(id < 0) return null;

        try(Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "exec spGetArtistById ?";

            PreparedStatement statement = connection.prepareStatement(query,Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, id);

            var result = statement.executeQuery();

            if(result.next()) {
                return new Artist(
                        result.getInt("Id"),
                        result.getString("Name")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public Artist createArtist(String name) {
        if(name == null) return null;
        if(name.isEmpty()) return null;

        try(Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "exec spNewArtist ? ";

            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, name);

            statement.executeUpdate();

            var res = statement.getGeneratedKeys();
            if(res.next()) {
                return new Artist(
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
    public void deleteArtist(Artist artist) {
        if(artist == null) return;
        if(artist.getId() < 0) return;

        try(Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "exec spDeleteArtist ?";

            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, artist.getId());

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
    public void updateArtist(Artist artist, String newName) {
        if(artist == null) return;
        if(newName == null) return;

        try(Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "exec spUpdateArtistById ?, ?";

            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setInt(1, artist.getId());
            statement.setString(2, newName);


            var affectedRows = statement.executeUpdate();

            if(affectedRows != 1) throw new SQLDeleteException(); // If not worked

            artist.setName(newName);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLDeleteException();
        }

    }

    @Override
    public void addSongToArtist(Song song, Artist artist) {
        if(song == null) return;
        if(artist == null) return;

        try(Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "exec spSetSongArtistRelation ?, ?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, song.getId());
            statement.setInt(2, artist.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
