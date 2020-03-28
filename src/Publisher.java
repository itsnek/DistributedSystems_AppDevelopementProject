import java.net.*;
import java.io.*;
import java.rmi.server.ServerCloneException;
import java.util.ArrayList;
import java.util.List;

public class Publisher extends Node{

    private static ArrayList <ArtistName> Artists = new ArrayList<ArtistName> (30);
    private static ArrayList <MusicFile> Songs = new ArrayList<MusicFile> (300);
   /* private static ArrayList<ServerSocket> serverSockets = new ArrayList<ServerSocket>();
    private static ArrayList<Socket> clientSockets = new ArrayList<Socket>();
    private static ArrayList<ObjectOutputStream> out = new ArrayList<ObjectOutputStream>();
    private static ArrayList<ObjectInputStream> in = new ArrayList<ObjectInputStream>();
    private static int socketCounter = 0; // It counts total sockets.
    private static int clientSocketCounter = 0; //It counts only sockets of clients.
    private static int serverSocketCounter = 0; //It counts only sockets of servers.*/
   private static final int startingSocketNumber = 50190;
    private List <Broker> Brokers;
    private Socket clientSocket = null;
    private ServerSocket serverSocket = null;
    String Hashkey;


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

    public void init (ArrayList <ArtistName> artists, ArrayList<MusicFile> songs) {

        Artists = artists;
        Songs = songs;

    }
    // Create client side connection.
    public void connectToServer () {
        try {
            /*
            clientSockets.set(socketCounter, new Socket(serverIp, startingSocketNumber + socketCounter));
            out.set(socketCounter, new ObjectOutputStream(clientSockets.get(socketCounter).getOutputStream()));
            in.set(socketCounter, new ObjectInputStream(clientSockets.get(socketCounter).getInputStream()));
            socketCounter++;
            clientSocketCounter++;*/
            serverSocket = new ServerSocket(startingSocketNumber,2);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        while (true) {

            try {

                System.out.println("mpainw");
                clientSocket = serverSocket.accept();
                System.out.println("vgainw");
                PublisherThread pt = new PublisherThread (clientSocket);
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

    /*public void push(ArtistName artN,Value v){

        try {

            //First returns the list of the artist's songs,or failure message.
            if (Artists.contains(artN)) {
                out.writeBytes(artN + "'s list of songs :");
                for (int i = 0; i < Songs.size(); i++) {
                    if (Songs.get(i).getArtistName().equals(artN.getArtistName())) {
                        out.writeBytes(Songs.get(i).getTrackName());
                    }
                }
            } else {
                notifyFailure();
            }

            //if client answers the song he requests then :
            for (int i = 0; i < Songs.size(); i++) {
                if (Songs.get(i) == v.getValue()) {
                    out.write(Songs.get(i).getMusicFileExtract());
                }
            }
        }catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    public  void notifyFailure(Broker br){
        out.writeBytes("Sorry,we don't have any songs of this artist.");
    }*/

    public static void main(String args[]){

        //Insert datasets
        File f = new File("");

        Publisher p = new Publisher();
        p.connectToServer();
        p.disconnect();

    }

}












