import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Message implements Serializable {
    String a,address;
    int hash, artist,port;
    ArrayList<ArtistName> artists;
    Hashtable hashtable;
    List<Broker> Brokers;
    List<Hashtable> BrokersHashtable;

    public Message(String a){
        this.a = a;
    }

    public Message(int a){
        this.hash = a;
    }

    public Message(int a, int hash){
        this.artist = a;
        this.hash = hash;
    }

    public Message(String a, int port){
        this.address = a;
        this.port = port;
    }

    public Message(ArrayList<ArtistName> artists){
        this.artists = artists;
    }

    public Message(Hashtable hashtable){
        this.hashtable = hashtable;
    }

    public Message(List<Hashtable> BrokersHashtable, List<Broker> Brokers){
        this.BrokersHashtable = BrokersHashtable;
        this.Brokers = Brokers;
    }

    public List<Broker> getBrokers() { return Brokers; }
    public Hashtable getHashtable() { return hashtable; }
    public String toString(){
        return (a);
    }
    public String getAddress() { return address; }
    public int getPort() { return port; }
    public int getHash(){ return hash; }
    public int getArtist(){ return artist; }

}
