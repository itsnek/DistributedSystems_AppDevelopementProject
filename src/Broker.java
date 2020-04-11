import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;

import static java.lang.Integer.parseInt;

public class Broker extends Node implements Serializable  {

    //private static final long serialVersionUID = 7526472295622776147L;
    private ServerSocket providerSocket = null;
    private ServerSocket providerSocketPub = null;
    private Socket connection = null;
    private Socket connectionPub = null;
    private ArrayList<Consumer> registeredUsers = new ArrayList<>();
    private ArrayList<Publisher> registeredPublishers =  new ArrayList<>();
    private ArrayList<Integer> artists =  new ArrayList<>();
    ArrayList<Broker> registeredBrokers;
    ArrayList<ArrayList<Integer>> BrokersHashtables = new ArrayList<>();
    ObjectInputStream in;
    final static int BrokersPort = 50850;
    int serverHash, port;
    String address,Scope;
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
                BrokersHashtables.add(null);
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
                    System.out.println(BrokersHashtables.size());
                    BrokersHashtables.add(i, artists);
                }
            }
            BrokerCommunicator BrC = new BrokerCommunicator(artists, registeredBrokers);
            new Thread(BrC).start();
        }catch (UnknownHostException unknownHost){
            System.out.println("Error!You are trying to connect to an unknown host!");
        }
    }

    public void receiveArtists(ArrayList<String> artistsMessage,Socket connection){
        int myHash = calculateKeys(connection);
        for(int i = 0; i < artistsMessage.size(); i++) {
            if (myHash > artistsMessage.get(i).hashCode() && !artists.contains(artistsMessage.get(i).hashCode())) {
                artists.add(artistsMessage.get(i).hashCode());
            }
        }
    }

    public void notifyPublisher() {

        try {
            providerSocketPub = new ServerSocket(getPort(), 10);

            while (true) {
                System.out.println("mphka");
                connectionPub = providerSocketPub.accept();
                ObjectOutputStream out = new ObjectOutputStream(connectionPub.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(connectionPub.getInputStream());

                Message temp = (Message)in.readObject();
                Scope = temp.toString();
                receiveArtists(temp.getArtists(),connectionPub);

                Publisher p = new Publisher(connectionPub.getInetAddress().getHostAddress(),Scope);
                registeredPublishers.add(p);

                if(artists.size() != 0) {
                    out.writeObject(new Message(artists));
                    out.flush();
                }

                if(registeredPublishers.size() == 2){
                    break;
                }
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }catch (ClassNotFoundException e) {
            System.out.println("/nUnknown object type received.");
            e.printStackTrace();
        }

    }

    public void acceptConnection() {
        try{
            System.out.println("mphka1");

            //Check if it's an incoming message from Broker.
            if (!entrance) {

                providerSocket = new ServerSocket(BrokersPort, 15);

                while (true) {
                    System.out.println("mphka2");

                    connection = providerSocket.accept();

                    Worker wk = new Worker(connection, registeredUsers, registeredPublishers, registeredBrokers, artists, BrokersHashtables);

                    System.out.println("U son of bitch.Im in.");
                    new Thread(wk).start();
                    while (!wk.getEndOfThread()) {
                       // System.out.println("Loading");
                        //connection.close();
                    }
                    BrokersHashtables = wk.getBrokersHashtable();
                    System.out.println(BrokersHashtables.size());

                    if (BrokersHashtables.size() == registeredBrokers.size()) {
                        System.out.println("Yo");
                        entrance = wk.getEntrance();
                        break;
                    }
                }
                //connection.close();
            }


            //if true, register the new client and start a worker in normal mode.
            //TODO: Create a check so old clients are only registered once.

            System.out.println(getPort() - 1);
            providerSocket = new ServerSocket(getPort() - 1, 10);

            while (true) {
                connection = providerSocket.accept();

                Worker wk = new Worker(connection, registeredUsers, registeredPublishers, registeredBrokers, artists, BrokersHashtables);
                wk.setEntrance(true);
                System.out.println("Worker created.");

                new Thread(wk).start();
                while (!wk.getEndOfThread()) {
                    //System.out.println("Loading..");
                }
                registeredUsers = wk.getRegisteredUsers();
            }
//                    if() {
//                        connection.close();
//                    }



        }catch (IOException ioException) {
            ioException.printStackTrace();
        }

}

    public void disconnect(){
        try{
            connectionPub.close();
        }catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static void main(String args[]) {

        File file = new File("src\\Brokers.txt");

        Broker br1 = new Broker();

        br1.setBrokers(file);

        br1.init();

        br1.notifyPublisher();
        br1.NotifyBrokers();
        br1.acceptConnection();

    }
}