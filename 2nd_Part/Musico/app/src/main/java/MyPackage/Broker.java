package MyPackage;

//import java.awt.datatransfer.MimeTypeParameterList;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.math.*;

public class Broker extends Node implements Serializable  {

    private static final long serialVersionUID = 3828930004421967914L;
    private ServerSocket providerSocket = null;
    private ServerSocket providerSocketPub = null;
    private Socket connection = null;
    private Socket connectionPub = null;
    private ArrayList<Consumer> registeredUsers = new ArrayList<>();
    private ArrayList<Publisher> registeredPublishers =  new ArrayList<>();
    private ArrayList<Long> artists =  new ArrayList<>();
    ArrayList<Broker> registeredBrokers;
    ArrayList<ArrayList<Long>> BrokersHashtables = new ArrayList<>();
    ArrayList<Long> HashList = new ArrayList<Long>();
    List<String> MegaArtistList = new ArrayList<>();
    final static int BrokersPort = 50850;
    int port,c = 0;
    long serverHash,myHash,biggestHash,smallestHash;
    String address,Scope;
    boolean entrance = false,found = false;
    Worker wk;

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

    public void setMyHash(long myHash) { this.myHash = myHash; }

    public String getAddress() {
        return address;
    }

    public int getPort(){
        return port;
    }

    public long getMyHash() { return myHash; }

    public List<String> getMegaArtistList() {
        return MegaArtistList;
    }

    public void init() {

        try{
            registeredBrokers = super.getBrokers();

            //Setting ip and port to my Broker.Also initializing the arraylist with the hashtables(we use arraylists though) of the brokers.
            for (int i = 0; i < registeredBrokers.size(); i++) {
                BrokersHashtables.add(null);
                if (registeredBrokers.get(i).getAddress().equals(InetAddress.getLocalHost().getHostAddress())) {
                    setAddress(registeredBrokers.get(i).getAddress());
                    setPort(registeredBrokers.get(i).getPort());
                }
            }

            calculateKeys();

        }catch (UnknownHostException unknownHost){
            System.out.println("Error!You are trying to connect to an unknown host!");
        }
    }

    public void calculateKeys(){

        for (int i = 0; i < registeredBrokers.size(); i++) {
            String ip = registeredBrokers.get(i).getAddress();
            String socketNumber = String.valueOf(registeredBrokers.get(i).getPort() - 1);
            String sum = ip + socketNumber;
            long Hash = (long)Math.abs(sum.hashCode());
            if(registeredBrokers.get(i).getAddress().equals(getAddress())){
                setMyHash(Hash);
            }

            int j = 0;
            while(j < HashList.size()){
                if(HashList.get(j)<Hash) {
                    HashList.add(j,Hash);
                    break;
                }else if(HashList.get(j)>Hash && j != HashList.size()-1){
                    j++;
                }else {
                    HashList.add(j+1,Hash);
                    break;
                }
            }
            if(HashList.size() == 0){
                HashList.add(Hash);
            }

        }

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

        smallestHash = HashList.size()-1;
        biggestHash = HashList.get(0);

        System.out.println("edw");
        //Check if the condition is false and if the hashkey is already included in the hashtable.
        for(int i = 0; i < artistsMessage.size(); i++) {
            MegaArtistList.add(artistsMessage.get(i));

            for (int j = 0;j < HashList.size(); j++) {

                if(HashList.get(j) == getMyHash() && j != HashList.size()-1){
                    if(getMyHash() >= artistsMessage.get(i).hashCode() && HashList.get(j+1) < artistsMessage.get(i).hashCode() && !artists.contains((long)artistsMessage.get(i).hashCode())){
                        artists.add((long)artistsMessage.get(i).hashCode());
                    }
                }else if(HashList.get(j) == getMyHash() && j == HashList.size()-1){
                    if(getMyHash() >= artistsMessage.get(i).hashCode() && !artists.contains((long)artistsMessage.get(i).hashCode())){
                        artists.add((long)artistsMessage.get(i).hashCode());
                    }else if(biggestHash < artistsMessage.get(i).hashCode()){
                        long artistHash = artistsMessage.get(i).hashCode();
                        artistHash = artistHash%smallestHash;
                        artists.add(artistHash);
                    }
                }

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
                System.out.println("edw");

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
            for(int i = 0 ; i < artists.size();i++){
                System.out.println(artists.get(i));
            }
            //Check if it's an incoming message from Broker.
            if (!entrance) {

                //Broker creates a listening channel.Listening in a specific port as every Broker will run on different machines.
                providerSocket = new ServerSocket(BrokersPort, 15);

                while (true) {
                    //Waits for an incoming request.
                    connection = providerSocket.accept();

                    //Creates a worker/handler thread,used to read the incoming messages from other Brokers.
                    wk = new Worker(connection, registeredUsers, registeredPublishers, registeredBrokers, artists, BrokersHashtables,getMyHash(),getMegaArtistList());

                    new Thread(wk).start();
                    System.out.println("Loading");

                    //Used so as to to keep the process busy while waiting for the thread's results.
                    while(true) {
                        if(!wk.getEndOfThread()){
                            System.out.println("teleiwse");
                            break;
                        }
                    }
                    //Retrieve the hashtables from the worker/handler.
                    BrokersHashtables = wk.getBrokersHashtable();
                    System.out.println("Loading");

                    //Returning boolean variable used for avoiding entering this field after Broker's communication has ended.
                    entrance = wk.getEntrance();
                    break;

                }
            }

            //Broker creates a listening channel.Waits for clients requests,listening in a nearby port of the given one by the txt file in the beginning of the app.
            providerSocket = new ServerSocket(getPort() - 1, 10);
            System.out.println(getPort() - 1);

            while (true) {
                c = 0;
                //Waits for an incoming request.
                System.out.println("perimenw");
                connection = providerSocket.accept();

                //Creates a worker/handler thread,used to read the incoming messages from clients.
                while(c < registeredUsers.size()) {
                    if (registeredUsers.get(c).getArg1().equals(connection.getInetAddress().getHostAddress())) {
                        System.out.println("ed eim");
                        System.out.println(MegaArtistList.size());

                        wk = new Worker(connection, registeredUsers, registeredPublishers, registeredBrokers, artists, BrokersHashtables, getMyHash(),getMegaArtistList());
                        c++;
                        found = true;
                    } else {
                        System.out.println("edw exw mpei");
                        c++;
                    }
                }
                if(!found){
                    System.out.println("edw15");
                    System.out.println(MegaArtistList.size());

                    wk = new Worker(connection, registeredUsers, registeredPublishers, registeredBrokers, artists, BrokersHashtables, getMyHash(),getMegaArtistList());

                    registeredUsers.add(new Consumer(connection.getInetAddress().getHostAddress()));
                }
                //Set a boolean value as true so as the worker recognises its a client and not an order broker anymore.
                wk.setEntrance(true);
                System.out.println("Worker created.");

                new Thread(wk).start();
                System.out.println("Loading..");

                //Used so as to to keep the process busy while waiting for the thread's results.
//                while (!wk.getEndOfThread()) {
//                    System.out.println("Loading..");
//                }
                while(true) {
                    if(!wk.getEndOfThread()){
                        System.out.println("teleiwse");
                        break;
                    }
                }
                System.out.println("Loading..");

                //Retrieve the arraylist of registered users/client from the worker/handler.
                registeredUsers = wk.getRegisteredUsers();

                found = false;

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

        System.out.println(br1.getMyHash());
        br1.acceptConnection();

    }
}