package dk.easv.mytunes.dal;

//Package imports
import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;
//Java imports
import java.sql.SQLException;
import java.util.List;

public interface IMytunesDataAccess {

    // Song operations
    Song createSong(Song song) throws SQLException;
    List<Song> getAllSongs() throws Exception;
    Song getSongById(int id) throws SQLException;
    void updateSong(Song song) throws SQLException;
    void deleteSong(int songId) throws SQLException;
    List<Song> searchSongs(String query) throws SQLException;

    List<Playlist> getAllPlaylists() throws SQLException;
    Playlist createPlaylist(Playlist newPlaylist) throws SQLException;
    void updatePlaylist(Playlist playlist) throws SQLException;
    void deletePlaylist(Playlist playlist) throws SQLException;



}
