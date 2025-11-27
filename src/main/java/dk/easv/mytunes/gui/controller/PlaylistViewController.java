package dk.easv.mytunes.gui.controller;
//Project imports
import dk.easv.mytunes.be.Playlist;
import dk.easv.mytunes.gui.model.PlaylistModel;
//Java imports
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class PlaylistViewController implements Initializable {

    @FXML private Label lblTitle;
    @FXML private TextField txtName;
    @FXML private Label lblError;

    private PlaylistModel playlistModel;
    private Playlist playlistToEdit;
    private boolean saveClicked = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            playlistModel = new PlaylistModel();
        } catch (IOException e) {
            showError("Failed to initialize: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sets the playlist to edit
     */
    public void setPlaylist(Playlist playlist) {
        this.playlistToEdit = playlist;

        if (playlist != null) {
            lblTitle.setText("Edit Playlist");
            txtName.setText(playlist.getName());
        } else {
            lblTitle.setText("New Playlist");
        }
    }

    /**
     * Sets the playlist model
     */
    public void setPlaylistModel(PlaylistModel model) {
        this.playlistModel = model;
    }

    /**
     * Handle save button click
     */
    @FXML
    private void handleSave(ActionEvent event) {
        if (validateInput()) {
            try {
                String name = txtName.getText().trim();

                if (playlistToEdit != null) {
                    // Edit existing playlist
                    playlistToEdit.setName(name);
                    playlistModel.updatePlaylist(playlistToEdit);
                } else {
                    // Create new playlist
                    playlistModel.createPlaylist(name);
                }

                saveClicked = true;
                closeWindow();

            } catch (Exception e) {
                showError("Error saving playlist: " + e.getMessage());
            }
        }
    }

    /**
     * Handle cancel button click
     */
    @FXML
    private void handleCancel(ActionEvent event) {
        saveClicked = false;
        closeWindow();
    }

    /**
     * Validates the input
     */
    private boolean validateInput() {
        if (txtName.getText().trim().isEmpty()) {
            showError("Playlist name is required.");
            return false;
        }

        lblError.setVisible(false);
        return true;
    }

    /**
     * Shows error message
     */
    private void showError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
    }

    /**
     * Closes the window
     */
    private void closeWindow() {
        Stage stage = (Stage) txtName.getScene().getWindow();
        stage.close();
    }

    /**
     * Returns whether save was clicked
     */
    public boolean isSaveClicked() {
        return saveClicked;
    }
}