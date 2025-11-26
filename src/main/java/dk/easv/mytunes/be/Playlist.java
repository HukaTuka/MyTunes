package dk.easv.mytunes.be;

// Java Imports
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a playlist containing an ordered list of songs
 */
public class Playlist {
    private int id;
    private String name;
    private List<Song> songs;

    public Playlist(int id, String name) {
        this.id = id;
        this.name = name;
        this.songs = new ArrayList<>();
    }

    public Playlist(String name) {
        this(-1, name);
    }

    public void addSong(Song song) {
        songs.add(song);
    }

    public void removeSong(Song song) {
        songs.remove(song);
    }

    public int getTotalDuration() {
        return songs.stream().mapToInt(Song::getDuration).sum();
    }
    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }

    @Override
    public String toString() {
        return name;
    }

}
