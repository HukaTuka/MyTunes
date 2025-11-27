package dk.easv.mytunes.gui.controller;
//Package imports
import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.gui.model.PlaylistModel;
import dk.easv.mytunes.gui.model.SongModel;
//Java imports
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class MyTunesViewController implements Initializable {

    @FXML
    private TableView<Playlist> tblPl;
    @FXML private TableView<Song> tblSongs;
    @FXML private ListView<Playlist> lstSongsOnPl;
    @FXML private TextField txtSearch;
    @FXML private Slider sldProgress;
    @FXML private Label lblPlaying;
    @FXML private TableColumn <Song, String> colSongTitle;
    @FXML private TableColumn <Song, String> colSongArtist;
    @FXML private TableColumn <Song, String> colSongCat;
    @FXML private TableColumn <Song, Integer> colSongTime;
    @FXML private TableColumn <Playlist, String> colPlName;
    @FXML private TableColumn <Playlist, Integer> colPlSongs;
    @FXML private TableColumn <Playlist, Integer> colPlTime;
    private SongModel songModel;
    private PlaylistModel playlistModel;

    public MyTunesViewController()  {
        try {
            songModel = new SongModel();
        } catch (Exception e) {
            displayError(e);
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        colSongTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colSongArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        colSongCat.setCellValueFactory(new PropertyValueFactory<>("category"));
        colSongTime.setCellValueFactory(new PropertyValueFactory<>("duration"));

        //colPlName.setCellValueFactory(new PropertyValueFactory<>("name"));
        //colPlSongs.setCellValueFactory(new PropertyValueFactory<>("songs"));
        //colPlTime.setCellValueFactory(new PropertyValueFactory<>("time"));

        // Create FilteredList wrapping the ObservableList
        FilteredList<Song> filteredList = new FilteredList<>(songModel.getObservableSongs(), p -> true);

        // Set up the selection listener
        tblSongs.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, selectedSong) ->
        {
            if (selectedSong != null) {
                lblPlaying.setText(selectedSong.getTitle() + " - " + selectedSong.getArtist());
            }
        });

        // Set up the search filter
        txtSearch.textProperty().addListener((observableValue, oldValue, newValue) -> {
            filteredList.setPredicate(song -> {
                // If filter text is empty, display all songs.
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                // check titel
                if (song.getTitle().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                    // check artist
                } else if (song.getArtist().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                    // check category
                } else if (song.getCategory().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                    //check duration
                } else return Integer.toString(song.getDuration()).contains(lowerCaseFilter);
            });
        });

        // Wrap the FilteredList in a SortedList
        SortedList<Song> sortedData = new SortedList<>(filteredList);
        sortedData.comparatorProperty().bind(tblSongs.comparatorProperty());
        tblSongs.setItems(sortedData);
    }

    private void displayError(Throwable t)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something went wrong");
        alert.setHeaderText(t.getMessage());
        alert.showAndWait();
    }

    @FXML private void btnSearch(ActionEvent e) {}
    @FXML private void btnAddNewPl(ActionEvent e) {}
    @FXML private void btnEditPl(ActionEvent e) {}
    @FXML private void btnDeletePl(ActionEvent e)
    {
        Playlist selectedPl = tblPl.getSelectionModel().getSelectedItem();

        if (selectedPl != null) {
            try {
                playlistModel.deletePlaylist(selectedPl);
            }
            catch (Exception err){
                displayError(err);
            }

        }
    }

    @FXML private void btnNewSong(ActionEvent e) {}
    @FXML private void btnEditSong(ActionEvent e) {}

    @FXML private void btnDeleteSong(ActionEvent e)
    {
        Song selectedSong = tblSongs.getSelectionModel().getSelectedItem();

        if (selectedSong != null) {
            try {
                songModel.deleteSong(selectedSong, true);
            }
            catch (Exception err){
                displayError(err);
            }

        }
    }
    @FXML private void btnPlay(ActionEvent e) {}
    @FXML private void btnSkip(ActionEvent e) {}
    @FXML private void btnPrevious(ActionEvent e) {}
    @FXML private void btnAddToPL(ActionEvent e) {}
    @FXML private void btnMoveUp(ActionEvent e) {}
    @FXML private void btnMoveDown(ActionEvent e) {}
}