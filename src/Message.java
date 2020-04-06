import java.io.Serializable;

public class Message implements Serializable {
    String a,address;
    int hash, artist,port;

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
    public Message(String a, int port){
        this.address = a;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public int getHash(){ return hash; }
    public int getArtist(){ return artist; }

}
