package MyPackage;

import java.util.ArrayList;
import java.util.List;

public class Communicator extends Thread {

    List<String> ArrayList = new ArrayList<>();
    Consumer cons = new Consumer();

    public Communicator(){}

    public List<String> getArrayList() {
        return ArrayList;
    }

    @Override
    public void run() {

        cons.getAllArtists();
        ArrayList = cons.getArtistList();

    }

}
