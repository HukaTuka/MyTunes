package dk.easv.mytunes.dal;

import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;

import java.sql.SQLException;
import java.util.List;

public class PlaylistDAO implements IPlaylistDataAccess {

    public List<Playlist> getAllPlaylists() {
        return List.of();
    }

    public Playlist createPlaylist(Playlist newPlaylist) throws SQLException {
        return null;
    }

    public void updatePlaylist(Playlist playlist) throws SQLException {

    }
    @Override
    public void deletePlaylist(Playlist playlist) throws SQLException {

    }

    @Override
    public List<Song> getSongsInPlaylist(int playlistId) throws SQLException {
        return List.of();
    }

    @Override
    public void addSongToPlaylist(int playlistId, int songId) throws SQLException {

    }

    @Override
    public void removeSongFromPlaylist(int playlistId, int songId) throws SQLException {

    }

    @Override
    public void moveSongUp(int playlistId, int songId) throws SQLException {

    }

    @Override
    public void moveSongDown(int playlistId, int songId) throws SQLException {

    }
}
