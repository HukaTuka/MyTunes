package dk.easv.mytunes.gui.controller;

import dk.easv.mytunes.bll.YouTubePlayer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MyTunesViewController {

    @FXML
    private TableView<?> tblPl;
    @FXML private TableView<?> tblSongs;
    @FXML private ListView<?> lstSongsOnPl;
    @FXML private TextField txtSearch;
    @FXML private Slider sldProgress;
    @FXML private Label lblPlaying;



    @FXML private void btnYoutube(ActionEvent e) {
        new YouTubePlayer().open();
    }
    @FXML private void btnSearch(ActionEvent e) {}
    @FXML private void btnAddNewPl(ActionEvent e) {}
    @FXML private void btnEditPl(ActionEvent e) {}
    @FXML private void btnDeletePl(ActionEvent e) {}
    @FXML private void btnNewSong(ActionEvent e) {}
    @FXML private void btnEditSong(ActionEvent e) {}
    @FXML private void btnDeleteSong(ActionEvent e) {}
    @FXML private void btnPlay(ActionEvent e) {}
    @FXML private void btnSkip(ActionEvent e) {}
    @FXML private void btnPrevious(ActionEvent e) {}
    @FXML private void btnAddToPL(ActionEvent e) {}
    @FXML private void btnMoveUp(ActionEvent e) {}
    @FXML private void btnMoveDown(ActionEvent e) {}
}
