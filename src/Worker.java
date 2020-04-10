import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import static java.lang.Integer.parseInt;

public class Worker extends Thread {

    private  int hash;
    private String Scope;
    private Socket requestSocket = null;
    private Socket connection = null;
    private Socket connectionPub = null;
    ObjectOutputStream out = null;
    ObjectInputStream in = null;
    ObjectOutputStream outPub = null;
    ObjectInputStream inPub = null;
    private ObjectOutputStream publisherOut = null;
    private ObjectInputStream publisherIn = null;
    private ArrayList<Consumer> registeredUsers =  new ArrayList<>();
    private ArrayList<Publisher> registeredPublishers =  new ArrayList<>();
    private ArrayList<Integer> artists =  new ArrayList<>();
    private List<Broker> registeredBrokers;
    private List<ArrayList<Integer>> BrokersHashtable;
    private boolean endOfThread = false;
    private boolean publisher = false,broker = true;

    public Worker(Socket connection,int key,boolean t){
        this.connectionPub = connection;
        this.hash = key;
        this.publisher = t;
        try {
            outPub = new ObjectOutputStream(connection.getOutputStream());
            inPub = new ObjectInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Worker(Socket connection,ArrayList<Consumer> registeredUsers,ArrayList<Publisher> registeredPublishers,List<Broker> registeredBrokers,ArrayList<Integer> artists,List<ArrayList<Integer>> BrokersHashtable) {
        this.registeredUsers = registeredUsers;
        this.registeredBrokers = registeredBrokers;
        this.registeredPublishers = registeredPublishers;
        this.artists = artists;
        this.BrokersHashtable = BrokersHashtable;
        this.connection = connection;
        try {
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Consumer> getRegisteredUsers() {
        return registeredUsers;
    }

    public ArrayList<Publisher> getRegisteredPublishers() { return registeredPublishers; }

    public boolean getPublisher(){ return publisher; }

    public boolean getBroker(){ return broker; }

    public boolean getEndOfThread () {
        return endOfThread;
    }

    public List<ArrayList<Integer>> getBrokersHashtable() {
        return BrokersHashtable;
    }

    public void receiveArtists(ArrayList<String> artistsMessage){
        int myHash = hash;
        for(int i = 0; i < artistsMessage.size(); i++) {
            if (myHash > artistsMessage.get(i).hashCode()) {
                artists.add(artistsMessage.get(i).hashCode());
            }
        }
    }

    public void run() {
        endOfThread = false;
        try {
            try {
                if(getPublisher()){
                    Message temp = (Message)in.readObject();
                    System.out.println(temp.toString());
                    Scope = temp.toString();
                    receiveArtists(temp.getArtists());
                    System.out.println(artists.size());

                    registeredPublishers.add(new Publisher(connectionPub.getInetAddress().getHostAddress(),Scope));
                    if(artists.size()!=0) {
                        out.writeObject(new Message(artists));
                        out.flush();
                    }

                }
                else if(getBroker()) {
                    for (int i = 0; i < registeredBrokers.size(); i++) {
                        if (registeredBrokers.get(i).getAddress().equals(connection.getInetAddress().getHostAddress())) {
                            System.out.println("You son of a bitch. Im in.");

                            Message temp = (Message) in.readObject();
                            BrokersHashtable.add(i, temp.getHashtable());
                            System.out.println("mphka2");
                            if (registeredBrokers.size() == BrokersHashtable.size()) {
                                broker = false;
                            }
                        }
                    }
                }
                else {

                    Message request = (Message) in.readObject();
                    System.out.println("Message received from Client.");

                    //Checks if the hash of the client is less than the Broker's.
                    if (!artists.contains(request.getArtistHash())) {
                        Message brokersInfo = new Message(BrokersHashtable, registeredBrokers);
                        out.writeObject(brokersInfo);
                    } else {

                        if(!registeredUsers.contains(new Consumer(connection.getInetAddress().getHostAddress()))){
                            registeredUsers.add(new Consumer(connection.getInetAddress().getHostAddress()));
                        }
                        try {
                            for (int i = 0; i < registeredPublishers.size(); i++) {
                                if (request.toString().charAt(0) > registeredPublishers.get(i).getScope().charAt(0) && request.toString().charAt(1) > registeredPublishers.get(i).getScope().charAt(1)) {

                                    requestSocket = new Socket(registeredPublishers.get(i).getAddress(), 50190); //opens connection
                                    publisherOut = new ObjectOutputStream(requestSocket.getOutputStream()); // streams
                                    publisherIn = new ObjectInputStream(requestSocket.getInputStream());    //  used

                                    publisherOut.writeObject(request); //send message
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
                        out.writeObject((Message) publisherIn.readObject());
                        System.out.println("Object returning to client...");
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