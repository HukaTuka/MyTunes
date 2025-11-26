package dk.easv.mytunes.dal;

//Package imports
import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;
//Java imports
import java.sql.SQLException;
import java.util.List;

public interface IPlaylistDataAccess {

    //Playlist operations
    List<Playlist> getAllPlaylists() throws SQLException;
    Playlist createPlaylist(Playlist newPlaylist) throws SQLException;
    void updatePlaylist(Playlist playlist) throws SQLException;
    void deletePlaylist(Playlist playlist) throws SQLException;

    //Playlist-Song relationship operations
    List<Song> getSongsInPlaylist(int playlistId) throws SQLException;
    void addSongToPlaylist(int playlistId, int songId) throws SQLException;
    void removeSongFromPlaylist(int playlistId, int songId) throws SQLException;
    void moveSongUp(int playlistId, int songId) throws SQLException;
    void moveSongDown(int playlistId, int songId) throws SQLException;

}
