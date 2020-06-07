package MyPackage;

import java.net.*;
import java.io.*;
import java.util.*;
import com.mpatric.mp3agic.*;

public class Publisher extends Node{

    private static ArrayList <String> Artists = new ArrayList<>(30);
    private static ArrayList <String> Songs = new ArrayList<String> (300);
    private static ArrayList <MusicFile> SongFiles = new ArrayList<MusicFile> (300);
    private static final int startingSocketNumber = 60000;
    private ArrayList <Broker> Brokers;
    private List<ArrayList<Long>> BrokersHashtables;
    private Socket clientSocket = null;
    private ServerSocket serverSocket = null;
    private Socket requestSocket = null;
    private ObjectOutputStream out = null;
    private ObjectInputStream in = null;
    private String scope,address;
    String artistname;
    private static final long serialVersionUID = 3828930004421967914L;

    //Constructors

    Publisher(){

    }

    Publisher(String address,String scope){
        this.address = address;
        this.scope = scope;
    }

    // Setters/Getters

    public String getAddress() {
        return address;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getScope() {
        return scope;
    }

    public void getBrokerList(){
        Brokers = super.getBrokers();
    }

    //Gives values inserted by an external file to the fields.
    public void init () {

        getSongNames();
        findArtistForEachSong(SongFiles); // It contains the name of artist of each song. So many names are being repeated because many songs have same artist.

    }

    private void ReadDataFile (String artistsToGet) {

        //Filepath
        File file = new File ("D:\\Nikos\\Documents\\οπα\\Κατανεμημένα συστήματα\\Project\\Datasets\\dataset2\\dataset2");
        Mp3File mp3File;
        ID3v1 id3v1Tag;
        ID3v2 id3v2Tag;
        File [] list = file.listFiles(); // Initiating list with all the files

        for (int i=0; i<list.length; i++) {

            //Distinct the files with null fields.
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

                }  catch (UnsupportedTagException unsTag) {
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
        //Get the registered Brokers from the parent class(inserted by a txt file).
        getBrokerList();

//        for (int i = 0; i < brokers.size(); i++) {
//            System.out.println(brokers.get(i).getAddress());
//        }

        try {
            for (int i = 0; i < brokers.size(); i++) {
                System.out.println("edw");

                System.out.println(brokers.get(i).getAddress());
                //Creates a request socket.
                requestSocket = new Socket(brokers.get(i).getAddress(), brokers.get(i).getPort());
                out = new ObjectOutputStream(requestSocket.getOutputStream());
                in = new ObjectInputStream(requestSocket.getInputStream());

                //Unite the Broker's information(Hashtable + Range of artists that he is responsible).
                Message ArtistListPlusScope = new Message(Artists, getScope());

                out.writeObject(ArtistListPlusScope);

                //Message temp = (Message) in.readObject();

            }
        }catch(UnknownHostException unknownHost){
            System.out.println("Error!You are trying to connect to an unknown host!");
        }catch(IOException ioException) {
            ioException.printStackTrace();
        }
//        }catch (ClassNotFoundException e) {
//            System.out.println("/nUnknown object type received.");
//            e.printStackTrace();
//        }

    }

    // Create client side connection.
    public void connect() {
        try {
            //Publisher creates a listening channel,listening in a specific port.
            serverSocket = new ServerSocket(startingSocketNumber, 2);

            while (true) {

                clientSocket = serverSocket.accept();

                PublisherThread pt = new PublisherThread(clientSocket, Artists, SongFiles);
                pt.start();


            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
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

            //Read file of songs.
            if (scanner.hasNextLine()) {
                String Scope = scanner.nextLine();
                p.setScope(Scope);
                p.ReadDataFile(Scope);
            }

            //Initiate the arraylists of each publisher with the appropriate songs.
            p.init();

            //Get the Broker's IPs and ports.
            p.setBrokers(new File("src\\Brokers.txt"));

            //Notify every Broker about your artist's Scope.
            p.notifyBrokers();

            //Make connection with Brokers.
            p.connect();

            //Close the connection channel.
            //p.disconnect();

        }catch (FileNotFoundException fnf){
            fnf.printStackTrace();
        }

    }

}
