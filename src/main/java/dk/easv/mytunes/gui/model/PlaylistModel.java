package dk.easv.mytunes.gui.model;

// Package imports
import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.bll.PlaylistManager;

// Java imports
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;

public class PlaylistModel
{
    private ObservableList<Playlist> playlistToBeViewed;
    private FilteredList<Playlist> filteredList;
    private PlaylistManager playlistManager;

    public PlaylistModel() throws Exception
    {
        playlistManager = new PlaylistManager();
        playlistToBeViewed = FXCollections.observableArrayList();
        playlistToBeViewed.addAll(playlistManager.getAllPlaylists());
        filteredList = new FilteredList<>(playlistToBeViewed);
    }
    public FilteredList<Playlist> getObservablePlaylists()
        {
        return filteredList;
        }
    public Playlist createPlaylist(Playlist newPlaylist) throws Exception
    {
        Playlist playlistCreated = playlistManager.createPlaylist(newPlaylist);
        playlistToBeViewed.add(playlistCreated);
        return playlistCreated;
    }

    public void updatePlaylist(Playlist playlistUpdate) throws Exception
    {
        PlaylistManager.updatePlaylist(playlistUpdate);

        int index = playlistToBeViewed.indexOf(playlistUpdate);
        playlistToBeViewed.set(index, playlistUpdate);
    }

    public void deletePlaylist(Playlist selectedPlaylist) throws Exception
    {
        PlaylistManager.deletePlaylist(selectedPlaylist);
        playlistToBeViewed.remove(selectedPlaylist);
    }

}
