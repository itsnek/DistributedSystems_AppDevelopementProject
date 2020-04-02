import java.net.*;
import java.io.*;
import java.util.*;
import com.mpatric.mp3agic.*;

public class Publisher extends Node{

    private static ArrayList <ArtistName> Artists = new ArrayList<ArtistName> (30);
    private static ArrayList <String> Songs = new ArrayList<String> (300);
    private static ArrayList <MusicFile> SongFiles = new ArrayList<MusicFile> (300);
    private static final int startingSocketNumber = 50190;
    private List <Broker> Brokers;
    private Socket clientSocket = null;
    private ServerSocket serverSocket = null;
    String Hashkey;
    int counter = 0;


    Publisher(){

    }

    Publisher(String Hashkey){
       this.Hashkey = Hashkey;
    }

    Publisher(ArrayList artN,ArrayList songs){

        Artists = artN;
        Songs = songs;

    }

    public void getBrokerList(){
        Brokers = super.getBrokers();
    }

    public void init () {

        getSongNames();
        findArtistForEachSong(SongFiles); // It contains the name of artist of each song. So many names are being repeated because many songs have same artist.

    }

    private void ReadDataFile (String artistsToGet) {

        File file = new File ("D:\\Nikos\\Documents\\GitHub\\distributed\\rsc\\dataset2");
        Mp3File mp3File;
        ID3v1 id3v1Tag;
        ID3v2 id3v2Tag;
        File [] list = file.listFiles(); // h arxikh lista pou periexei ola ta arxeia

        for (int i=0; i<list.length; i++) {

            if (!list[i].getName().startsWith("._") && list[i].getName().endsWith(".mp3")) {

                try {

                    mp3File = new Mp3File(list[i]);
                    String temp = list[i].getName().substring(0, list[i].getName().length() - 4);

                    if (mp3File.hasId3v1Tag()) {
                        id3v1Tag = mp3File.getId3v1Tag();
                        if ((id3v1Tag.getArtist().charAt(0) > artistsToGet.charAt(0)) && (id3v1Tag.getArtist().charAt(0) < artistsToGet.charAt(2))) {

                            if(id3v1Tag.getArtist() == null || id3v1Tag.getArtist().equals("")) {
                                String artistname = "Rafael Krux";
                                MusicFile ms = new MusicFile(temp, artistname, list[i].getName());
                                SongFiles.add(ms);
                            }else {
                                MusicFile ms = new MusicFile(temp, id3v1Tag.getArtist(), list[i].getName());
                                SongFiles.add(ms);
                            }

                        }
                    }
                    else if (mp3File.hasId3v2Tag()) {
                        id3v2Tag = mp3File.getId3v2Tag();
                        if ((id3v2Tag.getArtist().charAt(0) > artistsToGet.charAt(0)) && (id3v2Tag.getArtist().charAt(0) < artistsToGet.charAt(2))) {

                            if(id3v2Tag.getArtist() == null || id3v2Tag.getArtist() == "") {
                                String artistname = "Rafael Krux";
                                MusicFile ms = new MusicFile(temp, artistname, list[i].getName());
                                SongFiles.add(ms);
                            }else {
                                MusicFile ms = new MusicFile(temp, id3v2Tag.getArtist(), list[i].getName());
                                SongFiles.add(ms);
                            }

                        }

                    }

                }  catch (UnsupportedTagException unsTag) { // I put catch here so lines after "mp3File = new Mp3File (songs.get(i));" be not executed and program proceed to next repetition in for loop.
                    System.out.println("We caught UnsupportedTagException");
                    unsTag.printStackTrace();
                } catch (InvalidDataException invData) {
                    System.out.println ("We caught InvalidDataException");
                    invData.printStackTrace();
                } catch (IOException ioe) {
                    System.out.println ("We caught IOException");
                    ioe.printStackTrace();
                }
            }

        }

    }

    private void getSongNames(){

        for (int i=0; i<SongFiles.size(); i++) {

                Songs.add(SongFiles.get(i).getTrackName());

        }

    }

    // This method finds artist name for each song in the song list.
    private void findArtistForEachSong (ArrayList<MusicFile> songs) {
        for (int i=0; i<SongFiles.size(); i++) {
            if (!Artists.contains(SongFiles.get(i).getArtistName())) {
                Artists.add(new ArtistName(SongFiles.get(i).getArtistName()));
            }
        }
    }

    // Create client side connection.
    public void connect() {
        try {

            serverSocket = new ServerSocket(startingSocketNumber,2);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        while (true) {

            try {

                clientSocket = serverSocket.accept();

                PublisherThread pt = new PublisherThread (clientSocket,Artists,SongFiles);
                pt.start();

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        }
    }

    public void disconnect(){

        try {

            clientSocket.close();

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    public static void main(String args[]){

        try {
            //Insert Publisher Scope
            File file = new File(args[0]);
            Scanner scanner = new Scanner(file);
            Publisher p = new Publisher();
            if (scanner.hasNextLine()) {
                p.ReadDataFile(scanner.nextLine());
            }
            p.init();

            p.connect();
            //p.disconnect();

        }catch (FileNotFoundException fnf){
                fnf.printStackTrace();
        }

    }

}












