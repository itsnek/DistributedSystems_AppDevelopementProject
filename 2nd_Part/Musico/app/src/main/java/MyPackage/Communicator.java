package MyPackage;

import java.util.ArrayList;
import java.util.List;

public class Communicator extends Thread {

    public List<String> ArrayList = new ArrayList<>();
    Consumer cons = new Consumer();
    private static boolean end = false;

    public Communicator(){}

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

    @Override
    public void run() {

        cons.getAllArtists();
        setArrayList(cons.getArtistList());
        System.out.println(getArrayList().size());

        setEnd(true);
    }

}
