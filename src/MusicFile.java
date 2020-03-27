public class MusicFile {
    String trackName;
    String artistName;
    String albumInfo;
    String genre;
    byte[] musicFileExtract;

    MusicFile(String trackName,String artistName,String albumInfo,String genre,byte[] musicFileExtract){
        this.trackName = trackName;
        this.artistName = artistName;
        this.musicFileExtract = musicFileExtract;
    }

    MusicFile(String trackName){
        this.trackName = trackName;
    }

    //Getters
    public String getArtistName() {
        return artistName;
    }

    public byte[] getMusicFileExtract() {
        return musicFileExtract;
    }

    public String getTrackName() {
        return trackName;
    }

    //Setters
    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public void setMusicFileExtract(byte[] musicFileExtract) {
        this.musicFileExtract = musicFileExtract;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

}
