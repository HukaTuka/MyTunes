package dk.easv.mytunes.bll;

import dk.easv.mytunes.be.Song;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.io.File;
import java.util.List;

/**
 * Handles music playback functionality
 */
public class MusicPlayer {
    private MediaPlayer mediaPlayer;
    private List<Song> currentPlaylist;
    private int currentIndex = -1;
    private boolean autoPlayNext = true;
    private Runnable onSongChanged;

    /**
     * Loads and plays a song
     */
    public void playSong(Song song) {
        if (song == null) return;

        stopCurrentSong();

        try {
            File file = new File(song.getFilePath());
            Media media = new Media(file.toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            // Set up end of media listener for auto-play
            mediaPlayer.setOnEndOfMedia(() -> {
                if (autoPlayNext) {
                    playNext();
                }
            });

            mediaPlayer.play();
        } catch (Exception e) {
            System.err.println("Error playing song: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Plays a song from a playlist at a specific index
     */
    public void playSongFromPlaylist(List<Song> playlist, int index) {
        if (playlist == null || playlist.isEmpty() || index < 0 || index >= playlist.size()) {
            return;
        }

        this.currentPlaylist = playlist;
        this.currentIndex = index;
        playSong(playlist.get(index));
    }

    /**
     * Plays the next song in the playlist
     */
    public void playNext() {
        if (currentPlaylist == null || currentPlaylist.isEmpty()) {
            return;
        }

        currentIndex++;
        if (currentIndex >= currentPlaylist.size()) {
            currentIndex = 0; // Loop back to start
        }

        playSong(currentPlaylist.get(currentIndex));

        if (onSongChanged != null) {
            onSongChanged.run();
        }
    }

    /**
     * Plays the previous song in the playlist
     */
    public void playPrevious() {
        if (currentPlaylist == null || currentPlaylist.isEmpty()) {
            return;
        }

        currentIndex--;
        if (currentIndex < 0) {
            currentIndex = currentPlaylist.size() - 1; // Loop to end
        }

        playSong(currentPlaylist.get(currentIndex));

        if (onSongChanged != null) {
            onSongChanged.run();
        }
    }

    /**
     * Pauses the current song
     */
    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    /**
     * Resumes playback
     */
    public void resume() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
        }
    }

    /**
     * Stops the current song
     */
    public void stop() {
        stopCurrentSong();
    }

    /**
     * Sets the volume (0.0 to 1.0)
     */
    public void setVolume(double volume) {
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(Math.max(0.0, Math.min(1.0, volume)));
        }
    }

    /**
     * Gets the current volume
     */
    public double getVolume() {
        return mediaPlayer != null ? mediaPlayer.getVolume() : 0.5;
    }

    /**
     * Seeks to a specific time in the song
     */
    public void seek(double seconds) {
        if (mediaPlayer != null) {
            mediaPlayer.seek(Duration.seconds(seconds));
        }
    }

    /**
     * Gets the current playback time in seconds
     */
    public double getCurrentTime() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentTime().toSeconds();
        }
        return 0;
    }

    /**
     * Gets the total duration of the current song in seconds
     */
    public double getTotalDuration() {
        if (mediaPlayer != null && mediaPlayer.getTotalDuration() != null) {
            return mediaPlayer.getTotalDuration().toSeconds();
        }
        return 0;
    }

    /**
     * Checks if a song is currently playing
     */
    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }

    /**
     * Gets the current song being played
     */
    public Song getCurrentSong() {
        if (currentPlaylist != null && currentIndex >= 0 && currentIndex < currentPlaylist.size()) {
            return currentPlaylist.get(currentIndex);
        }
        return null;
    }

    /**
     * Sets a callback for when the song changes
     */
    public void setOnSongChanged(Runnable callback) {
        this.onSongChanged = callback;
    }

    /**
     * Stops and disposes of the current MediaPlayer
     */
    private void stopCurrentSong() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }

    /**
     * Cleanup method
     */
    public void shutdown() {
        stopCurrentSong();
        currentPlaylist = null;
        currentIndex = -1;
    }
}