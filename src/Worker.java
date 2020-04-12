import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import static java.lang.Integer.parseInt;

public class Worker extends Thread {

    int myHash,biggestHash,smallestHash;
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
    private ArrayList<Integer> artists =  new ArrayList<>();
    private ArrayList<Broker> registeredBrokers;
    private ArrayList<ArrayList<Integer>> BrokersHashtable;
    private boolean endOfThread = false;
    private boolean changed = false,entrance = false;

    // Constructors

    public Worker(Socket connection,ArrayList<Consumer> registeredUsers,ArrayList<Publisher> registeredPublishers,ArrayList<Broker> registeredBrokers,ArrayList<Integer> artists,ArrayList<ArrayList<Integer>> BrokersHashtable,int myHash) {
        this.registeredUsers = registeredUsers;
        this.registeredBrokers = registeredBrokers;
        this.registeredPublishers = registeredPublishers;
        this.artists = artists;
        this.BrokersHashtable = BrokersHashtable;
        this.connection = connection;
        this.myHash = myHash;
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Setters / Getters

    public ArrayList<Consumer> getRegisteredUsers() {
        return registeredUsers;
    }

    public boolean getEntrance(){
        return entrance;
    }

    public void setEntrance(boolean entrance) {
        this.entrance = entrance;
    }

    public boolean getEndOfThread () {
        return endOfThread;
    }

    public ArrayList<ArrayList<Integer>> getBrokersHashtable() {
        return BrokersHashtable;
    }


    public void run() {
        endOfThread = false;
        try {
            try {
                //Check the incoming from other Brokers.
                if(!getEntrance()) {
                    ArrayList<Integer> temporArray;
                    smallestHash = myHash;
                    for (int i = 0; i < registeredBrokers.size(); i++) {

                        if (registeredBrokers.get(i).getAddress().equals(connection.getInetAddress().getHostAddress())) {
                            //Receives the message from every Broker.
                            Message temp = (Message) in.readObject();
                            temporArray = temp.getHashtable();
                            //Adds value to this field in the the registeredBrokers existing arraylist.
                            registeredBrokers.get(i).setMyHash(temp.getMyHash());

                            //Compares the hashes so as to make the necessary changes if a hash has bigger value than all Brokers hashes.
                            if(smallestHash > temp.getMyHash()) {
                                smallestHash = temp.getMyHash();
                            }

                            //Here removes the hashes that already exist in Brokers with smaller/bigger hash,depending on circumstances.
                            if(myHash > temp.getMyHash()) {
                                biggestHash = myHash;
                                for (int j = 0; j < artists.size(); j++) {
                                    if (artists.get(j) < temp.getMyHash()) {
                                        artists.remove(artists.get(j));
                                    }
                                }
                            }else{
                                biggestHash = temp.getMyHash();
                                for (int j = 0; j < temporArray.size(); j++) {
                                    if (temporArray.get(j) < myHash) {
                                        temporArray.remove(temp.getHashtable().get(j));
                                    }
                                }
                            }

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
                    if (BrokersHashtable.size() == registeredBrokers.size()-1) {
                        entrance = true;
                    }
                }
                else { //Check the incoming from Client.

                    Message request = (Message) in.readObject();
                    System.out.println("Message received from Client.");

                    int artistHash = request.toString().hashCode();
                    requestedSong = request.getSong();

                    //Checks if the hash of the client is less than the Broker's.
                    if (!artists.contains(artistHash)) {
                        //Return Hashtables and BrokerList to the client.
                        Message brokersInfo = new Message(BrokersHashtable, registeredBrokers,false);
                        out.writeObject(brokersInfo);

                        //Compares the hashes so as to make the necessary changes if a hash has bigger value than all Brokers hashes.
//                        if(artistHash > biggestHash){
//
//                            artistHash = artistHash%smallestHash;
//
//                            for (int j = 0; j < BrokersHashtable.size(); j++) {
//                                if(registeredBrokers.get(j).getMyHash() == smallestHash){
//                                    BrokersHashtable.get(j).add(artistHash);
//                                }
//                            }
//
//                        }
                    } else {
                        //Return Hashtables and BrokerList to the client.
                        Message brokersInfo = new Message(BrokersHashtable, registeredBrokers, true);
                        out.writeObject(brokersInfo);
                        //Checks if the user is already registered.
                        if (!registeredUsers.contains(new Consumer(connection.getInetAddress().getHostAddress()))) {
                            registeredUsers.add(new Consumer(connection.getInetAddress().getHostAddress()));
                        }
                        if (requestedSong!=null) {

                            try {
                                for (int i = 0; i < registeredPublishers.size(); i++) {

                                    //Delivers the message to the appropriate Publisher.
                                    if (request.toString().charAt(0) >= registeredPublishers.get(i).getScope().charAt(0) && request.toString().charAt(0) <= registeredPublishers.get(i).getScope().charAt(1)) {

                                        requestSocket = new Socket(registeredPublishers.get(i).getAddress(), 50190); //opens connection
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

                                out.writeObject(chunk);
                                break; //It get out from while by "break;" .

                            }

                            //Getting the rest chunks.
                            int chunksSent = 1;
                            while (chunksSent < totalPartitions) {

                                Message chunk = (Message) publisherIn.readObject();

                                out.writeObject(chunk);

                                chunksSent++;

                            }

                            System.out.println("Object returning to client...");

                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                    out.close();
                    System.out.println("Bye");
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