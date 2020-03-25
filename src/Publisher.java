import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Publisher extends Node implements Runnable {

    private static ArrayList <ArtistName> Artists = new ArrayList<ArtistName> (30);
    private static ArrayList <MusicFile> Songs = new ArrayList<MusicFile> (300);
    private static ArrayList<ServerSocket> serverSockets = new ArrayList<ServerSocket>();
    private static ArrayList<Socket> clientSockets = new ArrayList<Socket>();
    private static ArrayList<ObjectOutputStream> out = new ArrayList<ObjectOutputStream>();
    private static ArrayList<ObjectInputStream> in = new ArrayList<ObjectInputStream>();
    private static final int startingSocketNumber = 1050;
    private static int socketCounter = 0; // It counts total sockets.
    private static int clientSocketCounter = 0; //It counts only sockets of clients.
    private static int serverSocketCounter = 0; //It counts only sockets of servers.
    private List <Broker> Brokers;
    private Socket clientSocket = null;
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

        for (int i=0; i < serverSockets.size(); i++) {
            serverSockets.add (null);
        }

        for (int i=0; i < clientSockets.size(); i++) {
            clientSockets.add (null);
        }

    }
    // Create client side connection.
    public void connectToServer (String serverIp) {
        try {
            clientSockets.set(socketCounter, new Socket(serverIp, startingSocketNumber + socketCounter));
            out.set(socketCounter, new ObjectOutputStream(clientSockets.get(socketCounter).getOutputStream()));
            in.set(socketCounter, new ObjectInputStream(clientSockets.get(socketCounter).getInputStream()));
            socketCounter++;
            clientSocketCounter++;
        } catch (UnknownHostException uhe) {
            System.err.println ("You are trying to connect to an unknown host.");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    public void disconnect (int connectionToClose) {
        try {
            in.get(connectionToClose).close();
            out.get(connectionToClose).close();
            serverSockets.get(connectionToClose).close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void push(ArtistName artN,Value v){

    }

    public  void notifyFailure(Broker br){

    }

    public void run() {

    }

    public void main(String args[]){

        //Insert datasets
        File f = new File("");
        
        //Create publishers
        Publisher p1 = new Publisher();
        Publisher p2 = new Publisher();

    }

}












