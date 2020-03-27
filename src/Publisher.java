import com.mpatric.mp3agic.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.io.File;

public class Publisher {
    private ArrayList <ArtistName> Artists = new ArrayList<ArtistName> (30);
    private ArrayList <MusicFile> Songs = new ArrayList<MusicFile> (300);
    private ServerSocket serverSocket;
    private Socket clientSocket;
    //private ArrayList<Socket> clientSockets = new ArrayList<Socket>();
    private ArrayList<ObjectOutputStream> out = new ArrayList<ObjectOutputStream>();
    private ArrayList<ObjectInputStream> in = new ArrayList<ObjectInputStream>();
    /*private static final int startingSocketNumber = 1050;
    private int socketCounter = 0; // It counts total sockets.
    private int clientSocketCounter = 0; //It counts only sockets of clients.
    private int serverSocketCounter = 0; //It counts only sockets of servers.*/

    public void init () {
        ArrayList<String> songs = getSongNames();
        ArrayList<ArtistName> artistNames = findArtistForEachSong(songs); // It contains the name of artist of each song. So many names are being repeated because many songs have same artist.

        serverSocket = null;

        /*for (int i=0; i < clientSockets.size(); i++) {
            clientSockets.add (null);
        }*/
    }
    // Create client side connection.
    public void connect () {
        try {
            serverSocket = new ServerSocket(1050);
        } catch (IOException ioe){
            System.err.println("Coudn't start server.");
        }
        while (true) {
            try {
                clientSocket = serverSocket.accept();
                PublisherThread pt = new PublisherThread (clientSocket);
                pt.start();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    private ArrayList<String> getSongNames () {
        File file = new File ("C:/Users/User/Desktop/Distributed Systems/Project/dataset2/dataset2");
        File [] list = file.listFiles(); // h arxikh lista pou periexei ola ta arxeia
        String [] finalList = new String [list.length]; // h lista pou periexei mono ta tragoydia
        String [] rawSongs; //= new String [list.length]; vriskontai mesa kai ta arxeia pou arxizoun me ._
        ArrayList<String> songs = new ArrayList<String>(100);
        for (int i=0; i<list.length; i++) {
            if (list[i].isDirectory()) {
                rawSongs = list[i].list();
                for (int j=0; j<rawSongs.length; j++) {
                    if (!rawSongs[j].startsWith("._") && rawSongs[j].endsWith(".mp3")) {
                        songs.add(rawSongs[j]);
                    }
                }
            } else if (!list[i].getName().startsWith("._") && list[i].getName().endsWith(".mp3")) {
                songs.add(list[i].getName());
            }
        }
        return songs;
    }

    // This method finds artist name for each song in the song list.
    private ArrayList<ArtistName> findArtistForEachSong (ArrayList<String> songs) {
        Mp3File mp3File;
        ID3v1 id3v1Tag;
        ID3v2 id3v2Tag;
        ArrayList<ArtistName> artistNames = new ArrayList<ArtistName>(100); // It contains the name of artist of each song. So many names are being repeated because many songs have same artist.
        for (int i=0; i<songs.size(); i++) {
            try {
                mp3File = new Mp3File (songs.get(i));
                if (mp3File.hasId3v1Tag()) {
                    id3v1Tag = mp3File.getId3v1Tag();
                    artistNames.add( new ArtistName( id3v1Tag.getArtist() ) );
                } else if (mp3File.hasId3v2Tag()) {
                    id3v2Tag = mp3File.getId3v2Tag();
                    artistNames.add( new ArtistName( id3v2Tag.getArtist() ) );
                }
            } catch (UnsupportedTagException unsTag) { // I put catch here so lines after "mp3File = new Mp3File (songs.get(i));" be not executed and program proceed to next repetition in for loop.

            } catch (InvalidDataException invData) { // I put catch here so lines after "mp3File = new Mp3File (songs.get(i));" be not executed and program proceed to next repetition in for loop.

            } catch (IOException ioe) { // I put catch here so lines after "mp3File = new Mp3File (songs.get(i));" be not executed and program proceed to next repetition in for loop.

            }
        }
        return artistNames;
    }
}












