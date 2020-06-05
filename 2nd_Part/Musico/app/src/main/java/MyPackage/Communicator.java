package MyPackage;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

public class Communicator extends Thread {

    public List<String> ArrayList = new ArrayList<>();
    Consumer cons = new Consumer();
    int Case = 0;
    String artist,song;
    private static boolean end = false;

    public Communicator(){}

    public Communicator(int Case){
        this.Case = Case;
    }

    public Communicator(int Case,String artist){
        this.Case = Case;
        this.artist = artist;
    }

    public Communicator(int Case,String artist,String song){
        this.Case = Case;
        this.artist = artist;
        this.song = song;
    }


    public void setArrayList(List<String> arrayList) {
        ArrayList = arrayList;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public List<String> getArrayList() {
        return ArrayList;
    }

    public static boolean getEnd() {
        return end;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void run() {

        switch (Case) {
            case 1 :
                cons.getAllArtists();
                setArrayList(cons.getArtistList());
                break;
            case 2 :
                cons.handshake(new ArtistName(artist));
                break;
            case 3 :
                cons.requestSong (new ArtistName(artist), song);
                break;
            case 4 :
                cons.playData(song);
                break;
        }

        setEnd(true);
    }

}
