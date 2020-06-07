package MyPackage;

import java.io.Serializable;

public class ArtistName implements Serializable {
    String artistName;

    ArtistName(String artistName){
        this.artistName = artistName;
    }

    public String getArtistName() {
        return artistName;
    }
}
