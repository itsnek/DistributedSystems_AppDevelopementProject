package MyPackage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Message implements Serializable {
    //private static final long serialVersionUID = 7526472295622776147L;
    String a, address,song;
    int hash, artist,port, artistHash;
    long myHash,Hash;
    ArrayList<String> artists;
    ArrayList<Long> hashtable;
    ArrayList<Broker> Brokers;
    List<ArrayList<Long>> BrokersHashtable;
    List<Broker> registeredBrokers;
    List<String> MegaArtistList;
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

    public Message(ArrayList<Long> hashtable){
        this.hashtable = hashtable;
    }

    public Message(ArrayList<Long> hashtable,long myHash){
        this.hashtable = hashtable;
        this.myHash = myHash;
    }

    public Message(List<ArrayList<Long>> BrokersHashtable, ArrayList<Broker> Brokers,boolean t){
        this.BrokersHashtable = BrokersHashtable;
        this.Brokers = Brokers;
        this.t=t;
    }

    public Message(List<String> MegaArtistList){
        this.MegaArtistList = MegaArtistList;
    }

    public Message(MusicChunk Chunk){
        this.Chunk = Chunk;
    }

    public Message(long Hash){
        this.Hash = Hash;
    }

    //  GETTERS

    public List<ArrayList<Long>> getBrokersHashtable() { return BrokersHashtable; }
    public List<String> getMegaArtistList() {return MegaArtistList;}
    public MusicChunk getChunk() { return Chunk; }
    public ArrayList<Broker> getBrokers() { return Brokers; }
    public ArrayList<Long> getHashtable() { return hashtable; }
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
    public long getMyHash() { return myHash; }

}
