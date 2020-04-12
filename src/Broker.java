import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;

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
    final static int BrokersPort = 50850;
    int serverHash, port , myHash;
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

    public void setMyHash(int myHash) { this.myHash = myHash; }

    public String getAddress() {
        return address;
    }

    public int getPort(){
        return port;
    }

    public int getMyHash() { return myHash; }

    public void init() {
        registeredBrokers = super.getBrokers();

        try{
            //Setting ip and port to my Broker.Also initializing the arraylist with the hashtables(we use arraylists though) of the brokers.
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

    //Calculating the hash for my Broker.
    public int calculateKeys(Socket connection) throws UnknownHostException {
        String ip = InetAddress.getLocalHost().getHostAddress();
        String socketNumber = String.valueOf(connection.getLocalPort());
        String sum = ip + socketNumber;
        serverHash = sum.hashCode();
        return serverHash;
    }

    //Method used for the communication between Brokers.
    public void NotifyBrokers(){
        try {
            //Setting the hashtable of this broker in the correct order,in order to match with the registeredBrokers's order.
            for (int i = 0; i < registeredBrokers.size(); i++) {
                if (registeredBrokers.get(i).getAddress().equals(InetAddress.getLocalHost().getHostAddress())) {
                    BrokersHashtables.add(i, artists);
                }
            }
            //Creating a thread that is responsible for delivering the hashtable and the hash of this Broker to the other Brokers.
            BrokerCommunicator BrC = new BrokerCommunicator(artists, registeredBrokers,getMyHash());
            new Thread(BrC).start();

        }catch (UnknownHostException unknownHost){
            System.out.println("Error!You are trying to connect to an unknown host!");
        }
    }

    //Checks which artists does this Broker have to include in his hashtable.
    public void receiveArtists(ArrayList<String> artistsMessage,Socket connection) throws UnknownHostException{
        myHash = calculateKeys(connection);

        for(int i = 0; i < artistsMessage.size(); i++) {
            //Check if the condition is false and if the hashkey is already included in the hashtable.
            if (myHash > artistsMessage.get(i).hashCode() && !artists.contains(artistsMessage.get(i).hashCode())) {
                artists.add(artistsMessage.get(i).hashCode());
            }
        }

    }

    //Method used for the communication between Publishers and Brokers.
    public void notifyPublisher() {

        try {
            //Broker creates a listening channel.Publishers start the whole process.
            providerSocketPub = new ServerSocket(getPort(), 10);

            while (true) {
                //Waits for an incoming request.
                connectionPub = providerSocketPub.accept();
                //Creates Streams.
                ObjectOutputStream out = new ObjectOutputStream(connectionPub.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(connectionPub.getInputStream());

                //Receives the message.
                Message temp = (Message)in.readObject();

                Scope = temp.toString();
                receiveArtists(temp.getArtists(),connectionPub);

                //Adds the Publisher in the arraylist.
                registeredPublishers.add(new Publisher(connectionPub.getInetAddress().getHostAddress(),Scope));

                //Used to avoid nullpointerException.
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
            //Check if it's an incoming message from Broker.
            if (!entrance) {

                //Broker creates a listening channel.Listening in a specific port as every Broker will run on different machines.
                providerSocket = new ServerSocket(BrokersPort, 15);

                while (true) {
                    //Waits for an incoming request.
                    connection = providerSocket.accept();
                    System.out.println(getMyHash());

                    //Creates a worker/handler thread,used to read the incoming messages from other Brokers.
                    Worker wk = new Worker(connection, registeredUsers, registeredPublishers, registeredBrokers, artists, BrokersHashtables,getMyHash());

                    new Thread(wk).start();

                    //Used so as to to keep the process busy while waiting for the thread's results.
                    while (!wk.getEndOfThread()) {
                       // System.out.println("Loading");
                    }
                    //Retrieve the hashtables from the worker/handler.
                    BrokersHashtables = wk.getBrokersHashtable();

                    //Returning boolean variable used for avoiding entering this field after Broker's communication has ended.
                    entrance = wk.getEntrance();
                    break;

                }
            }

            //Broker creates a listening channel.Waits for clients requests,listening in a nearby port of the given one by the txt file in the beginning of the app.
            providerSocket = new ServerSocket(getPort() - 1, 10);

            while (true) {
                System.out.println("Here we are");

                //Waits for an incoming request.
                connection = providerSocket.accept();

                //Creates a worker/handler thread,used to read the incoming messages from clients.
                Worker wk = new Worker(connection, registeredUsers, registeredPublishers, registeredBrokers, artists, BrokersHashtables,getMyHash());
                //Set a boolean value as true so as the worker recognises its a client and not an order broker anymore.
                wk.setEntrance(true);
                System.out.println("Worker created.");

                new Thread(wk).start();

                //Used so as to to keep the process busy while waiting for the thread's results.
                while (!wk.getEndOfThread()) {
                    //System.out.println("Loading..");
                }
                //Retrieve the arraylist of registered users/client from the worker/handler.
                registeredUsers = wk.getRegisteredUsers();
            }

        }catch (IOException ioException) {
            ioException.printStackTrace();
        }

}

    public void disconnect(Socket connection){
        try{
            connection.close();
        }catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public static void main(String args[]) {

        //Insert the IP addresses and ports of the Brokers.
        File file = new File("src\\Brokers.txt");

        //Create instance.
        Broker br1 = new Broker();

        //Calling a method for the parent class used to retrieve the information from the file and use them.
        br1.setBrokers(file);

        //Give values to this Broker instance.
        br1.init();

        //Explained above.
        br1.notifyPublisher();
        br1.NotifyBrokers();
        br1.acceptConnection();

    }
}