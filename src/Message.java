import java.io.Serializable;

public class Message implements Serializable {
    String a;
    int hash;
    ArtistName artist;

    public Message(String a){
        this.a = a;
    }

    public String toString(){
        return (a);
    }

    public Message(ArtistName a, int hash){
        this.artist = a;
        this.hash = hash;
    }

    public int getHash(){ return hash; }
    public ArtistName getArtist(){ return artist; }

}
