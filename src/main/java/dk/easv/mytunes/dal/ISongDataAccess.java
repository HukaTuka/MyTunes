package dk.easv.mytunes.dal;

import dk.easv.mytunes.be.Song;

import java.sql.SQLException;
import java.util.List;

public interface ISongDataAccess
{
    // Song operations
    Song createSong(Song song) throws SQLException;
    List<Song> getAllSongs() throws Exception;
    Song getSongById(int id) throws SQLException;
    void updateSong(Song song) throws SQLException;
    void deleteSong(int songId) throws SQLException;
    List<Song> searchSongs(String query) throws SQLException;
}
