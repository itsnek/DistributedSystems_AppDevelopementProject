import com.mpatric.mp3agic.*;

import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Scanner;

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

    public void init (String artistsToGet) {
        ArrayList<String> songs = getSongNamesAndArtistNames(artistsToGet);
        //ArrayList<ArtistName> artistNames = findArtistForEachSong(songs, artistsToGet); // It contains the name of artist of each song. So many names are being repeated because many songs have same artist.
        ArrayList<String> temp = new ArrayList<String> (); // Here are going to be inserted all artists's names but only one time each of them.
        for (int i=0; i<Artists.size(); i++) {
            if (!temp.contains(Artists.get(i).getArtistName()))
                temp.add(Artists.get(i).getArtistName());
        }
        for (int i=0; i<Artists.size(); i++) {
            Artists.remove(i);
        }
        for (int i=0; i<Artists.size(); i++){
            Artists.add (new ArtistName(temp.get(i) ) );
        }
        for (int i=0; i<Artists.size(); i++) {
            System.out.println (Artists.get(i).getArtistName());
        }
        serverSocket = null;

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

    private ArrayList<String> getSongNamesAndArtistNames (String artistsToGet) {
        File file = new File ("C:/Users/User/Desktop/Distributed Systems/Project/dataset1/dataset1");
        File [] list = file.listFiles(); // h arxikh lista pou periexei ola ta arxeia
        for (int i=0; i<list.length; i++) {
            System.out.println (list[i].getAbsolutePath().replace('\\', '/'));
        }
        File [] rawSongs; //= new String [list.length]; vriskontai mesa kai ta arxeia pou arxizoun me ._
        ArrayList<String> songs = new ArrayList<String>(100);
        for (int i=0; i<list.length; i++) {
            if (list[i].isDirectory()) {
                rawSongs = list[i].listFiles();
                for (int j=0; j<rawSongs.length; j++) {
                    if (!rawSongs[j].getName().startsWith("._") && rawSongs[j].getName().endsWith(".mp3")) {

                        if (findArtistForSong(rawSongs[j].getAbsolutePath(), artistsToGet)) {
                            songs.add(rawSongs[j].getName());
                        }
                    }
                }
            } else if (!list[i].getName().startsWith("._") && list[i].getName().endsWith(".mp3")) {
                if (findArtistForSong(list[i].getAbsolutePath(), artistsToGet)) {
                    songs.add(list[i].getName());
                }
            }
        }

        return songs;
    }

    // This method finds artist name for each song in the song list.
    /*private ArrayList<ArtistName> findArtistForEachSong (ArrayList<String> songs, String artistsToGet) {
        Mp3File mp3File;
        ID3v1 id3v1Tag;
        ID3v2 id3v2Tag;
        ArrayList<ArtistName> artistNames = new ArrayList<ArtistName>(100); // It contains the name of artist of each song. So many names are being repeated because many songs have same artist.
        for (int i=0; i<songs.size(); i++) {
            try {System.out.println ("We are in for    " + songs.get(i));
                mp3File = new Mp3File (songs.get(i));
                if (mp3File.hasId3v1Tag()) {
                    System.out.println ("We are in if");
                    id3v1Tag = mp3File.getId3v1Tag();
                    if ((id3v1Tag.getArtist().charAt(0) > artistsToGet.charAt(0)) && (id3v1Tag.getArtist().charAt(0) < artistsToGet.charAt(2))) {
                        artistNames.add(new ArtistName(id3v1Tag.getArtist() ) );
                        System.out.println(id3v1Tag.getArtist());
                    }
                } else if (mp3File.hasId3v2Tag()) {
                    id3v2Tag = mp3File.getId3v2Tag();
                    if ((id3v2Tag.getArtist().charAt(0) > artistsToGet.charAt(0)) && (id3v2Tag.getArtist().charAt(0) < artistsToGet.charAt(2))) {
                        artistNames.add( new ArtistName( id3v2Tag.getArtist() ) );
                        System.out.println(id3v2Tag.getArtist());
                    }
                }
            } catch (UnsupportedTagException unsTag) { // I put catch here so lines after "mp3File = new Mp3File (songs.get(i));" be not executed and program proceed to next repetition in for loop.
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
        return artistNames;
    }*/

    // Here we insert the name of the artist for each song in Artists. This means that many artists's names are repeated but it is fixed in init.
    private boolean findArtistForSong (String path, String artistsToGet) {
        try {
            Mp3File mp3File = new Mp3File(path.replace('\\', '/'));
            ID3v1 id3v1Tag;
            ID3v2 id3v2Tag;
            if (mp3File.hasId3v1Tag()) {
                System.out.println ("We are in if of 3v1");
                id3v1Tag = mp3File.getId3v1Tag();
                System.out.println(id3v1Tag.getArtist() + " " + path);
                System.out.println (id3v1Tag.getArtist().equals(""));
                if (id3v1Tag.getArtist().equals("")) {
                    return false;
                }
                if ((id3v1Tag.getArtist().charAt(0) > artistsToGet.charAt(0)) && (id3v1Tag.getArtist().charAt(0) < artistsToGet.charAt(2))) {
                    Artists.add(new ArtistName(id3v1Tag.getArtist() ) );

                    return true;
                }
            } else if (mp3File.hasId3v2Tag()) {
                System.out.println ("We are in if of 3v2");
                id3v2Tag = mp3File.getId3v2Tag();
                System.out.println(id3v2Tag.getArtist() + " " + path);
                System.out.println (id3v2Tag.getArtist().equals(""));
                if (id3v2Tag.getArtist().equals("")) {
                    return false;
                }
                if ((id3v2Tag.getArtist().charAt(0) > artistsToGet.charAt(0)) && (id3v2Tag.getArtist().charAt(0) < artistsToGet.charAt(2))) {
                    Artists.add( new ArtistName( id3v2Tag.getArtist() ) );
                    System.out.println(id3v2Tag.getArtist());
                    return true;
                }
            }
        } catch (UnsupportedTagException unsTag) { // I put catch here so lines after "mp3File = new Mp3File (songs.get(i));" be not executed and program proceed to next repetition in for loop.
            System.out.println("We caught UnsupportedTagException");
            unsTag.printStackTrace();
            return false;
        } catch (InvalidDataException invData) {
            System.out.println ("We caught InvalidDataException");
            invData.printStackTrace();
            return false;
        } catch (IOException ioe) {
            System.out.println ("We caught IOException");
            ioe.printStackTrace();
            return false;
        }
        return false;
    }

    public static void main(String[] args) {
        try {
        File file = new File (args[0]);
        Scanner scanner = new Scanner (file);
        Publisher p = new Publisher();
        if (scanner.hasNextLine()) {
            p.init(scanner.nextLine());
        }
        } catch (FileNotFoundException fnf) {
            fnf.printStackTrace();
        }
    }
}












