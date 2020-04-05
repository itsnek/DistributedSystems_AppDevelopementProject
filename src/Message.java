import java.io.Serializable;

public class Message implements Serializable {
    String a;
    int hash, artist;

    public Message(String a){
        this.a = a;
    }

    public Message(int a){
        this.hash = a;
    }

    public String toString(){
        return (a);
    }

    public Message(int a, int hash){
        this.artist = a;
        this.hash = hash;
    }

    public int getHash(){ return hash; }
    public int getArtist(){ return artist; }

}
