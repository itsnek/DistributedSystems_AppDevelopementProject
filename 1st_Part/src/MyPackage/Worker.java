package MyPackage;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import static java.lang.Integer.parseInt;

public class Worker extends Thread {

    private static final long serialVersionUID = 3828930004421967914L;
    int c = 0;
    long myHash,smallestHash;
    String requestedSong;
    Message tempA;
    private Socket requestSocket = null;
    private Socket connection = null;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    private ObjectOutputStream publisherOut = null;
    private ObjectInputStream publisherIn = null;
    private ArrayList<Consumer> registeredUsers =  new ArrayList<>();
    private ArrayList<Publisher> registeredPublishers =  new ArrayList<>();
    private ArrayList<Long> artists =  new ArrayList<>();
    private ArrayList<Broker> registeredBrokers;
    private ArrayList<ArrayList<Long>> BrokersHashtable;
    List<String> MegaArtistList;
    private boolean endOfThread = false , ask = false , found = false;
    private boolean changed = false,entrance = false;

    // Constructors

    public Worker(Socket connection,ArrayList<Consumer> registeredUsers,ArrayList<Publisher> registeredPublishers,ArrayList<Broker> registeredBrokers,ArrayList<Long> artists,ArrayList<ArrayList<Long>> BrokersHashtable,long myHash,List<String> MegaArtistList) {
        this.registeredUsers = registeredUsers;
        this.registeredBrokers = registeredBrokers;
        this.registeredPublishers = registeredPublishers;
        this.artists = artists;
        this.BrokersHashtable = BrokersHashtable;
        this.connection = connection;
        this.myHash = myHash;
        this.MegaArtistList = MegaArtistList;
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public Worker(Socket connection,List<String> MegaArtistList){
//        this.connection = connection;
//        this.MegaArtistList = MegaArtistList;
//        try {
//            out = new ObjectOutputStream(connection.getOutputStream());
//            in = new ObjectInputStream(connection.getInputStream());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    // Setters / Getters

    public void setEntrance(boolean entrance) {
        this.entrance = entrance;
    }

    public void setAsk(boolean ask) { this.ask = ask; }

    public ArrayList<Consumer> getRegisteredUsers() {
        return registeredUsers;
    }

    public boolean getEntrance(){
        return entrance;
    }

    public boolean getEndOfThread () {
        return endOfThread;
    }

    public ArrayList<ArrayList<Long>> getBrokersHashtable() {
        return BrokersHashtable;
    }

    public boolean getAsk(){ return ask; }

    public List<String> getMegaArtistList() {
        return MegaArtistList;
    }

    public void run() {
        endOfThread = false;
        smallestHash = myHash;

        try {
            try {
                //Check the incoming from other Brokers.
                if(!getEntrance()) {
                    ArrayList<Long> temporArray;
                    for (int i = 0; i < registeredBrokers.size(); i++) {

                        if (registeredBrokers.get(i).getAddress().equals(connection.getInetAddress().getHostAddress())) {
                            //Receives the message from every Broker.
                            Message temp = (Message) in.readObject();
                            temporArray = temp.getHashtable();
                            //Adds value to this field in the the registeredBrokers existing arraylist.
                            registeredBrokers.get(i).setMyHash(temp.getMyHash());

                            //Checks for any null value left from the initiating of the arraylist and removes it.
                            for (int j = 0; j < BrokersHashtable.size(); j++) {

                                if(BrokersHashtable.get(j) != null && !changed){
                                    BrokersHashtable.add(BrokersHashtable.get(j));
                                    BrokersHashtable.remove(BrokersHashtable.get(j+1));
                                    changed = true;
                                }
                                else if (BrokersHashtable.get(j) == null) {
                                    BrokersHashtable.remove(BrokersHashtable.get(j));
                                }

                            }
                            BrokersHashtable.add(i, temporArray);
                        }

                    }
                    //Condition so as to know when no more Broker message will occur.
                    if (BrokersHashtable.size() == registeredBrokers.size()) {
                        entrance = true;
                    }
                }
                else { //Check the incoming from Client.
//                    System.out.println(getAsk());

                    Message request = (Message) in.readObject();
                    System.out.println("Message received from Client.");
                    System.out.println(request.toString());

                    if(request.toString().equals("Hello")) {
                        //Checks if the user is already registered.
//                        if (!registeredUsers.contains(new Consumer(connection.getInetAddress().getHostAddress()))) {
//                            System.out.println("edw mpainw?");
//                            registeredUsers.add(new Consumer(connection.getInetAddress().getHostAddress()));
//                        }
                        while(c < registeredUsers.size()) {
                            System.out.println("Mesa");

                            if (registeredUsers.get(c).getArg1().equals(connection.getInetAddress().getHostAddress())) {
                                c++;
                                found = true;
                            } else {
                                c++;
                            }
                        }
                        if(!found){
                            registeredUsers.add(new Consumer(connection.getInetAddress().getHostAddress()));
                        }
                        System.out.println("e3w");

                        Message brokersInfo = new Message(MegaArtistList);
                        out.writeObject(brokersInfo);
                    }
                    else {
                        System.out.println("mphka2");
                        System.out.println(registeredUsers.size());

                        long artistHash = request.toString().hashCode();
                        requestedSong = request.getSong();

                        System.out.println(artists.size());
                        //Checks if the hash of the client is less than the Broker's.
                        if (!artists.contains(artistHash)) {
                            System.out.println("mphka3");

                            //Return Hashtables and BrokerList to the client.
                            Message brokersInfo = new Message(BrokersHashtable, registeredBrokers, false);
                            out.writeObject(brokersInfo);

                        } else {
                            System.out.println("mphka4");

                            //Return Hashtables and BrokerList to the client.
                            Message brokersInfo = new Message(BrokersHashtable, registeredBrokers, true);
                            out.writeObject(brokersInfo);

                            if (requestedSong != null) {
                                System.out.println("mphka5");

                                try {
                                    for (int i = 0; i < registeredPublishers.size(); i++) {

                                        //Delivers the message to the appropriate Publisher.
                                        if (request.toString().charAt(0) >= registeredPublishers.get(i).getScope().charAt(0) && request.toString().charAt(0) <= registeredPublishers.get(i).getScope().charAt(1)) {

                                            requestSocket = new Socket(registeredPublishers.get(i).getAddress(), 60000); //opens connection
                                            publisherOut = new ObjectOutputStream(requestSocket.getOutputStream()); // streams
                                            publisherIn = new ObjectInputStream(requestSocket.getInputStream());    //  used
                                            System.out.println(requestedSong);

                                            publisherOut.writeObject(new Message(requestedSong)); //send message
                                            publisherOut.flush();
                                            System.out.println("Message sent to publisher.");

                                        }
                                    }
                                } catch (UnknownHostException unknownHost) {
                                    System.out.println("Error! You are trying to connect to an unknown host!");
                                } catch (IOException ioException) {
                                    ioException.printStackTrace();
                                }

                                System.out.println("Job's done!");

                                //Reads incoming message(chunk) from the publisher and passes it back to the consumer.
                                //Getting first chunk.
                                int totalPartitions;
                                while (true) {

                                    Message chunk = (Message) publisherIn.readObject();

                                    totalPartitions = chunk.getChunk().getTotalPartitions();

                                    if(chunk.getChunk()!=null){
                                        System.out.println("stelnei");
                                        System.out.println(chunk.getChunk().getPartitionNumber());
                                    }
                                    out.writeObject(chunk);
                                    break; //It get out from while by "break;" .

                                }
                                System.out.println(totalPartitions);
                                //Getting the rest chunks.
                                int chunksSent = 1;
                                while (chunksSent < totalPartitions) {

                                    Message chunk = (Message) publisherIn.readObject();

                                    if(chunk!=null){
                                        System.out.println("stelnei");
                                        System.out.println(chunk.getChunk().getPartitionNumber());
                                    }
                                    out.writeObject(chunk);

                                    chunksSent++;

                                }

                                System.out.println("Object returning to client...");

                            }
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                    out.close();
                    endOfThread = true;
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}