package dk.easv.mytunes.bll;

import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.dal.IPlaylistDataAccess;
import dk.easv.mytunes.dal.PlaylistDAO;

import java.util.List;

public class PlaylistManager {

    private static IPlaylistDataAccess playlistDAO;

    public PlaylistManager(){
        playlistDAO = new PlaylistDAO();
    }
    public List<Playlist> getAllPlaylists() throws Exception {
        return playlistDAO.getAllPlaylists();
    }

    public Playlist createPlaylist(Playlist newPlaylist) throws Exception {
        return playlistDAO.createPlaylist(newPlaylist);
    }

    public static void updatePlaylist(Playlist updatePlaylist) throws Exception {
        playlistDAO.updatePlaylist(updatePlaylist);
    }

    public static void deletePlaylist(Playlist selectedPlaylist) throws Exception {
        playlistDAO.deletePlaylist(selectedPlaylist);
    }
}
