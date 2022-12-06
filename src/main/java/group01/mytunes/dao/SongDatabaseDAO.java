package group01.mytunes.dao;

import group01.mytunes.entities.Artist;
import group01.mytunes.entities.Song;
import group01.mytunes.dao.interfaces.ISongDAO;
import group01.mytunes.database.DatabaseConnectionHandler;
import group01.mytunes.exceptions.SQLDeleteException;
import group01.mytunes.exceptions.SQLUpdateException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SongDatabaseDAO implements ISongDAO {

    @Override
    public List<Song> getAllSongInfo() {
        try (Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "EXEC spGetAllSongInfo;";
            Statement statement = connection.createStatement();

            var resultSet = statement.executeQuery(query);
            var resultList = new ArrayList<Song>();

            while (resultSet.next()) {
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

    @Override
    public Song getSongById(int id) {
        try (Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "EXEC spGetSongById ?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);

            var result = statement.executeQuery();

            if (result.next()) {
                return new Song(
                        id,
                        result.getString("Title"),
                        result.getBytes("Data"),
                        result.getInt("Playtime")
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public void deleteSong(int id) throws SQLDeleteException {
        if (id < 0) return;

        try (Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "exec spDeleteSong ?";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLDeleteException();
        }
    }

    @Override
    public Song createSong(Song song) {
        if (song == null) return null;
        try (Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "spNewSong ?,?,?;";

            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, song.getTitle());
            statement.setInt(2, song.getPlaytime());
            statement.setBytes(3, song.getData());

            var res = statement.executeQuery();

            if (res.next()) {
                song.setId(res.getInt(1));

                return song;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public String getSongDataHash(int id) {
        try (Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "EXEC spHashSongData ?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);

            var result = statement.executeQuery();

            if (result.next()) return result.getString(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public void updateSong(Song selectedSong, String newSongName) {
        if (selectedSong == null) return;
        if (newSongName == null) return;

        try (Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "exec spUpdateSongTittle ?,?"; //sets song tittle

            PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            statement.setInt(1, selectedSong.getId());
            statement.setString(2, newSongName); //tittle

            statement.executeUpdate();

            selectedSong.setTitle(newSongName);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new SQLUpdateException();
        }
    }

    public List<Integer> getArtistsToSong(int id) {

        List<Integer> artistID = new ArrayList<>();

        try (Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {

            String sql = "SELECT * FROM Song_Artist_Relation WHERE SongId = ?";

            //Connnect prepared stametnt to sql
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setInt(1, id);

            stmt.executeQuery();

            //get the ID from DB
            ResultSet rs = stmt.getResultSet();

            while (rs.next()) {
                artistID.add(rs.getInt(1));
            }


        } catch (SQLException e) {

        }

        return artistID;
    }
}