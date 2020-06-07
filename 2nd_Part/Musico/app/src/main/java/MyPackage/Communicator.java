package MyPackage;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

public class Communicator extends Thread {

    public List<String> ArrayList = new ArrayList<>();
    Consumer cons = new Consumer();
    int Case = 0;
    String artist,song;
    private static boolean end = false;
    private ObjectInputStream in = null;
    private Context context;

    public Communicator(){}

    public Communicator(Consumer cons,int Case){
       this.cons = cons;
        this.Case = Case;
    }

    public Communicator(Consumer cons,int Case,String artist){
        this.cons = cons;
        this.Case = Case;
        this.artist = artist;
    }

    public Communicator(Consumer cons, Context con, int Case, String artist, String song){
        this.cons = cons;
        this.context = con;
        this.Case = Case;
        this.artist = artist;
        this.song = song;
    }

    public void setIn(ObjectInputStream in) {
        this.in = in;
    }

    public ObjectInputStream getInputStream(){
        return in;
    }


    public void setArrayList(List<String> arrayList) {
        ArrayList = arrayList;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public void setCase(int aCase) {
        Case = aCase;
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

        setEnd(false);

        switch (Case) {
            case 1 :
                cons.getAllArtists();
                setArrayList(cons.getArtistList());
                setIn(cons.getIn());
                break;
            case 2 :
                cons.handshake(new ArtistName(artist));
                setIn(cons.getIn());
                break;
            case 3 :
                setIn(cons.requestSong (new ArtistName(artist), song));
                try {
                    cons.playData(song,context);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case 4 :
                setIn(cons.requestSong (new ArtistName(artist), song));
                try {
                    cons.playSongOnline(cons,context,song);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;

        }

        setEnd(true);
    }

}
