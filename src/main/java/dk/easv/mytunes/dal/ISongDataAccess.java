package dk.easv.mytunes.dal;
//Project imports
import dk.easv.mytunes.be.Song;
//Java imports
import java.sql.SQLException;
import java.util.List;

public interface ISongDataAccess {

    Song createSong(Song song) throws SQLException;

    List<Song> getAllSongs() throws SQLException;

    Song getSongById(int id) throws SQLException;

    void updateSong(Song song) throws SQLException;

    void deleteSong(int songId) throws SQLException;

    List<Song> searchSongs(String query) throws SQLException;
}