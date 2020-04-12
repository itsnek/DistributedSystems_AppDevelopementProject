import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Message implements Serializable {
    //private static final long serialVersionUID = 7526472295622776147L;
    String a, address,song;
    int hash, artist,port, artistHash,myHash;
    ArrayList<String> artists;
    ArrayList<Integer> hashtable;
    ArrayList<Broker> Brokers;
    List<ArrayList<Integer>> BrokersHashtable;
    List<Broker> registeredBrokers;
    MusicChunk Chunk;
    boolean t;

    // CONSTRUCTORS

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

    public Message(String a, String song){
        this.a = a;
        this.song = song;
    }

    public Message(ArrayList<String> artists,String a){
        this.artists = artists;
        this.a = a;
    }

    public Message(ArrayList<Integer> hashtable){
        this.hashtable = hashtable;
    }

    public Message(ArrayList<Integer> hashtable,int myHash){
        this.hashtable = hashtable;
        this.myHash = myHash;
    }

    public Message(List<ArrayList<Integer>> BrokersHashtable, ArrayList<Broker> Brokers,boolean t){
        this.BrokersHashtable = BrokersHashtable;
        this.Brokers = Brokers;
        this.t=t;
    }

    public Message(MusicChunk Chunk){
        this.Chunk = Chunk;
    }


    //  GETTERS

    public List<ArrayList<Integer>> getBrokersHashtable() { return BrokersHashtable; }
    public MusicChunk getChunk() { return Chunk; }
    public ArrayList<Broker> getBrokers() { return Brokers; }
    public ArrayList<Integer> getHashtable() { return hashtable; }
    public String toString(){
        return (a);
    }
    public String getSong() { return song; }
    public String getAddress() { return address; }
    public int getPort() { return port; }
    public int getHash(){ return hash; }
    public ArrayList<String> getArtists(){ return artists; }
    public int getArtistHash(){ return artistHash; }
    public boolean getBoolean(){
        return t;
    }
    public int getMyHash() { return myHash; }

}
