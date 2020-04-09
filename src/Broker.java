import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.*;
import java.lang.*;

import static java.lang.Integer.parseInt;

public class Broker extends Node implements Runnable {

    private ServerSocket providerSocket = null;
    private ServerSocket providerSocketPub = null;
    private Socket connection = null;
    private Socket connectionPub = null;
    private ArrayList<Consumer> registeredUsers = new ArrayList<>();
    private ArrayList<Publisher> registeredPublishers =  new ArrayList<>();
    private ArrayList<ArtistName> artists =  new ArrayList<>();
    List<Broker> registeredBrokers;
    ObjectInputStream in;

    int serverHash, port;
    String address;
    Hashtable hashtable;
    boolean entrance = false;

    Broker(){}

    Broker(String address, int port){
        this.address = address;
        this.port = port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public int getPort(){
        return port;
    }

    public int calculateKeys() {
        int ip = parseInt(providerSocket.getInetAddress().getHostAddress());
        int socketNumber = providerSocket.getLocalPort();
        Integer sum = ip + socketNumber;
        serverHash = sum.hashCode();
        return serverHash;
    }

    public void initHashtable(){ hashtable = new Hashtable(10, (long)0.8); }

    public void NotifyBrokers(){
        registeredBrokers = super.getBrokers();

        try{
            //Setting ip and port to my Broker.
            for (int i = 0; i < registeredBrokers.size(); i++) {
                if (registeredBrokers.get(i).getAddress().equals(InetAddress.getLocalHost().getHostAddress())) {
                    setAddress(registeredBrokers.get(i).getAddress());
                    setPort(registeredBrokers.get(i).getPort());
                }
            }

            BrokerCommunicator BrC = new BrokerCommunicator(hashtable, registeredBrokers);
            new Thread(BrC).start();

        }catch (UnknownHostException unknownHost){
            System.out.println("Error!You are trying to connect to an unknown host!");
        }
    }

    public void receiveArtists(Message artistsMessage){
        int myHash = calculateKeys();
        ArrayList<ArtistName> temp = artistsMessage.getArtists();
        for(int i = 0; i < temp.size(); i++) {
            if (myHash > temp.get(i).hashCode()) {
                artists.add(temp.get(i));
            }
        }
    }

    public void acceptConnection() {
        try{

            int temp = getPort() -1;
            System.out.println(temp);

            providerSocket = new ServerSocket(temp, 10);

            while (true) {

                connection = providerSocket.accept();

                Worker wk = new Worker(connection, registeredUsers, registeredPublishers, registeredBrokers, artists);
                System.out.println("Worker created.");

                if (!entrance) {
                    for (int i = 0; i < registeredBrokers.size(); i++) {
                        if (registeredBrokers.get(i).getAddress().equals(connection.getInetAddress().getHostAddress())) {
                            System.out.println("You son of a bitch. Im in.");
                            new Thread(wk).start();
                        }
                    }
                }else {
                    if (connection.getPort() == 60450){
                        in = new ObjectInputStream(connection.getInputStream());
                        receiveArtists((Message)in.readObject());
                    }else {

                        if (!registeredUsers.contains(new Consumer(serverHash))) {
                            System.out.println("Client registered.");
                            registeredUsers.add(new Consumer(serverHash));
                        }
                    }
                }
                new Thread(wk).start();
                if (wk.getEndOfThread()) {
                    connection.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
}

    public void disconnect(){ super.disconnect();}

    public void run(){
        NotifyBrokers();
        acceptConnection();
    }

    public static void main(String args[]) {

        File file = new File("src\\Brokers.txt");

        Broker br1 = new Broker();
        //Broker br2 = new Broker();
        //Broker br3 = new Broker();

        br1.setBrokers(file);
        //br2.setBrokers(file);
        // br3.setBrokers(file);

        //br1.notifyPublisher();
        //br2.notifyPublisher();
        //br3.notifyPublisher();

        //First Broker
        new Thread(br1).start();
        //Second Broker
        // new Thread(br2).start();
        //Third Broker
        //new Thread(br3).start();
    }
}