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
    private ArrayList<Integer> artists =  new ArrayList<>();
    List<Broker> registeredBrokers;
    List<ArrayList<Integer>> BrokersHashtables;
    ObjectInputStream in;
    ObjectInputStream out;
    final static int BrokersPort = 50850;
    int serverHash, port;
    String address;
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

    public void init() {
        registeredBrokers = super.getBrokers();

        try{
            //Setting ip and port to my Broker.
            for (int i = 0; i < registeredBrokers.size(); i++) {
                if (registeredBrokers.get(i).getAddress().equals(InetAddress.getLocalHost().getHostAddress())) {
                    setAddress(registeredBrokers.get(i).getAddress());
                    setPort(registeredBrokers.get(i).getPort());
                }
            }

        }catch (UnknownHostException unknownHost){
            System.out.println("Error!You are trying to connect to an unknown host!");
        }
    }

    public int calculateKeys(Socket connection) {
        String ip = connection.getInetAddress().getHostAddress();
        String socketNumber = String.valueOf(connection.getLocalPort());
        String sum = ip + socketNumber;
        serverHash = sum.hashCode();
        return serverHash;
    }

   // public void initHashtable(){ hashtable = new Hashtable(10, (long)0.8); }

    public void NotifyBrokers(){

        try {
            for (int i = 0; i < registeredBrokers.size(); i++) {
                if (registeredBrokers.get(i).getAddress().equals(InetAddress.getLocalHost().getHostAddress())) {
                    BrokersHashtables.add(i, artists);
                }
            }
            BrokerCommunicator BrC = new BrokerCommunicator(artists, registeredBrokers);
            new Thread(BrC).start();
        }catch (UnknownHostException unknownHost){
            System.out.println("Error!You are trying to connect to an unknown host!");
        }
    }

    public void notifyPublisher() {

        try {
            providerSocketPub = new ServerSocket(getPort(), 10);

            while (true) {
                System.out.println("mphka");

                connectionPub = providerSocketPub.accept();

                int key = calculateKeys(connectionPub);
                Worker wk = new Worker(connectionPub,key,true);

                registeredPublishers = wk.getRegisteredPublishers();
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }

    public void acceptConnection() {
        try{
            System.out.println("mphka1");

            while (true) {

                //Check if it's an incoming message from Broker.
                if (!entrance) {

                    providerSocket = new ServerSocket(BrokersPort, 10);
                    connection = providerSocket.accept();

                    Worker wk = new Worker(connection, registeredUsers, registeredPublishers, registeredBrokers, artists,BrokersHashtables);

                    System.out.println("U son of bitch.Im in.");
                    new Thread(wk).start();

                    BrokersHashtables = wk.getBrokersHashtable();
                    entrance = wk.getBroker();

                    if (wk.getEndOfThread()) {
                        connection.close();
                    }

                }


                //if true, register the new client and start a worker in normal mode.
                //TODO: Create a check so old clients are only registered once.
                else {

                    providerSocket = new ServerSocket(getPort() - 1, 10);
                    connection = providerSocket.accept();

                    Worker wk = new Worker(connection,registeredUsers, registeredPublishers, registeredBrokers,artists, BrokersHashtables);

                    System.out.println("Worker created.");

                    new Thread(wk).start();

                    registeredUsers = wk.getRegisteredUsers();

                    if (wk.getEndOfThread()) {
                        connection.close();
                    }
                }

            }

        }catch (IOException ioException) {
            ioException.printStackTrace();
        }

}

    public void disconnect(){
        try{
//            in.close();
//            out.close();
            connectionPub.close();

        }catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void run(){
        notifyPublisher();
        NotifyBrokers();
        acceptConnection();
    }

    public static void main(String args[]) {

        File file = new File("src\\Brokers.txt");

        Broker br1 = new Broker();
        //Broker br2 = new Broker();
        //Broker br3 = new Broker();
        br1.setBrokers(file);

        br1.init();

//        br2.notifyPublisher();
//        br3.notifyPublisher();

        //br2.setBrokers(file);
        // br3.setBrokers(file);

        //First Broker
        new Thread(br1).start();
        //Second Broker
        // new Thread(br2).start();
        //Third Broker
        //new Thread(br3).start();
    }
}