import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;

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
}












