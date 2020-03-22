import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.List;
import java.lang.*;

import static java.lang.Integer.parseInt;

public class Broker extends Node implements Runnable {

    ServerSocket providerSocket = null;
    Socket connection = null;
    Socket requestSocket = null;
    List<Consumer> registeredUsers;
    List<Publisher> registeredPublishers;
    int port,address;

    Broker(){

    }

    Broker(int address){
        this.address = address;
    }


    public int calculateKeys() {

        int ip = parseInt(providerSocket.getInetAddress().getHostAddress());  // Make registry key for
        int socketNumber = providerSocket.getLocalPort();                     // registeredUsers list.
        Integer sum = ip + socketNumber;
        int serverHash = sum.hashCode();

        return serverHash;

    }

    public Publisher acceptConnection(Publisher pub) {

        int ip = parseInt(requestSocket.getInetAddress().getHostAddress()); // Make registry key for
        int socketNumber = requestSocket.getLocalPort();                    // registeredPublishers list.
        Integer sum = ip + socketNumber;
        int serverHash = sum.hashCode();
        return new Publisher(Integer.toString(serverHash));                 // Return key.

    }

    public void acceptConnection() {
        try{

            providerSocket = new ServerSocket(4321, 10);       // Accepts connection
            connection = providerSocket.accept();                            // with client.
            System.out.println("Connection opened successfully.");
            registeredUsers.add(new Consumer(Integer.toString(calculateKeys()))); // Insert user's key.
            System.out.println("Client registered.");
            Worker wk = new Worker(connection); // Enters worker class.
            System.out.println("Handler created.");
            new Thread(wk).start();


        }catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

    public void notifyPubliser(Message mes) {

        try{
            requestSocket = new Socket(pub, 4321);              // Requests connection with publisher.
            registeredPublishers.add(acceptConnection(pub));    // Inserts key.
            System.out.println("Publisher registered.");
            pull(new ArtistName(mes.getA()));                   // Goes to pull method.

        }catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

    public void disconnect(){

        try {
            providerSocket.close();             // Closes connection.
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

    public void pull(ArtistName aName) {

        Worker wk = new Worker(requestSocket);                              // Creates handler and passes to publisher the message.
        System.out.println("Handler created.");
        new Thread(wk).start();
        try {
            wk.out.writeObject(new Message(aName.getArtistName()));         // Gives outputStream value,because request == null.
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

    public void run(){

        // Accept connection with client and starts the whole process.
        acceptConnection();

       /*if(timeWithoutResponse = 500){
            disconnect();
        }*/

    }

    public static void main(String args[]) {

        new Thread(new Broker()).start();

    }

}
