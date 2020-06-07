package MyPackage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;

public class MusicFile {
    private static final long serialVersionUID = 3828930004421967914L;
    private String filename; // the absolute path to file
    private String trackName;
    private String artistName;
    public static final int MAXIMUM_CHUNK_SIZE = 524288; // chunk size in bytes 512 * 1024
    private ArrayList<MusicChunk> chunks;

    MusicFile(String trackName,String artistName, String filename){
        this.trackName = trackName;
        this.artistName = artistName;
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public ArrayList<MusicChunk> createChunks () {

        try {
            //Path of folder of the song plus tracks name.
            Path p = Paths.get("D:\\Nikos\\Documents\\οπα\\Κατανεμημένα συστήματα\\Project\\Datasets\\dataset2\\dataset2\\" + filename);

            byte [] bytesOfSong = Files.readAllBytes(p);

            chunks = new ArrayList<MusicChunk>();
            MusicChunk ch;
            if (bytesOfSong.length <= MAXIMUM_CHUNK_SIZE) {
                ch = new MusicChunk(this.artistName, this.trackName, bytesOfSong, 0);
                ch.setTotalPartitions(1);
                chunks.add(ch);
                return chunks;
            }
            int numberOfChunks = bytesOfSong.length / MAXIMUM_CHUNK_SIZE; // If last chunk has size less than 512 KB then total number of chunks is equal to numberOfChunks + 1.
            byte [] partition;
            for (int i=0; i<numberOfChunks; i++) {
                partition = Arrays.copyOfRange(bytesOfSong, i * MAXIMUM_CHUNK_SIZE, (i + 1) * MAXIMUM_CHUNK_SIZE);
                ch = new MusicChunk(this.artistName, this.trackName, partition, i);
                chunks.add(ch);
            }
            if (bytesOfSong.length % MAXIMUM_CHUNK_SIZE > 0) {
                partition = Arrays.copyOfRange (bytesOfSong, numberOfChunks * MAXIMUM_CHUNK_SIZE, bytesOfSong.length);
                ch = new MusicChunk(this.artistName, this.trackName, partition, numberOfChunks);
                numberOfChunks++;
                chunks.add(ch);
            }
            for (int i=0; i<chunks.size(); i++) {
                chunks.get(i).setTotalPartitions(numberOfChunks);
            }
            return chunks;

        } catch (IOException ioe) {
            ioe.getMessage();
            return null;
        }catch (OutOfMemoryError out) {
            return null;
        }catch (SecurityException sec) {
            return null;
        }
    }

    public MusicFile(String trackName){
        this.trackName = trackName;
    }

    //Getters
    public String getArtistName() {
        return artistName;
    }

    public String getTrackName() {
        return trackName;
    }

    //Setters

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }



}