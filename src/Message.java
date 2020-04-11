import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Message implements Serializable {
    String a, address;
    int hash, artist,port, artistHash;
    ArrayList<String> artists;
    ArrayList<Integer> hashtable;
    List<Broker> Brokers;
    List<ArrayList<Integer>> BrokersHashtable;
    List<Broker> registeredBrokers;
    private MusicChunk Chunk;
    boolean t;

    public Message(String a){
        this.a = a;
    }

    public Message(int artistHash){
        this.artistHash = artistHash;
    }

    public Message(int a, int hash){
        this.artist = a;
        this.hash = hash;
    }

    public Message(String a, int port){
        this.address = a;
        this.port = port;
    }

    public Message(ArrayList<String> artists,String a){
        this.artists = artists;
        this.a = a;
    }

    public Message(ArrayList<Integer> hashtable){
        this.hashtable = hashtable;
    }

    public Message(List<ArrayList<Integer>> BrokersHashtable, List<Broker> Brokers,boolean t){
        this.BrokersHashtable = BrokersHashtable;
        this.Brokers = Brokers;
        this.t=t;
    }

    public Message(MusicChunk Chunk){
        this.Chunk = Chunk;
    }

    public List<ArrayList<Integer>> getBrokersHashtable() { return BrokersHashtable; }
    public MusicChunk getChunk() { return Chunk; }
    public List<Broker> getBrokers() { return Brokers; }
    public ArrayList<Integer> getHashtable() { return hashtable; }
    public String toString(){
        return (a);
    }
    public String getAddress() { return address; }
    public int getPort() { return port; }
    public int getHash(){ return hash; }
    public ArrayList<String> getArtists(){ return artists; }
    public int getArtistHash(){ return artistHash; }
    public boolean getBoolean(){
        return t;
    }

}
