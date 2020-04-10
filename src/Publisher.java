import java.net.*;
import java.io.*;
import java.util.*;
import com.mpatric.mp3agic.*;

public class Publisher extends Node{

    private static ArrayList <String> Artists = new ArrayList<>(30);
    private static ArrayList <String> Songs = new ArrayList<String> (300);
    private static ArrayList <MusicFile> SongFiles = new ArrayList<MusicFile> (300);
    private static final int startingSocketNumber = 50190;
    private List <Broker> Brokers;
    private Socket clientSocket = null;
    private ServerSocket serverSocket = null;
    private Socket requestSocket = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    public static String Scope,address;
    String artistname;


    Publisher(){

    }

    /*Publisher(int Hashkey){
        this.Hashkey = Hashkey;
        //this.Scope = Scope;
    }*/

    Publisher(String address,String scope){
        this.address = address;
        Scope = scope;
    }

    public static String getAddress() {
        return address;
    }

    public static String getScope() {
        return Scope;
    }

    public void getBrokerList(){
        Brokers = super.getBrokers();
    }

    public void init () {

        getSongNames();
        findArtistForEachSong(SongFiles); // It contains the name of artist of each song. So many names are being repeated because many songs have same artist.

    }

    private void ReadDataFile (String artistsToGet) {

        File file = new File ("D:\\Nikos\\Documents\\οπα\\Κατανεμημένα συστήματα\\Project\\Datasets\\dataset2\\dataset2");
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

                        if(id3v1Tag.getArtist() == null || id3v1Tag.getArtist().equals("")) {
                            artistname = "Rafael Krux";
                        }else {
                            artistname = id3v1Tag.getArtist();
                        }

                        if ((artistname.charAt(0) >= artistsToGet.charAt(0)) && (artistname.charAt(0) <= artistsToGet.charAt(1))) {

                                MusicFile ms = new MusicFile(temp, artistname, list[i].getName());
                                SongFiles.add(ms);

                        }
                    }
                    else if (mp3File.hasId3v2Tag()) {
                        id3v2Tag = mp3File.getId3v2Tag();

                        if(id3v2Tag.getArtist() == null || id3v2Tag.getArtist().equals("")) {
                            artistname = "Rafael Krux";
                        }else {
                            artistname = id3v2Tag.getArtist();
                        }

                        if ((artistname.charAt(0) >= artistsToGet.charAt(0)) && (artistname.charAt(0) <= artistsToGet.charAt(1))) {

                            MusicFile ms = new MusicFile(temp, artistname, list[i].getName());
                            SongFiles.add(ms);

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

                Artists.add(SongFiles.get(i).getArtistName());

            }

        }

    }

    public void notifyBrokers(){
        getBrokerList();
        for(int i = 0; i < brokers.size(); i++){

            try {
                System.out.println(brokers.size());
                requestSocket = new Socket(brokers.get(i).getAddress(), brokers.get(i).getPort());
                out = new ObjectOutputStream(requestSocket.getOutputStream());
                in = new ObjectInputStream(requestSocket.getInputStream());

                Message ArtistListPlusScope = new Message(Artists,getScope());

                out.writeObject(ArtistListPlusScope);

            }catch(UnknownHostException unknownHost){
                System.out.println("Error!You are trying to connect to an unknown host!");
            }catch (IOException ioException) {
                ioException.printStackTrace();
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

            //Insert Publisher Scope.
            File file = new File(args[0]);
            Scanner scanner = new Scanner(file);
            //Make instances.
            Publisher p = new Publisher();
            //Publisher p2 = new Publisher();
            //Read file of songs.
            if (scanner.hasNextLine()) {
                Scope = scanner.nextLine();
                p.ReadDataFile(Scope);
                //p2.ReadDataFile(Scope);
            }
            System.out.println("eimai edw");

            //Initiate the arraylists of each publisher with the appropriate songs.
            p.init();
           // p2.init();
            //Get the Broker's ips and ports.
            p.setBrokers(new File("src\\Brokers.txt"));
            //Notify every Broker about your artist's Scope.
            p.notifyBrokers();
            //p2.notifyBrokers();
            //Make connection with Brokers.
            p.connect();
            //p2.connect();

            //Close the connection channel.
            //p.disconnect();

        }catch (FileNotFoundException fnf){
            fnf.printStackTrace();
        }

    }

}
