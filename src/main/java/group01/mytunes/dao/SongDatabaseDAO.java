package group01.mytunes.dao;

import com.microsoft.sqlserver.jdbc.SQLServerException;
import group01.mytunes.Main;
import group01.mytunes.Models.Song;
import group01.mytunes.dao.interfaces.ISongDAO;
import group01.mytunes.database.DatabaseConnectionHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SongDatabaseDAO implements ISongDAO {

    @Override
    public List<Song> getAllSongInfo() {
        try(Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "EXEC spGetAllSongInfo;";
            Statement statement = connection.createStatement();

            var resultSet = statement.executeQuery(query);
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

    @Override
    public Song getSongById(int id) {
        try(Connection connection = DatabaseConnectionHandler.getInstance().getConnection()) {
            String query = "EXEC spGetSongById ?;";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, id);

            var result = statement.executeQuery();

            if(result.next()) {
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
}
