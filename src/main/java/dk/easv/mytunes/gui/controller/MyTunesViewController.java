package dk.easv.mytunes.gui.controller;
//Package imports
import dk.easv.mytunes.MyTunesMain;
import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.be.Song;
import dk.easv.mytunes.gui.MusicPlayer;
import dk.easv.mytunes.gui.model.PlaylistModel;
import dk.easv.mytunes.gui.model.SongModel;
//Java imports
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MyTunesViewController implements Initializable {

    //Playlists
    @FXML private ListView<Playlist> lstPl;
    //Songs in Playlist Table
    @FXML private TableView<Song> tblSongsOnPl;
    @FXML private TableColumn<Song, String> colPlSongTitle;
    @FXML private TableColumn<Song, String> colPlSongArtist;
    @FXML private TableColumn<Song, String> colPlSongTime;
    //All Songs Table
    @FXML private TableView<Song> tblSongs;
    @FXML private TableColumn<Song, String> colSongTitle;
    @FXML private TableColumn<Song, String> colSongArtist;
    @FXML private TableColumn<Song, String> colSongCat;
    @FXML private TableColumn<Song, String> colSongTime;
    //Search/Filter
    @FXML private TextField txtSearch;
    @FXML private Button btnSearch;
    //Player Controls
    @FXML private Slider sldProgress;
    @FXML private Slider sldVolume;
    @FXML private Label lblPlaying;
    @FXML private Label lblCurrentTime;
    @FXML private Label lblTotalTime;
    @FXML private Button btnPlayPause;
    // Models
    private SongModel songModel;
    private PlaylistModel playlistModel;
    private MusicPlayer musicPlayer;
    // State
    private boolean isFiltering = false;
    private Timeline progressTimeline;
    private ObservableList<Song> currentPlaylistSongs;
    private boolean isSeeking = false;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Initialize models
            songModel = new SongModel();
            playlistModel = new PlaylistModel();
            musicPlayer = new MusicPlayer();

            // Setup table columns
            setupTableColumns();

            // Bind data
            lstPl.setItems(playlistModel.getObservablePlaylists());
            tblSongs.setItems(songModel.getObservableSongs());

            // Setup listeners
            setupListeners();

            // Setup progress timeline
            setupProgressTimeline();
            setupSeekFunctionality();

        } catch (Exception e) {
            showError("Initialization Error", "Failed to initialize application: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void setupTableColumns() {
        // Songs in Playlist table columns
        colPlSongTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colPlSongArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        colPlSongTime.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getFormattedDuration()
                )
        );

        // All Songs table columns
        colSongTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colSongArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        colSongCat.setCellValueFactory(new PropertyValueFactory<>("category"));
        colSongTime.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        cellData.getValue().getFormattedDuration()
                )
        );
    }

    private void setupListeners() {
        // Playlist selection listener
        lstPl.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadPlaylistSongs(newVal);
            }
        });

        // Double-click to play from songs table
        tblSongs.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tblSongs.getSelectionModel().getSelectedItem() != null) {
                playSongFromTable();
            }
        });

        // Double-click to play from playlist table
        tblSongsOnPl.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tblSongsOnPl.getSelectionModel().getSelectedItem() != null) {
                playSongFromPlaylist();
            }
        });

        // Volume slider listener
        sldVolume.valueProperty().addListener((obs, oldVal, newVal) -> {
            musicPlayer.setVolume(newVal.doubleValue() / 100.0);
        });
    }

    private void setupProgressTimeline() {
        progressTimeline = new Timeline(new KeyFrame(Duration.millis(100), event -> {
            if (musicPlayer.isPlaying()) {
                double current = musicPlayer.getCurrentTime();
                double total = musicPlayer.getTotalDuration();

                if (total > 0) {
                    sldProgress.setValue((current / total) * 100);
                    lblCurrentTime.setText(formatTime(current));
                    lblTotalTime.setText(formatTime(total));
                }
            }
        }));
        progressTimeline.setCycleCount(Timeline.INDEFINITE);
        progressTimeline.play();
    }
    private void setupSeekFunctionality() {

        //mouse pressed start seeking
        sldProgress.setOnMousePressed((MouseEvent event) -> {
            if (musicPlayer.isPlaying() || musicPlayer.getCurrentSong() != null) {
                isSeeking = true;
            }
        });

        // Mouse drag update position
        sldProgress.setOnMouseDragged((MouseEvent event) -> {
            if (isSeeking) {
                seekToPosition();
            }
        });

        //seek to position
        sldProgress.setOnMouseReleased((MouseEvent event) -> {
            if (isSeeking) {
                seekToPosition();
                isSeeking = false;
            }
        });

        //handle clicking directly on the slider
        sldProgress.setOnMouseClicked((MouseEvent event) -> {
            if (musicPlayer.getCurrentSong() != null) {
                seekToPosition();
            }
        });
    }

    private void seekToPosition() {
        double totalDuration = musicPlayer.getTotalDuration();
        if (totalDuration > 0) {
            double seekPosition = (sldProgress.getValue() / 100.0) * totalDuration;
            musicPlayer.seek(seekPosition);
            lblCurrentTime.setText(formatTime(seekPosition));
        }
    }
    private String formatTime(double seconds) {
        int mins = (int) seconds / 60;
        int secs = (int) seconds % 60;
        return String.format("%d:%02d", mins, secs);
    }

    private void loadPlaylistSongs(Playlist playlist) {
        if (currentPlaylistSongs == null) {
            currentPlaylistSongs = javafx.collections.FXCollections.observableArrayList();
            tblSongsOnPl.setItems(currentPlaylistSongs);
        }

        try {
            currentPlaylistSongs.setAll(
                    playlistModel.getSongsInPlaylist(playlist)
            );
        } catch (Exception e) {
            showError("Error", "Failed to load playlist songs: " + e.getMessage());
        }
    }

    private void playSongFromTable() {
        Song selected = tblSongs.getSelectionModel().getSelectedItem();
        if (selected != null) {
            musicPlayer.playSongFromPlaylist(tblSongs.getItems(),
                    tblSongs.getItems().indexOf(selected));
            updateNowPlaying();
            btnPlayPause.setText("⏸");
        }
    }

    private void playSongFromPlaylist() {
        Song selected = tblSongsOnPl.getSelectionModel().getSelectedItem();
        if (selected != null && currentPlaylistSongs != null) {
            musicPlayer.playSongFromPlaylist(currentPlaylistSongs,
                    currentPlaylistSongs.indexOf(selected));
            updateNowPlaying();
            btnPlayPause.setText("⏸");
        }
    }

    private void updateNowPlaying() {
        Song current = musicPlayer.getCurrentSong();
        if (current != null) {
            lblPlaying.setText("Now Playing: " + current.getTitle() + " - " + current.getArtist());
            sldProgress.setDisable(false);
        } else {
            sldProgress.setDisable(true);
            lblPlaying.setText("No song playing");
        }
    }

    @FXML
    private void btnSearch(ActionEvent e) {
        try {
            if(!isFiltering) {
                //Applies filter using DB search
                String query = txtSearch.getText().trim();
                songModel.searchSongs(query);
                btnSearch.setText("Clear");
                isFiltering = true;
                txtSearch.setDisable(true);
            } else {
                //Clears filter and reload all songs
                songModel.loadAllSongs();
                txtSearch.clear();
                btnSearch.setText("Filter");
                isFiltering = false;
                txtSearch.setDisable(false);
            }
        } catch (Exception ex) {
            showError("Error", "Failed to load songs: " + ex.getMessage());
        }
    }
    @FXML
    private void btnAddNewPl(ActionEvent e) {
        openPlaylistWindow(null);
    }

    @FXML
    private void btnEditPl(ActionEvent e) {
        Playlist selected = lstPl.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("No Selection", "Please select a playlist to edit");
            return;
        }
        openPlaylistWindow(selected);
    }

    @FXML
    private void btnDeletePl(ActionEvent e) {
        Playlist selected = lstPl.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("No Selection", "Please select a playlist to delete");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete Playlist");
        confirm.setContentText("Are you sure you want to delete: " + selected.getName() + "?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                playlistModel.deletePlaylist(selected);
                tblSongsOnPl.getItems().clear();
            } catch (Exception ex) {
                showError("Error", "Failed to delete playlist: " + ex.getMessage());
            }
        }
    }

    @FXML
    private void btnRemoveFromPl(ActionEvent e) {
        Playlist selectedPl = lstPl.getSelectionModel().getSelectedItem();
        Song selectedSong = tblSongsOnPl.getSelectionModel().getSelectedItem();

        if (selectedPl == null || selectedSong == null) {
            showWarning("No Selection", "Please select a song in the playlist to remove");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete Song in Playlist");
        confirm.setContentText("Are you sure you want to delete: " + selectedSong.getTitle() + "?");

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                playlistModel.removeSongFromPlaylist(selectedPl, selectedSong);
                loadPlaylistSongs(selectedPl);
            } catch (Exception ex) {
                showError("Error", "Failed to remove song from playlist: " + ex.getMessage());
            }
        }
    }

    @FXML
    private void btnAddToPL(ActionEvent e) {
        Playlist selectedPl = lstPl.getSelectionModel().getSelectedItem();
        Song selectedSong = tblSongs.getSelectionModel().getSelectedItem();

        if (selectedPl == null) {
            showWarning("No Selection", "Please select a playlist");
            return;
        }
        if (selectedSong == null) {
            showWarning("No Selection", "Please select a song");
            return;
        }
        if (currentPlaylistSongs != null && currentPlaylistSongs.contains(selectedSong)) {
            showWarning("Duplicate", "Song already exists in this playlist");
            return;
        }

        try {
            playlistModel.addSongToPlaylist(selectedPl, selectedSong);
            loadPlaylistSongs(selectedPl);
        } catch (Exception ex) {
            showError("Error", "Failed to add song to playlist: " + ex.getMessage());
        }
    }
    @FXML private void btnNewSong(ActionEvent e)
    {
        openSongWindow(null);
    }
    @FXML
    private void btnEditSong(ActionEvent e) {
        Song selected = tblSongs.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("No Selection", "Please select a song to edit");
            return;
        }
        openSongWindow(selected);
    }

    @FXML
    private void btnDeleteSong(ActionEvent e) {
        Song selected = tblSongs.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("No Selection", "Please select a song to delete");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Deletion");
        confirm.setHeaderText("Delete Song");
        confirm.setContentText("Delete: " + selected.getTitle() + "?");

        ButtonType deleteFile = new ButtonType("Delete File Too");
        ButtonType deleteLibrary = new ButtonType("Library Only");
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        confirm.getButtonTypes().setAll(deleteFile, deleteLibrary, cancel);

        Optional<ButtonType> result = confirm.showAndWait();

        try {
            if (result.isPresent() && result.get() == deleteFile) {
                songModel.deleteSong(selected, true);
            } else if (result.isPresent() && result.get() == deleteLibrary) {
                songModel.deleteSong(selected, false);
            }
        } catch (Exception ex) {
            showError("Error", "Failed to delete song: " + ex.getMessage());
        }
    }


    @FXML
    private void btnPlay(ActionEvent e) {
        if (musicPlayer.isPlaying()) {
            musicPlayer.pause();
            btnPlayPause.setText("▶");
        } else {
            if (musicPlayer.getCurrentSong() == null) {
                // Try to play selected song
                Song selected = tblSongs.getSelectionModel().getSelectedItem();
                if (selected == null) {
                    selected = tblSongsOnPl.getSelectionModel().getSelectedItem();
                }
                if (selected != null) {
                    if (tblSongsOnPl.isFocused() && currentPlaylistSongs != null) {
                        playSongFromPlaylist();
                    } else {
                        playSongFromTable();
                    }
                }
            } else {
                musicPlayer.resume();
                btnPlayPause.setText("⏸");
            }
        }
    }
    @FXML
    private void btnStop(ActionEvent e) {
        musicPlayer.stop();
        btnPlayPause.setText("▶");
        lblPlaying.setText("No song playing");
        lblCurrentTime.setText("0:00");
        lblTotalTime.setText("0:00");
        sldProgress.setValue(0);
        sldProgress.setDisable(true);
    }
    @FXML
    private void btnSkip(ActionEvent e) {
        musicPlayer.playNext();
        updateNowPlaying();
        btnPlayPause.setText("⏸");
    }

    @FXML
    private void btnPrevious(ActionEvent e) {
        musicPlayer.playPrevious();
        updateNowPlaying();
        btnPlayPause.setText("⏸");
    }
    @FXML
    private void btnMoveUp(ActionEvent e) {
        Playlist selectedPl = lstPl.getSelectionModel().getSelectedItem();
        Song selectedSong = tblSongsOnPl.getSelectionModel().getSelectedItem(); // Changed from tblSongs

        if (selectedPl == null || selectedSong == null) {
            return;
        }

        try {
            playlistModel.moveSongUp(selectedPl, selectedSong);
            loadPlaylistSongs(selectedPl);
            tblSongsOnPl.getSelectionModel().select(selectedSong);
        } catch (Exception ex) {
            showError("Error", "Failed to move song: " + ex.getMessage());
        }
    }

    @FXML
    private void btnMoveDown(ActionEvent e) {
        Playlist selectedPl = lstPl.getSelectionModel().getSelectedItem();
        Song selectedSong = tblSongsOnPl.getSelectionModel().getSelectedItem(); // Changed from tblSongs

        if (selectedPl == null || selectedSong == null) {
            return;
        }

        try {
            playlistModel.moveSongDown(selectedPl, selectedSong);
            loadPlaylistSongs(selectedPl);
            tblSongsOnPl.getSelectionModel().select(selectedSong);
        } catch (Exception ex) {
            showError("Error", "Failed to move song: " + ex.getMessage());
        }
    }

    private void openSongWindow(Song song) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dk/easv/mytunes/views/SongView.fxml"));
            Parent root = loader.load();

            SongViewController controller = loader.getController();
            controller.setSongModel(songModel);
            controller.setSong(song);

            Stage stage = new Stage();
            stage.setTitle(song == null ? "New Song" : "Edit Song");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException ex) {
            showError("Error", "Failed to open song window: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void openPlaylistWindow(Playlist playlist) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/dk/easv/mytunes/views/PlaylistView.fxml"));
            Parent root = loader.load();

            PlaylistViewController controller = loader.getController();
            controller.setPlaylistModel(playlistModel);
            controller.setPlaylist(playlist);

            Stage stage = new Stage();
            stage.setTitle(playlist == null ? "New Playlist" : "Edit Playlist");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException ex) {
            showError("Error", "Failed to open playlist window: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    @FXML
    private void btnYoutube(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MyTunesMain.class.getResource("views/YouTubePlayerView.fxml"));


            Stage stage = new Stage();
            Scene scene = new Scene(fxmlLoader.load());
            stage.setTitle("YouTube Player");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            System.err.println("Error loading YouTube player FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void btnExit(ActionEvent e) {
        musicPlayer.shutdown();
        System.exit(0);
    }

    @FXML
    private void btnAbout(ActionEvent e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About myTunes");
        alert.setHeaderText("myTunes Music Player");
        alert.setContentText("A JavaFX music player application\nVersion 1.0\n\nDeveloped by: Jon, Tobias, René & Felix");
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

